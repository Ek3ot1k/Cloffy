import { createContext, useCallback, useContext, useEffect, useState } from 'react'
import { clearToken, getToken } from '../api/client'
import { getMe } from '../services/api'
import { stompService } from '../websocket/StompService'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  const loadUser = useCallback(async () => {
    if (!getToken()) {
      setUser(null)
      setLoading(false)
      return
    }
    try {
      const me = await getMe()
      setUser(me)
      stompService.connect()
    } catch {
      clearToken()
      setUser(null)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    loadUser()
    const onLogout = () => {
      setUser(null)
      stompService.disconnect()
    }
    window.addEventListener('cloffy:logout', onLogout)
    return () => window.removeEventListener('cloffy:logout', onLogout)
  }, [loadUser])

  const logout = () => {
    clearToken()
    stompService.disconnect()
    setUser(null)
  }

  const refreshUser = async () => {
    const me = await getMe()
    setUser(me)
    return me
  }

  return (
    <AuthContext.Provider value={{ user, loading, logout, refreshUser, setUser, loadUser }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth вне AuthProvider')
  return ctx
}
