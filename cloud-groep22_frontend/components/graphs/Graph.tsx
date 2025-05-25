import React, { useEffect, useState } from "react";
import { Line } from "react-chartjs-2";
import type { ChartData, ChartOptions } from "chart.js";
import { Exercise } from "../../types";

type Props = {
    exercises: Exercise[];
};

const Graph: React.FC<Props> = ({ exercises }) => {
    const [topExercises, setTopExercises] = useState<Exercise[]>([]);

    const getTopExercises = (data: Exercise[]) => {
        return data
            .filter(
                (exercise) =>
                    exercise.type !== "BODYWEIGHT" && exercise.progressList.length > 1
            )
            .map((exercise) => {
                const startProgress = exercise.progressList[0];
                const endProgress =
                    exercise.progressList[exercise.progressList.length - 1];
                const progress =
                    exercise.type === "WEIGHTS"
                        ? (endProgress?.weight ?? 0) - (startProgress?.weight ?? 0)
                        : (endProgress?.duration ?? 0) - (startProgress?.duration ?? 0);

                return {
                    ...exercise,
                    progress,
                };
            })
            .sort((a, b) => b.progress - a.progress)
            .slice(0, 3);
    };

    useEffect(() => {
        setTopExercises(getTopExercises(exercises));
    }, [exercises]);

    const renderExercise = (exercise: Exercise) => {
        const formatDate = (dateString: string) => {
            const date = new Date(dateString);
            return `${date.getDate()}/${date.getMonth() + 1}`;
        };

        const labels = exercise.progressList.map((entry) => formatDate(entry.date));
        const data =
            exercise.type === "WEIGHTS"
                ? exercise.progressList.map((entry) => entry.weight)
                : exercise.progressList.map((entry) => entry.duration);

        const chartData: ChartData<"line"> = {
            labels,
            datasets: [
                {
                    label: exercise.type === "WEIGHTS" ? "Weight (kg)" : "Duration (s)",
                    data,
                    borderColor: "rgba(39, 174, 96, 1)",
                    backgroundColor: "rgba(39, 174, 96, 0.2)",
                    fill: true,
                    tension: 0.3,
                },
            ],
        };

        const options: ChartOptions<"line"> = {
            responsive: true,
            plugins: {
                legend: { display: false },
            },
            scales: {
                x: { title: { display: false } },
                y: { title: { display: false } },
            },
        };

        return (
            <div
                className="bg-white rounded-lg border border-gray-200 p-4 mb-4"
                key={exercise.id}
            >
                <div className="font-semibold text-base mb-2">{exercise.name}</div>
                <Line data={chartData} options={options} height={200} />
            </div>
        );
    };

    return (
        <div className="bg-gray-100">
            {topExercises.length < 1 && (
                <div className="mb-4">
                    Add auto increase to an exercise to start tracking your progress
                </div>
            )}
            {topExercises.map((exercise) => renderExercise(exercise))}
        </div>
    );
};

export default Graph;