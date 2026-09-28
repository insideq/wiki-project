import { useAuth } from "@shared/auth";
import { UserButton } from "@shared/auth/ui";
import { ToastMenu } from "@shared/toast/ui";
import { Container, Nav, Navbar, NavbarBrand } from "react-bootstrap";
import { JournalText } from "react-bootstrap-icons";
import { NavLink } from "react-router-dom";
import "./styles.css";

export const Header = () => {
    const { isAuthenticated } = useAuth();

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
                                <Nav.Link to="/wiki/new" as={NavLink}>
                                    Создать статью
                                </Nav.Link>
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