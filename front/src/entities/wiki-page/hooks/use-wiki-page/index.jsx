import { useEffect, useState } from "react";
import { fetchWikiPage } from "../../api";

export const useWikiPage = (id) => {
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (!id) return;
        const load = async () => {
            setLoading(true);
            setError(null);
            try {
                const result = await fetchWikiPage(id);
                setData(result);
            } catch (e) {
                setError(e.message);
            } finally {
                setLoading(false);
            }
        };
        load();
    }, [id]);

    return { data, loading, error, setData };
};