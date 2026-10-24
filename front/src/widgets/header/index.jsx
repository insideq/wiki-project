import { useAuth } from "@shared/auth";
import { UserButton } from "@shared/auth/ui";
import { ToastMenu } from "@shared/toast/ui";
import { Container, Nav, Navbar, NavbarBrand, NavDropdown } from "react-bootstrap";
import { JournalText } from "react-bootstrap-icons";
import { NavLink } from "react-router-dom";
import "./styles.css";

export const Header = () => {
    const { isAuthenticated, user } = useAuth();
    const isAdmin = user?.details?.role === "ROLE_ADMIN";

    return (
        <header>
            <Navbar expand="md" variant="dark">
                <Container fluid>
                    <NavbarBrand href="/">
                        <JournalText className="me-1" />
                        Wiki
                    </NavbarBrand>
                    <Navbar.Toggle aria-controls="navbar-nav" />
                    <Navbar.Collapse id="navbar-nav" className="justify-content-end">
                        <Nav>
                            <Nav.Link to="/wiki" as={NavLink}>
                                Статьи
                            </Nav.Link>
                            {isAuthenticated && (
                                <>
                                    <Nav.Link to="/wiki/new" as={NavLink}>
                                        Создать статью
                                    </Nav.Link>
                                    <Nav.Link to="/tag-request/create" as={NavLink}>
                                        Предложить тег
                                    </Nav.Link>
                                </>
                            )}
                            {isAdmin && (
                                <NavDropdown title="Админ-панель" id="admin-nav-dropdown">
                                    <NavDropdown.Item to="/admin/tag-requests" as={NavLink}>
                                        Запросы на теги
                                    </NavDropdown.Item>
                                    <NavDropdown.Item to="/admin/delete-requests" as={NavLink}>
                                        Запросы на удаление
                                    </NavDropdown.Item>
                                </NavDropdown>
                            )}
                        </Nav>
                        <ToastMenu className="ms-2" />
                        <UserButton className="ms-3" />
                    </Navbar.Collapse>
                </Container>
            </Navbar>
        </header>
    );
};