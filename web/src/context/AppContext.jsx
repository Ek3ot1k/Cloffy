import { createContext, useCallback, useContext, useEffect, useState } from 'react'
import { stompService } from '../websocket/StompService'
import { getFriendsLocations } from '../services/api'
import { useAuth } from './AuthContext'

const AppContext = createContext(null)

export function AppProvider({ children }) {
  const { user } = useAuth()
  const [friendLocations, setFriendLocations] = useState([])
  const [incomingMessage, setIncomingMessage] = useState(null)
  const [proximityAlert, setProximityAlert] = useState(null)
  const [incomingMeet, setIncomingMeet] = useState(null)
  const [meetUpdate, setMeetUpdate] = useState(null)
  const [wsConnected, setWsConnected] = useState(false)
  const [toast, setToast] = useState(null)

  const showToast = useCallback((message, type = 'info') => {
    setToast({ message, type })
    setTimeout(() => setToast(null), 3500)
  }, [])

  const refreshLocations = useCallback(async () => {
    try {
      const locs = await getFriendsLocations()
      setFriendLocations(locs)
    } catch {
      /* ignore */
    }
  }, [])

  useEffect(() => {
    refreshLocations()
    const interval = setInterval(refreshLocations, 30000)
    return () => clearInterval(interval)
  }, [refreshLocations])

  useEffect(() => {
    const unsubs = [
      stompService.on('connection', setWsConnected),
      stompService.on('location', () => refreshLocations()),
      stompService.on('message', (msg) => {
        if (msg.senderId === user?.id) return
        setIncomingMessage(msg)
        showToast(`Новое сообщение от ${msg.senderName}`, 'message')
      }),
      stompService.on('nearby', (data) => {
        setProximityAlert(data)
        showToast(`${data.username ?? 'Друг'} рядом!`, 'nearby')
      }),
      stompService.on('meetRequest', (meet) => {
        setIncomingMeet(meet)
        showToast(`${meet.requesterName} хочет встретиться`, 'meet')
      }),
      stompService.on('meetUpdate', (meet) => {
        setMeetUpdate(meet)
      }),
    ]
    return () => unsubs.forEach((u) => u())
  }, [refreshLocations, showToast, user?.id])

  return (
    <AppContext.Provider
      value={{
        friendLocations,
        refreshLocations,
        incomingMessage,
        clearIncomingMessage: () => setIncomingMessage(null),
        proximityAlert,
        clearProximityAlert: () => setProximityAlert(null),
        incomingMeet,
        clearIncomingMeet: () => setIncomingMeet(null),
        meetUpdate,
        clearMeetUpdate: () => setMeetUpdate(null),
        wsConnected,
        toast,
        showToast,
      }}
    >
      {children}
    </AppContext.Provider>
  )
}

export function useApp() {
  const ctx = useContext(AppContext)
  if (!ctx) throw new Error('useApp вне AppProvider')
  return ctx
}
