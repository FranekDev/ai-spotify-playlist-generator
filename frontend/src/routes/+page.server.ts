import { fail, superValidate } from "sveltekit-superforms";
import type { Actions, PageServerLoad } from "./$types";
import { createPlaylistSchema } from "$lib/types/spotify/schemas/create-playlist-schema";
import { zod } from "sveltekit-superforms/adapters";
import { apiClient } from "$lib/clients/api-client";
import { error, redirect } from "@sveltejs/kit";
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
        console.log("Form valid:", form.valid);
        if (!form.valid) {
            return { form };
        }
        
        const { name, description, public: isPublic } = form.data;
        
        const response = await apiClient.post<SpotifyCreatePlaylist>("/playlist", {
            name,
            description,
            public: isPublic,
        }, locals.user.jwt_token) as ApiResponse<SpotifyCreatePlaylist>;
        
        if (response.data != null) {
            const res = response as ApiResponse<SpotifyCreatePlaylist>;
            console.log("Playlist created successfully:", res);
            return redirect(303, `/playlist/${res.data?.id}`);
        }
        
        if (response.error != null) {
            console.error("Error creating playlist:", response.error);
            return fail(400, {
                form,
                apiError: response.error,
            });
        }

        console.error("Unexpected error:", response);
        return fail(500, {
            form,
            apiError: {
                message: "An unexpected error occurred.",
                status: 500,
            },
        });
    },
};
