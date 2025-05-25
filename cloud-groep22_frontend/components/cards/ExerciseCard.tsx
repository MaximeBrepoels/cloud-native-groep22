import React from "react";
import { useRouter } from "next/router";

interface Exercise {
    id: string;
    name: string;
    rest: string;
    type: string;
}

type Props = {
    exercise: Exercise;
    workoutId: string | string[] | undefined;
};

const ExerciseCard: React.FC<Props> = ({ exercise, workoutId }) => {
    const router = useRouter();

    const handleEdit = () => {
        router.push({
            pathname: "/editexercise",
            query: { exerciseId: exercise.id, workoutId },
        });
    };

    return (
        <button
            type="button"
            onClick={handleEdit}
            className="flex flex-col justify-between bg-white border border-gray-200 text-black rounded-lg p-4 w-full text-left hover:cursor-pointer"
        >
            <div className="font-semibold text-lg mb-2">{exercise.name}</div>
            <div className="flex justify-between text-gray-500 text-sm">
                <span>{exercise.type}</span>
                <span>{exercise.rest}s rest</span>
            </div>
        </button>
    );
};

export default ExerciseCard;