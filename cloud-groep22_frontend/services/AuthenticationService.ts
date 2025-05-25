import axios, { AxiosResponse } from 'axios';

export class AuthenticationService {
    private baseUrl: string;

    constructor() {
        this.baseUrl = process.env.NEXT_PUBLIC_API_URL || '';
    }

    // Register a new user
    async register(name: string, email: string, password: string): Promise<AxiosResponse<any, any>> {
        try {
            const response = await axios.post(`${this.baseUrl}/auth/register`, { name, email, password });
            return response;
        } catch (error: any) {
            return error.response;
        }
    }

    // Login a user
    async login(email: string, password: string): Promise<AxiosResponse<any, any>> {
        try {
            const response = await axios.post(`${this.baseUrl}/auth/login`, { email, password });
            return response;
        } catch (error: any) {
            return error.response;
        }
    }
}