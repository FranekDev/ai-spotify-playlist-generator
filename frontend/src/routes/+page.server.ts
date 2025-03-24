import { fail, superValidate } from "sveltekit-superforms";
import type { Actions, PageServerLoad } from "./$types";
import { createPlaylistSchema } from "$lib/types/spotify/schemas/create-playlist-schema";
import { zod } from "sveltekit-superforms/adapters";
import { apiClient } from "$lib/clients/api-client";
import { redirect } from "@sveltejs/kit";
import type { SpotifyCreatePlaylist } from "$lib/types/spotify/spotify-create-playlist";
import type { ApiResponse } from "$lib/types/api/api-response";

export const load: PageServerLoad = async ({ locals }) => {
    return { 
        user: locals.user,
        form: await superValidate(zod(createPlaylistSchema))
     };
};

export const actions: Actions = {
    createPlaylist: async ({ request, locals }) => {
        const form = await superValidate(request, zod(createPlaylistSchema));
        if (!form.valid) {
            return { form };
        }
        
        const { name, description, public: isPublic, tracksAmount } = form.data;
        
        const response = await apiClient.post<SpotifyCreatePlaylist>("/playlist", {
            name,
            description,
            public: isPublic,
            tracksAmount
        }, locals.user.jwt_token) as ApiResponse<SpotifyCreatePlaylist>;
        
        if (response.data != null) {
            const res = response as ApiResponse<SpotifyCreatePlaylist>;
            return redirect(303, `/playlist/${res.data?.id}`);
        }
        
        if (response.error != null) {
            return fail(400, {
                form,
                apiError: response.error,
            });
        }

        return fail(500, {
            form,
            apiError: {
                message: "An unexpected error occurred.",
                status: 500,
            },
        });
    },
};
