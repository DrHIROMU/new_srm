import apiClient from './apiClient'
import type { CreationOptions } from '@/types/vendor'

export const fetchCreationOptions = async (): Promise<CreationOptions> => {
  try {
    const response = await apiClient.get<CreationOptions>('/vendors/creation-options')
    return response.data
  } catch (error) {
    console.error('Error fetching creation options:', error)
    throw error // Re-throw the error for the component to handle
  }
}
