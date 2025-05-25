import React from "react";
import { useRouter } from "next/router";
import EditExerciseForm from "@/components/forms/EditExerciseForm";

const EditExercise: React.FC = () => {
    const router = useRouter();
    const { workoutId } = router.query;

    return (
        <div className="min-h-screen bg-custom-blue flex justify-center">
            <div className="w-full max-w-xl bg-forms-grey rounded-lg shadow-md p-8 mt-12">
                <h1 className="text-2xl text-white font-semibold mb-6">Edit Exercise</h1>
                <EditExerciseForm />
            </div>
        </div>
    );
};

export default EditExercise;