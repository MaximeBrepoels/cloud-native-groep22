import React from "react";

interface WorkoutCardProps {
    title?: string;
    duration?: string;
    isAddNew?: boolean;
    onClick?: () => void;
}

const WorkoutCard: React.FC<WorkoutCardProps> = ({ title, duration, isAddNew, onClick }) => {
    if (isAddNew) {
        return (
            <button
                className="bg-workout-card text-center shadow-md rounded-lg p-4 m-4 flex items-center justify-center w-full h-32 transform transition-transform hover:scale-105 hover:shadow-lg cursor-pointer"
                onClick={onClick}
                type="button"
                aria-label="Add new workout"
            >
                <span className="text-4xl font-bold text-white">+</span>
            </button>
        );
    }

    return (
        <button
            className="bg-workout-card text-center shadow-md rounded-lg p-4 m-4 w-full h-32 transform transition-transform hover:scale-105 hover:shadow-lg focus:outline-none cursor-pointer"
            onClick={onClick}
            type="button"
        >
            <h2 className="color-title-workout-card text-xl font-bold mb-2">{title}</h2>
            <p className="text-white">{duration}</p>
        </button>
    );
};

export default WorkoutCard;