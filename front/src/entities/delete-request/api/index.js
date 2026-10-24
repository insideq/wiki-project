import { apiCreateItem, apiGetAllItems } from "@shared/client";

const PATH = "delete-request";

export const fetchDeleteRequests = () => apiGetAllItems(PATH);
export const createDeleteRequest = (pageId) => apiCreateItem(`${PATH}?pageId=${pageId}`, {});

export const approveDeleteRequest = (id) => apiCreateItem(`${PATH}/${id}/approve`, {});
export const rejectDeleteRequest = (id) => apiCreateItem(`${PATH}/${id}/reject`, {});
