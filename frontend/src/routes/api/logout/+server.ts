import type { RequestHandler } from "@sveltejs/kit";

export const POST: RequestHandler = async ({ cookies, locals }) => {    
    cookies.delete('jwt_token', { 
        path: '/',
        httpOnly: true,
        secure: false,
        sameSite: 'strict'
    });

    locals.user = null;

    return new Response(null, { status: 200 });
};