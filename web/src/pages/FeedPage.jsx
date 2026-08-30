import { useCallback, useEffect, useState } from 'react'
import {
  getFriendsPosts,
  createPost,
  likePost,
  unlikePost,
  getComments,
  addComment,
} from '../services/api'
import { useApp } from '../context/AppContext'
import Avatar from '../components/Avatar'

export default function FeedPage() {
  const { showToast } = useApp()
  const [posts, setPosts] = useState([])
  const [loading, setLoading] = useState(true)
  const [showCreate, setShowCreate] = useState(false)
  const [imageUrl, setImageUrl] = useState('')
  const [caption, setCaption] = useState('')
  const [expandedComments, setExpandedComments] = useState({})
  const [comments, setComments] = useState({})
  const [commentText, setCommentText] = useState({})

  const load = useCallback(async () => {
    setLoading(true)
    try {
      const data = await getFriendsPosts()
      setPosts(data)
    } catch (err) {
      showToast(err.message, 'error')
    } finally {
      setLoading(false)
    }
  }, [showToast])

  useEffect(() => {
    load()
  }, [load])

  const handleCreate = async (e) => {
    e.preventDefault()
    try {
      const url = new URL(imageUrl)
      if (!['http:', 'https:'].includes(url.protocol) || url.hostname.includes('google.')) {
        throw new Error('Вставь прямую ссылку на файл изображения, а не страницу поиска Google.')
      }
    } catch (err) {
      showToast(err.message || 'Укажи корректный URL изображения', 'error')
      return
    }
    try {
      await createPost(imageUrl, caption)
      setShowCreate(false)
      setImageUrl('')
      setCaption('')
      showToast('Пост опубликован')
      load()
    } catch (err) {
      showToast(err.message, 'error')
    }
  }

  const toggleLike = async (post) => {
    try {
      if (post.likedByMe) await unlikePost(post.id)
      else await likePost(post.id)
      load()
    } catch (err) {
      showToast(err.message, 'error')
    }
  }

  const toggleComments = async (postId) => {
    if (expandedComments[postId]) {
      setExpandedComments((p) => ({ ...p, [postId]: false }))
      return
    }
    const data = await getComments(postId)
    setComments((p) => ({ ...p, [postId]: data }))
    setExpandedComments((p) => ({ ...p, [postId]: true }))
  }

  const submitComment = async (postId) => {
    const content = commentText[postId]?.trim()
    if (!content) return
    try {
      await addComment(postId, content)
      const data = await getComments(postId)
      setComments((p) => ({ ...p, [postId]: data }))
      setCommentText((p) => ({ ...p, [postId]: '' }))
      load()
    } catch (err) {
      showToast(err.message, 'error')
    }
  }

  return (
    <div className="page">
      <header className="page-header page-header--row">
        <h1>Лента</h1>
        <button type="button" className="btn btn--primary btn--sm" onClick={() => setShowCreate(true)}>
          + Пост
        </button>
      </header>

      {loading && <p className="text-muted page-loading">Загрузка…</p>}

      {!loading && posts.length === 0 && (
        <p className="text-muted empty-state">Лента пуста — добавь друзей и создай первый пост</p>
      )}

      <div className="feed">
        {posts.map((post) => (
          <article key={post.id} className="post-card">
            <div className="post-card__header">
              <Avatar name={post.username} size={36} />
              <div>
                <strong>{post.username}</strong>
                <time className="text-muted">
                  {new Date(post.createdAt).toLocaleString('ru-RU')}
                </time>
              </div>
            </div>
            {post.imageUrl && (
              <img src={post.imageUrl} alt="" className="post-card__image" loading="lazy" />
            )}
            {post.caption && <p className="post-card__caption">{post.caption}</p>}
            <div className="post-card__actions">
              <button type="button" onClick={() => toggleLike(post)}>
                {post.likedByMe ? '❤️' : '🤍'} {post.likesCount}
              </button>
              <button type="button" onClick={() => toggleComments(post.id)}>
                💬 {post.commentsCount}
              </button>
            </div>
            {expandedComments[post.id] && (
              <div className="post-comments">
                {(comments[post.id] ?? []).map((c) => (
                  <div key={c.id} className="post-comment">
                    <strong>{c.username}</strong> {c.content}
                  </div>
                ))}
                <div className="post-comment-form">
                  <input
                    value={commentText[post.id] ?? ''}
                    onChange={(e) => setCommentText((p) => ({ ...p, [post.id]: e.target.value }))}
                    placeholder="Комментарий…"
                  />
                  <button type="button" className="btn btn--ghost btn--sm" onClick={() => submitComment(post.id)}>
                    →
                  </button>
                </div>
              </div>
            )}
          </article>
        ))}
      </div>

      {showCreate && (
        <div className="sheet-overlay" onClick={() => setShowCreate(false)}>
          <div className="sheet sheet--wide" onClick={(e) => e.stopPropagation()}>
            <h3>Новый пост</h3>
            <form onSubmit={handleCreate} className="auth-form">
              <label>
                URL изображения
                <input
                  value={imageUrl}
                  onChange={(e) => setImageUrl(e.target.value)}
                  placeholder="https://images.example.com/photo.jpg"
                  type="url"
                  maxLength={2048}
                  required
                />
                <small className="form-hint">Нужна прямая ссылка на картинку, не ссылка из поисковой выдачи.</small>
              </label>
              <label>
                Подпись
                <input value={caption} onChange={(e) => setCaption(e.target.value)} placeholder="Что нового?" />
              </label>
              <div className="sheet__actions">
                <button type="button" className="btn btn--ghost" onClick={() => setShowCreate(false)}>
                  Отмена
                </button>
                <button type="submit" className="btn btn--primary">
                  Опубликовать
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}
