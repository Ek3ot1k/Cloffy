# Cloffy — Real-Time Location Sharing App

> A Zenly / Blink-inspired location sharing app for iOS, built with Spring Boot and Swift.

---

## What is Cloffy?

Cloffy lets you see where your friends are in real time on a map — just like Zenly used to. You get live locations, friend requests, stories, chat, and proximity alerts when a friend is nearby.

The app was built from scratch as a personal project to learn full-stack mobile development — from JWT auth and WebSockets to native iOS UI.

---

## Features

- 📍 **Live location sharing** — friends appear on the map in real time via WebSocket (STOMP)
- 💬 **Real-time chat** — direct messages delivered instantly via WebSocket
- 👫 **Friend system** — send, accept, decline friend requests; search users by name
- 📖 **Stories** — 24-hour stories visible on the map carousel (like Snapchat/Zenly)
- 🔔 **Proximity alerts** — get notified when a friend is within a set radius
- 🤝 **Meet requests** — send a "want to meet?" request to a friend with map coordinates
- 📝 **Posts feed** — share moments with your friends (image + caption)
- 🤖 **AI Assistant** — ask questions with optional location context (powered by DeepSeek)
- 🪙 **Wallet & Shop** — earn coins, buy and equip avatar frames
- 🔒 **Block system** — block and unblock users
- 🎓 **Education profile** — add school/university, find classmates
- 🌍 **Localization** — full Russian and English support

---

## Tech Stack

### Backend
| Technology | Purpose |
|---|---|
| Java 21 + Spring Boot 3 | REST API |
| Spring Security + JWT | Authentication |
| Spring WebSocket (STOMP) | Real-time location & chat |
| PostgreSQL + Flyway | Database & migrations |
| Redis | Caching & proximity tracking |
| Docker | Containerization |
| Railway | Cloud deployment |

### Mobile (iOS)
| Technology | Purpose |
|---|---|
| Swift + SwiftUI | Native iOS UI |
| MapKit | Interactive map |
| CoreLocation | Device GPS |
| URLSessionWebSocketTask | STOMP over WebSocket |

---

## Architecture

```
iOS App (SwiftUI)
    │
    ├── REST API ──────► Spring Boot ──► PostgreSQL
    │   (JWT auth)                  └──► Redis
    │
    └── WebSocket ─────► STOMP Broker
                              ├── /user/queue/locations
                              ├── /user/queue/messages
                              ├── /user/queue/nearby
                              └── /user/queue/meet-requests
```

---

## API

Full interactive API documentation available at:

```
https://cloffy-production.up.railway.app/swagger-ui/index.html
```

Key endpoint groups:
- `POST /api/v1/auth/login` — authenticate and get JWT
- `GET /api/v1/location/friends` — get all friends' locations
- `GET /api/v1/posts/friends` — friends' posts feed
- `POST /api/v1/ai/chat` — AI assistant with optional geo context
- `WS /ws-ios` — WebSocket endpoint for real-time events

---

## Running Locally

### Prerequisites
- Java 21
- PostgreSQL
- Redis

### Steps

```bash
git clone https://github.com/Ek3ot1k/Cloffy.git
cd Cloffy

# Set environment variables
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/cloffy
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
export JWT_SECRET=your_secret

./mvnw spring-boot:run
```

---


## Author

Built by Amin Huseynov — a student learning backend development by shipping real products.

- GitHub: [@Ek3ot1k](https://github.com/Ek3ot1k)
