import {
    DeleteRequestsPage,
    ForbiddenPage,
    HomePage,
    LoginPage,
    NotFoundPage,
    RegisterPage,
    TagRequestCreatePage,
    TagRequestsPage,
    WikiEditPage,
    WikiListPage, WikiViewPage,
} from "@pages/index";
import { AuthProvider } from "@shared/auth";
import { ModalContainer, ModalProvider } from "@shared/modal";
import { ToastProvider } from "@shared/toast";
import { ToastContainer } from "@shared/toast/ui";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { MainLayout, RoleBasedLayout } from "./layouts";

const BASENAME = import.meta.env.VITE_BASENAME;

export const App = () => {
    return (
        <AuthProvider>
            <ModalProvider>
                <ToastProvider>
                    <ModalContainer />
                    <ToastContainer />
                    <BrowserRouter basename={BASENAME}>
                        <Routes>
                            <Route path="/register" element={<RegisterPage />} />
                            <Route path="/login" element={<LoginPage />} />
                            
                            {/* Всё остальное - внутри MainLayout */}
                            <Route element={<MainLayout />}>
                                <Route path="/" element={<HomePage />} /> 
                                
                                {/* Публичные страницы */}
                                <Route path="/wiki" element={<WikiListPage />} />
                                <Route path="/wiki/:id" element={<WikiViewPage />} />
                                
                                {/* Только для авторизованных */}
                                <Route element={<RoleBasedLayout requiredRoles={[]} />}>
                                    <Route path="/wiki/new" element={<WikiEditPage />} />
                                    <Route path="/wiki/:id/edit" element={<WikiEditPage />} />
                                    <Route path="/tag-request/create" element={<TagRequestCreatePage />} />
                                </Route>

                                {/* Только для админа */}
                                <Route element={<RoleBasedLayout requiredRoles={["admin"]} />}>
                                    <Route path="/admin/tag-requests" element={<TagRequestsPage />} />
                                    <Route path="/admin/delete-requests" element={<DeleteRequestsPage />} />
                                </Route>

                                <Route path="/forbidden" element={<ForbiddenPage />} />
                                <Route path="*" element={<NotFoundPage />} />
                            </Route>
                        </Routes>
                    </BrowserRouter>
                </ToastProvider>
            </ModalProvider>
        </AuthProvider>
    );
};