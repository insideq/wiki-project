import { Button, Modal } from "react-bootstrap";
import { useModal } from "../context";

export const ModalContainer = () => {
    const { modal } = useModal();

    if (!modal) {
        return null;
    }

    return (
        <Modal show={true} key={modal.id} backdrop="static" centered onHide={modal.onClose}>
            <Modal.Header closeButton>
                <Modal.Title>{modal.title}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <p>{modal.text}</p>
            </Modal.Body>
            <Modal.Footer>
                <Button className="w-25" onClick={modal.onClose}>
                    Отмена
                </Button>
                <Button className="w-25" onClick={modal.onConfirm}>
                    ОК
                </Button>
            </Modal.Footer>
        </Modal>
    );
};
