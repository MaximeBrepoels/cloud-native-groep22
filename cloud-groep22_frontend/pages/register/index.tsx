import Head from "next/head";
import Header from "@/components/header";
import React, { useState } from "react";
import { useRouter } from "next/router";
import { AuthenticationService } from "@/services/AuthenticationService";

const Register: React.FC = () => {
    const authenticationService = new AuthenticationService();
    const router = useRouter();

    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [error, setError] = useState("");

    const handleRegister = async (e: React.FormEvent) => {
        e.preventDefault();
        if (password !== confirmPassword) {
            setError("Passwords do not match");
            return;
        }
        const response = await authenticationService.register(name, email, password);
        if (response.status === 200) {
            router.push("/login");
        } else {
            setError(response.data.message);
        }
    };

    return (
        <>
            <Head>
                <title>Register - FitApp</title>
            </Head>
            <div className="bg-custom-blue h-screen flex flex-col">
                <Header />
                <main className="flex-1 flex flex-col items-center">
                    <section className="flex flex-col items-center p-6 pt-0 w-full h-full">
                        <form
                            style={{ height: "640px" }}
                            className="w-1/3 m-auto flex flex-col items-center border border-black bg-forms-grey rounded-lg pt-7"
                            onSubmit={handleRegister}
                        >
                            <h1 className="font-semibold text-4xl mb-8 text-custom-blue">Register</h1>
                            {error && <div className="text-red-600 mb-3">{error}</div>}
                            <div className="w-full px-8">
                                <label htmlFor="name" className="text-white text-xl mb-2 block">
                                    Name
                                </label>
                                <input
                                    id="name"
                                    className="text-black w-full p-2.5 bg-white rounded-lg mb-4"
                                    type="text"
                                    placeholder="Name"
                                    value={name}
                                    onChange={e => setName(e.target.value)}
                                />
                            </div>
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
                            <div className="w-full px-8">
                                <label htmlFor="confirmPassword" className="text-white text-xl mb-2 block">
                                    Confirm Password
                                </label>
                                <input
                                    id="confirmPassword"
                                    className="text-black w-full p-2.5 bg-white rounded-lg mb-4"
                                    type="password"
                                    placeholder="Confirm Password"
                                    value={confirmPassword}
                                    onChange={e => setConfirmPassword(e.target.value)}
                                />
                            </div>
                            <div className="w-full flex flex-col items-center">
                                <button
                                    type="button"
                                    className="text-blue-500 underline mt-3"
                                    onClick={() => router.push("/login")}
                                >
                                    Go back to login
                                </button>
                                <button
                                    type="submit"
                                    className="bg-green-button-home text-white font-semibold mt-3 py-2 px-8 rounded-lg hover:bg-hover-button-home w-36"
                                >
                                    Create account
                                </button>
                            </div>
                        </form>
                    </section>
                </main>
            </div>
        </>
    );
};

export default Register;