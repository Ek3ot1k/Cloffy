// Базовый URL API. Пустая строка = тот же origin (Vite proxy в dev).
export const API_BASE = import.meta.env.VITE_API_URL ?? ''

// SockJS endpoint для WebSocket
export function getWsUrl() {
  if (import.meta.env.VITE_WS_URL) {
    return import.meta.env.VITE_WS_URL
  }
  if (import.meta.env.VITE_API_URL) {
    return `${import.meta.env.VITE_API_URL}/ws`
  }
  return `${window.location.origin}/ws`
}
