import { flushPromises, mount } from '@vue/test-utils'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import AuthCallbackView from '../AuthCallbackView.vue'

const hoisted = vi.hoisted(() => {
  const replaceMock = vi.fn()
  return {
    exchangeCodeForToken: vi.fn(),
    fetchUserInfo: vi.fn(),
    revokeToken: vi.fn(),
    logout: vi.fn(),
    replace: replaceMock,
    useRouter: vi.fn(() => ({ replace: replaceMock })),
    useRoute: vi.fn(() => ({ query: {} })),
  }
})

vi.mock('@/services/authService', () => ({
  exchangeCodeForToken: hoisted.exchangeCodeForToken,
  fetchUserInfo: hoisted.fetchUserInfo,
  revokeToken: hoisted.revokeToken,
  logout: hoisted.logout,
}))

vi.mock('vue-router', async () => {
  const actual = await vi.importActual<typeof import('vue-router')>('vue-router')
  return {
    ...actual,
    useRouter: hoisted.useRouter,
    useRoute: hoisted.useRoute,
  }
})

describe('AuthCallbackView', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    sessionStorage.clear()
    setActivePinia(createPinia())
    hoisted.useRouter.mockImplementation(() => ({ replace: hoisted.replace }))
    hoisted.useRoute.mockReturnValue({ query: {} })
    hoisted.exchangeCodeForToken.mockReset()
    hoisted.fetchUserInfo.mockReset()
    hoisted.revokeToken.mockReset()
    hoisted.logout.mockReset()
    hoisted.replace.mockResolvedValue(undefined)
  })

  afterEach(() => {
    sessionStorage.clear()
  })

  it('授權資訊有效時取得 Token 並導向首頁', async () => {
    sessionStorage.setItem('srm.oauth.state', 'state123')
    sessionStorage.setItem('srm.oauth.pkceCodeVerifier', 'verifier123')
    hoisted.useRoute.mockReturnValue({ query: { code: 'authCode', state: 'state123' } })

    hoisted.exchangeCodeForToken.mockResolvedValue({
      accessToken: 'access-token',
      refreshToken: null,
      expiresIn: 3600,
      scope: null,
      tokenType: 'Bearer',
      idToken: null,
    })
    hoisted.fetchUserInfo.mockResolvedValue({
      id: 'user-1',
      email: 'user@advantech.com',
      displayName: 'Advantech User',
      roles: ['ROLE_USER'],
      supplier: false,
      permissions: ['vendors.read'],
    })

    mount(AuthCallbackView)
    await flushPromises()

    expect(hoisted.exchangeCodeForToken).toHaveBeenCalledWith({
      code: 'authCode',
      codeVerifier: 'verifier123',
    })
    expect(hoisted.fetchUserInfo).toHaveBeenCalledTimes(1)
    expect(hoisted.replace).toHaveBeenLastCalledWith({ name: 'home' })
    expect(sessionStorage.getItem('srm.oauth.state')).toBeNull()
    expect(sessionStorage.getItem('srm.oauth.pkceCodeVerifier')).toBeNull()
  })

  it('state 不符時導回登入頁', async () => {
    sessionStorage.setItem('srm.oauth.state', 'expectedState')
    sessionStorage.setItem('srm.oauth.pkceCodeVerifier', 'verifier123')
    hoisted.useRoute.mockReturnValue({ query: { code: 'authCode', state: 'otherState' } })

    mount(AuthCallbackView)
    await flushPromises()

    expect(hoisted.exchangeCodeForToken).not.toHaveBeenCalled()
    expect(hoisted.replace).toHaveBeenLastCalledWith({
      name: 'login',
      query: { error: 'invalid_state' },
    })
    expect(sessionStorage.getItem('srm.oauth.pkceCodeVerifier')).toBeNull()
  })
})
