import UploadZone from "./components/UploadZone";

export default function App() {

  return (
    <div
      style={{
        minHeight: "100vh",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        padding: 24
      }}
    >

      <div
        style={{
          width: "100%",
          maxWidth: 900
        }}
      >

        <div
          style={{
            marginBottom: 40
          }}
        >

          <h1
            style={{
              fontSize: 42,
              marginBottom: 12
            }}
          >
            💊 AI Prescription Analyzer
          </h1>

          <p
            style={{
              color: "#94a3b8",
              fontSize: 18
            }}
          >
            Upload de receitas médicas com processamento por IA
          </p>

        </div>

        <UploadZone />

      </div>

    </div>
  );
}