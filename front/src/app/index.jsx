import {
    DeleteRequestsPage,
    ForbiddenPage,
    LoginPage,
    NotFoundPage,
    TagRequestCreatePage,
    TagRequestsPage,
    WikiEditPage,
    WikiListPage, WikiViewPage,
} from "@pages/index";
import { AuthProvider } from "@shared/auth";
import { ModalContainer, ModalProvider } from "@shared/modal";
import { ToastProvider } from "@shared/toast";
import { ToastContainer } from "@shared/toast/ui";
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { AuthLayout, MainLayout, RoleBasedLayout } from "./layouts";

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
                            <Route element={<AuthLayout />}>
                                <Route element={<MainLayout />}>
                                    <Route path="/login" element={<LoginPage />} />
                                    <Route path="/" element={<Navigate to="/wiki" replace />} />
                                    
                                    {/* Wiki Pages */}
                                    <Route path="/wiki" element={<WikiListPage />} />
                                    <Route path="/wiki/new" element={<WikiEditPage />} />
                                    <Route path="/wiki/:id" element={<WikiViewPage />} />
                                    <Route path="/wiki/:id/edit" element={<WikiEditPage />} />
                                    
                                    {/* Tag Request */}
                                    <Route path="/tag-request/create" element={<TagRequestCreatePage />} />

                                    {/* Admin Only */}
                                    <Route element={<RoleBasedLayout requiredRoles={["admin"]} />}>
                                        <Route path="/admin/tag-requests" element={<TagRequestsPage />} />
                                        <Route path="/admin/delete-requests" element={<DeleteRequestsPage />} />
                                    </Route>

                                    <Route path="/forbidden" element={<ForbiddenPage />} />
                                    <Route path="*" element={<NotFoundPage />} />
                                </Route>
                            </Route>
                        </Routes>
                    </BrowserRouter>
                </ToastProvider>
            </ModalProvider>
        </AuthProvider>
    );
};