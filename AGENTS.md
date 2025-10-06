# Repository Guidelines

## Project Structure & Module Organization
The workspace is split by concern: `front-end/srm-front-end` houses the Vue 3 app with feature folders (`src/views`, `src/stores`, `src/services`), while `back-end/srm` is a multi-module Spring Boot build (`srm-api`, `srm-auth`, `srm-domain`, `srm-persistence`, `srm-scheduler`). Tests live beside sources under `src/test`. Shared deployment assets sit in `infra` (`infra/app/docker-compose.yml` for local API + DB, `infra/cicd` for pipeline stacks).

## Build, Test, and Development Commands
Front-end: run `npm install` once, then `npm run dev` for Vite, `npm run build` for production bundles, `npm run test:unit` with Vitest, `npm run lint`, and `npm run format`. Back-end: from `back-end/srm`, use `./gradlew bootRun` to start the API, `./gradlew test` for the full suite, and `./gradlew build` to produce deployable jars/Docker contexts. `docker compose -f infra/app/docker-compose.yml up --build` will start the API against the bundled SQL Server image.

## Coding Style & Naming Conventions
TypeScript and Vue files follow `.editorconfig` (2-space indent, LF endings) and Prettier (`semi: false`, single quotes). Components and stores use PascalCase filenames; composables, services, and Pinia stores use camelCase exports. Lint with the provided `eslint.config.ts` before sending changes. Java code targets Java 21, packages under `com.advantech.srm.*`, and class/files should remain PascalCase with Spring stereotypes (`*Controller`, `*Service`) aligned to their module.

## Testing Guidelines
Create or update Vitest specs alongside features (`src/**/__tests__` or `*.spec.ts`). Mock external calls with Axios mocks and assert rendered DOM via Testing Library helpers. On the backend, add JUnit 5 tests in `src/test/java` mirroring package structure with `*Test` suffixes; prefer `@SpringBootTest` only when integration coverage is required. All new business logic must include focused unit tests, and existing suites should pass via `npm run test:unit` and `./gradlew test` before opening a PR.

## Commit & Pull Request Guidelines
Adopt the repository's Conventional Commit style--type plus optional scope (e.g., `feat(vendor): add vendor creation main page`) or bracketed type `[feat] message` when touching multiple areas. Keep messages in the imperative and describe the behaviour change. Pull requests should include a concise summary, linked Jira/GitHub issue, screenshots or curl samples for UI/API changes, and the commands used for validation. Request reviewers for both front-end and back-end when changes cross the boundary.

## Infrastructure Notes
Secrets belong in local `.env` files (see `infra/app/.env` template) and must never be committed. When adding services, extend the relevant compose file and document exposed ports in this guide.
