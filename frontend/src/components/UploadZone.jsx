import { useDropzone } from "react-dropzone";
import { useState } from "react";
import { UploadCloud } from "lucide-react";
import { api } from "../services/api";

export default function UploadZone() {

    const [loading, setLoading] = useState(false);
    const [result, setResult] = useState(null);

    async function uploadFile(file) {

        const formData = new FormData();
        formData.append("file", file);

        try {

            setLoading(true);

            const response = await api.post(
                "/prescriptions/upload",
                formData
            );

            setResult(response.data);

        } catch (error) {

            console.error(error);

            alert("Erro ao enviar receita");

        } finally {

            setLoading(false);

        }
    }

    const onDrop = (acceptedFiles) => {

        const file = acceptedFiles[0];

        if (!file) return;

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

            {result && (
                <div
                    style={{
                        marginTop: 30,
                        background: "#111827",
                        padding: 24,
                        borderRadius: 20
                    }}
                >

                    <h2 style={{ marginBottom: 16 }}>
                        ✅ Receita Processada
                    </h2>

                    <pre
                        style={{
                            color: "#cbd5e1",
                            overflow: "auto"
                        }}
                    >
                        {JSON.stringify(result, null, 2)}
                    </pre>

                </div>
            )}

        </div>
    );
}