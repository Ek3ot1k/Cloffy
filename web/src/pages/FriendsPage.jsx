import { useCallback, useEffect, useMemo, useState } from 'react'
import {
  getAllFriendships,
  searchUsers,
  sendFriendRequest,
  acceptFriendRequest,
  deleteFriend,
  blockUser,
  getBlockedUsers,
} from '../services/api'
import { useAuth } from '../context/AuthContext'
import { useApp } from '../context/AppContext'
import Avatar from '../components/Avatar'

export default function FriendsPage() {
  const { user } = useAuth()
  const { showToast } = useApp()
  const [friendships, setFriendships] = useState([])
  const [blockedIds, setBlockedIds] = useState(new Set())
  const [query, setQuery] = useState('')
  const [searchResults, setSearchResults] = useState([])
  const [tab, setTab] = useState('friends')
  const [loading, setLoading] = useState(true)

  const load = useCallback(async () => {
    setLoading(true)
    try {
      const [data, blocked] = await Promise.all([getAllFriendships(), getBlockedUsers()])
      setFriendships(data)
      setBlockedIds(new Set(blocked.map((u) => u.id)))
    } catch (err) {
      showToast(err.message, 'error')
    } finally {
      setLoading(false)
    }
  }, [showToast])

  useEffect(() => {
    load()
  }, [load])

  useEffect(() => {
    if (query.trim().length < 2) {
      setSearchResults([])
      return
    }
    const t = setTimeout(async () => {
      try {
        const results = await searchUsers(query.trim())
        setSearchResults(results)
      } catch {
        setSearchResults([])
      }
    }, 300)
    return () => clearTimeout(t)
  }, [query])

  const { accepted, incoming, outgoing } = useMemo(() => {
    const accepted = []
    const incoming = []
    const outgoing = []
    for (const f of friendships) {
      const isMeUser = f.user?.id === user?.id
      const other = isMeUser ? f.friend : f.user
      const entry = { ...f, other }
      if (f.status === 'ACCEPTED') accepted.push(entry)
      else if (f.status === 'PENDING') {
        if (isMeUser) outgoing.push(entry)
        else incoming.push(entry)
      }
    }
    return { accepted, incoming, outgoing }
  }, [friendships, user])

  const handleSendRequest = async (id) => {
    try {
      await sendFriendRequest(id)
      showToast('Запрос отправлен')
      load()
    } catch (err) {
      showToast(err.message, 'error')
    }
  }

  const handleAccept = async (id) => {
    try {
      await acceptFriendRequest(id)
      showToast('Друг добавлен!')
      load()
    } catch (err) {
      showToast(err.message, 'error')
    }
  }

  const handleDelete = async (id) => {
    if (!confirm('Удалить из друзей?')) return
    try {
      await deleteFriend(id)
      load()
    } catch (err) {
      showToast(err.message, 'error')
    }
  }

  const handleBlock = async (id) => {
    if (!confirm('Заблокировать пользователя?')) return
    try {
      await blockUser(id)
      showToast('Пользователь заблокирован')
      setBlockedIds((ids) => new Set([...ids, id]))
      setFriendships((items) => items.filter((f) => f.user?.id !== id && f.friend?.id !== id))
      load()
    } catch (err) {
      showToast(err.message, 'error')
    }
  }

  const alreadyFriendOrPending = (id) =>
    friendships.some(
      (f) =>
        (f.user?.id === id || f.friend?.id === id) &&
        (f.status === 'ACCEPTED' || f.status === 'PENDING')
    )

  return (
    <div className="page">
      <header className="page-header">
        <h1>Друзья</h1>
      </header>

      <div className="search-box">
        <input
          value={query}
          onChange={(e) => {
            setQuery(e.target.value)
            setTab('search')
          }}
          placeholder="Поиск по имени…"
        />
      </div>

      <div className="tabs">
        <button type="button" className={tab === 'friends' ? 'tabs__active' : ''} onClick={() => setTab('friends')}>
          Друзья ({accepted.length})
        </button>
        <button type="button" className={tab === 'requests' ? 'tabs__active' : ''} onClick={() => setTab('requests')}>
          Запросы ({incoming.length})
        </button>
      </div>

      {loading && <p className="text-muted page-loading">Загрузка…</p>}

      {tab === 'search' && query.trim().length >= 2 && (
        <section className="list-section">
          <h2>Результаты поиска</h2>
          {searchResults.length === 0 && <p className="text-muted">Никого не найдено</p>}
          {searchResults.map((u) => (
            <div key={u.id} className="list-item">
              <Avatar name={u.name} />
              <div className="list-item__body">
                <strong>{u.name}</strong>
                <span className="text-muted">{u.age} лет</span>
              </div>
              {!alreadyFriendOrPending(u.id) ? (
                <button type="button" className="btn btn--primary btn--sm" onClick={() => handleSendRequest(u.id)}>
                  Добавить
                </button>
              ) : (
                <span className="badge">Отправлено</span>
              )}
            </div>
          ))}
        </section>
      )}

      {tab === 'friends' && (
        <section className="list-section">
          {accepted.length === 0 && !loading && <p className="text-muted">Пока нет друзей — найди кого-нибудь через поиск</p>}
          {accepted.filter(({ other }) => !blockedIds.has(other.id)).map(({ other }) => (
            <div key={other.id} className="list-item">
              <Avatar name={other.name} />
              <div className="list-item__body">
                <strong>{other.name}</strong>
              </div>
              <div className="list-item__actions">
                <button type="button" className="btn btn--ghost btn--sm" onClick={() => handleDelete(other.id)}>
                  Удалить
                </button>
                <button type="button" className="btn btn--ghost btn--sm" onClick={() => handleBlock(other.id)}>
                  Блок
                </button>
              </div>
            </div>
          ))}
          {outgoing.length > 0 && (
            <>
              <h2 className="section-subtitle">Исходящие запросы</h2>
              {outgoing.map(({ other }) => (
                <div key={other.id} className="list-item">
                  <Avatar name={other.name} />
                  <div className="list-item__body">
                    <strong>{other.name}</strong>
                    <span className="badge">Ожидает</span>
                  </div>
                </div>
              ))}
            </>
          )}
        </section>
      )}

      {tab === 'requests' && (
        <section className="list-section">
          {incoming.length === 0 && !loading && <p className="text-muted">Нет входящих запросов</p>}
          {incoming.map(({ other }) => (
            <div key={other.id} className="list-item">
              <Avatar name={other.name} />
              <div className="list-item__body">
                <strong>{other.name}</strong>
              </div>
              <button type="button" className="btn btn--primary btn--sm" onClick={() => handleAccept(other.id)}>
                Принять
              </button>
            </div>
          ))}
        </section>
      )}
    </div>
  )
}
