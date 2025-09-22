import axios from 'axios'
import type { CreationOptions } from '@/types/vendor'

export const fetchCreationOptions = async (): Promise<CreationOptions> => {
  try {
    const response = await axios.get<CreationOptions>('http://localhost:8084/AdvantechAPI/api/vendors/creation-options')
    return response.data
  } catch (error) {
    console.error('Error fetching creation options:', error)
    throw error // Re-throw the error for the component to handle
  }
}