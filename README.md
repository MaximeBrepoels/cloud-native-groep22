# cloud-native-groep22

Monorepo for FitApp:

- `cloud-groep22_backend/`: Java Spring Boot backend (Maven)
- `cloud-groep22_frontend/`: Next.js frontend (TypeScript)

## Development

- Start backend: `cd cloud-groep22_backend && ./mvnw spring-boot:run`
- Start frontend: `cd cloud-groep22_frontend && npm run dev`

## Unified Start

- `npm install` (at root, after adding root `package.json`)
- `npm run start:all`