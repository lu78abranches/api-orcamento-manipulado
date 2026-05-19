export default function ResultCard({ data }) {

    if (!data) return null;

    return (
        <div style={{
            marginTop: 30,
            display: "grid",
            gap: 16
        }}>

            <div style={cardStyle}>
                <h3>💊 Medicamentos</h3>
                <ul>
                    {data.medications?.map((m, i) => (
                        <li key={i}>{m}</li>
                    ))}
                </ul>
            </div>

            <div style={cardStyle}>
                <h3>💰 Orçamento</h3>
                <p style={{ fontSize: 24, color: "#22c55e" }}>
                    R$ {data.budget}
                </p>
            </div>

            <div style={cardStyle}>
                <h3>📊 Status</h3>
                <p>{data.status}</p>
            </div>

        </div>
    );
}

const cardStyle = {
    background: "#111827",
    padding: 20,
    borderRadius: 16,
    border: "1px solid #1f2937"
};