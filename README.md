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

### 🔧 Back-end Setup (`Spring Boot`)

```bash
cd cloud-groep22_backend
./mvnw spring-boot:run
```

> This will start the backend on `http://localhost:3000`.

---

### 🎨 Front-end Setup (`Next.js`)

1. Create a `.env` file in `cloud-groep22_frontend/`:

   ```env
   NEXT_PUBLIC_API_URL=http://localhost:3000/api
   ```

2. Start the frontend:

   ```bash
   cd cloud-groep22_frontend
   npm install
   npm run dev
   ```

> The frontend runs on `http://localhost:8080`.

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

---
