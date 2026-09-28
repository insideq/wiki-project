import { authObserver } from "@shared/auth";
import { getAuthToken } from "@shared/storage";

const URL = import.meta.env.VITE_API_URL;
const AUTH_PREFIX = "auth/";

const makeRequest = async (path, params, vars, method = "GET", data = null) => {
    try {
        const requestParams = params ? `?${params}` : "";
        const pathVariables = vars ? `/${vars}` : "";
        const options = { method };
        const authToken = getAuthToken();
        if (authToken) {
            options.headers = {
                ...options.headers,
                Authorization: `Bearer ${authToken}`,
            };
        }
        const hasBody = (method === "POST" || method === "PUT") && data;
        if (hasBody) {
            options.headers = {
                ...options.headers,
                "Content-Type": "application/json;charset=utf-8",
            };
            if (data) {
                options.body = JSON.stringify(data);
            }
        }
        const response = await fetch(`${URL}${path}${pathVariables}${requestParams}`, options);
        if (response.status === 401) {
            authObserver.notifyUnauthorized();
        }
        if (!response.ok) {
            const errorJson = await response.json();
            console.debug(errorJson);
            throw new Error(`Response status: ${response.status}: ${errorJson.message}`);
        }
        const contentType = response.headers.get("content-type");
        let result;
        if (contentType?.includes("application/json")) {
            result = await response.json();
        } else {
            result = await response.text();
        }
        console.debug(path, result);
        return result;
    } catch (error) {
        throw new Error(error.message);
    }
};

export const apiGetAllItems = (path, params) => makeRequest(path, params);

export const apiGetItem = (path, id) => makeRequest(path, null, id);

export const apiCreateItem = (path, data) => makeRequest(path, null, null, "POST", data);

export const apiUpdateItem = (path, id, data) => makeRequest(path, null, id, "PUT", data);

export const apiDeleteItem = (path, id) => makeRequest(path, null, id, "DELETE");

export const apiLogin = async (credentials) => makeRequest(AUTH_PREFIX + "login", null, null, "POST", credentials);

export const apiWhoAmI = async () => makeRequest(AUTH_PREFIX + "whoami", null, null, "GET", null);

export const apiLogout = async () => makeRequest(AUTH_PREFIX + "logout", null, null, "POST", null);
