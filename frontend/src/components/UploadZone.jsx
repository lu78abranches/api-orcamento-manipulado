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

            if (elapsed >= 6000) {
                showFinal();
            } else {
                timerRef.current = setTimeout(showFinal, 6000 - elapsed);
            }

        } catch (error) {
            if (timerRef.current) {
                clearTimeout(timerRef.current);
            }
            console.error(error);
            setPreviewResult(null);
            setError("Erro ao enviar a receita. Verifique o nome e tente novamente.");
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
            "image/*": []
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

                    <pre
                        style={{
                            color: "#cbd5e1",
                            overflow: "auto",
                            whiteSpace: "pre-wrap",
                            wordBreak: "break-word"
                        }}
                    >
                        {(previewResult || result).markdownContent}
                    </pre>

                </div>
            )}

        </div>
    );
}