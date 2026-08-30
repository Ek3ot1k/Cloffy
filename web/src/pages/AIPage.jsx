import { useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import { aiChat } from '../services/api'
import { getCurrentLocation } from '../services/geolocation'

export default function AIPage() {
  const [messages, setMessages] = useState([
    { role: 'assistant', text: 'Привет! Я AI-ассистент Cloffy. Спроси меня что угодно — могу учитывать твою геолокацию.' },
  ])
  const [input, setInput] = useState('')
  const [loading, setLoading] = useState(false)
  const [useLocation, setUseLocation] = useState(true)
  const [locationWarning, setLocationWarning] = useState('')
  const bottomRef = useRef(null)
  const locationRef = useRef(null)
  const locationAttemptedRef = useRef(false)

  const send = async (e) => {
    e.preventDefault()
    const text = input.trim()
    if (!text || loading) return

    setInput('')
    setMessages((m) => [...m, { role: 'user', text }])
    setLoading(true)

    let lat
    let lng
    if (useLocation && !locationAttemptedRef.current) {
      locationAttemptedRef.current = true
      try {
        locationRef.current = await getCurrentLocation()
        setLocationWarning('')
      } catch {
        setLocationWarning('Не удалось получить геолокацию. Следующие сообщения отправляю без неё.')
      }
    }

    if (useLocation && locationRef.current) {
      lat = locationRef.current.lat
      lng = locationRef.current.lng
    }

    try {
      const res = await aiChat(text, lat, lng)
      setMessages((m) => [...m, { role: 'assistant', text: res.reply }])
    } catch (err) {
      setMessages((m) => [...m, { role: 'assistant', text: `Ошибка: ${err.message}` }])
    } finally {
      setLoading(false)
      setTimeout(() => bottomRef.current?.scrollIntoView({ behavior: 'smooth' }), 50)
    }
  }

  return (
    <div className="chat-page ai-page">
      <header className="chat-header">
        <Link to="/profile" className="back-link">←</Link>
        <strong>AI Ассистент</strong>
      </header>

      <label className="ai-toggle">
        <input
          type="checkbox"
          checked={useLocation}
          onChange={(e) => {
            setUseLocation(e.target.checked)
            if (e.target.checked) locationAttemptedRef.current = false
          }}
        />
        Учитывать мою геолокацию
      </label>
      {locationWarning && <p className="ai-warning">{locationWarning}</p>}

      <div className="chat-messages ai-messages">
        {messages.map((m, i) => (
          <div key={i} className={`chat-bubble ${m.role === 'user' ? 'chat-bubble--mine' : 'chat-bubble--theirs'}`}>
            <p>{m.text}</p>
          </div>
        ))}
        {loading && <div className="chat-bubble chat-bubble--theirs"><p>Думаю…</p></div>}
        <div ref={bottomRef} />
      </div>

      <form className="chat-input" onSubmit={send}>
        <input
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder="Спроси что-нибудь…"
          disabled={loading}
        />
        <button type="submit" className="btn btn--primary btn--sm" disabled={loading || !input.trim()}>
          →
        </button>
      </form>
    </div>
  )
}
