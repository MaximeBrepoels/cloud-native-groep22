import axios, { AxiosInstance, AxiosResponse } from 'axios';

export class UserService {
    private axiosInstance: AxiosInstance;

    constructor() {
        this.axiosInstance = axios.create({
            baseURL: process.env.NEXT_PUBLIC_API_URL || 'https://cloud-groep22-fitapp-functions.azurewebsites.net/api',
        });

        this.axiosInstance.interceptors.request.use(
            (config) => {
                const token = sessionStorage.getItem('session_token');
                if (token) {
                    config.headers['Authorization'] = `Bearer ${token}`;
                }
                return config;
            },
            (error) => Promise.reject(error)
        );
    }

    async getUsers(): Promise<AxiosResponse<any, any>> {
        try {
            return await this.axiosInstance.get('/users');
        } catch (error: any) {
            return error.response;
        }
    }

    async getUserById(id: string): Promise<AxiosResponse<any, any>> {
        try {
            return await this.axiosInstance.get(`/users/${id}`);
        } catch (error: any) {
            return error.response;
        }
    }

    async getWorkoutsByUserId(userId: number): Promise<AxiosResponse<any, any>> {
        try {
            return await this.axiosInstance.get(`/workouts/user/${userId}`);
        } catch (error: any) {
            return error.response;
        }
    }

    async updatePassword(userId: number, currentPassword: string, newPassword: string): Promise<AxiosResponse<any, any>> {
        try {
            return await this.axiosInstance.put(`/users/${userId}/password`, {
                currentPassword,
                newPassword,
            });
        } catch (error: any) {
            return error.response;
        }
    }

    async updateStreakGoal(userId: number, streakGoal: number): Promise<AxiosResponse<any, any>> {
        try {
            return await this.axiosInstance.put(`/users/${userId}/streakGoal/${streakGoal}`);
        } catch (error: any) {
            return error.response;
        }
    }

    async updateStreakProgress(userId: number): Promise<AxiosResponse<any, any>> {
        try {
            return await this.axiosInstance.put(`/users/${userId}/streakProgress`);
        } catch (error: any) {
            return error.response;
        }
    }
}