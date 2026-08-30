import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { register } from '../services/api'
import { useAuth } from '../context/AuthContext'

export default function RegisterPage() {
  const navigate = useNavigate()
  const { loadUser } = useAuth()
  const [form, setForm] = useState({ name: '', email: '', age: 18, password: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const set = (key) => (e) => setForm({ ...form, [key]: e.target.value })

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await register({ ...form, age: Number(form.age) })
      await loadUser()
      navigate('/')
    } catch (err) {
      setError(err.message || 'Ошибка регистрации')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-card">
        <div className="auth-logo">Cloffy</div>
        <p className="auth-subtitle">Создай аккаунт</p>

        <form onSubmit={handleSubmit} className="auth-form">
          <label>
            Имя пользователя
            <input value={form.name} onChange={set('name')} required minLength={2} />
          </label>
          <label>
            Email
            <input type="email" value={form.email} onChange={set('email')} required />
          </label>
          <label>
            Возраст
            <input type="number" value={form.age} onChange={set('age')} min={13} max={120} required />
          </label>
          <label>
            Пароль
            <input type="password" value={form.password} onChange={set('password')} required minLength={4} />
          </label>
          {error && <p className="form-error">{error}</p>}
          <button type="submit" className="btn btn--primary" disabled={loading}>
            {loading ? 'Регистрация…' : 'Зарегистрироваться'}
          </button>
        </form>

        <p className="auth-footer">
          Уже есть аккаунт? <Link to="/login">Войти</Link>
        </p>
      </div>
    </div>
  )
}
