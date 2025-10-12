const CODE_VERIFIER_LENGTH = 64

const base64UrlEncode = (arrayBuffer: ArrayBuffer): string => {
  const bytes = new Uint8Array(arrayBuffer)
  let binary = ''
  bytes.forEach((b) => {
    binary += String.fromCharCode(b)
  })
  return btoa(binary).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '')
}

const generateRandomString = (length: number): string => {
  const charset = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~'
  const randomValues = new Uint32Array(length)
  window.crypto.getRandomValues(randomValues)

  let result = ''
  for (let i = 0; i < length; i += 1) {
    const randomIndex = randomValues[i] % charset.length
    result += charset.charAt(randomIndex)
  }
  return result
}

export interface PkcePair {
  codeVerifier: string
  codeChallenge: string
}

export const generateCodeVerifier = (): string => generateRandomString(CODE_VERIFIER_LENGTH)

export const generateCodeChallenge = async (codeVerifier: string): Promise<string> => {
  const encoder = new TextEncoder()
  const data = encoder.encode(codeVerifier)
  const digest = await window.crypto.subtle.digest('SHA-256', data)
  return base64UrlEncode(digest)
}

export const createPkcePair = async (): Promise<PkcePair> => {
  const codeVerifier = generateCodeVerifier()
  const codeChallenge = await generateCodeChallenge(codeVerifier)
  return { codeVerifier, codeChallenge }
}
