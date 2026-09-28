import { TOAST_ACTION, useToasts, useToastsDispatch } from "@shared/toast/context";
import { Dropdown } from "react-bootstrap";
import { BellFill, BellSlashFill } from "react-bootstrap-icons";
import "./styles.css";

export const ToastMenu = ({ className }) => {
    const toasts = useToasts();
    const toast = useToastsDispatch();

    const data = toasts.filter((toast) => !toast.show);
    const isEmpty = data.length === 0;
    const icon = isEmpty ? <BellSlashFill /> : <BellFill />;
    const variant = isEmpty ? "secondary" : "primary";

    const handleDelete = (id) => {
        toast({
            type: TOAST_ACTION.delete,
            id,
        });
    };

    const handleDeleteAll = () => {
        toast({
            type: TOAST_ACTION.deleteAll,
        });
    };

    const content = data.map((toast) => {
        return (
            <Dropdown.Item key={toast.id} className="toast-menu-item" onClick={() => handleDelete(toast.id)}>
                <span className="fw-bold">{toast.date}</span>
                &nbsp;
                <span>{toast.text}</span>
            </Dropdown.Item>
        );
    });

    return (
        <Dropdown className={className}>
            <Dropdown.Toggle className="toast-toggle" variant={variant}>
                {icon}
            </Dropdown.Toggle>

            <Dropdown.Menu className="toast-menu" align="end">
                {content}
                <Dropdown.Item
                    disabled={isEmpty}
                    className="toast-menu-item text-center text-body-tertiary"
                    onClick={handleDeleteAll}
                >
                    Очистить
                </Dropdown.Item>
            </Dropdown.Menu>
        </Dropdown>
    );
};
