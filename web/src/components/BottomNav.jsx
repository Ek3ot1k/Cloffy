import { NavLink } from 'react-router-dom'

const tabs = [
  { to: '/', label: 'Карта', icon: '🗺️' },
  { to: '/friends', label: 'Друзья', icon: '👫' },
  { to: '/chat', label: 'Чат', icon: '💬' },
  { to: '/feed', label: 'Лента', icon: '📸' },
  { to: '/profile', label: 'Профиль', icon: '👤' },
]

export default function BottomNav() {
  return (
    <nav className="bottom-nav">
      {tabs.map((tab) => (
        <NavLink
          key={tab.to}
          to={tab.to}
          end={tab.to === '/'}
          className={({ isActive }) => `bottom-nav__item${isActive ? ' bottom-nav__item--active' : ''}`}
        >
          <span className="bottom-nav__icon">{tab.icon}</span>
          <span className="bottom-nav__label">{tab.label}</span>
        </NavLink>
      ))}
    </nav>
  )
}
