export default function DashboardLayout({ children }) {
    return (
        <div style={{
            minHeight: "100vh",
            background: "#0b0f19",
            color: "white",
            padding: "40px",
            fontFamily: "Inter"
        }}>

            <div style={{ maxWidth: 1000, margin: "0 auto" }}>

                <header style={{ marginBottom: 40 }}>
                    <h1 style={{ fontSize: 28 }}>💊 AI Prescription Analyzer</h1>
                    <p style={{ color: "#9ca3af" }}>
                        Upload de receitas + IA + orçamento automático
                    </p>
                </header>

                {children}

            </div>

        </div>
    );
}