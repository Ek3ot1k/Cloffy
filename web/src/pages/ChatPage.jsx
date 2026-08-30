import { useCallback, useEffect, useRef, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { getConversation, getUserInfo, sendMessage } from '../services/api'
import { useApp } from '../context/AppContext'
import { useAuth } from '../context/AuthContext'
import Avatar from '../components/Avatar'

export default function ChatPage() {
  const { userId } = useParams()
  const partnerId = Number(userId)
  const { user } = useAuth()
  const { incomingMessage, clearIncomingMessage } = useApp()

  const [partner, setPartner] = useState(null)
  const [messages, setMessages] = useState([])
  const [text, setText] = useState('')
  const [sending, setSending] = useState(false)
  const [error, setError] = useState('')
  const bottomRef = useRef(null)

  const load = useCallback(async () => {
    const [msgs, info] = await Promise.all([
      getConversation(partnerId),
      getUserInfo(partnerId),
    ])
    setMessages(msgs)
    setPartner(info)
  }, [partnerId])

  useEffect(() => {
    load()
  }, [load])

  useEffect(() => {
    if (
      incomingMessage &&
      (incomingMessage.senderId === partnerId || incomingMessage.receiverId === partnerId)
    ) {
      setMessages((prev) => {
        if (prev.some((m) => m.id === incomingMessage.id)) return prev
        return [...prev, incomingMessage]
      })
      clearIncomingMessage()
    }
  }, [incomingMessage, partnerId, clearIncomingMessage])

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [messages])

  const send = async (e) => {
    e.preventDefault()
    const content = text.trim()
    if (!content || sending) return

    setSending(true)
    setError('')
    try {
      const message = await sendMessage(partnerId, content)
      setMessages((prev) => (prev.some((item) => item.id === message.id) ? prev : [...prev, message]))
      setText('')
    } catch (err) {
      setError(err.message || 'Не удалось отправить сообщение')
    } finally {
      setSending(false)
    }
  }

  return (
    <div className="chat-page">
      <header className="chat-header">
        <Link to="/chat" className="back-link">←</Link>
        {partner && (
          <>
            <Avatar name={partner.name} size={36} />
            <strong>{partner.name}</strong>
          </>
        )}
      </header>

      <div className="chat-messages">
        {messages.map((m) => {
          const mine = m.senderId === user?.id
          return (
            <div key={m.id} className={`chat-bubble ${mine ? 'chat-bubble--mine' : 'chat-bubble--theirs'}`}>
              <p>{m.content}</p>
              <time>{new Date(m.timestamp).toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' })}</time>
            </div>
          )
        })}
        <div ref={bottomRef} />
      </div>

      <form className="chat-input" onSubmit={send}>
        <input
          value={text}
          onChange={(e) => setText(e.target.value)}
          placeholder="Сообщение…"
          autoComplete="off"
        />
        <button type="submit" className="btn btn--primary btn--sm" disabled={!text.trim() || sending}>
          {sending ? '…' : '→'}
        </button>
      </form>
      {error && <p className="chat-error" role="alert">{error}</p>}
    </div>
  )
}
