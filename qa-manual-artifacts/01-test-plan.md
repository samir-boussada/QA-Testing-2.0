# 📋 Master Test Plan (MTP) — NovaShop E-Commerce

**Project:** NovaShop Full-Stack E-Commerce Platform  
**Author:** Samir Boussada (QA & SDET Engineer)  
**Target Architecture:** Java 17 / Spring Boot 3.3.5 / Spring Security / MySQL / Playwright  
**Test Strategy:** Pyramid Testing (Unit + Integration + E2E + Manual Functional)  
**Target Release:** v1.0.0-Production  

---

## 1. Executive Summary & Quality Strategy
The objective of this Test Plan is to define the testing strategy, test coverage, execution environment, and quality acceptance criteria for the **NovaShop E-Commerce Platform**. 

To deliver high product confidence prior to Hostinger production hosting, testing is conducted across **four distinct layers**:
1. **Unit Testing (JUnit 5 / Mockito):** Business logic, cart discount calculations, and inventory limits.
2. **Integration Testing (Spring MockMvc):** REST API security, session persistence, and transactional order fulfillment.
3. **Automated E2E Testing (Playwright):** Full browser journeys (Guest $\to$ Cart $\to$ Auth $\to$ Checkout $\to$ Order Confirmation).
4. **Manual Functional & Exploratory Testing:** Edge cases, boundary value analysis (BVA), responsive design, and administrative controls.

---

## 2. Test Scope & Feature Matrix

### In-Scope Modules:
* **User Authentication & RBAC:** Customer registration, login, session timeout, role restriction (`ROLE_CUSTOMER` vs. `ROLE_ADMIN`).
* **Product Catalog & Search:** Live product listing, category filtering, keyword search, out-of-stock badges.
* **Shopping Cart & Pricing Engine:** Subtotal calculations, free shipping threshold ($100), promo codes (`SAVE10`, `NOVA20`), quantity controls.
* **Order Checkout & Fulfillment:** Address and phone validation, transactional stock decrement, order number generation.
* **Admin Management Portal:** Real-time revenue metrics, inventory CRUD, and order tracking.

### Out-of-Scope:
* Third-party live payment gateways (simulated/mocked checkout).
* Multi-currency exchange rate conversions (single currency USD/EUR).

---

## 3. Test Environments & Database Profiles

| Layer | Environment | Database | Tools Used |
| :--- | :--- | :--- | :--- |
| **Unit Tests** | Local JVM | Isolated Mockito Stubs | JUnit 5, AssertJ, Mockito |
| **Integration Tests** | Spring Test Context | In-Memory H2 (`application-test.properties`) | Spring Boot Test, MockMvc |
| **E2E Automation** | Headless / Headed Browsers | Local Spring Boot (`:8080`) | Playwright, TypeScript |
| **Production Staging** | Hostinger Cloud VPS | MySQL 8.0 (`application-prod.properties`) | Nginx, Systemd, SSL |

---

## 4. Acceptance & Exit Criteria
* **Unit & Integration:** 100% test pass rate with zero compilation errors (`mvn test`).
* **E2E Automation:** 100% pass across Chromium, Firefox, and WebKit without flaky timeouts.
* **Defect Policy:** Zero open Blocker/Critical defects; any Major defects must have documented workarounds.
