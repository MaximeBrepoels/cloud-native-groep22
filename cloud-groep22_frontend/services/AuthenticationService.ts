import axios, { AxiosResponse, AxiosError } from 'axios';

export class AuthenticationService {
    private baseUrl: string;

    constructor() {
        this.baseUrl = process.env.NEXT_PUBLIC_API_URL || 'https://cloud-groep22-fitapp-functions.azurewebsites.net/api';
    }

    // Register a new user
    async register(name: string, email: string, password: string): Promise<AxiosResponse<any, any>> {
        try {
            const response = await axios.post(`${this.baseUrl}/auth/register`, { name, email, password });
            return response;
        } catch (error: any) {
            console.error('Registration error:', error);
            if (error.response) {
                return error.response;
            }
            return {
                data: { message: error.message || 'Network error occurred' },
                status: 0,
                statusText: 'Network Error',
                headers: {},
                config: error.config
            } as AxiosResponse;
        }
    }

    // Login a user
    async login(email: string, password: string): Promise<AxiosResponse<any, any>> {
        try {
            const response = await axios.post(`${this.baseUrl}/auth/login`, { email, password });
            return response;
        } catch (error: any) {
            console.error('Login error:', error);
            if (error.response) {
                return error.response;
            }
            return {
                data: { message: error.message || 'Network error occurred' },
                status: 0,
                statusText: 'Network Error',
                headers: {},
                config: error.config
            } as AxiosResponse;
        }
    }
}