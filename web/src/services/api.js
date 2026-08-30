import { apiRequest, setToken } from '../api/client'

// ——— Auth ———
export async function login(name, password) {
  const data = await apiRequest('/api/v1/auth/login', {
    method: 'POST',
    body: JSON.stringify({ name, password }),
  })
  setToken(data['jwt-token'])
  return data
}

export async function register(user) {
  const data = await apiRequest('/api/v1/auth/registration', {
    method: 'POST',
    body: JSON.stringify(user),
  })
  setToken(data['jwt-token'])
  return data
}

// ——— User ———
export const getMe = () => apiRequest('/api/v1/user/me')
export const searchUsers = (name) =>
  apiRequest(`/api/v1/user/search?name=${encodeURIComponent(name)}`)
export const getUserInfo = (id) => apiRequest(`/api/v1/user/${id}/info`)

// ——— Friends ———
export const getAllFriendships = () => apiRequest('/friends/allFriends')
export const sendFriendRequest = (id) =>
  apiRequest(`/friends/sendRequest/${id}`, { method: 'POST' })
export const acceptFriendRequest = (id) =>
  apiRequest(`/friends/acceptRequest/${id}`, { method: 'POST' })
export const deleteFriend = (id) =>
  apiRequest(`/friends/deleteFriend/${id}`, { method: 'POST' })

// ——— Location ———
export const getFriendsLocations = () => apiRequest('/api/v1/location/friends')

// ——— Chat ———
export const getConversations = () => apiRequest('/api/v1/chat/conversations')
export const getConversation = (userId) => apiRequest(`/api/v1/chat/${userId}`)
export const sendMessage = (receiverId, content) =>
  apiRequest(`/api/v1/chat/${receiverId}`, {
    method: 'POST',
    body: JSON.stringify({ content }),
  })

// ——— Posts ———
export const getFriendsPosts = () => apiRequest('/api/v1/posts/friends')
export const createPost = (imageUrl, caption) =>
  apiRequest('/api/v1/posts', {
    method: 'POST',
    body: JSON.stringify({ imageUrl, caption }),
  })
export const likePost = (postId) =>
  apiRequest(`/api/v1/posts/${postId}/like`, { method: 'POST' })
export const unlikePost = (postId) =>
  apiRequest(`/api/v1/posts/${postId}/like`, { method: 'DELETE' })
export const getComments = (postId) =>
  apiRequest(`/api/v1/posts/${postId}/comments`)
export const addComment = (postId, content) =>
  apiRequest(`/api/v1/posts/${postId}/comments`, {
    method: 'POST',
    body: JSON.stringify({ content }),
  })

// ——— Stories ———
export const getFriendsStories = () => apiRequest('/api/v1/stories/friends')
export const createStory = (imageUrl, caption) =>
  apiRequest('/api/v1/stories', {
    method: 'POST',
    body: JSON.stringify({ imageUrl, caption }),
  })

// ——— AI ———
export const aiChat = (message, lat, lng) =>
  apiRequest('/api/v1/ai/chat', {
    method: 'POST',
    body: JSON.stringify({ message, lat, lng }),
  })

// ——— Meets ———
export const getActiveMeets = () => apiRequest('/api/v1/meets')
export const requestMeet = (receiverId, meetLat, meetLng) =>
  apiRequest(`/api/v1/meets/${receiverId}`, {
    method: 'POST',
    body: JSON.stringify({ meetLat, meetLng }),
  })
export const acceptMeet = (meetId) =>
  apiRequest(`/api/v1/meets/${meetId}/accept`, { method: 'POST' })
export const declineMeet = (meetId) =>
  apiRequest(`/api/v1/meets/${meetId}/decline`, { method: 'POST' })

// ——— Block ———
export const blockUser = (userId) =>
  apiRequest(`/api/v1/block/${userId}`, { method: 'POST' })
export const unblockUser = (userId) =>
  apiRequest(`/api/v1/block/${userId}`, { method: 'DELETE' })
export const getBlockedUsers = () => apiRequest('/api/v1/block')

// ——— Wallet & Shop ———
export const getWallet = () => apiRequest('/api/v1/wallet')
export const getShopFrames = () => apiRequest('/api/v1/shop/frames')
export const getMyFrames = () => apiRequest('/api/v1/shop/frames/my')
export const buyFrame = (frameId) =>
  apiRequest(`/api/v1/shop/frames/${frameId}/buy`, { method: 'POST' })
export const equipFrame = (frameId) =>
  apiRequest(`/api/v1/shop/frames/${frameId}/equip`, { method: 'POST' })
export const unequipFrame = () =>
  apiRequest('/api/v1/shop/frames/equip', { method: 'DELETE' })

// ——— Education ———
export const getEducation = () => apiRequest('/api/v1/education')
export const updateEducation = (school, university) =>
  apiRequest('/api/v1/education', {
    method: 'PUT',
    body: JSON.stringify({ school, university }),
  })
export const searchClassmates = (school, university) => {
  const params = new URLSearchParams()
  if (school) params.set('school', school)
  if (university) params.set('university', university)
  return apiRequest(`/api/v1/education/search?${params}`)
}
