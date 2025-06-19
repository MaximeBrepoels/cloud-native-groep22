import React from "react";
import Link from "next/link";
import Header from "@/components/header";
import Head from "next/head";

const LandingPage: React.FC = () => (
    <>
        <Head>
            <title>FitApp - Track Your Fitness</title>
            <meta name="description" content="Track your workouts, monitor your progress, and stay motivated" />
            <meta name="viewport" content="width=device-width, initial-scale=1" />
        </Head>
        <div className="max-h-screen flex flex-col bg-custom-blue">
            <main className="flex-1 text-center md:p-24 pb-0 m-0 mt-16">
                <h1 className="text-white font-bold text-6xl mb-8">Welcome to FitApp</h1>
                <p className="text-white max-w-2xl mx-auto">
                    Track your workouts, monitor your progress, and stay motivated on your fitness journey.
                </p>
                <Link href="/login">
                    <button className="bg-green-button-home text-white font-semibold py-4 px-8 rounded-lg mt-8 hover: bg-hover-button-home cursor-pointer">
                        Get Started
                    </button>
                </Link>
            </main>
        </div>
    </>
);

export default LandingPage;