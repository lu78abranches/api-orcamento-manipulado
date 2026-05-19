import { useDropzone } from "react-dropzone";
import { UploadCloud } from "lucide-react";
import { api } from "../services/api";
import { useState } from "react";

export default function UploadZone({ onResult }) {

    const [loading, setLoading] = useState(false);

    const onDrop = async (files) => {

        const file = files[0];

        const formData = new FormData();
        formData.append("file", file);

        try {
            setLoading(true);

            const res = await api.post("/prescriptions/upload", formData, {
                headers: { "Content-Type": "multipart/form-data" }
            });

            onResult(res.data);

        } catch (err) {
            alert("Erro ao processar receita");
        } finally {
            setLoading(false);
        }
    };

    const { getRootProps, getInputProps, isDragActive } = useDropzone({
        onDrop,
        accept: { "image/*": [] }
    });

    return (
        <div
            {...getRootProps()}
            style={{
                border: "2px dashed #374151",
                padding: 40,
                borderRadius: 16,
                textAlign: "center",
                background: isDragActive ? "#111827" : "#0f172a",
                cursor: "pointer"
            }}
        >

            <input {...getInputProps()} />

            <UploadCloud size={40} />

            <p style={{ marginTop: 10 }}>
                {isDragActive
                    ? "Solte a receita aqui..."
                    : "Arraste ou clique para enviar a receita"}
            </p>

            {loading && <p>🧠 Processando IA...</p>}
        </div>
    );
}