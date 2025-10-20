import { mount } from '@vue/test-utils'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import LoginView from '../LoginView.vue'

const hoisted = vi.hoisted(() => ({
  startLogin: vi.fn(),
  useRoute: vi.fn(() => ({ query: {} })),
}))

vi.mock('@/services/authService', () => ({
  startLogin: hoisted.startLogin,
  fetchSession: vi.fn(),
  logout: vi.fn(),
}))

vi.mock('vue-router', async () => {
  const actual = await vi.importActual<typeof import('vue-router')>('vue-router')
  return {
    ...actual,
    useRoute: hoisted.useRoute,
  }
})

describe('LoginView', () => {
  const originalLocation = window.location
  const assignSpy = vi.fn()

  beforeEach(() => {
    vi.clearAllMocks()
    setActivePinia(createPinia())
    hoisted.useRoute.mockReturnValue({ query: {} })
    Object.defineProperty(window, 'location', {
      writable: true,
      value: {
        assign: assignSpy,
      },
    })
  })

  it('calls BFF login endpoint when button clicked', async () => {
    const wrapper = mount(LoginView)
    const button = wrapper.get('button')

    expect(button.attributes('disabled')).toBeUndefined()

    await button.trigger('click')

    expect(hoisted.startLogin).toHaveBeenCalledTimes(1)
    expect(assignSpy).toHaveBeenCalledTimes(0)
    expect(button.attributes('disabled')).toBeDefined()
  })

  it('displays error when query contains error flag', () => {
    hoisted.useRoute.mockReturnValue({ query: { error: 'oauth_failed' } })
    const wrapper = mount(LoginView)

    expect(wrapper.text()).toContain('登入失敗，請重新嘗試')
  })

  afterEach(() => {
    Object.defineProperty(window, 'location', {
      writable: true,
      value: originalLocation,
    })
  })
})
