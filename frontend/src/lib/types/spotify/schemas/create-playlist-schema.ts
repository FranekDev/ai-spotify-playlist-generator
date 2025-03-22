import { z } from 'zod';

export const createPlaylistSchema = z.object({
    name: z.string().min(1).max(100),
    description: z.string().min(1).max(300),
    tracksAmount: z.number().min(1).max(100),
    public: z.boolean().default(false),
});

export type CreatePlaylistSchema = typeof createPlaylistSchema;