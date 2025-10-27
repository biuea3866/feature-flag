# Quickstart

This guide provides instructions on how to get the project running locally for development and testing.

## Prerequisites

- Docker
- Docker Compose

## Running the Application

1.  **Build the application**:

    ```bash
    ./gradlew build
    ```

2.  **Start the services using Docker Compose**:

    ```bash
    docker-compose up --build
    ```

This will start the following services:
- The Spring Boot backend application
- A PostgreSQL database
- A Redis instance

The backend application will be available at `http://localhost:8080`.
