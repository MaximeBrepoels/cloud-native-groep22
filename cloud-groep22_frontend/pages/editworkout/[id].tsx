// File: pages/editworkout/[id].tsx

import { useRouter } from "next/router";
import EditWorkoutForm from "@/components/forms/EditWorkoutForm";

const EditWorkoutPage = () => {
    const router = useRouter();
    const { id } = router.query;

    return (
        <main className="bg-custom-blue min-h-screen flex flex-col items-center py-10 px-2">
            <section className="w-full max-w-3xl">
                <h2 className="text-white text-2xl font-bold mb-6">Edit Workout</h2>
                <EditWorkoutForm workoutId={id} />
            </section>
        </main>
    );
};

export default EditWorkoutPage;