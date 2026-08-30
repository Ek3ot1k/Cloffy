import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

const backendTarget = process.env.VITE_BACKEND_PROXY_URL ?? 'http://localhost:8080'

export default defineConfig({
  plugins: [react()],
  // sockjs-client still references Node's `global` in its browser bundle.
  // Vite 8 no longer polyfills it automatically.
  define: {
    global: 'globalThis',
  },
  server: {
    port: 5173,
    proxy: {
      '/api': { target: backendTarget, changeOrigin: true },
      '/friends': { target: backendTarget, changeOrigin: true },
      '/ws': { target: backendTarget, ws: true, changeOrigin: true },
    },
  },
})
