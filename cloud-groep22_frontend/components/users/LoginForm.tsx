import React, { useState, useEffect } from "react";
import { useRouter } from "next/router";
import { AuthenticationService } from "@/services/AuthenticationService";
import { updateStreak } from "@/utils/StreakUtil";

const LoginForm: React.FC = () => {
    const authenticationService = new AuthenticationService();
    const router = useRouter();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [emailError, setEmailError] = useState<string | null>(null);
    const [passwordError, setPasswordError] = useState<string | null>(null);
    const [error, setError] = useState("");

    useEffect(() => {
        const token = sessionStorage.getItem("session_token");
        if (token) {
            router.replace("/");
        }
    }, [router]);

    const clearErrors = () => {
        setEmailError(null);
        setPasswordError(null);
        setError("");
    };

    const validate = (): boolean => {
        let valid = true;
        if ((!email || email.trim() === "") && (!password || password.trim() === "")) {
            setError("Please enter your email and password");
            valid = false;
        } else if (!email || email.trim() === "") {
            setError("Email is required");
            valid = false;
        } else if (!password || password.trim() === "") {
            setError("Password is required");
            valid = false;
        } else {
            setError("");
        }
        return valid;
    };

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        clearErrors();
        if (!validate()) return;

        try {
            const response = await authenticationService.login(email, password);

            if (response.status === 0) {
                setError("Unable to connect to server. Please check your connection and try again.");
                return;
            }

            if (response.status === 200) {
                sessionStorage.setItem("session_token", response.data.token);
                sessionStorage.setItem("session_id", response.data.userId);
                await updateStreak();
                router.replace("/");
            } else {
                setError(response.data?.message || "Login failed. Please try again.");
            }
        } catch (error: any) {
            console.error("Login error:", error);
            setError("An unexpected error occurred. Please try again.");
        }
    };

    return (
        <form
            style={{ height: '460' +
                    'px' }}
            className="w-1/3 m-auto mt-4 flex flex-col items-center border border-black bg-forms-grey rounded-lg"
            onSubmit={handleLogin}
        >
            <h1 className="font-semibold text-4xl mt-10 mb-8">Login</h1>
            {error && <div className="text-red-600 mb-3">{error}</div>}
            <div className="w-full px-8">
                <label htmlFor="email" className="text-white text-xl mb-2 block">
                    Email
                </label>
                <input
                    id="email"
                    className="text-black w-full p-2.5 bg-white rounded-lg mb-4"
                    type="email"
                    placeholder="Email"
                    value={email}
                    onChange={e => setEmail(e.target.value)}
                />
            </div>
            <div className="w-full px-8">
                <label htmlFor="password" className="text-white text-xl mb-2 block">
                    Password
                </label>
                <input
                    id="password"
                    className="text-black w-full p-2.5 bg-white rounded-lg mb-4"
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={e => setPassword(e.target.value)}
                />
            </div>
            <div className="w-full flex flex-col items-center">
                <button
                    type="button"
                    className="text-blue-500 underline"
                    onClick={() => router.push("/register")}
                >
                    Don't have an account? Register here!
                </button>
                <button
                    type="submit"
                    className="bg-green-button-home text-white font-semibold mt-3 py-2 px-8 rounded-lg hover: bg-hover-button-home cursor-pointer w-36"
                >
                    Login
                </button>
            </div>
        </form>
    );
};

export default LoginForm;