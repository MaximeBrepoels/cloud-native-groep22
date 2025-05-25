import axios, { AxiosInstance, AxiosResponse } from "axios";

export class WorkoutService {
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

    async createWorkout(userId: number, workout: { name: string }): Promise<AxiosResponse<any, any>> {
        try {
            return await this.axiosInstance.post(
                `/workouts?userId=${userId}`,
                workout
            );
        } catch (error: any) {
            return error.response;
        }
    }

    async getWorkoutById(id: number): Promise<AxiosResponse<any, any>> {
        try {
            return await this.axiosInstance.get(`/workouts/${id}`);
        } catch (error: any) {
            return error.response;
        }
    }

    async updateWorkout(id: number, name: string, rest: number, exerciseIds: string[]): Promise<AxiosResponse<any, any>> {
        try {
            return await this.axiosInstance.put(`/workouts/${id}`, {
                name,
                rest,
                exerciseIds,
            });
        } catch (error: any) {
            return error.response;
        }
    }

    async deleteWorkout(id: number): Promise<AxiosResponse<any, any>> {
        try {
            return await this.axiosInstance.delete(`/workouts/${id}`);
        } catch (error: any) {
            return error.response;
        }
    }

    async addExercise(workoutId: number, exercise: { name: string; type: string }, goal: string): Promise<AxiosResponse<any, any>> {
        try {
            return await this.axiosInstance.post(
                `/workouts/${workoutId}/addExercise/${goal}`,
                exercise
            );
        } catch (error: any) {
            return error.response;
        }
    }
}