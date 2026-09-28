import { useCallback } from "react";
import { createWikiPage, deleteWikiPage, updateWikiPage } from "../../api";

export const useWikiMutations = () => {
    const create = useCallback((data) => createWikiPage(data), []);
    const update = useCallback((id, data) => updateWikiPage(id, data), []);
    const remove = useCallback((id) => deleteWikiPage(id), []);

    return { create, update, remove };
};