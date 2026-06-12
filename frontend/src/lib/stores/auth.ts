import { writable } from 'svelte/store';

type AuthState = {
  isAuthenticated: boolean;
  token: string | null;
};

function createAuthStore() {
  // Initialize from localStorage safely
  const initialToken = typeof localStorage !== 'undefined' ? localStorage.getItem('jwt') : null;
  
  const { subscribe, set, update } = writable<AuthState>({
    isAuthenticated: !!initialToken,
    token: initialToken
  });

  return {
    subscribe,
    login: (token: string) => {
      if (typeof localStorage !== 'undefined') {
        localStorage.setItem('jwt', token);
      }
      set({ isAuthenticated: true, token });
    },
    logout: () => {
      if (typeof localStorage !== 'undefined') {
        localStorage.removeItem('jwt');
      }
      set({ isAuthenticated: false, token: null });
      // Clear URL params that might hold sensitive info or redirect
      if (typeof window !== 'undefined') {
        window.location.href = '/login';
      }
    }
  };
}

export const auth = createAuthStore();
