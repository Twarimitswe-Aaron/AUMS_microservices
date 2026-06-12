<script lang="ts">
  import { auth } from '$lib/stores/auth';
  
  let username = '';
  let password = '';
  let error = '';
  let loading = false;

  async function handleLogin() {
    error = '';
    loading = true;
    try {
      const res = await fetch('http://localhost:8080/api/v1/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
      });
      
      const data = await res.json();
      if (!res.ok) {
        throw new Error(data.message || 'Login failed');
      }
      
      auth.login(data.token);
      window.location.href = '/dashboard';
    } catch (err: any) {
      error = err.message;
    } finally {
      loading = false;
    }
  }
</script>

<div class="flex items-center justify-center min-h-[80vh]">
  <div class="w-full max-w-md p-8 bg-[#f8fafc] border border-[#000080] rounded-sm">
    <h2 class="text-3xl font-bold text-[#000080] mb-6 text-center">Login</h2>
    
    {#if error}
      <div class="bg-red-100 text-red-700 p-3 mb-4 rounded-sm font-semibold">{error}</div>
    {/if}

    <form onsubmit={(e) => { e.preventDefault(); handleLogin(); }} class="flex flex-col gap-4">
      <div>
        <label for="username" class="block font-semibold mb-1 text-[#000080]">Username</label>
        <input 
          id="username" 
          type="text" 
          bind:value={username} 
          required 
          class="w-full p-3 flat-input rounded-sm"
        />
      </div>
      
      <div>
        <label for="password" class="block font-semibold mb-1 text-[#000080]">Password</label>
        <input 
          id="password" 
          type="password" 
          bind:value={password} 
          required 
          class="w-full p-3 flat-input rounded-sm"
        />
      </div>

      <button 
        type="submit" 
        disabled={loading}
        class="w-full py-3 mt-4 bg-[#000080] text-white font-bold rounded-sm hover:opacity-90 disabled:opacity-50"
      >
        {loading ? 'Logging in...' : 'Login'}
      </button>
    </form>
    
    <p class="mt-6 text-center text-sm">
      Don't have an account? <a href="/register" class="text-[#000080] font-bold hover:underline">Register here</a>
    </p>
  </div>
</div>
