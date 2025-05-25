import React, { useEffect, useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/router';

const Header: React.FC = () => {
    const [loggedIn, setLoggedIn] = useState(false);
    const [currentPath, setCurrentPath] = useState('');
    const router = useRouter();

    useEffect(() => {
        setCurrentPath(window.location.pathname);

        const id = sessionStorage.getItem('session_id');
        setLoggedIn(!!id);
    }, []);

    const handleLogOut = () => {
        if (typeof window !== 'undefined') {
            sessionStorage.removeItem('session_id');
            sessionStorage.removeItem('session_token');
            setLoggedIn(false);
            window.location.replace('/');
        }
    };

    return (
        <header className="bg-custom-blue text-white flex items-center py-6 px-4 relative">
            {/* Left: Logo */}
            <div className="flex flex-row mini:ml-10 mt-1 ml-4 w-1/3">
                <Link href="/">
                    <span className="font-bold text-3xl tracking-tight text-white cursor-pointer">FitApp</span>
                </Link>
            </div>
            {/* Center: Nav */}
            <nav className="absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 flex items-center">
                <Link
                    href="/"
                    className={`relative px-4 text-2xl font-semibold after:content-[''] after:bg-white after:absolute after:h-0.5 after:w-0 after:bottom-0 after:left-1 hover:after:w-11/12 after:transition-all after:duration-300 mr-7 ${
                        currentPath === '/' ? 'text-[#86C232]' : ''
                    }`}
                >
                    Home
                </Link>
                {!loggedIn ? (
                    <Link
                        href="/login"
                        className={`relative px-4 text-2xl font-semibold after:content-[''] after:bg-white after:absolute after:h-0.5 after:w-0 after:bottom-0 after:left-1 hover:after:w-11/12 after:transition-all after:duration-300 ${
                            currentPath === '/login' ? 'text-[#86C232]' : ''
                        }`}
                    >
                        Login
                    </Link>
                ) : (
                    <>
                        <Link
                            href="/profile"
                            className={`relative px-4 text-2xl font-semibold after:content-[''] after:bg-white after:absolute after:h-0.5 after:w-0 after:bottom-0 after:left-1 hover:after:w-11/12 after:transition-all after:duration-300 mr-7 ${
                                currentPath === '/profile' ? 'text-[#86C232]' : ''
                            }`}
                        >
                            Profile
                        </Link>
                        <button
                            onClick={handleLogOut}
                            className="relative px-4 text-2xl font-semibold after:content-[''] after:bg-white after:absolute after:h-0.5 after:w-0 after:bottom-0 after:left-1 hover:after:w-11/12 after:transition-all after:duration-300 cursor-pointer bg-transparent border-none text-white"
                        >
                            Logout
                        </button>
                    </>
                )}
            </nav>
        </header>
    );
};

export default Header;