<script lang="ts">
  import { auth } from '$lib/stores/auth';
  import { notifications } from '$lib/stores/notifications';
  import Toast from '$lib/components/Toast.svelte';
  import { onMount, onDestroy } from 'svelte';

  /**
   * Decodes a JWT payload without verification (verification is done server-side).
   * Used only to extract display data like userId and role.
   */
  function parseJwt(token: string): Record<string, string> | null {
    try {
      return JSON.parse(atob(token.split('.')[1]));
    } catch (e) {
      return null;
    }
  }

  let courses: any[] = $state([]);
  let loading = $state(true);
  let error = $state('');

  // Extract userId and role from the decoded JWT — no hardcoded values
  let jwtPayload = $derived($auth.token ? parseJwt($auth.token) : null);
  let currentUserId = $derived(jwtPayload?.userId ?? null);
  let currentRole = $derived(jwtPayload?.role ?? null);

  onMount(async () => {
    if (!$auth.isAuthenticated || !currentUserId) {
      window.location.href = '/login';
      return;
    }

    // Connect WebSocket using the JWT token — server validates it during handshake
    notifications.connect($auth.token!);

    // Load historical notifications from the REST endpoint
    try {
      const histRes = await fetch('http://localhost:8080/api/v1/notifications', {
        headers: { 'Authorization': `Bearer ${$auth.token}` }
      });
      if (histRes.ok) {
        const historical = await histRes.json();
        notifications.loadHistorical(historical);
      }
    } catch {
      // Non-critical: live WS will still deliver new notifications
    }

    try {
      const res = await fetch('http://localhost:8080/api/v1/academic/courses', {
        headers: { 'Authorization': `Bearer ${$auth.token}` }
      });
      if (!res.ok) throw new Error('Failed to load courses');
      courses = await res.json();
    } catch (err: any) {
      error = err.message;
    } finally {
      loading = false;
    }
  });

  onDestroy(() => {
    notifications.disconnect();
  });

  async function enroll(courseId: string) {
    if (!currentUserId) {
      alert('Session expired. Please log in again.');
      return;
    }
    try {
      const res = await fetch('http://localhost:8080/api/v1/academic/enroll', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${$auth.token}`
        },
        body: JSON.stringify({ studentId: currentUserId, courseId })
      });
      if (!res.ok) {
        const errData = await res.json().catch(() => ({}));
        throw new Error(errData.message || 'Failed to enroll');
      }
      // Refresh courses to get updated enrollment count
      const refreshed = await fetch('http://localhost:8080/api/v1/academic/courses', {
        headers: { 'Authorization': `Bearer ${$auth.token}` }
      });
      if (refreshed.ok) courses = await refreshed.json();
      alert('Successfully enrolled!');
    } catch (err: any) {
      alert(err.message);
    }
  }
</script>

<Toast />

<div class="max-w-6xl mx-auto mt-8">
  <div class="flex justify-between items-center mb-8 border-b-2 border-[#000080] pb-2">
    <h2 class="text-3xl font-bold text-[#000080]">Course Catalog</h2>
    {#if currentRole}
      <span class="bg-[#000080] text-white text-sm font-bold px-3 py-1 rounded-sm">{currentRole}</span>
    {/if}
  </div>

  {#if loading}
    <p class="text-xl text-gray-500 font-semibold">Loading courses from Redis...</p>
  {:else if error}
    <div class="bg-red-100 text-red-700 p-4 rounded-sm font-semibold">{error}</div>
  {:else}
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      {#each courses as course}
        <div class="bg-[#f8fafc] border border-[#000080] rounded-sm p-6 flex flex-col justify-between hover:-translate-y-1 transition-transform duration-200">
          <div>
            <div class="flex justify-between items-start mb-2">
              <h3 class="text-xl font-bold text-[#000080]">{course.title}</h3>
              <span class="bg-[#000080] text-white text-xs font-bold px-2 py-1 rounded-sm">{course.code}</span>
            </div>
            <p class="text-gray-700 mb-4 font-semibold">
              Capacity: {course.currentEnrollment} / {course.capacity}
            </p>
          </div>
          <button
            onclick={() => enroll(course.id)}
            disabled={course.currentEnrollment >= course.capacity}
            class="w-full py-2 bg-white border-2 border-[#000080] text-[#000080] font-bold rounded-sm hover:bg-[#000080] hover:text-white disabled:opacity-50 disabled:hover:bg-white disabled:hover:text-[#000080] transition-colors"
          >
            {course.currentEnrollment >= course.capacity ? 'Full' : 'Enroll'}
          </button>
        </div>
      {/each}
    </div>
  {/if}
</div>
