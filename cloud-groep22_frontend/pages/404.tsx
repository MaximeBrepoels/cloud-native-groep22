import { useEffect } from 'react';
import { useRouter } from 'next/router';
import Head from 'next/head';
import Header from '@/components/header';

export default function Custom404() {
    const router = useRouter();

    useEffect(() => {
        // For SPA routing, redirect to home and let client-side routing handle it
        const timer = setTimeout(() => {
            router.push('/');
        }, 3000);

        return () => clearTimeout(timer);
    }, [router]);

    return (
        <>
            <Head>
                <title>404 - Page Not Found</title>
            </Head>
            <div className="bg-custom-blue min-h-screen flex flex-col">
                <Header />
                <div className="flex-1 flex items-center justify-center">
                    <div className="text-center">
                        <h1 className="text-4xl font-bold text-white mb-4">404 - Page Not Found</h1>
                        <p className="text-white mb-4">Redirecting to home page...</p>
                        <button
                            onClick={() => router.push('/')}
                            className="bg-green-button-home text-white py-2 px-4 rounded cursor-pointer"
                        >
                            Go Home Now
                        </button>
                    </div>
                </div>
            </div>
        </>
    );
}