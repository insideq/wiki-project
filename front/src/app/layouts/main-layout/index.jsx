import { Footer, Header } from "@widgets/index";
import { Outlet } from "react-router-dom";

export const MainLayout = () => {
    return (
        <>
            <Header />
            <main className="flex-grow-1 container-fluid p-2">
                <Outlet />
            </main>
            <Footer />
        </>
    );
};
