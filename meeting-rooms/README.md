# Meeting Rooms (Unisales)

## Prerequisites

Before you begin, ensure you have the following installed on your machine:

- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/)
- [Maven](https://maven.apache.org/install.html) (for building the application image)

## Getting Started

Follow these steps to set up and run the project.

### 1. Build the Docker Image

First, make sure you have been initialized Docker, then, to build the Docker image for this project, use the following Maven command:

```bash
mvn clean spring-boot:build-image
```

### 2. Run the Application

After building the image, start the application using docker-compose.yml. Run the command below:

```bash
docker-compose up -d
```

This will initialize all required containers and services defined in the docker-compose.yml file.

### 3. Stopping the Application

To stop the application, simply run:

```bash
docker-compose down
```

Or, to stop clearing volumes:

```bash
docker-compose down -v
```

This command will stop and remove the containers created for the project.

### (+) Access documentation

Once the application is running, you can access the Swagger API documentation at:

```http request
http://localhost:8080/docs.html
```

This endpoint provides an interactive UI for exploring and testing the API endpoints.

### Note on Authentication for Testing Purposes

This is an example and testing purposes project. The endpoints that typically require user authentication are designed to simulate a user by including a custom Host-Id header. This header, added as @RequestHeader("Host-Id") in requests, simulates a Bearer token, which would otherwise result in a user ID for authenticated access.