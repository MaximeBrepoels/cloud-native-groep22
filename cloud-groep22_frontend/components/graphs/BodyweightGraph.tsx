import React from "react";
import { Line } from "react-chartjs-2";
import { Chart, LineElement, PointElement, LinearScale, CategoryScale, Tooltip, Legend } from "chart.js";
import { Bodyweight } from "@/types";

Chart.register(LineElement, PointElement, LinearScale, CategoryScale, Tooltip, Legend);

type Props = {
    bodyweightData: Bodyweight[];
};

const formatDate = (dateString: string) => {
    // Assumes date is in ISO format (YYYY-MM-DD)
    const [year, month, day] = dateString.split("-");
    return `${day}/${month}`;
};

const BodyweightGraph: React.FC<Props> = ({ bodyweightData }) => {
    if (bodyweightData.length < 1) {
        return <div>Add your bodyweight to start tracking your progress!</div>;
    }

    const labels = bodyweightData.map((entry) => formatDate(entry.date));
    const data = bodyweightData.map((entry) => entry.bodyWeight);

    const chartData = {
        labels,
        datasets: [
            {
                label: "Bodyweight (kg)",
                data,
                fill: false,
                borderColor: "rgba(39, 174, 96, 1)",
                backgroundColor: "rgba(39, 174, 96, 0.2)",
                tension: 0.3,
                pointRadius: 4,
            },
        ],
    };

    const options = {
        responsive: true,
        plugins: {
            legend: { display: false },
            tooltip: { enabled: true },
        },
        scales: {
            y: {
                beginAtZero: false,
                title: { display: true, text: "kg" },
            },
            x: {
                title: { display: true, text: "Date" },
            },
        },
    };

    return (
        <div className="bg-white rounded-lg border border-gray-200 p-6 mb-4">
            <Line data={chartData} options={options} height={200} />
        </div>
    );
};

export default BodyweightGraph;