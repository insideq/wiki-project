import { useEffect, useState } from "react";
import { fetchWikiPages } from "../../api";

export const useWikiPages = ({ page = 1, size = 10, query = "", tagId = null } = {}) => {
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        const load = async () => {
            setLoading(true);
            setError(null);
            try {
                const params = new URLSearchParams();
                params.set("page", page);
                params.set("size", size);
                if (query) params.set("query", query);
                if (tagId) params.set("tagId", tagId);
                const result = await fetchWikiPages(params.toString());
                setData(result);
            } catch (e) {
                setError(e.message);
            } finally {
                setLoading(false);
            }
        };
        load();
    }, [page, size, query, tagId]);

    return { data, loading, error };
};