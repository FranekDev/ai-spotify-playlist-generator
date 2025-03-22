import type { Handle } from "@sveltejs/kit";
import type { User } from "./lib/types/user";

export const handle: Handle = async ({ event, resolve }) => {
    const jwt = event.cookies.get("jwt_token");
    
    if (!jwt) {
        event.locals.user = null;
    } else {
        event.locals.user = { jwt_token: jwt } as User;
    }
    
    return await resolve(event);
};