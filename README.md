# Genetic Programming AI Platform

A modular full-stack application for training and visualizing Genetic Programming models for dataset classification.

## Project Structure
- `/` - Spring Boot (Java) Backend
- `/frontend` - Next.js (TypeScript) Dashboard

## Prerequisites
- Java 17 or higher
- Maven
- Node.js 18+ and npm

## Commands

### 1. Development (Run Both Simultanously)
This command starts the Spring Boot API (8080) and the Next.js dev server (3000).
```bash
npm run dev
```

### 2. Build for Production
Compiles the Java JAR and builds the Next.js optimized production bundle.
```bash
npm run build
```

### 3. Individual Components
- **Backend Only:** `npm run backend` (or `mvn spring-boot:run`)
- **Frontend Only:** `npm run frontend` (or `cd frontend && npm run dev`)

## API Documentation
Once the backend is running, visit the interactive Swagger UI:
[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## Features
- **Modular AI Architecture:** Add new algorithms by implementing the `Classifier` interface.
- **Dynamic Dashboard:** Plot Best vs. Average fitness in real-time.
- **CSV Support:** Upload custom datasets for training and testing.
- **Swagger Integrated:** Fully documented RESTful API.
