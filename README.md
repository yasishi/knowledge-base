# Knowledge Base Application

This is a knowledge base application built with Spring Boot and React.

## Setup

1. Clone the repository:

   ```
   git clone https://github.com/yasishi/knowledge-base.git
   cd knowledge-base
   ```

2. Start the DynamoDB Local and DynamoDB Admin using Docker Compose:

   ```
   docker-compose up -d
   ```

3. Run the Spring Boot application:

   ```
   ./mvnw spring-boot:run
   ```

4. In a new terminal, navigate to the frontend directory and start the React app:

   ```
   cd frontend
   npm install
   npm start
   ```

5. Open your browser and go to `http://localhost:3000` to view the application.

## Development

- The backend API is available at `http://localhost:8080/api`
- DynamoDB Admin UI is available at `http://localhost:8001`

## Technologies Used

- Backend: Spring Boot, DynamoDB
- Frontend: React, Material-UI
- Database: Amazon DynamoDB (local for development)
