import React, { useEffect, useState } from "react";
import LandingPage from "@/components/homepages/LandingPage";
import HomepageUser from "@/components/homepages/HomepageUser";
import Header from "@/components/header";

const Home: React.FC = () => {
    const [loggedIn, setLoggedIn] = useState(false);
    const [userId, setUserId] = useState<number | null>(null);

    useEffect(() => {
        const id = typeof window !== "undefined" ? sessionStorage.getItem("session_id") : null;
        setLoggedIn(!!id);
        setUserId(id ? parseInt(id) : null);
    }, []);

    return (
        <div className="flex flex-col h-screen bg-custom-blue">
            <Header />
            <div className="flex-1 flex flex-col">
                { !loggedIn ? <LandingPage /> : userId ? <HomepageUser userId={userId} /> : null }
            </div>
        </div>
    );
};

export default Home;