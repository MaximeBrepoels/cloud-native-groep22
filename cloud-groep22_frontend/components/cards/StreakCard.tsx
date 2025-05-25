import React, { useEffect, useState } from "react";
import ModelComponent from "../other/ModelComponent";
import { UserService } from "@/services/UserService";

interface StreakCardProps {
    userId: number;
}

const StreakCard: React.FC<StreakCardProps> = ({ userId }) => {
    const [streak, setStreak] = useState(0);
    const [goal, setGoal] = useState(0);
    const [isModalVisible, setModalVisible] = useState(false);
    const [selectedGoal, setSelectedGoal] = useState(0);

    const userService = new UserService();

    const fetchStreak = async () => {
        const response = await userService.getUserById(String(userId));
        setStreak(response.data.streak);
        setGoal(response.data.streakGoal);
        setSelectedGoal(response.data.streakGoal);
    };

    useEffect(() => {
        fetchStreak();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [userId]);

    const handleSetGoal = () => {
        setModalVisible(true);
    };

    const saveGoal = async () => {
        await userService.updateStreakGoal(userId, selectedGoal);
        setGoal(selectedGoal);
        setModalVisible(false);
    };

    return (
        <>
            <div className="relative bg-white border border-gray-300 rounded-lg p-4 w-full mb-4">
                <button
                    className="absolute top-2 right-2 bg-white p-2 rounded"
                    onClick={handleSetGoal}
                    aria-label="Set goal"
                >
                    <img
                        src="/assets/change_streak.png"
                        alt="Edit"
                        className="w-6 h-6"
                    />
                </button>
                {goal > 0 ? (
                    <>
                        <div className={streak === 0 ? "text-black font-medium text-lg" : "text-orange-600 font-bold text-lg"}>
                            {streak > 0 ? "🔥 " : ""}
                            {streak} week{streak !== 1 ? "s" : ""}
                        </div>
                        <div className="text-gray-500 text-sm mt-2">
                            Weekly goal: {goal} time{goal !== 1 ? "s" : ""} per week
                        </div>
                    </>
                ) : (
                    <>
                        <div className="text-black font-medium text-lg">0 weeks</div>
                        <div className="text-gray-500 text-sm mt-2">Set your goal to get started!</div>
                    </>
                )}
            </div>
            <ModelComponent visible={isModalVisible} onClose={() => setModalVisible(false)}>
                <div className="text-2xl font-semibold mb-4 text-center">Set your weekly goal!</div>
                <select
                    className="w-full border border-gray-300 rounded p-2 mb-4"
                    value={selectedGoal}
                    onChange={e => setSelectedGoal(Number(e.target.value))}
                >
                    {[...Array(8).keys()].map(i => (
                        <option key={i} value={i}>
                            {i} time{i !== 1 ? "s" : ""} per week
                        </option>
                    ))}
                </select>
                <button
                    className="w-1/2 bg-white border border-gray-300 rounded py-2 font-bold text-black mt-2"
                    onClick={saveGoal}
                >
                    Save
                </button>
            </ModelComponent>
        </>
    );
};

export default StreakCard;