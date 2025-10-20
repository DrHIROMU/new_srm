import { flushPromises, mount } from '@vue/test-utils'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import AuthCallbackView from '../AuthCallbackView.vue'

const hoisted = vi.hoisted(() => {
  const replaceMock = vi.fn()
  return {
    fetchSession: vi.fn(),
    logout: vi.fn(),
    startLogin: vi.fn(),
    replace: replaceMock,
    useRouter: vi.fn(() => ({ replace: replaceMock })),
    useRoute: vi.fn(() => ({ query: {} })),
  }
})

vi.mock('@/services/authService', () => ({
  fetchSession: hoisted.fetchSession,
  logout: hoisted.logout,
  startLogin: hoisted.startLogin,
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
    setActivePinia(createPinia())
    hoisted.useRouter.mockImplementation(() => ({ replace: hoisted.replace }))
    hoisted.useRoute.mockReturnValue({ query: {} })
    hoisted.fetchSession.mockReset()
    hoisted.replace.mockResolvedValue(undefined)
  })

  it('redirects to home when session refresh succeeds', async () => {
    hoisted.fetchSession.mockResolvedValue({
      id: 'user-1',
      email: 'user@advantech.com',
      displayName: 'Advantech User',
      roles: ['ROLE_USER'],
      supplier: false,
      permissions: ['vendors.read'],
    })

    mount(AuthCallbackView)
    await flushPromises()

    expect(hoisted.fetchSession).toHaveBeenCalledTimes(1)
    expect(hoisted.replace).toHaveBeenCalledWith({ name: 'home' })
  })

  it('redirects back to login when session is missing', async () => {
    hoisted.fetchSession.mockRejectedValue({ response: { status: 401 } })

    mount(AuthCallbackView)
    await flushPromises()

    expect(hoisted.fetchSession).toHaveBeenCalledTimes(1)
    expect(hoisted.replace).toHaveBeenCalledWith({
      name: 'login',
      query: { error: 'unauthenticated' },
    })
  })

  it('redirects back to login when refresh fails unexpectedly', async () => {
    hoisted.fetchSession.mockRejectedValue(new Error('boom'))

    mount(AuthCallbackView)
    await flushPromises()

    expect(hoisted.fetchSession).toHaveBeenCalledTimes(1)
    expect(hoisted.replace).toHaveBeenCalledWith({
      name: 'login',
      query: { error: 'callback_error' },
    })
  })
})
