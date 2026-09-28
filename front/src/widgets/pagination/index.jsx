import { Pagination as BSPagination } from "react-bootstrap";

export const Pagination = ({ page, total, onChange }) => {
    const items = [...Array(total || 0).keys()].map((item) => {
        const index = item + 1;
        const isActive = index === page;
        const handler = isActive ? null : () => onChange(index);
        return (
            <BSPagination.Item key={index} active={isActive} onClick={handler}>
                {index}
            </BSPagination.Item>
        );
    });

    return <BSPagination className="justify-content-center">{items}</BSPagination>;
};
