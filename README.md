# Cloffy

> Mobile-first web app for sharing location and coordinating with friends in real time.

Cloffy is a full-stack educational project inspired by the social mechanics of location-sharing apps: private friend network, live map, direct chat, short-lived stories and lightweight coordination tools.

The goal was to practise building a cohesive real-time system rather than a collection of isolated CRUD endpoints: JWT authentication, REST, STOMP WebSocket events, background location processing and a containerised local environment.

## What it does

- Authentication with JWT and profile management
- Search, friend requests, friendship management and blocking
- Map with the latest shared locations of accepted friends
- Direct chat: REST persistence plus STOMP delivery in real time
- Posts, comments and 24-hour stories
- Meet requests and proximity notifications
- Coins, purchasable avatar frames and active-frame selection
- Optional AI chat with location context

## Important product constraints

- Location sharing works only while the web app is open and the browser/OS grants permission. It is not background GPS tracking like a native mobile app.
- Posts and stories currently accept a direct image URL. File upload storage is not implemented.
- The AI assistant is optional: without `DEEPSEEK_API_KEY`, the rest of the app works normally.

## Stack

| Layer | Technologies |
| --- | --- |
| Web client | React, Vite, React Router, Leaflet, SockJS/STOMP, PWA |
| Backend | Java 21, Spring Boot 3, Spring Security, JWT, Spring WebSocket |
| Data & events | PostgreSQL, Flyway, Redis, Kafka |
| Local environment | Docker Compose |

## Architecture

```text
React PWA
  ├── REST + JWT ─────────────► Spring Boot ───► PostgreSQL
  └── STOMP WebSocket ────────► Spring broker
                                      ├── Redis: current locations
                                      └── Kafka: location events
```

## Run locally

### Prerequisites

- Docker Desktop

### Start

```bash
git clone https://github.com/Ek3ot1k/Cloffy.git
cd Cloffy
cp .env.example .env
docker compose up --build
```

Open:

- Web app: <http://localhost:5173>
- API documentation: <http://localhost:8080/swagger-ui/index.html>

On the first run, Flyway creates the schema and seeds the avatar-frame shop. Register two accounts in separate browser profiles, add them as friends and allow location access to try the map and real-time scenarios.

To stop the environment:

```bash
docker compose down
```

## Project structure

```text
.
├── src/                 # Spring Boot application
├── web/                 # React PWA client
├── docker-compose.yml   # PostgreSQL, Redis, Kafka, API and web client
├── Dockerfile           # Backend image
└── .env.example         # Safe configuration template
```

## Backend verification

The local Compose environment has been smoke-tested from an empty database:

- registration and JWT authentication;
- friend request, acceptance and blocking;
- chat persistence and delivery API;
- posts, stories and own-post feed visibility;
- frame purchase, equip and wallet consistency;
- Flyway bootstrap and seeded shop data.

## Author

Built by Amin Huseynov as a full-stack backend learning project.
