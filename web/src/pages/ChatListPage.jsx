import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getConversations } from '../services/api'
import Avatar from '../components/Avatar'

export default function ChatListPage() {
  const [conversations, setConversations] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    getConversations()
      .then(setConversations)
      .finally(() => setLoading(false))
  }, [])

  return (
    <div className="page">
      <header className="page-header">
        <h1>Чат</h1>
      </header>

      {loading && <p className="text-muted page-loading">Загрузка…</p>}

      {!loading && conversations.length === 0 && (
        <p className="text-muted empty-state">Нет диалогов. Напиши другу из списка друзей!</p>
      )}

      <div className="list-section">
        {conversations.map((c) => (
          <Link key={c.partnerId} to={`/chat/${c.partnerId}`} className="list-item list-item--link">
            <Avatar name={c.partnerUsername} />
            <div className="list-item__body">
              <strong>{c.partnerUsername}</strong>
              <span className="list-item__preview">{c.lastMessage}</span>
            </div>
            {c.lastMessageAt && (
              <time className="text-muted chat-time">
                {new Date(c.lastMessageAt).toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' })}
              </time>
            )}
          </Link>
        ))}
      </div>
    </div>
  )
}
