Digipay

Digipay is a robust, secure, and modern digital payment web application built with a microservice-style architecture. It allows users to manage their digital wallets, securely transfer funds, and view their transaction history in real-time.

Features
--------
- Secure Authentication: JWT-based stateless authentication with strict password policies and Zod-enforced frontend validation.
- Wallet Management: Users receive a complimentary Rs 100 sign-up bonus upon registration.
- Money Transfers: Send money securely to other registered users.
- Risk Scoring: Built-in AI-driven transaction risk assessment to prevent fraud and ensure secure transfers.
- Real-time Dashboard: Real-time polling mechanisms ensure the transaction history and wallet balances stay perfectly synced.
- Modern UI/UX: A stunning glassmorphic design built with React, Vite, and custom CSS without relying on heavy frameworks.

Tech Stack
----------
- Frontend: React (Vite), React Router, React Hook Form, Zod, Lucide React
- Backend: Java, Spring Boot, Spring Security (JWT), Spring Data JPA, Hibernate
- Database: PostgreSQL
- Deployment: Docker & Docker Compose

Prerequisites
-------------
- Docker
- Docker Compose

Getting Started
---------------
1. Clone the repository
   git clone https://github.com/i0asad/digipay_app.git
   cd digipay_app

2. Start the application
   Run the following command to build the Docker images and start all the services (Frontend, Backend, and Database) in detached mode:
   docker-compose up -d --build

3. Access the application
   Once the containers are up and running, open your browser and navigate to:
   - Frontend UI: http://localhost:5173
   - Backend API: http://localhost:8080/api

4. Stopping the application
   docker-compose down

Project Structure
-----------------
- /frontend: Contains the React application code, styles, and Vite configuration.
- /digipay: Contains the Spring Boot backend service, security filters, and database entities.
- docker-compose.yml: Orchestrates the PostgreSQL database, Spring Boot backend, and React frontend containers.

Security & Architecture Notes
-----------------------------
- All API requests from the frontend are intercepted by Axios to automatically attach JWT tokens.
- Invalid tokens or expired sessions will automatically log the user out to protect their account.
- The PostgreSQL database is mapped internally. For local development outside of Docker, remember to use port 5433 as defined in the docker-compose.yml.
