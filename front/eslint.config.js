import js from "@eslint/js";
import eslintConfigPrettier from "eslint-config-prettier/flat";
import * as pluginImport from "eslint-plugin-import";
import reactPlugin from "eslint-plugin-react";
import reactHooks from "eslint-plugin-react-hooks";
import reactRefresh from "eslint-plugin-react-refresh";
import globals from "globals";
import viteConfigObj from "./vite.config.js";

export default [
    { ignores: ["dist", "vite.config.js"] },
    {
        files: ["**/*.{js,jsx}"],
        languageOptions: {
            globals: globals.browser,
            parserOptions: {
                ecmaVersion: 2020,
            },
        },
        settings: {
            react: {
                version: "detect",
            },
            "import/resolver": {
                node: {
                    extensions: [".js", ".jsx", ".ts", ".tsx"],
                },
                vite: {
                    viteConfig: viteConfigObj,
                },
            },
        },
        plugins: {
            react: reactPlugin,
            "react-hooks": reactHooks,
        },
    },
    js.configs.recommended,
    pluginImport.flatConfigs.recommended,
    reactRefresh.configs.recommended,
    reactPlugin.configs.flat.recommended,
    reactPlugin.configs.flat["jsx-runtime"],
    eslintConfigPrettier,
    {
        rules: {
            ...reactHooks.configs.recommended.rules,
            "no-unused-vars": ["error", { varsIgnorePattern: "^[A-Z_]" }],
            "react-refresh/only-export-components": ["warn", { allowConstantExport: true }],
            "react/prop-types": ["off"],
        },
    },
];
