import React, { useState } from "react";

interface Set {
    id: number;
    reps: number;
    weight: number;
    duration: number;
}

interface SetCardProps {
    set: Set;
    exerciseType: string;
    deleteSet?: () => void;
    updateSet: (id: number, updatedSet: Partial<Set>) => void;
}

const SetCard: React.FC<SetCardProps> = ({ set,  exerciseType,  deleteSet,  updateSet }) => {
    const [reps, setReps] = useState(set.reps);
    const [weight, setWeight] = useState(set.weight);
    const [duration, setDuration] = useState(set.duration);

    const handleRepsChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = Number(e.target.value);
        setReps(value);
        updateSet(set.id, { reps: value });
    };

    const handleWeightChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = Number(e.target.value);
        setWeight(value);
        updateSet(set.id, { weight: value });
    };

    const handleDurationChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = Number(e.target.value);
        setDuration(value);
        updateSet(set.id, { duration: value });
    };

    return (
        <div className="flex flex-row items-center w-full gap-2">
            {exerciseType !== "DURATION" && (
                <div className="flex-1 flex flex-row items-center justify-between p-4 bg-white rounded-lg border border-gray-300">
                    <input
                        className="text-base font-normal text-black w-4/5 outline-none"
                        type="number"
                        value={reps}
                        onChange={handleRepsChange}
                        placeholder="0"
                        min={0}
                    />
                    <span className="text-xs text-gray-500 mt-1">reps</span>
                </div>
            )}
            {exerciseType === "WEIGHTS" && (
                <div className="flex-1 flex flex-row items-center justify-between p-4 bg-white rounded-lg border border-gray-300">
                    <input
                        className="text-base font-normal text-black w-4/5 outline-none"
                        type="number"
                        value={weight}
                        onChange={handleWeightChange}
                        placeholder="0"
                        min={0}
                    />
                    <span className="text-xs text-gray-500 mt-1">kg</span>
                </div>
            )}
            {exerciseType === "DURATION" && (
                <div className="flex-1 flex flex-row items-center justify-between p-4 bg-white rounded-lg border border-gray-300">
                    <input
                        className="text-base font-normal text-black w-4/5 outline-none"
                        type="number"
                        value={duration}
                        onChange={handleDurationChange}
                        placeholder="0"
                        min={0}
                    />
                    <span className="text-xs text-gray-500 mt-1">seconds</span>
                </div>
            )}
            {deleteSet && (
                <button
                    type="button"
                    className="ml-2 p-2 text-lg text-black text-red-500 hover:scale-125 text-2xl cursor-pointer"
                    onClick={deleteSet}
                    aria-label="Delete set"
                >
                    ✕
                </button>
            )}
        </div>
    );
};

export default SetCard;