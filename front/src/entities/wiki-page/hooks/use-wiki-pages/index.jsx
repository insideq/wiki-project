import { useEffect, useState } from "react";
import { fetchWikiPages } from "../../api";

export const useWikiPages = ({ page = 1, size = 10, query = "", tagIds = [] } = {}) => {
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const tagsString = tagIds.join(",");

    useEffect(() => {
        const load = async () => {
            setLoading(true);
            setError(null);
            try {
                const params = new URLSearchParams();
                params.set("page", page);
                params.set("size", size);
                if (query) params.set("query", query);
                if (tagsString) params.set("tagIds", tagsString);
                const result = await fetchWikiPages(params.toString());
                setData(result);
            } catch (e) {
                setError(e.message);
            } finally {
                setLoading(false);
            }
        };
        load();
    }, [page, size, query, tagsString]);

    return { data, loading, error };
};