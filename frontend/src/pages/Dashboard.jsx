import { useState } from "react";
import DashboardLayout from "../components/DashboardLayout";
import UploadZone from "../components/UploadZone";
import ResultCard from "../components/ResultCard";
import ProcessingState from "../components/ProcessingState";

export default function Dashboard() {

    const [result, setResult] = useState(null);
    const [loading] = useState(false);

    return (
        <DashboardLayout>

            <UploadZone
                onResult={(data) => setResult(data)}
            />

            {loading && <ProcessingState />}

            <ResultCard data={result} />

        </DashboardLayout>
    );
}