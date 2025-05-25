import React, { useEffect, useState } from "react";
import { UserService } from "@/services/UserService";

const ProfileInfo: React.FC = () => {
    const [name, setName] = useState("");
    const [email, setEmail] = useState("");

    useEffect(() => {
        const id = sessionStorage.getItem("session_id");
        if (id) {
            const userService = new UserService();
            userService.getUserById(id).then(response => {
                setName(response.data.name);
                setEmail(response.data.email);
            });
        }
    }, []);

    return (
        <section className="bg-forms-grey rounded-2xl shadow-lg p-8 flex flex-col items-center md:col-span-1">
            <div className="flex-shrink-0 w-32 h-32 bg-gradient-to-br from-green-400 to-blue-500 rounded-full flex items-center justify-center text-5xl font-bold mb-4">
                {name ? name[0].toUpperCase() : "?"}
            </div>
            <div className="flex-1 text-center">
                <h2 className="text-2xl font-bold mb-2">Profile</h2>
                <p className="text-lg mb-1"><strong>Name:</strong> {name}</p>
                <p className="text-lg"><strong>Email:</strong> {email}</p>
            </div>
        </section>
    );
};

export default ProfileInfo;