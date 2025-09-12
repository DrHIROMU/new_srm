import axios from 'axios';
import type { LoginCredentials } from '@/types/auth';

const apiClient = axios.create({
  baseURL: 'http://localhost:8084/AdvantechAPI/api',
  timeout: 10000,
});

export const login = (credentials: LoginCredentials) => {
  return apiClient.post('/auth/login', credentials);
};
