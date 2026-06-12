<script lang="ts">
  import { auth } from '$lib/stores/auth';
  
  let username = $state('');
  let email = $state('');
  let password = $state('');
  let role = $state('STUDENT');
  let error = $state('');
  let loading = $state(false);

  let usernameError = $derived(
    username.length > 0 && (username.length < 3 || username.length > 50 || !/^[a-zA-Z0-9]+$/.test(username)) 
      ? 'Username must be 3-50 alphanumeric characters' 
      : ''
  );
  
  let emailError = $derived(
    email.length > 0 && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email) 
      ? 'Please enter a valid email address' 
      : ''
  );

  let passwordError = $derived(
    password.length > 0 && password.length < 4 
      ? 'Password must be at least 4 characters' 
      : ''
  );

  let isFormValid = $derived(
    username.length >= 3 && /^[a-zA-Z0-9]+$/.test(username) &&
    /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email) &&
    password.length >= 4
  );

  async function handleRegister() {
    if (!isFormValid) return;
    error = '';
    loading = true;
    try {
      const res = await fetch('http://localhost:8080/api/v1/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, email, password, role })
      });
      
      const data = await res.json();
      if (!res.ok) {
        if (data.errors) {
           const errMsgs = Object.values(data.errors).join(', ');
           throw new Error(errMsgs);
        }
        throw new Error(data.message || 'Registration failed');
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
    <h2 class="text-3xl font-bold text-[#000080] mb-6 text-center">Register</h2>
    
    {#if error}
      <div class="bg-red-100 text-red-700 p-3 mb-4 rounded-sm font-semibold">{error}</div>
    {/if}

    <form onsubmit={(e) => { e.preventDefault(); handleRegister(); }} class="flex flex-col gap-4">
      {#if loading}
        <div class="animate-pulse flex flex-col gap-4 mb-2">
          <div><div class="h-4 bg-gray-200 rounded w-1/4 mb-2"></div><div class="h-12 bg-gray-200 rounded-sm w-full"></div></div>
          <div><div class="h-4 bg-gray-200 rounded w-1/4 mb-2"></div><div class="h-12 bg-gray-200 rounded-sm w-full"></div></div>
          <div><div class="h-4 bg-gray-200 rounded w-1/4 mb-2"></div><div class="h-12 bg-gray-200 rounded-sm w-full"></div></div>
          <div><div class="h-4 bg-gray-200 rounded w-1/4 mb-2"></div><div class="h-12 bg-gray-200 rounded-sm w-full"></div></div>
        </div>
      {:else}
        <div>
          <label for="username" class="block font-semibold mb-1 text-[#000080]">Username</label>
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
          <label for="email" class="block font-semibold mb-1 text-[#000080]">Email</label>
          <input 
            id="email" 
            type="email" 
            bind:value={email} 
            required 
            class="w-full p-3 flat-input rounded-sm {emailError ? 'border-red-500 bg-red-50' : ''}"
          />
          {#if emailError}<p class="text-red-500 text-sm mt-1 font-medium">{emailError}</p>{/if}
        </div>
        
        <div>
          <label for="password" class="block font-semibold mb-1 text-[#000080]">Password</label>
          <input 
            id="password" 
            type="password" 
            bind:value={password} 
            required 
            class="w-full p-3 flat-input rounded-sm {passwordError ? 'border-red-500 bg-red-50' : ''}"
          />
          {#if passwordError}<p class="text-red-500 text-sm mt-1 font-medium">{passwordError}</p>{/if}
        </div>

        <div>
          <label for="role" class="block font-semibold mb-1 text-[#000080]">Role</label>
          <select id="role" bind:value={role} class="w-full p-3 flat-input rounded-sm">
            <option value="STUDENT">Student</option>
            <option value="ADMIN">Admin</option>
          </select>
        </div>
      {/if}

      <button 
        type="submit" 
        disabled={loading || !isFormValid}
        class="w-full py-3 mt-4 bg-[#000080] text-white font-bold rounded-sm hover:opacity-90 disabled:opacity-50 flex items-center justify-center gap-2"
      >
        {#if loading}
          <div class="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
          Registering...
        {:else}
          Register
        {/if}
      </button>
    </form>
    
    <p class="mt-6 text-center text-sm">
      Already have an account? <a href="/login" class="text-[#000080] font-bold hover:underline">Login here</a>
    </p>
  </div>
</div>
