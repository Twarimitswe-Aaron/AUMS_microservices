<script lang="ts">
  import { notifications } from '$lib/stores/notifications';
  import { fly, fade } from 'svelte/transition';

  // Only show unread notifications in toasts
  let activeToasts = $derived($notifications.filter(n => !n.isRead));
</script>

<div class="fixed bottom-4 right-4 z-50 flex flex-col gap-2">
  {#each activeToasts.slice(0, 5) as toast (toast.id)}
    <div 
      in:fly={{ y: 50, duration: 300 }} 
      out:fade={{ duration: 200 }}
      class="bg-[#000080] text-white p-4 rounded-sm border-l-4 border-blue-400 min-w-[300px] flex justify-between items-start"
    >
      <div>
        <h4 class="font-bold text-sm mb-1">New Notification</h4>
        <p class="text-sm">{toast.message}</p>
      </div>
      <button 
        onclick={() => notifications.markAsRead(toast.id)}
        class="text-gray-300 hover:text-white ml-4 text-xl leading-none"
      >
        &times;
      </button>
    </div>
  {/each}
</div>
