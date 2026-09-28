import { apiGetAllItems, apiGetItem } from "@shared/client";

const PATH = "tag";

export const fetchTags = () => apiGetAllItems(PATH);
export const fetchTag = (id) => apiGetItem(PATH, id);
