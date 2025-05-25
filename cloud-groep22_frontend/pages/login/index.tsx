// pages/login/start.tsx
import Head from "next/head";
import Header from "@/components/header";
import LoginForm from "@/components/users/LoginForm";

const Login: React.FC = () => {
    return (
        <>
            <Head>
                <title>Login - FitApp</title>
            </Head>
            <Header />
            <main>
                <section className="bg-custom-blue p-6 min-h-screen flex flex-col items-center justify-center">
                    <LoginForm />
                </section>
            </main>
        </>
    );
};

export default Login;