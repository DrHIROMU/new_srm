import axios from 'axios'
import type { TokenResponse, User } from '@/types/auth'

const authBaseUrl = import.meta.env.VITE_AUTH_BASE_URL
const clientId = import.meta.env.VITE_OAUTH_CLIENT_ID
const redirectUri = import.meta.env.VITE_OAUTH_REDIRECT_URI
const defaultScope = import.meta.env.VITE_OAUTH_SCOPE || 'openid profile'

const authClient = axios.create({
  baseURL: authBaseUrl,
  timeout: 10000,
  withCredentials: true,
})

export interface AuthorizeUrlOptions {
  codeChallenge: string
  state: string
  scope?: string
  redirectUriOverride?: string
}

export interface TokenExchangeParams {
  code: string
  codeVerifier: string
  redirectUriOverride?: string
}

export const buildAuthorizeUrl = ({
  codeChallenge,
  state,
  scope,
  redirectUriOverride,
}: AuthorizeUrlOptions): string => {
  const authorizeUrl = new URL('/oauth2/authorize', authBaseUrl)
  authorizeUrl.searchParams.set('response_type', 'code')
  authorizeUrl.searchParams.set('client_id', clientId)
  authorizeUrl.searchParams.set('redirect_uri', redirectUriOverride ?? redirectUri)
  authorizeUrl.searchParams.set('scope', scope ?? defaultScope)
  authorizeUrl.searchParams.set('code_challenge', codeChallenge)
  authorizeUrl.searchParams.set('code_challenge_method', 'S256')
  authorizeUrl.searchParams.set('state', state)
  return authorizeUrl.toString()
}

export const exchangeCodeForToken = async ({
  code,
  codeVerifier,
  redirectUriOverride,
}: TokenExchangeParams): Promise<TokenResponse> => {
  const params = new URLSearchParams()
  params.set('grant_type', 'authorization_code')
  params.set('code', code)
  params.set('redirect_uri', redirectUriOverride ?? redirectUri)
  params.set('client_id', clientId)
  params.set('code_verifier', codeVerifier)

  const response = await authClient.post('/oauth2/token', params, {
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
  })

  const payload = response.data
  return {
    accessToken: payload.access_token,
    refreshToken: payload.refresh_token ?? null,
    expiresIn: payload.expires_in ?? null,
    scope: payload.scope ?? null,
    tokenType: payload.token_type ?? 'Bearer',
    idToken: payload.id_token ?? null,
  }
}

export const fetchUserInfo = async (): Promise<User> => {
  const response = await authClient.get<User>('/api/auth/userinfo')
  return response.data
}

export const revokeToken = async (token: string): Promise<void> => {
  const params = new URLSearchParams()
  params.set('token', token)
  await authClient.post('/oauth2/revoke', params, {
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
  })
}

export const logout = async (): Promise<void> => {
  await authClient.post('/api/auth/logout')
}
