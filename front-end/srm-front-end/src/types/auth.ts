export interface User {
  id: string
  email: string
  displayName: string
  roles: string[]
  supplier: boolean
  permissions: string[]
}

export interface TokenResponse {
  accessToken: string
  refreshToken: string | null
  expiresIn: number | null
  scope: string | null
  tokenType: string
  idToken: string | null
}
