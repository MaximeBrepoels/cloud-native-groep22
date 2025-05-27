# cloud-native-groep22

Monorepo for FitApp:

- `cloud-groep22_backend/`: Java Spring Boot backend (Maven)
- `cloud-groep22_frontend/`: Next.js frontend (TypeScript)

## Development Setup

### 1. Configure Azure Cosmos DB Environment Variables

Before running the backend, set the following environment variables in your terminal:

```bash
export AZURE_COSMOS_URI="contact me for the uri"
export AZURE_COSMOS_KEY="contact me for the key"
export AZURE_COSMOS_DATABASE_NAME="cloud-native-groep22-db"

echo "URI: $AZURE_COSMOS_URI"
echo "KEY: $AZURE_COSMOS_KEY"
echo "DB: $AZURE_COSMOS_DATABASE_NAME"
```

### 2. Start the Backend

- For cleanup operations (optional):

  ```bash
  ./mvnw spring-boot:run -Dspring-boot.run.profiles=cleanup
  ```

- To start the backend normally:

  ```bash
  ./mvnw spring-boot:run
  ```

### 3. Start the Frontend

```bash
cd cloud-groep22_frontend
npm run dev
```

## Unified Start (if using a root package.json)

- Install dependencies at the root:

  ```bash
  npm install
  ```

- Start all services:

  ```bash
  npm run start:all
  ```
