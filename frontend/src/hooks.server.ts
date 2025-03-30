import { redirect, type Handle } from "@sveltejs/kit";
import type { User } from "./lib/types/user";

export const handle: Handle = async ({ event, resolve }) => {
    const publicRoutes = ["/", "/success"];
    const jwt = event.cookies.get("jwt_token");
    
    if (!jwt) {
        event.locals.user = null;
        if (!publicRoutes.includes(event.url.pathname)) {
            return redirect(302, "/");
        }
    } else {
        event.locals.user = { jwt_token: jwt } as User;
    }
    
    return await resolve(event);
};