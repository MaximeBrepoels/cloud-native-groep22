import axios, { AxiosInstance, AxiosResponse } from "axios";

export class BodyweightService {
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

    async addBodyweight(userId: number, weight: number): Promise<AxiosResponse<any, any>> {
        try {
            const response = await this.axiosInstance.post(`/bodyweight/add/${userId}`, { bodyWeight:weight });
            return response;
        } catch (error: any) {
            return error.response;
        }
    }

    async getBodyweightByUserId(userId: number): Promise<AxiosResponse<any, any>> {
        try {
            const response = await this.axiosInstance.get(`/bodyweight/${userId}`);
            return response;
        } catch (error: any) {
            return error.response;
        }
    }
}