import { useEffect, useState } from "react";
import { fetchTags } from "../../api";

export const useTags = () => {
    const [data, setData] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        const load = async () => {
            setLoading(true);
            try {
                const result = await fetchTags();
                setData(result);
            } finally {
                setLoading(false);
            }
        };
        load();
    }, []);

    return { data, loading };
};