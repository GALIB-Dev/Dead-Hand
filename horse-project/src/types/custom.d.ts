declare module 'web-push' {
    export function setVapidDetails(
        subject: string,
        publicKey: string,
        privateKey: string
    ): void;
    
    export function sendNotification(
        subscription: PushSubscription,
        payload: string
    ): Promise<void>;
}

declare module '@godaddy/terminus' {
    export function createTerminus(
        server: any,
        options: any
    ): void;
}
