import { useEffect, useMemo, useRef, useState } from 'react'
import { MapContainer, TileLayer, Marker, Popup, useMap } from 'react-leaflet'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'
import { useAuth } from '../context/AuthContext'
import { useApp } from '../context/AppContext'
import { stompService } from '../websocket/StompService'
import { getFriendsStories, acceptMeet, declineMeet } from '../services/api'
import { getCurrentLocation, updateCachedLocation } from '../services/geolocation'
import Avatar from '../components/Avatar'

// Исправление иконок Leaflet в Vite
import markerIcon2x from 'leaflet/dist/images/marker-icon-2x.png'
import markerIcon from 'leaflet/dist/images/marker-icon.png'
import markerShadow from 'leaflet/dist/images/marker-shadow.png'

delete L.Icon.Default.prototype._getIconUrl
L.Icon.Default.mergeOptions({
  iconRetinaUrl: markerIcon2x,
  iconUrl: markerIcon,
  shadowUrl: markerShadow,
})

const friendIcon = (name) =>
  L.divIcon({
    className: 'friend-marker',
    html: `<div class="friend-marker__pin" style="background:hsl(${name.split('').reduce((a, c) => a + c.charCodeAt(0), 0) % 360},70%,50%)">${name[0]?.toUpperCase()}</div>`,
    iconSize: [36, 36],
    iconAnchor: [18, 36],
  })

function MapController({ center }) {
  const map = useMap()
  useEffect(() => {
    if (center) map.setView(center, map.getZoom())
  }, [center, map])
  return null
}

export default function MapPage() {
  const { user } = useAuth()
  const {
    friendLocations,
    refreshLocations,
    proximityAlert,
    clearProximityAlert,
    incomingMeet,
    clearIncomingMeet,
    wsConnected,
  } = useApp()

  const [myPos, setMyPos] = useState(null)
  const [stories, setStories] = useState([])
  const [selectedFriend, setSelectedFriend] = useState(null)
  const [locationError, setLocationError] = useState('')
  const [locationAttempt, setLocationAttempt] = useState(0)
  const watchId = useRef(null)
  const lastPosition = useRef(null)
  const lastSentAt = useRef(0)

  const sendLocation = (position) => {
    const now = Date.now()
    if (now - lastSentAt.current < 5000) return
    if (stompService.sendLocation(position.lat, position.lng, null)) {
      lastSentAt.current = now
    }
  }

  useEffect(() => {
    getFriendsStories().then(setStories).catch(() => {})
  }, [])

  useEffect(() => {
    if (!navigator.geolocation) {
      setLocationError('Этот браузер не поддерживает геолокацию.')
      return undefined
    }

    const unsubscribe = stompService.on('connection', (connected) => {
      if (connected && lastPosition.current) sendLocation(lastPosition.current)
    })

    const applyPosition = (position) => {
      setLocationError('')
      setMyPos(position)
      lastPosition.current = position
      updateCachedLocation(position)
      sendLocation(position)
    }

    const onPosition = (pos) => applyPosition({ lat: pos.coords.latitude, lng: pos.coords.longitude })

    const onPositionError = (error) => {
      setLocationError(
        error.code === 1
          ? 'Разреши доступ к геолокации для этого сайта в браузере.'
          : error.code === 3
            ? 'Браузер не получил координаты. Проверь «Службы геолокации» macOS и разрешение для браузера.'
            : 'Браузер не смог определить местоположение. Попробуй отключить VPN и обновить страницу.'
      )
    }

    getCurrentLocation()
      .then((position) => {
        applyPosition(position)
        watchId.current = navigator.geolocation.watchPosition(
          onPosition,
          onPositionError,
          { enableHighAccuracy: false, maximumAge: 60000, timeout: 30000 }
        )
      })
      .catch(onPositionError)

    return () => {
      unsubscribe()
      if (watchId.current != null) navigator.geolocation.clearWatch(watchId.current)
    }
  }, [locationAttempt])

  const storyGroups = useMemo(() => {
    const map = new Map()
    for (const s of stories) {
      if (!map.has(s.userId)) map.set(s.userId, { userId: s.userId, username: s.username, items: [] })
      map.get(s.userId).items.push(s)
    }
    return [...map.values()]
  }, [stories])

  const defaultCenter = myPos ?? (friendLocations[0] ? [friendLocations[0].lat, friendLocations[0].lng] : [55.7558, 37.6173])

  const handleAcceptMeet = async () => {
    if (!incomingMeet) return
    await acceptMeet(incomingMeet.id)
    clearIncomingMeet()
  }

  const handleDeclineMeet = async () => {
    if (!incomingMeet) return
    await declineMeet(incomingMeet.id)
    clearIncomingMeet()
  }

  return (
    <div className="map-page">
      <div className="map-header">
        <div>
          <h1 className="map-header__title">Cloffy</h1>
          <span className={`ws-badge ${wsConnected ? 'ws-badge--on' : ''}`}>
            {wsConnected ? '● онлайн' : '○ офлайн'}
          </span>
        </div>
        <button className="btn btn--ghost btn--sm" onClick={refreshLocations}>
          Обновить
        </button>
      </div>

      {storyGroups.length > 0 && (
        <div className="stories-bar">
          {storyGroups.map((g) => (
            <button key={g.userId} className="story-circle" type="button">
              <Avatar name={g.username} size={48} />
              <span>{g.username}</span>
            </button>
          ))}
        </div>
      )}

      <div className="map-container">
        <MapContainer center={defaultCenter} zoom={13} className="leaflet-map">
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OSM</a>'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />
          {myPos && (
            <Marker position={[myPos.lat, myPos.lng]}>
              <Popup>Вы ({user?.name})</Popup>
            </Marker>
          )}
          {friendLocations.map((loc) => (
            <Marker
              key={loc.userId}
              position={[loc.lat, loc.lng]}
              icon={friendIcon(loc.username)}
              eventHandlers={{ click: () => setSelectedFriend(loc) }}
            >
              <Popup>
                <strong>{loc.username}</strong>
                {loc.batteryLevel != null && <div>🔋 {loc.batteryLevel}%</div>}
              </Popup>
            </Marker>
          ))}
          <MapController center={myPos ? [myPos.lat, myPos.lng] : null} />
        </MapContainer>
      </div>

      {locationError && (
        <div className="map-notice" role="alert">
          <p>{locationError}</p>
          <button type="button" className="btn btn--ghost btn--sm" onClick={() => setLocationAttempt((n) => n + 1)}>
            Определить ещё раз
          </button>
        </div>
      )}
      {!locationError && !wsConnected && (
        <p className="map-notice">Подключаемся к real-time сервису…</p>
      )}

      {proximityAlert && (
        <div className="banner banner--proximity">
          <span>🔔 {proximityAlert.username ?? 'Друг'} рядом!</span>
          <button type="button" onClick={clearProximityAlert}>✕</button>
        </div>
      )}

      {incomingMeet && (
        <div className="banner banner--meet">
          <div>
            <strong>{incomingMeet.requesterName}</strong> предлагает встретиться
          </div>
          <div className="banner__actions">
            <button type="button" className="btn btn--primary btn--sm" onClick={handleAcceptMeet}>
              Принять
            </button>
            <button type="button" className="btn btn--ghost btn--sm" onClick={handleDeclineMeet}>
              Отклонить
            </button>
          </div>
        </div>
      )}

      {selectedFriend && (
        <div className="sheet-overlay" onClick={() => setSelectedFriend(null)}>
          <div className="sheet" onClick={(e) => e.stopPropagation()}>
            <Avatar name={selectedFriend.username} size={56} />
            <h3>{selectedFriend.username}</h3>
            {selectedFriend.batteryLevel != null && <p>🔋 {selectedFriend.batteryLevel}%</p>}
            {selectedFriend.updatedAt && (
              <p className="text-muted">
                Обновлено: {new Date(selectedFriend.updatedAt).toLocaleString('ru-RU')}
              </p>
            )}
            <button type="button" className="btn btn--ghost" onClick={() => setSelectedFriend(null)}>
              Закрыть
            </button>
          </div>
        </div>
      )}
    </div>
  )
}
