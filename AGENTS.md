# AGENTS.md

> 本檔提供給 **Codex**（與其他程式碼代理）在本機完成可運作的 BFF + API + Auth + Vue 專案。
> 目標：**在本機完成登入（Authorization Code + PKCE + BFF）、分身分來源（DB / SOAP）驗證、透過 BFF 代理呼叫 API**。

---

## 🔭 專案目標與架構

- 前端：**Vue 3（Vite dev server）**，`http://localhost:4200`
- BFF：**Spring Boot（OAuth2 Client + Proxy）**，`http://localhost:8081`
- API：**Spring Boot（Resource Server / JWT）**，`http://localhost:8080`
- Auth：**Spring Authorization Server（SAS）**，`http://localhost:9000`
- 流程：**Authorization Code + PKCE + BFF**。前端不保存 Token，BFF 握 access/refresh token 並代理 API。
- **驗證規則**：email **含** `@advantech.com` → 走 **SOAP**；否則 → 走 **DB**（Postgres 可留接口，先用 InMemory / H2）。

> 開發模式 **不使用 Nginx**。為避免 CORS/Cookie 跨站問題，**請使用 Vite 開發代理（dev proxy）** 讓瀏覽器視角同源（4200 → 轉送 8081）。

---

## 🗂️ 專案結構（Monorepo）

