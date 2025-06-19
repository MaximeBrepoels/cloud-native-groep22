import React, { useEffect, useState } from "react";
import { useRouter } from "next/router";
import { WorkoutService } from "@/services/WorkoutService";
import { ExerciseService } from "@/services/ExerciseService";
import ExerciseCard from "@/components/cards/ExerciseCard";
import AddExerciseButton from "@/components/other/AddExerciseButton";

interface Exercise {
    id: string;
    name: string;
    rest: string;
    type: string;
}

const exerciseNameSuggestions = [
    // Bodyweight Exercises
    "Push-up",
    "Pull-up",
    "Plank",
    "Side Plank",
    "Burpees",
    "Mountain Climbers",
    "Lunges",
    "Jump Squat",
    "Glute Bridge",
    "Hollow Hold",
    "Wall Sit",
    "Pistol Squat",
    "Spiderman Push-up",
    "Archer Push-up",
    "Clap Push-up",
    "Bear Crawl",
    "Bird Dog",
    "Reverse Lunge",
    "Bulgarian Split Squat",
    "Incline Push-up",

    // Barbell Exercises
    "Bench Press",
    "Squat",
    "Deadlift",
    "Overhead Press",
    "Barbell Row",
    "Romanian Deadlift",
    "Incline Bench Press",
    "Good Morning",
    "Sumo Deadlift",
    "Snatch",
    "Clean and Jerk",
    "Front Squat",
    "Barbell Shrug",

    // Dumbbell Exercises
    "Dumbbell Curl",
    "Incline Dumbbell Curl",
    "Hammer Curl",
    "Zottman Curl",
    "Arnold Press",
    "Seated Dumbbell Press",
    "Dumbbell Lateral Raise",
    "Dumbbell Front Raise",
    "Chest Fly",
    "Dumbbell Row",
    "Dumbbell Bench Press",
    "Dumbbell Pullover",
    "Goblet Squat",
    "Single-Arm Dumbbell Press",
    "Renegade Row",
    "Dumbbell Romanian Deadlift",

    // Cable Machine Exercises
    "Cable Chest Press",
    "Cable Row",
    "Lat Pulldown",
    "Tricep Pushdown",
    "Face Pull",
    "Cable Kickback",
    "Cable Lateral Raise",
    "Cable Front Raise",
    "Cable Curl",
    "Cable Fly",
    "Cable Reverse Fly",
    "Cable Pull-Through",
    "Cable Woodchopper",

    // Bench Exercises
    "Bench Press",
    "Incline Bench Press",
    "Dumbbell Bench Press",
    "Chest Fly",
    "Tricep Dips",
    "Step-Up",
    "Split Squat",
    "Glute Bridge on Bench",
    "Hip Thrust on Bench"
];

type EditWorkoutFormProps = {
    workoutId?: string | string[];
};

const EditWorkoutForm: React.FC<EditWorkoutFormProps> = ({ workoutId }) => {
    const router = useRouter();

    const [workoutName, setWorkoutName] = useState("");
    const [restTime, setRestTime] = useState(60);
    const [exercises, setExercises] = useState<Exercise[]>([]);
    const [isModalVisible, setModalVisible] = useState(false);
    const [exerciseName, setExerciseName] = useState("");
    const [exerciseGoal, setExerciseGoal] = useState("POWER");
    const [exerciseType, setExerciseType] = useState("WEIGHTS");
    const [error, setError] = useState("");
    const [suggestions, setSuggestions] = useState<string[]>([]);
    const [loading, setLoading] = useState(true);
    const [loadError, setLoadError] = useState<string | null>(null);

    const workoutService = new WorkoutService();
    const exerciseService = new ExerciseService();

    const getWorkoutIdNumber = (): number | undefined => {
        if (typeof workoutId === "string") return Number(workoutId);
        if (Array.isArray(workoutId) && workoutId.length > 0) return Number(workoutId[0]);
        return undefined;
    };

    const workoutIdNumber = getWorkoutIdNumber();

    useEffect(() => {
        const loadWorkout = async () => {
            if (workoutIdNumber !== undefined && !isNaN(workoutIdNumber)) {
                try {
                    setLoading(true);
                    setLoadError(null);

                    const response = await workoutService.getWorkoutById(workoutIdNumber);

                    if (response.status === 200) {
                        setWorkoutName(response.data.name || "");
                        setRestTime(response.data.rest || 60);
                        setExercises(response.data.exercises || []);
                    } else {
                        setLoadError(`Failed to load workout: ${response.data?.message || 'Unknown error'}`);
                        setExercises([]);
                    }
                } catch (error: any) {
                    console.error("Error loading workout:", error);
                    setLoadError(`Error loading workout: ${error.message}`);
                    setExercises([]);
                } finally {
                    setLoading(false);
                }
            } else {
                setLoading(false);
                setLoadError("Invalid workout ID");
                setExercises([]);
            }
        };

        loadWorkout();
    }, [workoutIdNumber]);

    const handleExerciseNameChange = (name: string) => {
        setExerciseName(name);
        setSuggestions(
            exerciseNameSuggestions.filter((s) =>
                s.toLowerCase().includes(name.toLowerCase())
            )
        );
    };

    const selectSuggestion = (name: string) => {
        setExerciseName(name);
        setSuggestions([]);
    };

    const updateWorkout = async () => {
        if (workoutIdNumber === undefined || isNaN(workoutIdNumber)) return;

        try {
            const response = await workoutService.updateWorkout(
                workoutIdNumber,
                workoutName,
                restTime,
                exercises.map((e) => e.id)
            );

            if (response.status === 200) {
                router.push("/");
            } else {
                setError(`Failed to update workout: ${response.data?.message || 'Unknown error'}`);
            }
        } catch (error: any) {
            setError(`Error updating workout: ${error.message}`);
        }
    };

    const createExercise = async () => {
        if (exerciseName.trim() === "") {
            setError("Exercise name cannot be empty");
            return;
        }
        if (workoutIdNumber === undefined || isNaN(workoutIdNumber)) return;

        try {
            const response = await workoutService.addExercise(
                workoutIdNumber,
                { name: exerciseName, type: exerciseType },
                exerciseGoal
            );

            if (response.status === 200) {
                setExercises([...exercises, response.data]);
                setModalVisible(false);
                setExerciseName("");
                setExerciseGoal("POWER");
                setExerciseType("WEIGHTS");
                setError("");
            } else {
                setError(`Failed to create exercise: ${response.data?.message || 'Unknown error'}`);
            }
        } catch (error: any) {
            setError(`Error creating exercise: ${error.message}`);
        }
    };

    const deleteWorkout = async () => {
        if (workoutIdNumber === undefined || isNaN(workoutIdNumber)) return;

        try {
            const response = await workoutService.deleteWorkout(workoutIdNumber);
            if (response.status === 204 || response.status === 200) {
                router.push("/");
            } else {
                setError(`Failed to delete workout: ${response.data?.message || 'Unknown error'}`);
            }
        } catch (error: any) {
            setError(`Error deleting workout: ${error.message}`);
        }
    };

    const moveItem = (index: number, direction: "up" | "down") => {
        const newExercises = [...exercises];
        const [removed] = newExercises.splice(index, 1);
        const newIndex = direction === "up" ? index - 1 : index + 1;
        newExercises.splice(newIndex, 0, removed);
        setExercises(newExercises);
    };

    const deleteExercise = async (exerciseId: string | number) => {
        if (workoutIdNumber === undefined || isNaN(workoutIdNumber)) return;
        const exerciseIdNumber = Number(exerciseId);
        if (isNaN(exerciseIdNumber)) return;

        try {
            const response = await exerciseService.deleteExerciseFromWorkout(workoutIdNumber, exerciseIdNumber);

            if (response.status === 200) {
                const workoutResponse = await workoutService.getWorkoutById(workoutIdNumber);
                if (workoutResponse.status === 200) {
                    setExercises(workoutResponse.data.exercises || []);
                }
            } else {
                setError(`Failed to delete exercise: ${response.data?.message || 'Unknown error'}`);
            }
        } catch (error: any) {
            console.error("Exercise could not be deleted!", error);
            setError(`Error deleting exercise: ${error.message}`);
        }
    };

    useEffect(() => {
        if (isModalVisible) {
            document.body.style.overflow = "hidden";
        } else {
            document.body.style.overflow = "";
        }
        return () => {
            document.body.style.overflow = "";
        };
    }, [isModalVisible]);

    if (loading) {
        return (
            <div className="w-full flex flex-col items-center justify-center p-8">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-gray-900 mb-4"></div>
                <p className="text-white">Loading workout...</p>
            </div>
        );
    }

    // Show error state
    if (loadError) {
        return (
            <div className="w-full flex flex-col items-center justify-center p-8">
                <div className="text-red-500 mb-4">❌ {loadError}</div>
                <button
                    onClick={() => router.push("/")}
                    className="bg-white text-black py-2 px-4 rounded font-semibold cursor-pointer"
                >
                    Go Back
                </button>
            </div>
        );
    }

    return (
        <form className="w-full flex flex-col items-start">
            <label className="font-medium mt-2">Name</label>
            <input
                className="w-full p-3 border border-gray-300 rounded mb-2"
                value={workoutName}
                onChange={(e) => setWorkoutName(e.target.value)}
            />
            <label className="font-medium mt-2">Rest in between exercises</label>
            <input
                className="w-full p-3 border border-gray-300 rounded mb-2"
                type="number"
                value={restTime}
                onChange={(e) => setRestTime(Number(e.target.value))}
            />
            <label className="font-medium mt-2">Exercises</label>

            {/* Show message if no exercises */}
            {exercises.length === 0 ? (
                <p className="text-gray-500 mb-4">No exercises added yet. Add your first exercise below!</p>
            ) : (
                exercises.map((exercise, index) => (
                    <div key={exercise.id} className="flex items-center w-full mb-4 gap-2">
                        <ExerciseCard exercise={exercise} workoutId={workoutId} />
                        <div className="flex flex-col gap-1">
                            <button
                                type="button"
                                className={`p-2 border rounded ${index === 0 ? "opacity-50" : ""}`}
                                onClick={() => moveItem(index, "up")}
                                disabled={index === 0}
                            >
                                ↑
                            </button>
                            <button
                                type="button"
                                className={`p-2 border rounded ${index === exercises.length - 1 ? "opacity-50" : ""}`}
                                onClick={() => moveItem(index, "down")}
                                disabled={index === exercises.length - 1}
                            >
                                ↓
                            </button>
                        </div>
                        <button
                            type="button"
                            className="ml-2 text-red-600 hover: cursor-pointer"
                            onClick={() => deleteExercise(exercise.id)}
                        >
                            ✕
                        </button>
                    </div>
                ))
            )}

            <div className="w-full flex justify-center">
                <AddExerciseButton onClick={() => setModalVisible(true)} />
            </div>

            {error && (
                <div className="text-red-600 mt-2 mb-2 w-full text-center">{error}</div>
            )}

            <div className="flex gap-4 mt-8 w-full">
                <button
                    type="button"
                    className="flex-1 bg-white text-black py-3 rounded font-semibold cursor-pointer"
                    onClick={() => router.push("/")}
                >
                    Cancel
                </button>
                <button
                    type="button"
                    className="flex-1 bg-green-button-home text-white py-3 rounded font-semibold cursor-pointer"
                    onClick={updateWorkout}
                >
                    Save
                </button>
            </div>
            <div className="flex w-full justify-center mt-4">
                <button
                    type="button"
                    className="flex-1 bg-red-600 text-white py-3 rounded font-semibold cursor-pointer"
                    onClick={deleteWorkout}
                >
                    Delete
                </button>
            </div>

            {isModalVisible && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
                    <div className="bg-workout-card rounded-lg shadow-lg p-6 max-w-md w-full relative">
                        <button
                            className="absolute top-3 right-3 text-red-500 hover:scale-125 text-2xl cursor-pointer"
                            onClick={() => setModalVisible(false)}
                            aria-label="Close"
                        >
                            &times;
                        </button>
                        <h2 className="text-2xl font-semibold mb-4 text-custom-blue">Add new exercise</h2>
                        <label className="font-medium">Name</label>
                        <input
                            className="bg-white h-10 w-full border border-gray-200 my-2 px-3 rounded text-black placeholder-gray-400"
                            placeholder="Name"
                            value={exerciseName}
                            onChange={(e) => handleExerciseNameChange(e.target.value)}
                        />
                        {suggestions.length > 0 && (
                            <div className="bg-white border border-gray-300 rounded max-h-40 w-full overflow-y-auto">
                                {suggestions.map((s, i) => (
                                    <button
                                        key={i}
                                        className="block w-full text-left px-3 py-2 border-b border-gray-100 hover:bg-gray-50 text-black"
                                        onClick={() => selectSuggestion(s)}
                                    >
                                        {s}
                                    </button>
                                ))}
                            </div>
                        )}
                        <label className="font-medium mt-4">Type</label>
                        <div className="flex gap-2 mb-2">
                            {["WEIGHTS", "BODYWEIGHT", "DURATION"].map((type) => (
                                <button
                                    key={type}
                                    type="button"
                                    className={`flex-1 border rounded p-2 ${exerciseType === type ? "bg-gray-200 text-gray-700" : ""}`}
                                    onClick={() => setExerciseType(type)}
                                >
                                    {type}
                                </button>
                            ))}
                        </div>
                        {exerciseType !== "DURATION" && (
                            <>
                                <label className="font-medium mt-4">Goal</label>
                                <div className="flex gap-2 mb-2">
                                    {["POWER", "MUSCLE", "ENDURANCE"].map((goal) => (
                                        <button
                                            key={goal}
                                            type="button"
                                            className={`flex-1 border rounded p-2 ${exerciseGoal === goal ? "bg-gray-200 text-gray-700" : ""}`}
                                            onClick={() => setExerciseGoal(goal)}
                                        >
                                            {goal}
                                        </button>
                                    ))}
                                </div>
                            </>
                        )}
                        <div className="text-red-600 mt-2">{error}</div>
                        <div className="flex gap-4 mt-6 w-full">
                            <button
                                type="button"
                                className="flex-1 bg-white text-black py-3 rounded font-semibold cursor-pointer"
                                onClick={() => {
                                    setExerciseName("");
                                    setModalVisible(false);
                                    setError("");
                                }}
                            >
                                Cancel
                            </button>
                            <button
                                type="button"
                                className="flex-1 bg-green-button-home text-white py-3 rounded font-semibold cursor-pointer"
                                onClick={createExercise}
                            >
                                Create
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </form>
    );
};

export default EditWorkoutForm;