export default {
    extends: ["@commitlint/config-conventional"],
    rules: {
        "type-enum": [
            2,
            "always",
            ["feat", "fix", "docs", "style", "refactor", "test", "chore", "build", "ci", "perf", "revert"],
        ],
        "subject-case": [2, "never", ["upper-case"]],
        "header-max-length": [2, "always", 72],
    },
};
