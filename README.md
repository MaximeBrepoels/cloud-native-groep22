# FitApp – Cloud Native Project (Groep 22)

Welcome to the **FitApp** monorepo – a cloud-native fitness tracking application developed by **Groep 22**. This project integrates a Java Spring Boot back-end with a TypeScript Next.js front-end.

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

> This will start the back-end on `http://localhost:3000`.

---

### 🎨 Front-end Setup (`Next.js`)

1. Create a `.env` file in `cloud-groep22_frontend/`:

   ```env
   NEXT_PUBLIC_API_URL=http://localhost:3000/api
   ```

2. Start the front-end:

   ```bash
   cd cloud-groep22_frontend
   npm install
   npm run dev
   ```

> The front-end runs on `http://localhost:8080`.

---

## 🔄 Unified Start

Run **both front-end and back-end** with a single command:

- `npm install` (at root, `cloud-native-groep22/`)
- `npm run start:all` (at root)

---

## 🛠 Tech Stack

* **Front-end:** Next.js (React, TypeScript)
* **Back-end:** Spring Boot (Java, Maven)
* **Communication:** RESTful API

---
