function formatBudget(value) {
    if (value == null || value === "") {
        return "—";
    }
    const numeric = Number(value);
    if (Number.isNaN(numeric)) {
        return String(value);
    }
    return numeric.toLocaleString("pt-BR", {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
}

export default function ResultCard({ data }) {

    if (!data) return null;

    const medications = data.medications ?? [];
    const hasMedications = medications.length > 0;

    return (
        <div className="result-cards">

            <div style={cardStyle}>
                <h3>💊 Medicamentos</h3>
                {hasMedications ? (
                    <ul style={listStyle}>
                        {medications.map((m, i) => (
                            <li key={i}>{m}</li>
                        ))}
                    </ul>
                ) : (
                    <p style={mutedStyle}>Nenhum item identificado</p>
                )}
            </div>

            <div style={cardStyle}>
                <h3>💰 Orçamento</h3>
                <p style={{ fontSize: 24, color: "#22c55e", marginTop: 8 }}>
                    R$ {formatBudget(data.budget)}
                </p>
            </div>

            <div style={cardStyle}>
                <h3>📊 Status</h3>
                <p style={{ marginTop: 8, lineHeight: 1.5 }}>{data.status || "—"}</p>
                {data.cliente && (
                    <p style={mutedStyle}>
                        {data.cliente}
                        {data.data ? ` · ${data.data}` : ""}
                    </p>
                )}
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

const listStyle = {
    marginTop: 12,
    paddingLeft: 20,
    lineHeight: 1.6
};

const mutedStyle = {
    marginTop: 8,
    color: "#94a3b8",
    fontSize: 14
};
