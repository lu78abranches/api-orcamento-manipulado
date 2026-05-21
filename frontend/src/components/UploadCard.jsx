import { useState } from "react";
import { api } from "../services/api";

export default function UploadCard() {

    const [loading, setLoading] = useState(false);
    const [response, setResponse] = useState(null);

    async function handleUpload(event) {

        const file = event.target.files[0];

        if (!file) return;

        const formData = new FormData();
        formData.append("file", file);

        try {

            setLoading(true);

            const result = await api.post(
                "/prescriptions/upload",
                formData
            );

            setResponse(result.data);

        } catch (error) {

            console.error(error);
            alert("Erro ao enviar receita");

        } finally {

            setLoading(false);

        }
    }

    return (
        <div>

            <input
                type="file"
                accept="image/*"
                onChange={handleUpload}
            />

            {loading && (
                <p>🧠 Processando receita...</p>
            )}

            {response && (
                <pre>
                    {JSON.stringify(response, null, 2)}
                </pre>
            )}

        </div>
    );
}