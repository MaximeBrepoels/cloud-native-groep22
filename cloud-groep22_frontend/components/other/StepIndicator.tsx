import React from "react";

interface StepIndicatorProps {
    currentPosition: number;
    stepCount: number;
}

const StepIndicator: React.FC<StepIndicatorProps> = ({ currentPosition, stepCount }) => (
    <div className="flex gap-2">
        {Array.from({ length: stepCount }).map((_, idx) => (
            <div
                key={idx}
                className={`w-7 h-7 rounded-full flex items-center justify-center
          ${idx < currentPosition ? "bg-gray-200 font-bold color-title-workout-card" : "bg-gray-200"}
        `}
            >
                {idx < currentPosition ? "✔" : ""}
            </div>
        ))}
    </div>
);

export default StepIndicator;