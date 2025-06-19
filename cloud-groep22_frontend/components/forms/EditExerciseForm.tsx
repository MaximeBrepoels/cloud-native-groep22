import React, { useState, useEffect } from "react";
import { useRouter } from "next/router";
import { ExerciseService } from "@/services/ExerciseService";
import SetCard from "@/components/cards/SetCard";
import AddExerciseButton from "@/components/other/AddExerciseButton";

interface Set {
    id: number;
    reps: number;
    weight: number;
    duration: number;
}

const EditExerciseForm: React.FC = () => {
    const router = useRouter();
    const { exerciseId, workoutId } = router.query;

    const [exerciseName, setExerciseName] = useState("");
    const [restTime, setRestTime] = useState(60);
    const [exerciseType, setExerciseType] = useState("");
    const [autoIncrease, setAutoIncrease] = useState(true);
    const [increaseFactor, setIncreaseFactor] = useState(1.05);
    const [startWeight, setStartWeight] = useState("0");
    const [weightSteps, setWeightSteps] = useState("0");
    const [minSets, setMinSets] = useState("0");
    const [maxSets, setMaxSets] = useState("0");
    const [minReps, setMinReps] = useState("0");
    const [maxReps, setMaxReps] = useState("0");
    const [durationSets, setDurationSets] = useState("0");
    const [startDuration, setStartDuration] = useState("0");
    const [currentDuration, setCurrentDuration] = useState("0");
    const [currentWeight, setCurrentWeight] = useState("0");
    const [currentSets, setCurrentSets] = useState("0");
    const [currentReps, setCurrentReps] = useState("0");
    const [sets, setSets] = useState<Set[]>([]);

    const exerciseService = new ExerciseService();

    const getWorkoutIdNumber = (): number | undefined => {
        if (typeof workoutId === "string") return Number(workoutId);
        if (Array.isArray(workoutId) && workoutId.length > 0) return Number(workoutId[0]);
        return undefined;
    };
    const workoutIdNumber = getWorkoutIdNumber();

    const getExerciseIdNumber = (): number | undefined => {
        if (typeof exerciseId === "string") return Number(exerciseId);
        if (Array.isArray(exerciseId) && exerciseId.length > 0) return Number(exerciseId[0]);
        return undefined;
    };
    const exerciseIdNumber = getExerciseIdNumber();

    useEffect(() => {
        if (exerciseIdNumber !== undefined && !isNaN(exerciseIdNumber)) {
            exerciseService.getExerciseById(exerciseIdNumber).then((response) => {
                setExerciseName(response.data.name);
                setRestTime(response.data.rest);
                setExerciseType(response.data.type);
                setAutoIncrease(response.data.autoIncrease);
                setIncreaseFactor(response.data.autoIncreaseFactor);
                setSets(response.data.sets);
                setStartWeight(response.data.autoIncreaseStartWeight?.toString() || "0");
                setWeightSteps(response.data.autoIncreaseWeightStep?.toString() || "0");
                setMinSets(response.data.autoIncreaseMinSets?.toString() || "0");
                setMaxSets(response.data.autoIncreaseMaxSets?.toString() || "0");
                setMinReps(response.data.autoIncreaseMinReps?.toString() || "0");
                setMaxReps(response.data.autoIncreaseMaxReps?.toString() || "0");
                setDurationSets(response.data.autoIncreaseDurationSets?.toString() || "0");
                setStartDuration(response.data.autoIncreaseStartDuration?.toString() || "0");
                setCurrentDuration(response.data.autoIncreaseCurrentDuration?.toString() || "0");
                setCurrentWeight(response.data.autoIncreaseCurrentWeight?.toString() || "0");
                setCurrentSets(response.data.autoIncreaseCurrentSets?.toString() || "0");
                setCurrentReps(response.data.autoIncreaseCurrentReps?.toString() || "0");
            });
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [exerciseIdNumber]);

    const handleExerciseTypeChange = (type: string) => setExerciseType(type);

    const deleteExercise = () => {
        if (
            workoutIdNumber === undefined ||
            isNaN(workoutIdNumber) ||
            exerciseIdNumber === undefined ||
            isNaN(exerciseIdNumber)
        )
            return;
        exerciseService
            .deleteExerciseFromWorkout(workoutIdNumber, exerciseIdNumber)
            .then(() => router.push(`/editworkout/${workoutIdNumber}`))
            .catch((error) => console.error("Exercise could not be deleted!", error));
    };

    const addSet = () => {
        const currentIndex = sets.length > 0 ? Number(sets[sets.length - 1].id) + 1 : 0;
        setSets([...sets, { id: currentIndex, reps: 0, weight: 0, duration: 0 }]);
    };

    const updateSet = (id: number, updatedSet: Partial<Set>) => {
        setSets(sets.map((set) => (set.id === id ? { ...set, ...updatedSet } : set)));
    };

    const deleteSet = (id: number) => {
        setSets(sets.filter((set) => set.id !== id));
    };

    const validateCurrentSets = (value: number) => {
        const min = parseInt(minSets);
        const max = parseInt(maxSets);
        if (value < min) return min;
        if (value > max) return max;
        return value;
    };

    const validateCurrentReps = (value: number) => {
        const min = parseInt(minReps);
        const max = parseInt(maxReps);
        if (value < min) return min;
        if (value > max) return max;
        return value;
    };

    const validateCurrentWeight = (value: number) => {
        const start = parseFloat(startWeight.replace(",", "."));
        const step = parseFloat(weightSteps.replace(",", "."));
        if (value < start) return start;
        if (step && value % step !== 0) return Math.floor(value / step) * step;
        return value;
    };

    const validateCurrentDuration = (value: number) => {
        const start = parseInt(startDuration);
        if (value < start) return start;
        return value;
    };

    const updateExercise = () => {
        if (exerciseIdNumber === undefined || isNaN(exerciseIdNumber) || workoutIdNumber === undefined || isNaN(workoutIdNumber)) return;

        const exerciseData = {
            name: exerciseName,
            type: exerciseType,
            rest: restTime,
            autoIncrease: autoIncrease,
            autoIncreaseFactor: increaseFactor,
            autoIncreaseWeightStep: parseFloat(weightSteps.replace(",", ".")),
            autoIncreaseStartWeight: parseInt(startWeight),
            autoIncreaseMinSets: parseInt(minSets),
            autoIncreaseMaxSets: parseInt(maxSets),
            autoIncreaseMinReps: parseInt(minReps),
            autoIncreaseMaxReps: parseInt(maxReps),
            autoIncreaseStartDuration: parseInt(startDuration),
            autoIncreaseDurationSets: parseInt(durationSets),
            autoIncreaseCurrentSets: validateCurrentSets(parseInt(currentSets)),
            autoIncreaseCurrentReps: validateCurrentReps(parseInt(currentReps)),
            autoIncreaseCurrentWeight: validateCurrentWeight(parseFloat(currentWeight.replace(",", "."))),
            autoIncreaseCurrentDuration: validateCurrentDuration(parseInt(currentDuration)),
            sets: sets.map((set) => ({
                id: set.id,
                reps: set.reps,
                weight: set.weight,
                duration: set.duration,
            })),
        };

        // Add debug logging
        console.log("Updating exercise with data:", exerciseData);
        console.log("Sets being sent:", exerciseData.sets);

        exerciseService
            .updateExercise(exerciseIdNumber, exerciseData)
            .then((response) => {
                console.log("Update response:", response);
                if (response.status === 200) {
                    router.push(`/editworkout/${workoutIdNumber}`);
                } else {
                    console.error("Update failed:", response.data);
                }
            })
            .catch((error) => {
                console.error("Exercise could not be updated!", error);
            });
    };

    return (
        <form
            className="w-full flex flex-col items-start"
            onSubmit={(e) => {
                e.preventDefault();
                updateExercise();
            }}
        >
            <label className="font-medium mt-2 text-white">Name</label>
            <input
                className="text-black w-full p-2.5 bg-white rounded mb-2"
                value={exerciseName}
                onChange={(e) => setExerciseName(e.target.value)}
            />

            <label className="font-medium mt-2">Type</label>
            <div className="flex gap-2 mb-2 w-full">
                {["WEIGHTS", "BODYWEIGHT", "DURATION"].map((type) => (
                    <button
                        key={type}
                        type="button"
                        className={`flex-1 border rounded p-2 ${exerciseType === type ? "bg-gray-200 text-gray-700" : ""}`}
                        onClick={() => handleExerciseTypeChange(type)}
                    >
                        {type.charAt(0) + type.slice(1).toLowerCase()}
                    </button>
                ))}
            </div>

            <label className="font-medium mt-2">Auto increase</label>
            <div className="flex gap-2 mb-2 w-full">
                <button
                    type="button"
                    className={`flex-1 border rounded p-2 ${autoIncrease ? "bg-gray-200 text-gray-700" : ""}`}
                    onClick={() => setAutoIncrease(true)}
                >
                    Automatic
                </button>
                <button
                    type="button"
                    className={`flex-1 border rounded p-2 ${!autoIncrease ? "bg-gray-200 text-gray-700" : ""}`}
                    onClick={() => setAutoIncrease(false)}
                >
                    Manual
                </button>
            </div>

            {autoIncrease && (
                <>
                    <label className="font-medium mt-2">Increase intensity</label>
                    <div className="flex gap-2 mb-2 w-full">
                        {[1.05, 1.1, 1.15].map((factor) => (
                            <button
                                key={factor}
                                type="button"
                                className={`flex-1 border rounded p-2 ${increaseFactor === factor ? "bg-gray-200 text-gray-700" : ""}`}
                                onClick={() => setIncreaseFactor(factor)}
                            >
                                {factor === 1.05 ? "Easy" : factor === 1.1 ? "Medium" : "Hard"}
                            </button>
                        ))}
                    </div>
                </>
            )}

            <label className="font-medium mt-2">Rest in between sets</label>
            <input
                className="text-black w-full p-2.5 bg-white rounded mb-2"
                type="number"
                value={restTime}
                onChange={(e) => setRestTime(Number(e.target.value))}
            />

            {exerciseType === "WEIGHTS" && autoIncrease && (
                <>
                    <label className="font-medium mt-2">Start weight</label>
                    <input
                        className="text-black w-full p-2.5 bg-white rounded mb-2"
                        type="number"
                        value={startWeight}
                        onChange={(e) => setStartWeight(e.target.value)}
                    />
                    <label className="font-medium mt-2">Weight steps equipment</label>
                    <input
                        className="text-black w-full p-2.5 bg-white rounded mb-2"
                        type="number"
                        value={weightSteps}
                        onChange={(e) => setWeightSteps(e.target.value)}
                    />
                </>
            )}

            {exerciseType !== "DURATION" && autoIncrease && (
                <>
                    <div className="flex gap-2 w-full">
                        <div className="flex-1">
                            <label className="font-medium mt-2">Min sets</label>
                            <input
                                className="text-black w-full p-2.5 bg-white rounded mb-2"
                                type="number"
                                value={minSets}
                                onChange={(e) => setMinSets(e.target.value)}
                            />
                        </div>
                        <div className="flex-1">
                            <label className="font-medium mt-2">Max sets</label>
                            <input
                                className="text-black w-full p-2.5 bg-white rounded mb-2"
                                type="number"
                                value={maxSets}
                                onChange={(e) => setMaxSets(e.target.value)}
                            />
                        </div>
                    </div>
                    <div className="flex gap-2 w-full">
                        <div className="flex-1">
                            <label className="font-medium mt-2">Min reps</label>
                            <input
                                className="text-black w-full p-2.5 bg-white rounded mb-2"
                                type="number"
                                value={minReps}
                                onChange={(e) => setMinReps(e.target.value)}
                            />
                        </div>
                        <div className="flex-1">
                            <label className="font-medium mt-2">Max reps</label>
                            <input
                                className="text-black w-full p-2.5 bg-white rounded mb-2"
                                type="number"
                                value={maxReps}
                                onChange={(e) => setMaxReps(e.target.value)}
                            />
                        </div>
                    </div>
                </>
            )}

            {exerciseType === "DURATION" && autoIncrease && (
                <>
                    <label className="font-medium mt-2">Sets</label>
                    <input
                        className="text-black w-full p-2.5 bg-white rounded mb-2"
                        type="number"
                        value={durationSets}
                        onChange={(e) => setDurationSets(e.target.value)}
                    />
                    <label className="font-medium mt-2">Start duration</label>
                    <input
                        className="text-black w-full p-2.5 bg-white rounded mb-2"
                        type="number"
                        value={startDuration}
                        onChange={(e) => setStartDuration(e.target.value)}
                    />
                </>
            )}

            {autoIncrease && (
                <>
                    <label className="font-medium mt-4 text-lg">Current</label>
                    {(exerciseType === "WEIGHTS" || exerciseType === "BODYWEIGHT") && (
                        <div className="flex gap-2 w-full">
                            <div className="flex-1">
                                <label className="font-medium mt-2">Sets</label>
                                <input
                                    className="text-black w-full p-2.5 bg-white rounded mb-2"
                                    type="number"
                                    value={currentSets}
                                    onChange={(e) => setCurrentSets(e.target.value)}
                                />
                            </div>
                            <div className="flex-1">
                                <label className="font-medium mt-2">Reps</label>
                                <input
                                    className="text-black w-full p-2.5 bg-white rounded mb-2"
                                    type="number"
                                    value={currentReps}
                                    onChange={(e) => setCurrentReps(e.target.value)}
                                />
                            </div>
                            {exerciseType === "WEIGHTS" && (
                                <div className="flex-1">
                                    <label className="font-medium mt-2">Weight</label>
                                    <input
                                        className="text-black w-full p-2.5 bg-white rounded mb-2"
                                        type="number"
                                        value={currentWeight}
                                        onChange={(e) => setCurrentWeight(e.target.value)}
                                    />
                                </div>
                            )}
                        </div>
                    )}
                    {exerciseType === "DURATION" && (
                        <div className="flex-1">
                            <label className="font-medium mt-2">Duration</label>
                            <input
                                className="text-black w-full p-2.5 bg-white rounded mb-2"
                                type="number"
                                value={currentDuration}
                                onChange={(e) => setCurrentDuration(e.target.value)}
                            />
                        </div>
                    )}
                </>
            )}

            {!autoIncrease && (
                <>
                    <label className="font-medium mt-2">Sets</label>
                    {sets.map((set, index) => (
                        <div key={set.id} className="w-full mb-4">
                            <SetCard
                                set={set}
                                exerciseType={exerciseType}
                                deleteSet={() => deleteSet(set.id)}
                                updateSet={updateSet}
                            />
                        </div>
                    ))}
                    <div className="w-full flex justify-center">
                        <AddExerciseButton onClick={addSet} />
                    </div>
                </>
            )}

            <div className="flex gap-4 mt-8 w-full">
                <button
                    type="button"
                    className="flex-1 bg-white text-black py-3 rounded font-semibold cursor-pointer"
                    onClick={() => {
                        if (workoutIdNumber !== undefined && !isNaN(workoutIdNumber)) {
                            router.push(`/editworkout/${workoutIdNumber}`);
                        }
                    }}
                >
                    Cancel
                </button>
                <button
                    type="submit"
                    className="flex-1 bg-green-button-home text-white py-3 rounded font-semibold cursor-pointer"
                >
                    Save
                </button>
            </div>
            <div className="flex w-full justify-center mt-4">
                <button
                    type="button"
                    className="flex-1 bg-red-600 text-white py-3 rounded font-semibold cursor-pointer"
                    onClick={deleteExercise}
                >
                    Delete
                </button>
            </div>
        </form>
    );
};

export default EditExerciseForm;