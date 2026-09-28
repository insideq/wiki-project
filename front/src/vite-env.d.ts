interface ImportMetaEnv {
    readonly VITE_API_URL: string;
    readonly VITE_BASENAME: string;
}

interface ImportMeta {
    readonly env: ImportMetaEnv;
}
