import { useDropzone } from "react-dropzone";
import { useState, useRef, useEffect } from "react";
import { UploadCloud } from "lucide-react";
import { api } from "../services/api";

export default function UploadZone({ onResult }) {

    const [name, setName] = useState("");
    const [loading, setLoading] = useState(false);
    const [result, setResult] = useState(null);
    const [previewResult, setPreviewResult] = useState(null);
    const [error, setError] = useState("");
    const timerRef = useRef(null);

    const processingMarkdown = `# 💊 Orçamento de Manipulação

## ⏳ Processando receita e aguardando aprovação farmacêutica

Por favor, aguarde enquanto nosso farmacêutico verifica os itens e calcula o orçamento.

---

Aguarde alguns instantes...`;

    const markdownToHtml = (markdown) => {
        if (!markdown) {
            return "";
        }

        const rows = markdown.split("\n");
        const htmlLines = [];
        let inList = false;
        let inTable = false;
        const tableLines = [];

        const flushTable = () => {
            if (!inTable) {
                return;
            }
            inTable = false;
            const header = tableLines[0]?.split("|").map((cell) => cell.trim()).filter(Boolean) || [];
            const body = tableLines.slice(2).map((line) => line.split("|").map((cell) => cell.trim()).filter(Boolean));
            htmlLines.push("<table>");
            htmlLines.push("<thead><tr>" + header.map((cell) => `<th>${cell}</th>`).join("") + "</tr></thead>");
            if (body.length) {
                htmlLines.push("<tbody>");
                body.forEach((row) => {
                    htmlLines.push("<tr>" + row.map((cell) => `<td>${cell}</td>`).join("") + "</tr>");
                });
                htmlLines.push("</tbody>");
            }
            htmlLines.push("</table>");
            tableLines.length = 0;
        };

        rows.forEach((line) => {
            const trimmed = line.trim();

            if (trimmed === "") {
                if (inList) {
                    htmlLines.push("</ul>");
                    inList = false;
                }
                flushTable();
                return;
            }

            if (/^---+$/.test(trimmed)) {
                if (inList) {
                    htmlLines.push("</ul>");
                    inList = false;
                }
                flushTable();
                htmlLines.push("<hr />");
                return;
            }

            if (/^#{3}\s+/.test(trimmed)) {
                if (inList) {
                    htmlLines.push("</ul>");
                    inList = false;
                }
                flushTable();
                htmlLines.push(`<h3>${trimmed.substring(4)}</h3>`);
                return;
            }

            if (/^#{2}\s+/.test(trimmed)) {
                if (inList) {
                    htmlLines.push("</ul>");
                    inList = false;
                }
                flushTable();
                htmlLines.push(`<h2>${trimmed.substring(3)}</h2>`);
                return;
            }

            if (/^#\s+/.test(trimmed)) {
                if (inList) {
                    htmlLines.push("</ul>");
                    inList = false;
                }
                flushTable();
                htmlLines.push(`<h1>${trimmed.substring(2)}</h1>`);
                return;
            }

            if (/^\|.*\|/.test(trimmed)) {
                inTable = true;
                tableLines.push(trimmed);
                return;
            }

            if (/^-\s+/.test(trimmed)) {
                if (!inList) {
                    inList = true;
                    htmlLines.push("<ul>");
                }
                const content = trimmed.substring(2);
                htmlLines.push(`<li>${content}</li>`);
                return;
            }

            const strong = trimmed.replace(/\*\*(.*?)\*\*/g, "<strong>$1</strong>");
            htmlLines.push(`<p>${strong}</p>`);
        });

        if (inList) {
            htmlLines.push("</ul>");
            inList = false;
        }
        flushTable();

        return htmlLines.join("");
    };

    const renderMarkdown = (markdown) => ({ __html: markdownToHtml(markdown) });

    useEffect(() => {
        return () => {
            if (timerRef.current) {
                clearTimeout(timerRef.current);
            }
        };
    }, []);

    async function uploadFile(file) {

        const formData = new FormData();
        formData.append("file", file);
        formData.append("nome", name.trim());

        if (timerRef.current) {
            clearTimeout(timerRef.current);
        }

        setPreviewResult({ markdownContent: processingMarkdown });
        setResult(null);
        setError("");
        setLoading(true);

        const startTime = Date.now();

        try {
            const response = await api.post(
                "/prescriptions/upload",
                formData
            );

            const elapsed = Date.now() - startTime;
            const finalResponse = response.data;

            const showFinal = () => {
                setResult(finalResponse);
                setPreviewResult(null);
                if (onResult) {
                    onResult(finalResponse);
                }
            };

            if (elapsed >= 8000) {
                showFinal();
            } else {
                timerRef.current = setTimeout(showFinal, 8000 - elapsed);
            }

        } catch (error) {
            if (timerRef.current) {
                clearTimeout(timerRef.current);
            }
            console.error("Upload error", error);
            setPreviewResult(null);
            const serverMessage = error?.response?.data?.message || error?.response?.data || error?.message;
            setError(
                `Erro ao enviar a receita. ${serverMessage || "Verifique o nome e tente novamente."}`
            );
        } finally {
            setLoading(false);
        }
    }

    const onDrop = (acceptedFiles) => {
        const file = acceptedFiles[0];
        if (!file) return;

        if (!name.trim()) {
            setError("Por favor, informe o nome do cliente antes de enviar a receita.");
            return;
        }

        uploadFile(file);
    };

    const {
        getRootProps,
        getInputProps,
        isDragActive
    } = useDropzone({
        onDrop,
        accept: {
            "image/*": [],
            "application/pdf": [],
            "text/plain": []
        }
    });

    return (
        <div>

            <div style={{ marginBottom: 16 }}>
                <label
                    style={{
                        display: "block",
                        marginBottom: 8,
                        color: "#cbd5e1",
                        fontWeight: 600
                    }}
                >
                    Nome do Cliente
                </label>

                <input
                    type="text"
                    value={name}
                    onChange={(event) => setName(event.target.value)}
                    placeholder="João Silva"
                    style={{
                        width: "100%",
                        padding: 12,
                        borderRadius: 12,
                        border: "1px solid #334155",
                        background: "#0f172a",
                        color: "#f8fafc"
                    }}
                />
            </div>

            {error && (
                <div
                    style={{
                        marginBottom: 16,
                        color: "#fecaca",
                        fontWeight: 600
                    }}
                >
                    {error}
                </div>
            )}

            <div
                {...getRootProps()}
                style={{
                    border: "2px dashed #334155",
                    borderRadius: 24,
                    padding: 60,
                    textAlign: "center",
                    cursor: "pointer",
                    background: isDragActive
                        ? "#111827"
                        : "#0f172a",
                    transition: "0.2s"
                }}
            >

                <input {...getInputProps()} />

                <UploadCloud size={64} />

                <h2
                    style={{
                        marginTop: 20,
                        marginBottom: 12
                    }}
                >
                    Arraste sua receita aqui
                </h2>

                <p
                    style={{
                        color: "#94a3b8"
                    }}
                >
                    ou clique para selecionar uma imagem
                </p>

            </div>

            {loading && (
                <div
                    style={{
                        marginTop: 30,
                        background: "#111827",
                        padding: 24,
                        borderRadius: 20
                    }}
                >

                    <p style={{ marginBottom: 12 }}>
                        🧠 IA analisando receita...
                    </p>

                    <p style={{ color: "#94a3b8" }}>
                        Extraindo medicamentos e gerando orçamento
                    </p>

                </div>
            )}

            {(previewResult || result) && (
                <div
                    style={{
                        marginTop: 30,
                        background: "#111827",
                        padding: 24,
                        borderRadius: 20
                    }}
                >

                    <h2 style={{ marginBottom: 16 }}>
                        {previewResult ? "⏳ Processando Receita" : "✅ Receita Processada"}
                    </h2>

                    <div
                        style={{
                            color: "#cbd5e1",
                            overflow: "auto",
                            whiteSpace: "normal",
                            wordBreak: "break-word"
                        }}
                        dangerouslySetInnerHTML={renderMarkdown((previewResult || result).markdownContent)}
                    />

                </div>
            )}

        </div>
    );
}