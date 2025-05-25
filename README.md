Here's a clean, professional `README.md` tailored for your **FitApp monorepo** project (`cloud-native-groep22`), combining clear structure, developer onboarding guidance, and tooling notes:

---

# FitApp – Cloud Native Project (Groep 22)

Welcome to the **FitApp** monorepo – a cloud-native fitness tracking application developed by **Groep 22**. This project integrates a Java Spring Boot backend with a TypeScript Next.js frontend.

---

## 📁 Project Structure

```
cloud-native-groep22/
├── cloud-groep22_backend/   # Java Spring Boot backend (Maven)
├── cloud-groep22_frontend/  # Next.js frontend (TypeScript)
└── package.json             # Root-level script orchestration
```

---

## 🚀 Getting Started

### ✅ Prerequisites

* Java 17+
* Node.js (v18 or newer recommended)
* Maven (or use the included `mvnw` wrapper)
* npm (or `yarn`/`pnpm` if preferred)

---

### 🔧 Backend Setup (`Spring Boot`)

```bash
cd cloud-groep22_backend
./mvnw spring-boot:run
```

> This will start the backend on `http://localhost:8080`.

---

### 🎨 Frontend Setup (`Next.js`)

1. Create a `.env` file in `cloud-groep22_frontend/`:

   ```env
   NEXT_PUBLIC_API_URL=http://localhost:8080/api
   ```

2. Start the frontend:

   ```bash
   cd cloud-groep22_frontend
   npm install
   npm run dev
   ```

> The frontend runs on `http://localhost:3000`.

---

## 🔄 Unified Start (Optional)

To run **both frontend and backend** with a single command:

1. Ensure the root-level `package.json` (included in this repo) is present.

2. Install root dependencies:

   ```bash
   npm install
   ```

3. Start both services in parallel:

   ```bash
   npm run start:all
   ```

> Requires `concurrently`, which is included as a dev dependency.

---

## 🛠 Tech Stack

* **Frontend:** Next.js (React, TypeScript)
* **Backend:** Spring Boot (Java, Maven)
* **Communication:** RESTful API
* **Environment:** Local development using `.env` files
* **Monorepo Tools:** Root `package.json` with `concurrently` for orchestration

---
