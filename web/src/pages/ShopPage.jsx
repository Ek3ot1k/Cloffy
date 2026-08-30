import { useCallback, useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getShopFrames, getMyFrames, buyFrame, equipFrame, getWallet } from '../services/api'
import { useApp } from '../context/AppContext'
import { useAuth } from '../context/AuthContext'

export default function ShopPage() {
  const { showToast } = useApp()
  const { user, refreshUser } = useAuth()
  const [frames, setFrames] = useState([])
  const [myFrameIds, setMyFrameIds] = useState(new Set())
  const [coins, setCoins] = useState(0)
  const [loading, setLoading] = useState(true)
  const [activeFrameId, setActiveFrameId] = useState(null)

  const load = useCallback(async () => {
    setLoading(true)
    try {
      const [all, mine, wallet] = await Promise.all([getShopFrames(), getMyFrames(), getWallet()])
      setFrames(all)
      setMyFrameIds(new Set(mine.map((f) => f.id)))
      setCoins(wallet.coins)
      setActiveFrameId(user?.activeFrame?.id ?? null)
    } catch (err) {
      showToast(err.message, 'error')
    } finally {
      setLoading(false)
    }
  }, [showToast, user?.activeFrame?.id])

  useEffect(() => {
    load()
  }, [load])

  const handleBuy = async (frameId) => {
    try {
      const res = await buyFrame(frameId)
      setCoins(res.coinsRemaining)
      await refreshUser()
      showToast('Рамка куплена! Теперь нажми «Надеть».')
      load()
    } catch (err) {
      showToast(err.message, 'error')
    }
  }

  const handleEquip = async (frameId) => {
    try {
      await equipFrame(frameId)
      setActiveFrameId(frameId)
      await refreshUser()
      showToast('Рамка надета!')
    } catch (err) {
      showToast(err.message, 'error')
    }
  }

  return (
    <div className="page">
      <header className="page-header page-header--row">
        <Link to="/profile" className="back-link">←</Link>
        <h1>Магазин</h1>
        <span className="profile-coins">🪙 {coins}</span>
      </header>

      {loading && <p className="text-muted page-loading">Загрузка…</p>}

      <div className="shop-grid">
        {frames.map((frame) => {
          const owned = myFrameIds.has(frame.id)
          const active = activeFrameId === frame.id
          const frameClass = `frame-style frame-style--${frame.name.toLowerCase()}`
          return (
            <div key={frame.id} className="shop-card">
              <div className={`shop-card__preview ${frameClass}`}>
                {frame.name?.[0]}
              </div>
              <h3>{frame.name}</h3>
              <p className="shop-card__description">{frame.description}</p>
              <p className="text-muted">🪙 {frame.price}</p>
              {!owned ? (
                <button
                  type="button"
                  className="btn btn--primary btn--sm"
                  onClick={() => handleBuy(frame.id)}
                  disabled={coins < frame.price}
                >
                  Купить
                </button>
              ) : (
                <button type="button" className="btn btn--ghost btn--sm" onClick={() => handleEquip(frame.id)} disabled={active}>
                  {active ? 'Надета' : 'Надеть'}
                </button>
              )}
            </div>
          )
        })}
      </div>
    </div>
  )
}
