import { defineConfig } from 'cypress'

export default defineConfig({
  video: true,
  screenshotOnRunFailure: true,
  retries: { runMode: 1, openMode: 0 },
  reporter: 'junit',
  reporterOptions: {
    mochaFile: 'cypress/reports/e2e-[hash].xml',
    toConsole: true,
  },
  e2e: {
    baseUrl: process.env.CYPRESS_BASE_URL || 'http://localhost:5174',
    supportFile: false,
    specPattern: 'cypress/e2e/**/*.cy.js',
  },
})
