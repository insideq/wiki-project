class AuthObserver {
    constructor() {
        this.subscribers = [];
    }

    subscribe(callback) {
        this.subscribers.push(callback);
        return () => {
            this.subscribers = this.subscribers.filter((sub) => sub !== callback);
        };
    }

    notifyUnauthorized() {
        for (const callback of this.subscribers) {
            callback();
        }
    }
}

export const authObserver = new AuthObserver();
