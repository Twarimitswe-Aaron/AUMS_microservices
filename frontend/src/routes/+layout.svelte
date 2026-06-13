<script lang="ts">
  import '../app.css';
  import { onMount } from 'svelte';
  import { auth } from '$lib/stores/auth';
  
  let { children } = $props();

  onMount(() => {
    // Implement localStorage tampering protection
    const syncAuth = () => {
      const storedToken = localStorage.getItem('jwt');
      const currentAuth = $auth;
      
      // If token in store differs from localStorage, force override localStorage
      if (currentAuth.token && storedToken !== currentAuth.token) {
        localStorage.setItem('jwt', currentAuth.token);
      } else if (!currentAuth.token && storedToken) {
        // If user is logged out but token exists, remove it
        localStorage.removeItem('jwt');
      }
    };

    window.addEventListener('storage', syncAuth);
    return () => window.removeEventListener('storage', syncAuth);
  });
</script>

<div class="min-h-screen bg-white text-[#0f172a]">
  <!-- Simple Top Bar -->
  <header class="bg-[#000080] text-white p-4 flex justify-between items-center rounded-sm mx-2 mt-2">
    <h1 class="font-bold text-xl tracking-wide">SUMP Platform</h1>
    <nav>
      {#if $auth.isAuthenticated}
        <button onclick={async () => await auth.logout()} class="px-4 py-2 bg-white text-[#000080] font-semibold rounded-sm hover:opacity-90 transition-opacity">
          Logout
        </button>
      {:else}
        <a href="/login" class="mr-4 text-white hover:underline">Login</a>
        <a href="/register" class="px-4 py-2 bg-white text-[#000080] font-semibold rounded-sm hover:opacity-90 transition-opacity">Register</a>
      {/if}
    </nav>
  </header>

  <main class="p-6">
    {@render children()}
  </main>
</div>
