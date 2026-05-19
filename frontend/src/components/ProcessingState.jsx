import { motion } from "framer-motion";

export default function ProcessingState() {
    return (
        <div style={{ textAlign: "center", marginTop: 20 }}>

            <motion.div
                animate={{ rotate: 360 }}
                transition={{ repeat: Infinity, duration: 1 }}
                style={{
                    width: 40,
                    height: 40,
                    border: "3px solid #3b82f6",
                    borderTop: "3px solid transparent",
                    borderRadius: "50%",
                    margin: "0 auto"
                }}
            />

            <p style={{ marginTop: 10, color: "#9ca3af" }}>
                🧠 IA analisando receita...
            </p>

        </div>
    );
}