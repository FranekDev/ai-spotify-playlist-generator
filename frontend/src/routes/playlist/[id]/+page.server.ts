import type { PageServerLoad } from "./$types";

export const load: PageServerLoad = async ({ params, locals }) => {
    const { id } = params;
    
    return {
        id,
        user: locals.user
    };
};