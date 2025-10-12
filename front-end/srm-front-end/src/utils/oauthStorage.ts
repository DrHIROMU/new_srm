const CODE_VERIFIER_KEY = 'srm.oauth.pkceCodeVerifier'
const OAUTH_STATE_KEY = 'srm.oauth.state'

export const saveCodeVerifier = (codeVerifier: string): void => {
  sessionStorage.setItem(CODE_VERIFIER_KEY, codeVerifier)
}

export const getCodeVerifier = (): string | null => sessionStorage.getItem(CODE_VERIFIER_KEY)

export const consumeCodeVerifier = (): string | null => {
  const value = sessionStorage.getItem(CODE_VERIFIER_KEY)
  if (value) {
    sessionStorage.removeItem(CODE_VERIFIER_KEY)
  }
  return value
}

export const saveState = (state: string): void => {
  sessionStorage.setItem(OAUTH_STATE_KEY, state)
}

export const getState = (): string | null => sessionStorage.getItem(OAUTH_STATE_KEY)

export const consumeState = (): string | null => {
  const value = sessionStorage.getItem(OAUTH_STATE_KEY)
  if (value) {
    sessionStorage.removeItem(OAUTH_STATE_KEY)
  }
  return value
}
