import React, { useState } from "react";
import { Line } from "react-chartjs-2";

type Workout = {
    id: string;
    name: string;
    exercises: any[];
};

type Props = {
    workouts: Workout[];
};

const WorkoutProgressSection: React.FC<Props> = ({ workouts }) => {
    const [selectedWorkoutId, setSelectedWorkoutId] = useState(workouts[0]?.id || "");
    const selectedWorkout = workouts.find(w => w.id === selectedWorkoutId);

    const handleWorkoutChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        setSelectedWorkoutId(e.target.value);
    };

    if (!workouts.length) return null;

    return (
        <section className="mt-12 w-full max-w-3xl">
            <h2 className="text-white text-2xl font-bold mb-4">Progress Tracker</h2>
            <div className="mb-6">
                <label className="text-white mr-3 font-semibold">Select Workout:</label>
                <select
                    className="rounded px-3 py-2 border hover: cursor-pointer"
                    value={selectedWorkoutId}
                    onChange={handleWorkoutChange}
                >
                    {workouts.map(w => (
                        <option key={w.id} value={w.id}>{w.name}</option>
                    ))}
                </select>
            </div>
            <div>
                {selectedWorkout?.exercises
                    .filter((ex: any) =>
                        ex.type !== "BODYWEIGHT" &&
                        ex.progressList &&
                        ex.progressList.length > 0
                    )
                    .map((exercise: any) => {
                        const labels = exercise.progressList.map((entry: any) => {
                            const d = new Date(entry.date);
                            return `${d.getDate()}/${d.getMonth() + 1}`;
                        });
                        const data = exercise.type === "WEIGHTS"
                            ? exercise.progressList.map((entry: any) => entry.weight)
                            : exercise.progressList.map((entry: any) => entry.duration);

                        const chartData = {
                            labels,
                            datasets: [{
                                label: exercise.type === "WEIGHTS" ? "Weight (kg)" : "Duration (s)",
                                data,
                                fill: false,
                                borderColor: "rgba(39, 174, 96, 1)",
                                backgroundColor: "rgba(39, 174, 96, 0.2)",
                                tension: 0.3,
                                pointRadius: 4,
                            }],
                        };
                        const options = {
                            responsive: true,
                            plugins: { legend: { display: false }, tooltip: { enabled: true } },
                            scales: {
                                y: { beginAtZero: false, title: { display: true, text: exercise.type === "WEIGHTS" ? "kg" : "s" } },
                                x: { title: { display: true, text: "Date" } },
                            },
                        };
                        return (
                            <div key={exercise.id} className="bg-white rounded-lg border border-gray-200 p-6 mb-4">
                                <div className="font-semibold mb-2 text-black">{exercise.name}</div>
                                <Line data={chartData} options={options} height={200} />
                            </div>
                        );
                    })}
                {selectedWorkout?.exercises.filter((ex: any) =>
                    ex.type !== "BODYWEIGHT" &&
                    ex.progressList &&
                    ex.progressList.length > 0
                ).length === 0 && (
                    <div className="text-gray-400 text-center">
                        No progress data for this workout.<br />
                        Add auto increase to an exercise to start tracking your progress!
                    </div>
                )}
            </div>
        </section>
    );
};

export default WorkoutProgressSection;