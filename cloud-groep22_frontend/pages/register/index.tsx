import Head from "next/head";
import Header from "@/components/header";
import React, {useState} from "react";
import {useRouter} from "next/router";
import {AuthenticationService} from "@/services/AuthenticationService";

const Register: React.FC = () => {
    const authenticationService = new AuthenticationService();
    const router = useRouter();

    // Name
    const [name, setName] = useState("");
    const [nameError, setNameError] = useState("");
    // Email
    const [email, setEmail] = useState("");
    const [emailError, setEmailError] = useState("");
    const [emailTouched, setEmailTouched] = useState(false);
    // Password
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [passwordError, setPasswordError] = useState("");
    const [passwordTouched, setPasswordTouched] = useState(false);
    const [confirmPasswordError, setConfirmPasswordError] = useState("");
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [processing, setProcessing] = useState("");

    // Name input field
    const handleNameChange = (nameValue: string)=> {
        setName(nameValue);
        if (nameError && nameValue.trim()) {
            setNameError("");
        }
    };

    // Email input field
    const emailValidation = (emailValue: string): string => {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(emailValue)) {
            return "Please enter a valid email address.";
        }
        return "";
    };

    const handleEmailBlur = () => {
        setEmailTouched(true);
        setEmailError(emailValidation(email));
    };

    const handleEmailChange = (emailValue: string) => {
        setEmail(emailValue);
    };


    // Password input field
    const passwordValidation = (passwordValue: string): string => {
       if (passwordValue.length < 8) {
            return "Password must be at least 8 characters.";
        }
        const uppercaseRegex = /[A-Z]/;
        const numberRegex = /[0-9]/;
        if (!uppercaseRegex.test(passwordValue) || !numberRegex.test(passwordValue)) {
            return "Password must contain at least one uppercase letter and one number.";
        }
        return "";
    };

    const handlePasswordBlur = () => {
      setPasswordTouched(true);
      setPasswordError(passwordValidation(password));
    };

    const handlePasswordChange = (passwordValue: string) => {
        setPassword(passwordValue);
    };

    // Handle validation of the whole form
    const registerFormValidation = () => {
        const errors: { [key: string]: string } = {};
        if (!name.trim()) {
            errors.name = "Name is required.";
        }
        if (!email.trim()) {
            errors.email = "Email is required.";
        }
        if (!password) {
            errors.password = "Password is required.";
        } else {
            const passwordError = passwordValidation(password);
            if (passwordError) {
                errors.password = passwordError
            }
        }
        if (!confirmPassword) {
            errors.confirmPassword = "Confirm password is required.";
        } else if (password !== confirmPassword) {
            errors.confirmPassword = "Confirm Password does not match Password."
        }
        return errors;
    };


    const handleRegister = async (e: React.FormEvent) => {
        e.preventDefault();
        const errors = registerFormValidation();
        if (Object.keys(errors).length > 0) {
            setNameError(errors.name || "");
            setEmailError(errors.email || "");
            setPasswordError(errors.password || "");
            setConfirmPasswordError(errors.confirmPassword || "");
            setEmailTouched(true);
            setPasswordTouched(true);
            setError("");
            return;
        }
        setConfirmPasswordError("");

        const response = await authenticationService.register(name, email, password);
        setProcessing("Processing registration.");
        if (response && (response.status === 200 || response.status === 201)) {
            setProcessing("");
            setSuccess("Registration is successful!");
            setTimeout(() => {
                router.push("/login");
            }, 2000);
        } else if (response && response.data && response.data.message) {
            setError(response.data.message);
        } else {
            setError("Registration failed. Please try again.");
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
                            {success && <div className="text-green-600 mb-3">{success}</div> }
                            {processing && <div className="text-white mb-3">{processing}</div> }
                            <div className="w-full px-8">
                                <label htmlFor="name" className="text-white text-xl mb-2 block">
                                    Name
                                </label>
                                {nameError && <div className="text-red-600 mb-1">{nameError}</div>}
                                <input
                                    id="name"
                                    className="text-black w-full p-2.5 bg-white rounded-lg mb-4"
                                    type="text"
                                    placeholder="Name"
                                    value={name}
                                    onChange={e => handleNameChange(e.target.value)}
                                />
                            </div>
                            <div className="w-full px-8">
                                <label htmlFor="email" className="text-white text-xl mb-2 block">
                                    Email
                                </label>
                                {emailTouched && emailError && (
                                    <div className="text-red-600">{emailError}</div>
                                )}
                                <input
                                    id="email"
                                    className="text-black w-full p-2.5 bg-white rounded-lg mb-4"
                                    type="email"
                                    placeholder="Email"
                                    value={email}
                                    onBlur={handleEmailBlur}
                                    onChange={e => handleEmailChange(e.target.value)}
                                />
                            </div>
                            <div className="w-full px-8">
                                <label htmlFor="password" className="text-white text-xl mb-2 block">
                                    Password
                                </label>
                                {passwordTouched && passwordError && (
                                    <div className="text-red-600">{passwordError}</div>
                                )}
                                <input
                                    id="password"
                                    className="text-black w-full p-2.5 bg-white rounded-lg mb-4"
                                    type="password"
                                    placeholder="Password"
                                    value={password}
                                    onBlur={handlePasswordBlur}
                                    onChange={e => handlePasswordChange(e.target.value)}
                                />
                            </div>
                            <div className="w-full px-8">
                                <label htmlFor="confirmPassword" className="text-white text-xl mb-2 block">
                                    Confirm Password
                                </label>
                                {confirmPasswordError && (
                                    <div className="text-red-600 mb-1">{confirmPasswordError}</div>
                                )}
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
                                    className="text-blue-500 underline mt-3 cursor-pointer"
                                    onClick={() => router.push("/login")}
                                >
                                    Go back to login
                                </button>
                                <button
                                    type="submit"
                                    className="bg-green-button-home text-white font-semibold mt-3 py-2 px-8 rounded-lg hover: bg-hover-button-home cursor-pointer w-36"
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