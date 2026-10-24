import { apiCreateItem, apiGetAllItems } from "@shared/client";

const PATH = "tag-request";

export const fetchTagRequests = () => apiGetAllItems(PATH);
export const createTagRequest = (data) => apiCreateItem(PATH, data);

// Для approve/reject бэкенд ожидает POST без тела, передаём пустой объект
export const approveTagRequest = (id) => apiCreateItem(`${PATH}/${id}/approve`, {});
export const rejectTagRequest = (id) => apiCreateItem(`${PATH}/${id}/reject`, {});
