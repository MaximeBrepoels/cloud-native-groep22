import React, { useEffect, useState } from "react";
import { BodyweightService } from "@/services/BodyweightService";
import ProgressGraph from "@/components/graphs/BodyweightGraph";
import { useRouter } from "next/router";

const BodyweightCard: React.FC = () => {
    const [showForm, setShowForm] = useState(false);
    const [bodyweight, setBodyweight] = useState("");
    const [bodyweightCollection, setBodyweightCollection] = useState<any[]>([]);
    const [error, setError] = useState("");
    const router = useRouter();

    useEffect(() => {
        fetchBodyweightCollection();
    }, []);

    const fetchBodyweightCollection = async () => {
        const id = sessionStorage.getItem("session_id");
        if (id) {
            const bodyweightService = new BodyweightService();
            const response = await bodyweightService.getBodyweightByUserId(Number(id));
            if (response.status === 200) {
                const bodyWeights = response.data
                    .filter((entry: any) => entry.bodyWeight !== null)
                    .map((entry: any) => ({
                        date: entry.date,
                        bodyWeight: entry.bodyWeight,
                    }));
                setBodyweightCollection(bodyWeights);
            } else {
                setError(response.data.message);
            }
        } else {
            router.replace("/login");
        }
    };

    const handleBodyWeightSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (bodyweight === "") {
            setError("Body weight is required");
            return;
        }
        const id = sessionStorage.getItem("session_id");
        if (id) {
            const bodyweightService = new BodyweightService();
            const response = await bodyweightService.addBodyweight(Number(id), parseFloat(bodyweight));
            if (response.status === 200) {
                setBodyweight("");
                setShowForm(false);
                await fetchBodyweightCollection();
            } else {
                setError(response.data.message);
            }
        } else {
            setError("User ID is not available");
            router.replace("/login");
        }
    };

    return (
        <section className="bg-forms-grey rounded-2xl shadow-lg p-8">
            <h3 className="text-2xl font-semibold mb-4">Bodyweight Progress</h3>
            <ProgressGraph bodyweightData={bodyweightCollection} />
            {!showForm ? (
                <button
                    className="bg-green-button-home text-white py-3 px-6 rounded-lg mt-4 hover: bg-hover-button-home cursor-pointer"
                    onClick={() => setShowForm(true)}
                >
                    Add Bodyweight
                </button>
            ) : (
                <form onSubmit={handleBodyWeightSubmit} className="mt-4 flex flex-col gap-3">
                    <input
                        className="bg-white text-black border rounded px-4 py-2"
                        type="number"
                        placeholder="Enter your bodyweight"
                        value={bodyweight}
                        onChange={e => setBodyweight(e.target.value)}
                    />
                    <div className="flex gap-4">
                        <button
                            type="button"
                            className="bg-gray-200 text-black flex-1 py-2 rounded hover: cursor-pointer"
                            onClick={() => setShowForm(false)}
                        >
                            Cancel
                        </button>
                        <button type="submit" className="bg-green-button-home text-white flex-1 py-2 rounded hover: cursor-pointer">
                            Submit
                        </button>
                    </div>
                </form>
            )}
            <div className="text-red-400">{error}</div>
        </section>
    );
};

export default BodyweightCard;