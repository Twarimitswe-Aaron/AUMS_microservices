<script lang="ts">
  import { auth } from '$lib/stores/auth';
  
  let username = $state('');
  let password = $state('');
  let error = $state('');
  let loading = $state(false);

  let usernameError = $derived(
    username.length > 0 && username.length < 3 
      ? 'Username must be at least 3 characters' 
      : ''
  );

  let passwordError = $derived(
    password.length > 0 && password.length < 4 
      ? 'Password must be at least 4 characters' 
      : ''
  );

  let isFormValid = $derived(
    username.length >= 3 && password.length >= 4
  );

  async function handleLogin() {
    if (!isFormValid) return;
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
        if (data.errors) {
           const errMsgs = Object.values(data.errors).join(', ');
           throw new Error(errMsgs);
        }
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
  <div class="w-full max-w-md p-8 bg-surface border border-primary rounded-sm">
    <h2 class="text-3xl font-bold text-primary mb-6 text-center">Login</h2>
    
    {#if error}
      <div class="bg-red-100 text-red-700 p-3 mb-4 rounded-sm font-semibold">{error}</div>
    {/if}

    <form onsubmit={(e) => { e.preventDefault(); handleLogin(); }} class="flex flex-col gap-4">
      {#if loading}
        <div class="animate-pulse flex flex-col gap-4 mb-2">
          <div><div class="h-4 bg-gray-200 rounded w-1/4 mb-2"></div><div class="h-12 bg-gray-200 rounded-sm w-full"></div></div>
          <div><div class="h-4 bg-gray-200 rounded w-1/4 mb-2"></div><div class="h-12 bg-gray-200 rounded-sm w-full"></div></div>
        </div>
      {:else}
        <div>
          <label for="username" class="block font-semibold mb-1 text-primary">Username</label>
          <input 
            id="username" 
            type="text" 
            bind:value={username} 
            required 
            class="w-full p-3 flat-input rounded-sm {usernameError ? 'border-red-500 bg-red-50' : ''}"
          />
          {#if usernameError}<p class="text-red-500 text-sm mt-1 font-medium">{usernameError}</p>{/if}
        </div>
        
        <div>
          <label for="password" class="block font-semibold mb-1 text-primary">Password</label>
          <input 
            id="password" 
            type="password" 
            bind:value={password} 
            required 
            class="w-full p-3 flat-input rounded-sm {passwordError ? 'border-red-500 bg-red-50' : ''}"
          />
          {#if passwordError}<p class="text-red-500 text-sm mt-1 font-medium">{passwordError}</p>{/if}
        </div>
      {/if}

      <button 
        type="submit" 
        disabled={loading || !isFormValid}
        class="w-full py-3 mt-4 bg-primary text-white font-bold rounded-sm hover:opacity-90 disabled:opacity-50 flex items-center justify-center gap-2"
      >
        {#if loading}
          <div class="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
          Logging in...
        {:else}
          Login
        {/if}
      </button>
    </form>
    
    <p class="mt-6 text-center text-sm">
      Don't have an account? <a href="/register" class="text-primary font-bold hover:underline">Register here</a>
    </p>
  </div>
</div>
