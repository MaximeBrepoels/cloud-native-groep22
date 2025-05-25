import React, { useState } from "react";
import { UserService } from "@/services/UserService";
import { useRouter } from "next/router";

const PasswordCard: React.FC = () => {
    const [showForm, setShowForm] = useState(false);
    const [currentPassword, setCurrentPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const router = useRouter();

    const handlePasswordChange = async (e: React.FormEvent) => {
        e.preventDefault();
        setError("");
        setSuccess("");
        if (newPassword !== confirmPassword) {
            setError("Passwords do not match");
            return;
        }
        if (!currentPassword || !newPassword || !confirmPassword) {
            setError("All fields are required");
            return;
        }
        const id = sessionStorage.getItem("session_id");
        if (id) {
            const userService = new UserService();
            const response = await userService.updatePassword(Number(id), currentPassword, newPassword);
            if (response.status === 200) {
                setSuccess("Password changed successfully");
                setShowForm(false);
                setCurrentPassword("");
                setNewPassword("");
                setConfirmPassword("");
            } else {
                setError(response.data.message);
            }
        } else {
            setError("User ID is not available");
            router.replace("/login");
        }
    };

    return (
        <section className="bg-forms-grey rounded-2xl shadow-lg p-8">
            <h3 className="text-2xl font-semibold mb-4">Change Password</h3>
            {success && <div className="text-green-500 mb-4">{success}</div>}
            {!showForm ? (
                <button
                    className="bg-green-button-home text-white py-3 px-6 rounded-lg hover: bg-hover-button-home cursor-pointer"
                    onClick={() => { setShowForm(true); setSuccess(""); setError(""); }}
                >
                    Change Password
                </button>
            ) : (
                <form onSubmit={handlePasswordChange} className="flex flex-col gap-3">
                    <input
                        className="bg-white text-black border rounded px-4 py-2"
                        type="password"
                        placeholder="Current Password"
                        value={currentPassword}
                        onChange={e => setCurrentPassword(e.target.value)}
                    />
                    <input
                        className="bg-white text-black border rounded px-4 py-2"
                        type="password"
                        placeholder="New Password"
                        value={newPassword}
                        onChange={e => setNewPassword(e.target.value)}
                    />
                    <input
                        className="bg-white text-black border rounded px-4 py-2"
                        type="password"
                        placeholder="Confirm New Password"
                        value={confirmPassword}
                        onChange={e => setConfirmPassword(e.target.value)}
                    />
                    <div className="text-red-400">{error}</div>
                    <div className="flex gap-4 mt-2">
                        <button
                            type="button"
                            className="flex-1 bg-gray-200 text-black py-2 rounded hover: cursor-pointer"
                            onClick={() => setShowForm(false)}
                        >
                            Cancel
                        </button>
                        <button type="submit" className="flex-1 bg-green-button-home text-white py-2 rounded hover: cursor-pointer">
                            Update
                        </button>
                    </div>
                </form>
            )}
        </section>
    );
};

export default PasswordCard;