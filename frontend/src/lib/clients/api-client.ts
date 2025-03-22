import type { ApiError } from "$lib/types/api/api-error";
import type { ApiResponse } from "$lib/types/api/api-response";

class ApiClient {
    private baseUrl: string;
  
    constructor(baseUrl: string) {
      this.baseUrl = baseUrl;
    }
  
    private async request<T>(endpoint: string, options?: RequestInit): Promise<ApiResponse<T>> {
      try {
        const response = await fetch(`${this.baseUrl}${endpoint}`, options);
        if (!response.ok) {
            const error: ApiError = {
                message: response.statusText,
                status: response.status,
            };
            return { data: null, error };
        }
        const jsonResponse = await response.json();
        
        if (jsonResponse.data !== undefined && jsonResponse.error !== undefined) {
            return jsonResponse as ApiResponse<T>;
        }
        
        return { data: jsonResponse as T, error: null };
      } catch (error) {
        throw new Error(`HTTP error! status: ${error}`);
      }
    }
  
    public async get<T>(endpoint: string, jwtToken: string): Promise<ApiResponse<T>> {
      return await this.request<T>(endpoint, { 
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${jwtToken}`,
          'Content-Type': 'application/json',
        },
       });
    }
  
    public async post<T>(endpoint: string, body: any, jwtToken: string): Promise<ApiResponse<T>> {
      return await this.request<T>(endpoint, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${jwtToken}`,
        },
        body: JSON.stringify(body),
      });
    }
  
    public delete<T>(endpoint: string): Promise<ApiResponse<T>> {
      return this.request<T>(endpoint, { method: 'DELETE' });
    }
  }
  
//export const apiClient = new ApiClient(import.meta.env.VITE_API_URL as string);
export const apiClient = new ApiClient("http://localhost:8080");
