let cachedPosition = null
let pendingRequest = null

export async function getCurrentLocation() {
  if (cachedPosition) return cachedPosition
  if (!navigator.geolocation) throw new Error('UNSUPPORTED')
  if (pendingRequest) return pendingRequest

  pendingRequest = new Promise((resolve, reject) => {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        cachedPosition = { lat: position.coords.latitude, lng: position.coords.longitude }
        resolve(cachedPosition)
      },
      reject,
      { enableHighAccuracy: false, maximumAge: 60000, timeout: 30000 }
    )
  }).finally(() => {
    pendingRequest = null
  })

  return pendingRequest
}

export function updateCachedLocation(position) {
  cachedPosition = position
}
