# FitApp - Cloud-Native Fitness Tracking Application

## About the Project

FitApp is a full-stack fitness tracking web application that helps users log their workouts, monitor their progress, and achieve their fitness goals through data-driven insights. Built with a **serverless, cloud-native architecture** on Microsoft Azure, this project demonstrates modern software engineering practices including microservices, NoSQL databases, caching strategies, and automated CI/CD pipelines.

### What Problem Does It Solve?

Many fitness enthusiasts struggle to track their workout progress consistently and lack visibility into their long-term fitness trends. FitApp provides:
- **Centralized Workout Logging**: Record exercises, sets, reps, and weights in one place
- **Progress Visualization**: Interactive charts that show strength gains and bodyweight trends over time
- **Personalized Tracking**: Secure user accounts with individual workout histories
- **Accessible Anywhere**: Cloud-hosted static site with globally distributed content delivery

### Technical Highlights

This project showcases:
- **Serverless Architecture**: Cost-effective, auto-scaling Azure Functions backend
- **Modern Frontend**: Next.js with React 19 and TypeScript for type-safe development
- **NoSQL Database**: Azure Cosmos DB for flexible, scalable data storage
- **Caching Layer**: Redis implementation for improved API performance
- **Automated Deployment**: GitHub Actions CI/CD pipelines for continuous delivery
- **Security**: JWT-based authentication with secure password hashing

## Key Features

- **User Authentication**: Secure user registration and login with JWT-based authentication
- **Workout Tracking**: Log exercises, sets, and workout sessions
- **Progress Monitoring**: Track your fitness progress over time with visual charts
- **Bodyweight Tracking**: Monitor weight changes and trends
- **Data Visualization**: Interactive charts powered by Chart.js
- **Cloud-Native Architecture**: Serverless backend with Azure Functions and Cosmos DB

## Demo & Screenshots

> **Note**: This application was built as part of a Cloud Native Engineering course to demonstrate modern cloud architecture patterns and full-stack development skills.

**User Experience Flow:**
1. Users create an account and securely log in
2. Add exercises to their personal library (e.g., bench press, squats, deadlifts)
3. Log workout sessions with sets, reps, and weights
4. Track bodyweight measurements over time
5. View interactive charts showing progress trends and personal records

**Architecture Highlights:**
- Frontend deployed as a static site on Azure Blob Storage for cost-efficiency and global CDN distribution
- Backend uses serverless Azure Functions that scale automatically based on demand
- Cosmos DB provides multi-region replication and low-latency data access
- Redis caching layer reduces database queries and improves response times

## Tech Stack

### Frontend
- **Framework**: Next.js 15 (React 19)
- **Language**: TypeScript
- **Styling**: Tailwind CSS 4
- **Authentication**: NextAuth.js
- **Charts**: Chart.js with react-chartjs-2
- **Deployment**: Azure Blob Storage (Static Website Hosting)

### Backend
- **Runtime**: Azure Functions (Java 21)
- **Database**: Azure Cosmos DB (NoSQL)
- **Caching**: Azure Cache for Redis
- **Authentication**: JWT tokens with Spring Security
- **Build Tool**: Maven

### DevOps
- **CI/CD**: GitHub Actions
- **Cloud Platform**: Microsoft Azure
- **Version Control**: Git

## Project Structure

```
cloud-native-groep22/
├── cloud-groep22_frontend/     # Next.js frontend application
│   ├── components/             # React components
│   ├── pages/                  # Next.js pages
│   ├── services/               # API service layer
│   ├── styles/                 # Global styles
│   ├── types/                  # TypeScript type definitions
│   └── utils/                  # Utility functions
│
├── cloud-groep22_functions/    # Azure Functions backend
│   └── src/main/java/cloudnative/fitapp/
│       ├── functions/          # HTTP-triggered Azure Functions
│       ├── service/            # Business logic layer
│       ├── dto/                # Data transfer objects
│       ├── security/           # JWT & authentication utilities
│       ├── cache/              # Redis caching layer
│       └── enums/              # Enumerations
│
└── .github/workflows/          # CI/CD pipelines
```

## Getting Started

### Prerequisites

- **Node.js** 20.x or higher
- **Java** 21 (JDK)
- **Maven** 3.x
- **Azure CLI** (for deployment)
- **npm** or **yarn**

### Installation

1. Clone the repository:
```bash
git clone https://github.com/MaximeBrepoels/cloud-native-groep22.git
cd cloud-native-groep22
```

2. Install root dependencies:
```bash
npm install
```

3. Install frontend dependencies:
```bash
cd cloud-groep22_frontend
npm install
cd ..
```

### Development

#### Start Both Frontend and Backend
```bash
npm run start:all
```

#### Start Frontend Only
```bash
npm run start:frontend
# or
cd cloud-groep22_frontend && npm run dev
```
The frontend will be available at `http://localhost:8080`

#### Start Backend Only
```bash
npm run start:backend
# or
cd cloud-groep22_functions && mvn azure-functions:run
```

## Deployment

The application uses automated CI/CD pipelines via GitHub Actions:

### Frontend Deployment
- **Trigger**: Push to `master` branch with changes in `cloud-groep22_frontend/**`
- **Target**: Azure Blob Storage (Static Website)
- **Workflow**: `.github/workflows/upload-static-site.yml`

### Backend Deployment
- **Trigger**: Push to `master` branch with changes in `cloud-groep22_functions/**`
- **Target**: Azure Functions App
- **Workflow**: `.github/workflows/deploy-azure-functions.yml`

## API Endpoints

The backend exposes RESTful API endpoints through Azure Functions:

- **Authentication**: User registration, login, JWT token management
- **Users**: User profile management and password updates
- **Workouts**: CRUD operations for workout sessions
- **Exercises**: Manage exercise library
- **Sets**: Track individual exercise sets
- **Progress**: Retrieve fitness progress data
- **Bodyweight**: Track and retrieve bodyweight measurements

## Environment Variables

### Frontend
Create a `.env.local` file in `cloud-groep22_frontend/`:
```env
NEXT_PUBLIC_API_URL=https://your-function-app-name.azurewebsites.net/api
```

### Backend
Azure Functions configuration (set in Azure Portal or `local.settings.json`):
```json
{
  "Values": {
    "COSMOS_ENDPOINT": "your-cosmos-endpoint",
    "COSMOS_KEY": "your-cosmos-key",
    "COSMOS_DATABASE": "your-database-name",
    "JWT_SECRET": "your-jwt-secret",
    "REDIS_HOST": "your-redis-host",
    "REDIS_PASSWORD": "your-redis-password"
  }
}
```

## Technologies & Dependencies

### Key Libraries

**Frontend:**
- `next`: ^15.3.2
- `react`: ^19.1.0
- `next-auth`: ^4.24.11
- `chart.js`: ^4.4.9
- `axios`: ^1.9.0
- `tailwindcss`: ^4

**Backend:**
- Azure Functions Java Library: 3.0.0
- Azure Cosmos DB SDK: 4.53.0
- JJWT (JSON Web Tokens): 0.11.5
- Jedis (Redis Client): 5.1.0
- Jackson (JSON Processing): 2.15.2

## What I Learned

This project demonstrates practical experience with:

**Cloud Architecture & DevOps:**
- Designing and implementing serverless applications on Azure
- Setting up CI/CD pipelines with GitHub Actions
- Managing cloud resources (Functions, Cosmos DB, Redis, Blob Storage)
- Implementing automated testing and deployment workflows

**Full-Stack Development:**
- Building type-safe React applications with TypeScript
- Creating RESTful APIs with Azure Functions (Java)
- Implementing JWT-based authentication and authorization
- Integrating multiple Azure services in a cohesive application

**Software Engineering Best Practices:**
- Monorepo structure for managing multiple services
- Environment-based configuration management
- Security practices (password hashing, token-based auth, CORS)
- Performance optimization with caching strategies

## Future Enhancements

Potential improvements for this project:
- Add social features (share workouts, follow friends)
- Implement workout templates and recommended exercises
- Add mobile app using React Native
- Integrate with fitness wearables APIs
- Implement advanced analytics and AI-powered workout recommendations
- Add comprehensive unit and integration tests

## License

This project is part of a Cloud Native Engineering course assignment.

## Contact

**Maxime Brepoels** - [GitHub](https://github.com/MaximeBrepoels)

Feel free to reach out if you have questions about this project or want to discuss cloud-native development!

## Acknowledgments

- Built as part of Cloud Native Engineering coursework
- Demonstrates modern cloud-native architecture patterns
- Implements industry-standard security and DevOps practices