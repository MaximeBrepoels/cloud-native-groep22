import Head from "next/head";
import Header from "@/components/header";
import ProfileInfo from "@/components/profile/ProfileInfo";
import PasswordCard from "@/components/profile/PasswordCard";
import BodyweightCard from "@/components/profile/BodyweightCard";

const Profile: React.FC = () => (
    <>
        <Head>
            <title>Profile</title>
            <meta name="description" content="User profile" />
        </Head>
        <div className="bg-custom-blue min-h-screen flex flex-col">
            <Header />
            <main className="flex-1 text-white py-10 px-4 flex flex-col items-center">
                <div className="w-full max-w-6xl flex flex-col gap-8">
                    <h1 className="text-4xl font-bold mb-6 text-center">Your Profile</h1>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
                        <ProfileInfo />
                        <div className="flex flex-col gap-8 md:col-span-2">
                            <PasswordCard />
                            <BodyweightCard />
                        </div>
                    </div>
                </div>
            </main>
        </div>
    </>
);

export default Profile;