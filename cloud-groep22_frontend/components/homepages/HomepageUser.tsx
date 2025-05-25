import React, { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import WorkoutCard from "@/components/cards/WorkoutCard";
import LoadingScreen from "@/components/other/LoadingScreen";
import WorkoutProgressSection from "@/components/homepages/WorkoutProgressSection";
import { UserService } from "@/services/UserService";
import { WorkoutService } from "@/services/WorkoutService";
import { Exercise } from "@/types";

import { Chart, LineElement, PointElement, LinearScale, CategoryScale, Tooltip, Legend } from "chart.js";
Chart.register(LineElement, PointElement, LinearScale, CategoryScale, Tooltip, Legend);


const workoutNameSuggestions = [
    "Full Body",
    "Functional Training",
    "Core",
    "Push",
    "Pull",
    "Chest & Triceps",
    "Back & Biceps",
    "Shoulders",
    "Arm",
    "Leg",
    "Glutes & Hamstrings",
    "Quad Focus",
    "Lower Body",
    "Calf",
    "Abs & Core",
    "Core Stability",
    "Plank",
    "Six-Pack Abs",
    "Lower Back",
    "Pilates",
    "Powerlifting",
    "Deadlift",
    "Barbell",
    "No Equipment",
    "Calisthenics",
    "Bodyweight",
    "Home Workout",
    "Yoga",
    "Stretch",
    "Mobility",
    "Dynamic Stretching",
    "Active Recovery",
    "Functional Fitness",
];

const HomepageUser: React.FC<{ userId: number }> = ({ userId }) => {
    const router = useRouter();

    const [loading, setLoading] = useState(true);
    const [workouts, setWorkouts] = useState<any[]>([]);
    const [progressExercises, setProgressExercises] = useState<Exercise[]>([]);
    const [isModelVisible, setModelVisible] = useState(false);
    const [isModelVisible2, setModelVisible2] = useState(false);
    const [selectedWorkout, setSelectedWorkout] = useState<any>(null);
    const [workoutName, setWorkoutName] = useState("");
    const [suggestions, setSuggestions] = useState<string[]>([]);
    const [error, setError] = useState("");

    const userService = new UserService();
    const workoutService = new WorkoutService();

    useEffect(() => {
        return () => {
            document.body.style.overflow = "";
        };
    }, []);

    useEffect(() => {
        const fetchData = async () => {
            setLoading(true);
            try {
                const userWorkouts = await userService.getWorkoutsByUserId(userId);
                setWorkouts(userWorkouts.data || []);
                setProgressExercises([]);
            } catch (e) {}
            setLoading(false);
        };
        fetchData();
    }, [userId]);

    const calcDuration = (workout: any) => {
        if (!workout || !workout.exercises) return "0 min";
        const totalSeconds = workout.exercises.reduce((sum: number, ex: any) => {
            let exerciseDuration = 0;
            if (ex.sets && Array.isArray(ex.sets)) {
                exerciseDuration += ex.sets.reduce((setSum: number, set: any) => setSum + (set.duration || 0), 0);
            } else {
                exerciseDuration += (ex.duration || 0);
            }
            // Add rest time (in seconds) if present
            if (ex.rest) {
                exerciseDuration += ex.rest;
            }
            return sum + exerciseDuration;
        }, 0);
        const totalMinutes = Math.round(totalSeconds / 60);
        return `${totalMinutes} min`;
    };

    const handleWorkoutPress = (index: number) => {
        setSelectedWorkout(workouts[index]);
        setModelVisible(true);
        document.body.style.overflow = "hidden";
    };

    const closeModal = () => {
        setModelVisible(false);
        setSelectedWorkout(null);
        document.body.style.overflow = "";
    };

    const closeAddModal = () => {
        setModelVisible2(false);
        document.body.style.overflow = "";
    };

    const editWorkout = () => {
        if (selectedWorkout) {
            router.push(`/editworkout/${selectedWorkout.id}`);
        }
    };

    const startWorkout = () => {
        if (selectedWorkout) {
            router.push(`/workout/${selectedWorkout.id}/start`);
        }
    };

    const handleWorkoutNameChange = (value: string) => {
        setWorkoutName(value);
        setError("");
        if (value.length > 0) {
            setSuggestions(
                workoutNameSuggestions.filter(s =>
                    s.toLowerCase().includes(value.toLowerCase())
                )
            );
        } else {
            setSuggestions([]);
        }
    };

    const selectSuggestion = (suggestion: string) => {
        setWorkoutName(suggestion);
        setSuggestions([]);
    };

    const createWorkout = async () => {
        if (!workoutName.trim()) {
            setError("Workout name is required.");
            return;
        }
        try {
            await workoutService.createWorkout(userId, { name: workoutName.trim() });
            // Fetch the updated workouts list
            const userWorkouts = await userService.getWorkoutsByUserId(userId);
            setWorkouts(userWorkouts.data || []);
            setWorkoutName("");
            setSuggestions([]);
            closeAddModal();
        } catch (e) {
            setError("Failed to create workout.");
        }
    };

    if (loading) {
        return (
            <main className="bg-custom-blue min-h-screen flex flex-col items-center justify-center">
                <LoadingScreen />
            </main>
        );
    }

    return (
        <main className="bg-custom-blue flex flex-col items-center py-10 px-2">
            <section className="w-full max-w-3xl">
                <h2 className="text-white text-2xl font-bold mb-4">My Workouts</h2>
                <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4 justify-center">
                    {workouts.length === 0 ? (
                        <p className="text-center text-l mb-2 col-span-full pt-5 text-white">No workouts yet.</p>
                    ) : (
                        workouts.map((workout, index) => (
                            <div
                                key={workout.id}
                                className="p-4 m-2 transform transition-transform hover:scale-105"
                            >
                                <WorkoutCard
                                    title={workout.name}
                                    duration={calcDuration(workout)}
                                    onClick={() => handleWorkoutPress(index)}
                                />
                            </div>
                        ))
                    )}
                </div>
                <div className="flex justify-center mt-6">
                    <button
                        className="bg-green-button-home text-white font-semibold py-4 px-8 rounded-lg bg-hover-button-home mt-8 cursor-pointer"
                        onClick={() => { setModelVisible2(true); document.body.style.overflow = "hidden"; }}
                    >
                        + Add new workout
                    </button>
                </div>
            </section>

            {isModelVisible && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
                    <div className="bg-workout-card rounded-lg shadow-lg p-6 max-w-md w-full relative">
                        <button
                            className="absolute top-3 right-3 text-red-500 hover:scale-125 text-2xl cursor-pointer"
                            onClick={closeModal}
                            aria-label="Close"
                        >
                            &times;
                        </button>
                        <h3 className="text-2xl font-semibold mb-4 text-custom-blue">
                            {selectedWorkout?.name}
                        </h3>
                        <div className="flex gap-4 mt-4 w-full">
                            <button
                                className="flex-1 bg-white text-black py-3 rounded font-semibold cursor-pointer"
                                onClick={editWorkout}
                            >
                                Edit
                            </button>
                            <button
                                className="flex-1 bg-green-button-home text-white py-3 rounded font-semibold cursor-pointer"
                                onClick={startWorkout}
                            >
                                Start
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {isModelVisible2 && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
                    <div className="bg-workout-card rounded-lg shadow-lg p-6 max-w-md w-full relative">
                        <button
                            className="absolute top-3 right-3 text-red-500 hover:scale-125 text-2xl cursor-pointer"
                            onClick={closeAddModal}
                            aria-label="Close"
                        >
                            &times;
                        </button>
                        <h3 className="text-2xl font-semibold mb-4 text-custom-blue">Add new workout</h3>
                        <input
                            className="bg-white h-10 w-full border border-gray-200 my-4 px-3 rounded text-black placeholder-gray-400"
                            placeholder="Name"
                            value={workoutName}
                            onChange={e => handleWorkoutNameChange(e.target.value)}
                        />
                        {suggestions.length > 0 && (
                            <div className="bg-white border border-gray-300 rounded max-h-40 w-full overflow-y-auto">
                                {suggestions.map((suggestion, idx) => (
                                    <button
                                        key={idx}
                                        className="block w-full text-left px-3 py-2 border-b border-gray-100 hover:bg-gray-50 text-black"
                                        onClick={() => selectSuggestion(suggestion)}
                                    >
                                        {suggestion}
                                    </button>
                                ))}
                            </div>
                        )}
                        <div className="text-red-500">{error}</div>
                        <div className="flex gap-4 mt-4 w-full">
                            <button
                                className="flex-1 bg-white text-black py-3 rounded font-semibold cursor-pointer"
                                onClick={closeAddModal}
                            >
                                Cancel
                            </button>
                            <button
                                className="flex-1 bg-green-button-home text-white py-3 rounded font-semibold cursor-pointer"
                                onClick={createWorkout}
                            >
                                Create
                            </button>
                        </div>
                    </div>
                </div>
            )}

            <WorkoutProgressSection workouts={workouts} />
        </main>
    );
};

export default HomepageUser;