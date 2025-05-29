import React, { useEffect, useState } from "react";
import { useRouter } from "next/router";
import { WorkoutService } from "@/services/WorkoutService";
import { ExerciseService } from "@/services/ExerciseService";
import { UserService } from "@/services/UserService";
import { Exercise } from "@/types";
import StepIndicator from "@/components/other/StepIndicator";

const WorkoutFlow: React.FC = () => {
    const [title, setTitle] = useState("");
    const [currentExerciseIndex, setCurrentExerciseIndex] = useState(0);
    const [currentSetIndex, setCurrentSetIndex] = useState(0);
    const [exercises, setExercises] = useState<Exercise[]>([]);
    const [isResting, setIsResting] = useState(false);
    const [timer, setTimer] = useState(0);
    const [isReadyScreen, setIsReadyScreen] = useState(true);
    const [isCompletedScreen, setIsCompletedScreen] = useState(false);
    const [isLoading, setIsLoading] = useState(true);
    const [setsGenerated, setSetsGenerated] = useState(false);
    const [setResults, setSetResults] = useState<boolean[]>([]);
    const [userId, setUserId] = useState<number | null>(null);

    const workoutService = new WorkoutService();
    const exerciseService = new ExerciseService();
    const userService = new UserService();
    const router = useRouter();
    const { id: workoutId } = router.query;

    useEffect(() => {
        const id = sessionStorage.getItem("session_id");
        if (id) setUserId(parseInt(id));
        else router.push("/login");
    }, [router]);

    const getExercises = async () => {
        try {
            const response = await workoutService.getWorkoutById(Number(workoutId));
            if (response.status !== 200) {
                handleSessionError();
            } else {
                setTitle(response.data.name);
                setExercises(response.data.exercises);
            }
        } catch (error) {
            alert("Failed to load workout.");
            router.push("/");
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        if (workoutId && !Array.isArray(workoutId) && !isNaN(Number(workoutId))) {
            getExercises();
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [workoutId]);

    const removeExerciseWithoutSets = (exs: Exercise[]) =>
        exs.filter((exercise) => exercise.sets.length > 0);

    const generateAutoIncreaseSets = () => {
        let updatedExercises = exercises.map((exercise) => {
            if (exercise.autoIncrease) {
                const sets = [];
                const currentSets = exercise.autoIncreaseCurrentSets || 3;
                const currentWeight = exercise.autoIncreaseCurrentWeight !== undefined ? exercise.autoIncreaseCurrentWeight : 0;
                const currentReps = exercise.autoIncreaseCurrentReps !== undefined ? exercise.autoIncreaseCurrentReps : 0;
                const currentDuration = exercise.autoIncreaseCurrentDuration !== undefined ? exercise.autoIncreaseCurrentDuration : 0;

                for (let i = 0; i < currentSets; i++) {
                    sets.push({
                        id: i,
                        weight: currentWeight,
                        reps: currentReps,
                        duration: currentDuration,
                    });
                }
                return { ...exercise, sets };
            }
            return exercise;
        });
        updatedExercises = removeExerciseWithoutSets(updatedExercises);
        setExercises(updatedExercises);
        setSetsGenerated(true);
    };


    useEffect(() => {
        if (exercises.length > 0 && !setsGenerated) {
            const timeoutId = setTimeout(() => {
                generateAutoIncreaseSets();
            }, 100);
            return () => clearTimeout(timeoutId);
        }
    }, [exercises]);

    const handleSessionError = () => {
        sessionStorage.removeItem("session_id");
        router.push("/login");
    };

    const increaseDifficulty = async () => {
        const currentExercise = exercises[currentExerciseIndex];
        if (currentExercise.autoIncrease) {
            await exerciseService.autoIncrease(currentExercise.id);
        }
    };

    const decreaseDifficulty = async () => {
        const currentExercise = exercises[currentExerciseIndex];
        if (currentExercise.autoIncrease) {
            await exerciseService.autoDecrease(currentExercise.id);
        }
    };

    const changeDifficulty = async () => {
        if (setResults.includes(false)) {
            await decreaseDifficulty();
        } else if (setResults.length > 0) {
            await increaseDifficulty();
        }
    };

    const handleSuccess = async () => {
        setSetResults((prev) => [...prev, true]);
        const currentExercise = exercises[currentExerciseIndex];
        if (currentSetIndex < currentExercise.sets.length - 1) {
            setCurrentSetIndex((prev) => prev + 1);
            startRest(currentExercise.rest);
        } else if (currentExerciseIndex < exercises.length - 1) {
            await changeDifficulty();
            moveToNextExercise();
            setSetResults([]);
        } else {
            await changeDifficulty();
            completeWorkout();
        }
    };

    const handleFail = async () => {
        setSetResults((prev) => [...prev, false]);
        const currentExercise = exercises[currentExerciseIndex];
        if (currentSetIndex < currentExercise.sets.length - 1) {
            setCurrentSetIndex((prev) => prev + 1);
            startRest(currentExercise.rest);
        } else if (currentExerciseIndex < exercises.length - 1) {
            await changeDifficulty();
            moveToNextExercise();
            setSetResults([]);
        } else {
            await changeDifficulty();
            completeWorkout();
        }
    };

    const moveToNextExercise = () => {
        setCurrentSetIndex(0);
        setCurrentExerciseIndex((prev) => prev + 1);
        const nextExercise = exercises[currentExerciseIndex + 1];
        if (nextExercise) startRest(nextExercise.rest);
    };

    const completeWorkout = () => {
        if (userId) userService.updateStreakProgress(userId);
        setIsCompletedScreen(true);
    };

    const confirmCompletion = () => {
        router.push("/");
    };

    const currentExercise = exercises[currentExerciseIndex];

    useEffect(() => {
        let interval: NodeJS.Timeout | null = null;
        if (timer > 0) {
            interval = setInterval(() => setTimer((prev) => prev - 1), 1000);
        } else if (timer === 0 && isResting) {
            setIsResting(false);
            if (currentExercise?.type === "DURATION") {
                setTimer(currentExercise.sets[currentSetIndex]?.duration || 0);
            }
        }
        return () => {
            if (interval) clearInterval(interval);
        };
    }, [timer, isResting, currentExercise, currentSetIndex]);

    useEffect(() => {
        if (!isResting && currentExercise && currentExercise.type === "DURATION") {
            setTimer(currentExercise.sets[currentSetIndex]?.duration || 0);
        }
    }, [currentSetIndex, currentExercise, isResting]);

    const startRest = (duration: number) => {
        setTimer(duration);
        setIsResting(true);
    };

    const skipRest = () => {
        setIsResting(false);
        if (currentExercise?.type === "DURATION") {
            setTimer(currentExercise.sets[currentSetIndex]?.duration || 0);
        }
    };

    const getNextSetExerciseName = () => {
        if (currentSetIndex < exercises[currentExerciseIndex].sets.length - 1) {
            return exercises[currentExerciseIndex].name;
        }
        return exercises[currentExerciseIndex].name;
    };

    if (isLoading) {
        return (
            <div className="flex flex-col items-center justify-center min-h-screen bg-custom-blue">
                <div className="text-lg mb-2">Loading workout...</div>
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-gray-900" />
            </div>
        );
    }

    {/* READY SCREEN before workout starts */}
    if (isReadyScreen) {
        return (
            <div className="flex flex-col min-h-screen bg-custom-blue p-8">
                <div className="flex flex-row justify-between w-full max-w-xl mb-8">
                    <div>
                        <div className="text-2xl font-semibold color-title-workout-card">{title}</div>
                        <div className="text-l text-white">
                            set {currentSetIndex + 1}/
                            {currentExercise.autoIncrease
                                ? currentExercise.autoIncreaseCurrentSets
                                : currentExercise.sets.length}
                            - exercise {currentExerciseIndex + 1}/{exercises.length}
                        </div>
                    </div>
                    <div className="absolute top-0 right-0 m-6">
                        <button
                            onClick={() => router.push("/")}
                            className="px-3 py-1.5 bg-red-600 rounded-full text-lg text-white font-semibold shadow hover:scale-105 transition-all cursor-pointer"
                        >
                            Exit workout
                        </button>
                    </div>
                </div>
                <div className="flex flex-col items-center justify-center pt-44">
                    <div className="text-3xl font-bold mb-4 color-title-workout-card">Ready?</div>
                    <div className="mb-6 text-white">
                        {exercises.map((exercise, index) => (
                            <div key={exercise.id} className="text-base">
                                {index + 1}. {exercise.name}
                                {exercise.autoIncrease && exercise.type === "WEIGHTS"
                                    ? ` - ${exercise.autoIncreaseCurrentWeight} kg`
                                    : ""}
                            </div>
                        ))}
                    </div>
                    <button
                        className="w-20 h-20 rounded-full bg-white border border-gray-300 flex items-center justify-center text-3xl font-bold text-green-500 hover:scale-105 transition-all  cursor-pointer"
                        onClick={() => setIsReadyScreen(false)}
                    >
                        ✓
                    </button>
                </div>
            </div>
        );
    }

    {/* COMPLETION SCREEN after workout */}
    if (isCompletedScreen) {
        return (
            <div className="flex flex-col min-h-screen bg-custom-blue p-8">
                <div className="flex flex-row justify-between w-full max-w-xl mb-8">
                    <div>
                        <div className="text-2xl font-semibold color-title-workout-card">{title}</div>
                        <div className="text-l text-white">
                            set {currentSetIndex + 1}/
                            {currentExercise.autoIncrease
                                ? currentExercise.autoIncreaseCurrentSets
                                : currentExercise.sets.length}
                            - exercise {currentExerciseIndex + 1}/{exercises.length}
                        </div>
                    </div>
                    <div className="absolute top-0 right-0 m-6">
                        <button
                            onClick={() => router.push("/")}
                            className="px-3 py-1.5 bg-red-600 rounded-full text-lg text-white font-semibold shadow hover:scale-105 transition-all cursor-pointer"
                        >
                            Exit workout
                        </button>
                    </div>
                </div>
                <div className="flex flex-col items-center justify-center pt-44">
                    <div className="text-3xl font-bold mb-2 color-title-workout-card">Successfully completed workout!</div>
                    <button
                        className="w-20 h-20 rounded-full bg-white border border-gray-300 flex items-center justify-center text-3xl font-bold text-green-500 hover:scale-105 transition-all cursor-pointer"
                        onClick={confirmCompletion}
                    >
                        ✓
                    </button>
                </div>
            </div>
        );
    }

    if (exercises.length > 0 && currentExercise) {
        return (
            <div className="flex flex-col min-h-screen bg-custom-blue p-8">

                {/* Header with workout name and current set and exercise info & Exit Workout button */}
                <div className="flex flex-row justify-between w-full max-w-xl mb-8">
                    <div>
                        <div className="text-2xl font-semibold color-title-workout-card">{title}</div>
                        <div className="text-l text-white">
                            set {currentSetIndex + 1}/
                            {currentExercise.autoIncrease
                                ? currentExercise.autoIncreaseCurrentSets
                                : currentExercise.sets.length}
                            - exercise {currentExerciseIndex + 1}/{exercises.length}
                        </div>
                    </div>
                    <div className="absolute top-0 right-0 m-6">
                        <button
                            onClick={() => router.push("/")}
                            className="px-3 py-1.5 bg-red-600 rounded-full text-lg text-white font-semibold shadow hover:scale-105 transition-all cursor-pointer"
                        >
                            Exit workout
                        </button>
                    </div>
                </div>
                <div className="mb-8">
                    <StepIndicator
                        currentPosition={currentExerciseIndex}
                        stepCount={exercises.length}
                    />
                </div>
                <div className="flex flex-col items-center flex-1 pt-36">
                    {/* REST between exercises & display of EXERCISE NAME and DURATION/WEIGHT */}
                    {isResting ? (
                        <>
                            <div className="text-4xl font-bold color-title-workout-card">
                                {`${Math.floor(timer / 60)}m${timer % 60 < 10 ? "0" : ""}${timer % 60}s`}
                            </div>
                            <div className="text-xl font-semibold mb-2 text-white">Rest</div>
                            <div className="text-gray-300">Next: {getNextSetExerciseName()}</div>
                        </>
                    ) : (
                        <>
                            {currentExercise.type === "DURATION" ? (
                                <div className="text-4xl font-bold color-title-workout-card">
                                    {`${Math.floor(timer / 60)}m${timer % 60 < 10 ? "0" : ""}${timer % 60}s`}
                                </div>
                            ) : (
                                <div className="text-4xl font-bold mb-2 color-title-workout-card">
                                    {currentExercise.type === "WEIGHTS" && currentExercise.sets && currentExercise.sets[currentSetIndex] &&
                                        `${currentExercise.sets[currentSetIndex].weight || 0}kg ${currentExercise.sets[currentSetIndex].reps || 0}reps`}
                                    {currentExercise.type === "BODYWEIGHT" && currentExercise.sets && currentExercise.sets[currentSetIndex] &&
                                        `${currentExercise.sets[currentSetIndex].reps || 0} reps`}
                                </div>
                            )}
                            <div className="text-xl text-white">{currentExercise.name}</div>
                        </>
                    )}
                </div>
                <div className="flex flex-col items-center mt-8">
                    {!isResting && currentExercise && (
                        <>
                            <div className="mb-2 text-white">Completed set successfully?</div>
                            <div className="flex flex-row gap-4">
                                <button
                                    className="flex-1 border border-gray-300 rounded p-4 text-2xl text-red-500 cursor-pointer"
                                    onClick={handleFail}
                                >
                                    ✗
                                </button>
                                <button
                                    className="flex-1 border border-gray-300 rounded p-4 text-2xl text-green-500 cursor-pointer"
                                    onClick={handleSuccess}
                                >
                                    ✓
                                </button>
                            </div>
                        </>
                    )}
                    {isResting && (
                        <>
                            <div className="mb-2 text-white">Skip rest?</div>
                            <button
                                className="border border-gray-300 rounded p-4 text-2xl text-green-500 cursor-pointer"
                                onClick={skipRest}
                            >
                                ✓
                            </button>
                        </>
                    )}
                </div>
            </div>
        );
    }

    return <div>No exercise found</div>;
};

export default WorkoutFlow;