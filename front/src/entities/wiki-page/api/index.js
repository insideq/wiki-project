import { apiCreateItem, apiDeleteItem, apiGetAllItems, apiGetItem, apiUpdateItem } from "@shared/client";

const PATH = "wiki";

export const fetchWikiPages = (params) => apiGetAllItems(PATH, params);
export const fetchWikiPage = (id) => apiGetItem(PATH, id);
export const createWikiPage = (data) => apiCreateItem(PATH, data);
export const updateWikiPage = (id, data) => apiUpdateItem(PATH, id, data);
export const deleteWikiPage = (id) => apiDeleteItem(PATH, id);
