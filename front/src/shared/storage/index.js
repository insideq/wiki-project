export const lsSave = (key, value) => {
    localStorage.setItem(key, JSON.stringify(value));
};

export const lsReadArray = (key) => JSON.parse(localStorage.getItem(key)) || [];

const TOKEN_KEY = "authToken";
const USER_KEY = "currentUser";

const writeToStorage = (key, value) => {
    if (value) {
        localStorage.setItem(key, value);
    } else {
        localStorage.removeItem(key);
    }
};

export const getAuthToken = () => localStorage.getItem(TOKEN_KEY) || null;

export const setAuthToken = (token) => writeToStorage(TOKEN_KEY, token);

export const getCurrentUser = () => JSON.parse(localStorage.getItem(USER_KEY)) || null;

export const setCurrentUser = (user) => writeToStorage(USER_KEY, user ? JSON.stringify(user) : null);
