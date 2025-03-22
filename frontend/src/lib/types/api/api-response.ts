import type { ApiError } from "./api-error";

export type ApiResponse<T> = {
    data: T | null;
    error: ApiError | null;
};