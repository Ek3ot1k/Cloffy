import { Outlet } from 'react-router-dom'
import BottomNav from './BottomNav'
import Toast from './Toast'
import { useApp } from '../context/AppContext'

export default function Layout() {
  const { toast } = useApp()

  return (
    <div className="app-layout">
      <main className="app-main">
        <Outlet />
      </main>
      <BottomNav />
      {toast && <Toast message={toast.message} type={toast.type} />}
    </div>
  )
}
