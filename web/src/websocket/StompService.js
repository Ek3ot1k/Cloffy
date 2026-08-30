import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { getToken } from '../api/client'
import { getWsUrl } from '../config'

class StompService {
  constructor() {
    this.client = null
    this.listeners = {
      location: new Set(),
      message: new Set(),
      nearby: new Set(),
      meetRequest: new Set(),
      meetUpdate: new Set(),
      connection: new Set(),
    }
  }

  on(event, callback) {
    this.listeners[event]?.add(callback)
    return () => this.listeners[event]?.delete(callback)
  }

  emit(event, data) {
    this.listeners[event]?.forEach((cb) => cb(data))
  }

  connect() {
    const token = getToken()
    if (!token) return

    this.disconnect()

    this.client = new Client({
      webSocketFactory: () => new SockJS(getWsUrl()),
      connectHeaders: { Authorization: `Bearer ${token}` },
      reconnectDelay: 5000,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
      onConnect: () => {
        this.emit('connection', true)
        this.subscribe('/user/queue/locations', 'location')
        this.subscribe('/user/queue/messages', 'message')
        this.subscribe('/user/queue/nearby', 'nearby')
        this.subscribe('/user/queue/meet-requests', 'meetRequest')
        this.subscribe('/user/queue/meet-updates', 'meetUpdate')
      },
      onDisconnect: () => this.emit('connection', false),
      onStompError: () => this.emit('connection', false),
    })

    this.client.activate()
  }

  subscribe(destination, event) {
    this.client?.subscribe(destination, (msg) => {
      try {
        const data = JSON.parse(msg.body)
        this.emit(event, data)
      } catch {
        /* ignore */
      }
    })
  }

  disconnect() {
    if (this.client) {
      this.client.deactivate()
      this.client = null
    }
  }

  sendLocation(lat, lng, batteryLevel) {
    if (!this.client?.connected) return false
    this.client.publish({
      destination: '/app/location',
      body: JSON.stringify({ lat, lng, batteryLevel }),
    })
    return true
  }

  sendChatMessage(receiverId, content) {
    if (!this.client?.connected) return false
    this.client.publish({
      destination: '/app/chat',
      body: JSON.stringify({ receiverId, content }),
    })
    return true
  }

  get isConnected() {
    return this.client?.connected ?? false
  }
}

export const stompService = new StompService()
