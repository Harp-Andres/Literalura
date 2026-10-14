import { defineConfig, devices } from "@playwright/test";

const PUERTO = 4173;
const URL_SITIO = `http://127.0.0.1:${PUERTO}/Literalura/`;

export default defineConfig({
    testDir: "./e2e",
    fullyParallel: true,
    forbidOnly: Boolean(process.env.CI),
    reporter: process.env.CI ? [["list"], ["html", { open: "never" }]] : "list",
    use: {
        baseURL: URL_SITIO,
        trace: "retain-on-failure"
    },
    projects: [
        { name: "chromium", use: { ...devices["Desktop Chrome"] } }
    ],
    webServer: {
        command: "node servidor-estatico.js",
        url: URL_SITIO,
        env: { PORT: String(PUERTO) },
        reuseExistingServer: !process.env.CI
    }
});
