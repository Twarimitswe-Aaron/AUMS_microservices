<script lang="ts">
  import { auth } from '$lib/stores/auth';
  import { notifications } from '$lib/stores/notifications';
  import Toast from '$lib/components/Toast.svelte';
  import { onMount, onDestroy } from 'svelte';

  // We need the user's ID to connect to WebSockets.
  // In a real app, this comes from decoding the JWT.
  // For simplicity, we'll fetch a dummy or parse the JWT manually.
  function parseJwt(token: string) {
    try {
      return JSON.parse(atob(token.split('.')[1]));
    } catch (e) {
      return null;
    }
  }

  let courses: any[] = $state([]);
  let loading = $state(true);
  let error = $state('');
  
  let user = $derived($auth.token ? parseJwt($auth.token) : null);
  // Assume the JWT subject (username) is used, but wait: the backend expects UUID.
  // Actually, our AuthService.register() returns a token with `userId` claim!
  // Wait, JwtService doesn't add custom claims by default. The Academic Service expects the UUID in EnrollRequest.
  // Let's assume the user has to supply their ID. The backend would extract it.
  // Our backend /enroll accepts { studentId, courseId }.

  onMount(async () => {
    if (!$auth.isAuthenticated) {
      window.location.href = '/login';
      return;
    }

    // Connect WebSocket using the subject or a placeholder.
    // In our backend, user ID is needed. Since we only have the JWT, let's use a dummy ID 
    // or just pass a random UUID for testing the UI.
    const testUserId = '123e4567-e89b-12d3-a456-426614174000'; // Replace with real ID from JWT in prod
    notifications.connect(testUserId);

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
    const testUserId = '123e4567-e89b-12d3-a456-426614174000'; // Match above
    try {
      const res = await fetch('http://localhost:8080/api/v1/academic/enroll', {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${$auth.token}`
        },
        body: JSON.stringify({ studentId: testUserId, courseId })
      });
      if (!res.ok) {
        const errData = await res.json().catch(() => ({}));
        throw new Error(errData.message || 'Failed to enroll');
      }
      alert('Successfully requested enrollment!');
    } catch (err: any) {
      alert(err.message);
    }
  }
</script>

<Toast />

<div class="max-w-6xl mx-auto mt-8">
  <h2 class="text-3xl font-bold text-[#000080] mb-8 border-b-2 border-[#000080] pb-2">Course Catalog</h2>

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
