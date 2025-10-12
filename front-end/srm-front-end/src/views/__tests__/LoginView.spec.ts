import { mount } from '@vue/test-utils'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import LoginView from '../LoginView.vue'

const hoisted = vi.hoisted(() => ({
  buildAuthorizeUrl: vi.fn(() => 'https://auth.example.com/oauth2/authorize'),
  createPkcePair: vi.fn(() =>
    Promise.resolve({
      codeVerifier: 'test-code-verifier',
      codeChallenge: 'test-code-challenge',
    }),
  ),
  saveCodeVerifier: vi.fn(),
  saveState: vi.fn(),
  useRoute: vi.fn(() => ({ query: {} })),
}))

vi.mock('@/services/authService', () => ({
  buildAuthorizeUrl: hoisted.buildAuthorizeUrl,
}))

vi.mock('@/utils/pkce', () => ({
  createPkcePair: hoisted.createPkcePair,
}))

vi.mock('@/utils/oauthStorage', () => ({
  saveCodeVerifier: hoisted.saveCodeVerifier,
  saveState: hoisted.saveState,
}))

vi.mock('vue-router', async () => {
  const actual = await vi.importActual<typeof import('vue-router')>('vue-router')
  return {
    ...actual,
    useRoute: hoisted.useRoute,
  }
})

describe('LoginView', () => {
  const originalCrypto = window.crypto
  const originalLocation = window.location
  const assignSpy = vi.fn()

  beforeEach(() => {
    vi.clearAllMocks()
    hoisted.useRoute.mockReturnValue({ query: {} })
    hoisted.buildAuthorizeUrl.mockReset()
    hoisted.createPkcePair.mockReset()
    hoisted.saveCodeVerifier.mockReset()
    hoisted.saveState.mockReset()
    Object.defineProperty(window, 'crypto', {
      configurable: true,
      value: {
        getRandomValues: (array: Uint32Array) => {
          for (let i = 0; i < array.length; i += 1) {
            array[i] = i
          }
          return array
        },
        subtle: originalCrypto.subtle,
      },
    })
    Object.defineProperty(window, 'location', {
      writable: true,
      value: {
        assign: assignSpy,
      },
    })
  })

  afterEach(() => {
    Object.defineProperty(window, 'crypto', {
      configurable: true,
      value: originalCrypto,
    })
    Object.defineProperty(window, 'location', {
      writable: true,
      value: originalLocation,
    })
  })

  it('啟動登入流程時會儲存 PKCE 資訊並導向 OAuth2 授權頁', async () => {
    const wrapper = mount(LoginView)
    const button = wrapper.get('button')

    await button.trigger('click')

    expect(hoisted.createPkcePair).toHaveBeenCalledTimes(1)
    expect(hoisted.saveCodeVerifier).toHaveBeenCalledWith('test-code-verifier')
    expect(hoisted.saveState).toHaveBeenCalledTimes(1)
    expect(hoisted.buildAuthorizeUrl).toHaveBeenCalledWith({
      codeChallenge: 'test-code-challenge',
      state: expect.any(String),
    })
    expect(assignSpy).toHaveBeenCalledWith('https://auth.example.com/oauth2/authorize')
    expect(button.attributes('disabled')).toBeDefined()
  })
})
