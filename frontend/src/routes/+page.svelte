<script lang="ts">
  import { auth } from '$lib/stores/auth';
</script>

<div class="flex flex-col items-center justify-center min-h-[70vh]">
  <h1 class="text-4xl font-bold text-primary mb-4">Smart University Management Platform</h1>
  <p class="text-lg text-gray-700 max-w-2xl text-center mb-8">
    Experience a blazing fast, reactive portal for academic success. Built with modern microservices.
  </p>
  
  {#if $auth.isAuthenticated}
    <a href="/dashboard" class="px-8 py-3 bg-primary text-white font-bold rounded-sm text-lg hover:opacity-90">
      Go to Dashboard
    </a>
  {:else}
    <div class="flex gap-4">
      <a href="/login" class="px-8 py-3 border-2 border-primary text-primary font-bold rounded-sm text-lg hover:bg-surface">
        Login
      </a>
      <a href="/register" class="px-8 py-3 bg-primary text-white font-bold rounded-sm text-lg hover:opacity-90">
        Register
      </a>
    </div>
  {/if}
</div>
