# Cloffy Web — mobile-first PWA

Веб-клиент для [Cloffy](../Rest/README.md) — mobile-first PWA для координации с друзьями в реальном времени.

Геолокация передаётся только пока пользователь открыл приложение и дал явное разрешение. Это сознательное ограничение web-платформы: сайт не притворяется нативным приложением с постоянным фоновым GPS-трекингом.

## Стек

- **React 19** + **Vite**
- **Leaflet** — интерактивная карта
- **SockJS + STOMP** — WebSocket (локация, чат, встречи)
- Тёмная тема, mobile-first UI
- PWA-манифест и offline app shell

## Возможности

| Раздел | Что умеет |
|--------|-----------|
| 🗺️ Карта | Геолокация в браузере, друзья на карте, сторис, уведомления о близости и встречах |
| 👫 Друзья | Поиск, запросы, принятие, блокировка |
| 💬 Чат | Диалоги и сообщения в реальном времени |
| 📸 Лента | Посты друзей, лайки, комментарии |
| 👤 Профиль | Образование, истории, кошелёк, магазин рамок |
| 🤖 AI | Ассистент с опциональной геолокацией |

## Запуск

### Один запуск для всего проекта

Из корня репозитория:

```bash
docker compose up --build
```

Открой **http://localhost:5173**. Compose сам запускает PostgreSQL, Redis, Kafka, Spring Boot и Vite.

### Отдельный запуск для разработки

#### 1. Запусти backend-инфраструктуру

```bash
docker compose up postgres redis kafka backend
```

Бэкенд должен быть доступен на `http://localhost:8080`.

#### 2. Запусти frontend

```bash
cd web
npm install
npm run dev
```

Открой **http://localhost:5173** в браузере.

Vite автоматически проксирует `/api`, `/friends` и `/ws` на бэкенд.

### Подключение к удалённому backend

Создай файл `.env`:

```env
VITE_API_URL=https://cloffy-production.up.railway.app
VITE_WS_URL=https://cloffy-production.up.railway.app/ws
```

```bash
npm run build
npm run preview
```

## Структура

```
web/
├── src/
│   ├── api/client.js       # HTTP-клиент с JWT
│   ├── services/api.js     # все REST-вызовы
│   ├── websocket/          # STOMP WebSocket
│   ├── context/            # Auth + глобальное состояние
│   ├── pages/              # экраны приложения
│   └── components/         # UI-компоненты
└── vite.config.js          # proxy на localhost:8080
```

## Примечания

- Для карты браузер запросит разрешение на геолокацию. За пределами `localhost` приложение должно работать через HTTPS.
- Посты и истории принимают **URL изображения** (загрузка файлов — отдельная задача; можно использовать любой публичный URL).
- WebSocket использует endpoint `/ws` (SockJS), как предусмотрено в `WebSocketConfig` бэкенда.
