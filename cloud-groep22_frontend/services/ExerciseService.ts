import axios, { AxiosInstance, AxiosResponse } from "axios";
import { Exercise } from "@/types";

export class ExerciseService {
    private axiosInstance: AxiosInstance;

    constructor() {
        this.axiosInstance = axios.create({
            baseURL: process.env.NEXT_PUBLIC_API_URL,
        });

        this.axiosInstance.interceptors.request.use(
            (config) => {
                const token = sessionStorage.getItem("session_token");
                if (token) {
                    config.headers["Authorization"] = `Bearer ${token}`;
                }
                return config;
            },
            (error) => Promise.reject(error)
        );
    }

    async getExerciseById(id: number): Promise<AxiosResponse<any, any>> {
        try {
            return await this.axiosInstance.get(`/exercises/${id}`);
        } catch (error: any) {
            return error.response;
        }
    }

    async getExercisesByUserId(userId: number): Promise<AxiosResponse<any, any>> {
        try {
            return await this.axiosInstance.get(`/exercises/user/${userId}`);
        } catch (error: any) {
            return error.response;
        }
    }

    async deleteExerciseFromWorkout(workoutId: number, exerciseId: number): Promise<AxiosResponse<any, any>> {
        try {
            return await this.axiosInstance.delete(
                `/exercises/workout/${workoutId}/exercise/${exerciseId}`
            );
        } catch (error: any) {
            return error.response;
        }
    }

    async updateExercise(
        id: number,
        exercise: {
            name: string;
            type: string;
            rest: number;
            autoIncrease: boolean;
            autoIncreaseFactor: number;
            autoIncreaseWeightStep: number;
            autoIncreaseStartWeight: number;
            autoIncreaseMinSets: number;
            autoIncreaseMaxSets: number;
            autoIncreaseMinReps: number;
            autoIncreaseMaxReps: number;
            autoIncreaseStartDuration: number;
            autoIncreaseDurationSets: number;
            autoIncreaseCurrentSets: number;
            autoIncreaseCurrentReps: number;
            autoIncreaseCurrentWeight: number;
            autoIncreaseCurrentDuration: number;
            sets: Array<{
                id: number;
                reps: number;
                weight: number;
                duration: number;
            }>;
        }
    ): Promise<AxiosResponse<any, any>> {
        try {
            return await this.axiosInstance.put(`/exercises/${id}`, exercise);
        } catch (error: any) {
            return error.response;
        }
    }

    async autoIncrease(id: number): Promise<AxiosResponse<any, any>> {
        try {
            return await this.axiosInstance.put(`/exercises/increase/${id}`);
        } catch (error: any) {
            return error.response;
        }
    }

    async autoDecrease(id: number): Promise<AxiosResponse<any, any>> {
        try {
            return await this.axiosInstance.put(`/exercises/decrease/${id}`);
        } catch (error: any) {
            return error.response;
        }
    }
}