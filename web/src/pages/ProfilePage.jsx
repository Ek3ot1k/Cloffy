import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { useApp } from '../context/AppContext'
import {
  getWallet,
  getEducation,
  updateEducation,
  getBlockedUsers,
  unblockUser,
  createStory,
} from '../services/api'
import Avatar from '../components/Avatar'

export default function ProfilePage() {
  const { user, logout } = useAuth()
  const { showToast } = useApp()
  const navigate = useNavigate()

  const [coins, setCoins] = useState(null)
  const [education, setEducation] = useState({ school: '', university: '' })
  const [blocked, setBlocked] = useState([])
  const [editEdu, setEditEdu] = useState(false)
  const [showStory, setShowStory] = useState(false)
  const [storyUrl, setStoryUrl] = useState('')

  useEffect(() => {
    getWallet().then((w) => setCoins(w.coins)).catch(() => {})
    getEducation().then(setEducation).catch(() => {})
    getBlockedUsers().then(setBlocked).catch(() => {})
  }, [])

  const saveEducation = async () => {
    try {
      const data = await updateEducation(education.school, education.university)
      setEducation(data)
      setEditEdu(false)
      showToast('Профиль обновлён')
    } catch (err) {
      showToast(err.message, 'error')
    }
  }

  const handleUnblock = async (id) => {
    await unblockUser(id)
    setBlocked((b) => b.filter((u) => u.id !== id))
    showToast('Разблокирован')
  }

  const handleStory = async (e) => {
    e.preventDefault()
    try {
      const url = new URL(storyUrl)
      if (!['http:', 'https:'].includes(url.protocol) || url.hostname.includes('google.')) {
        throw new Error('Вставь прямую ссылку на файл изображения, а не страницу поиска Google.')
      }
    } catch (err) {
      showToast(err.message || 'Укажи корректный URL изображения', 'error')
      return
    }
    try {
      await createStory(storyUrl, '')
      setShowStory(false)
      setStoryUrl('')
      showToast('История опубликована')
    } catch (err) {
      showToast(err.message, 'error')
    }
  }

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <div className="page">
      <header className="page-header">
        <h1>Профиль</h1>
      </header>

      <div className="profile-card">
        <Avatar name={user?.name} size={72} frame={user?.activeFrame?.name} />
        <h2>{user?.name}</h2>
        <p className="text-muted">{user?.email}</p>
        {user?.status && <span className="badge">{user.status}</span>}
        {coins != null && <p className="profile-coins">🪙 {coins} монет</p>}
        {user?.activeFrame && <p className="text-muted">Рамка: {user.activeFrame.name}</p>}
      </div>

      <div className="profile-links">
        <Link to="/ai" className="profile-link">🤖 AI Ассистент</Link>
        <Link to="/shop" className="profile-link">🛍️ Магазин рамок</Link>
        <button type="button" className="profile-link" onClick={() => setShowStory(true)}>
          📖 Добавить историю
        </button>
      </div>

      <section className="profile-section">
        <div className="profile-section__header">
          <h3>Образование</h3>
          <button type="button" className="btn btn--ghost btn--sm" onClick={() => setEditEdu(!editEdu)}>
            {editEdu ? 'Отмена' : 'Изменить'}
          </button>
        </div>
        {editEdu ? (
          <div className="auth-form">
            <label>
              Школа
              <input
                value={education.school ?? ''}
                onChange={(e) => setEducation({ ...education, school: e.target.value })}
              />
            </label>
            <label>
              Университет
              <input
                value={education.university ?? ''}
                onChange={(e) => setEducation({ ...education, university: e.target.value })}
              />
            </label>
            <button type="button" className="btn btn--primary btn--sm" onClick={saveEducation}>
              Сохранить
            </button>
          </div>
        ) : (
          <div className="text-muted">
            <p>Школа: {education.school || '—'}</p>
            <p>Университет: {education.university || '—'}</p>
          </div>
        )}
      </section>

      {blocked.length > 0 && (
        <section className="profile-section">
          <h3>Заблокированные</h3>
          {blocked.map((u) => (
            <div key={u.id} className="list-item">
              <Avatar name={u.name} size={32} />
              <span>{u.name}</span>
              <button type="button" className="btn btn--ghost btn--sm" onClick={() => handleUnblock(u.id)}>
                Разблокировать
              </button>
            </div>
          ))}
        </section>
      )}

      <button type="button" className="btn btn--danger profile-logout" onClick={handleLogout}>
        Выйти
      </button>

      {showStory && (
        <div className="sheet-overlay" onClick={() => setShowStory(false)}>
          <div className="sheet" onClick={(e) => e.stopPropagation()}>
            <h3>Новая история</h3>
            <form onSubmit={handleStory} className="auth-form">
              <label>
                URL изображения
                <input value={storyUrl} onChange={(e) => setStoryUrl(e.target.value)} required type="url" maxLength={2048} placeholder="https://images.example.com/story.jpg" />
                <small className="form-hint">Нужна прямая ссылка на файл изображения, не страница поиска.</small>
              </label>
              <button type="submit" className="btn btn--primary">Опубликовать</button>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}
