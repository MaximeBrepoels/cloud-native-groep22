import React from "react";

interface AddExerciseButtonProps {
    onClick: () => void;
}

const AddExerciseButton: React.FC<AddExerciseButtonProps> = ({ onClick }) => {
    return (
        <button
            type="button"
            className="w-10 h-10 mb-4 bg-green-button-home border-gray-300 rounded-full flex justify-center items-center shadow hover:opacity-80 transition cursor-pointer"
            onClick={onClick}
            aria-label="Add exercise"
        >
            <span className="text-xl font-bold text-white">+</span>
        </button>
    );
};

export default AddExerciseButton;