import { writable } from 'svelte/store';

type AuthState = {
  isAuthenticated: boolean;
  token: string | null;
};

function createAuthStore() {
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
    /**
     * Calls the server logout endpoint to blacklist the JWT in Redis,
     * then clears local state and redirects to login.
     */
    logout: async () => {
      let currentToken: string | null = null;
      update(state => { currentToken = state.token; return state; });

      // Notify the server to blacklist the token
      if (currentToken) {
        try {
          await fetch('http://localhost:8080/api/v1/auth/logout', {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${currentToken}` }
          });
        } catch {
          // Fire-and-forget: even if the server call fails, clear local state
        }
      }

      if (typeof localStorage !== 'undefined') {
        localStorage.removeItem('jwt');
      }
      set({ isAuthenticated: false, token: null });
      if (typeof window !== 'undefined') {
        window.location.href = '/login';
      }
    }
  };
}

export const auth = createAuthStore();
