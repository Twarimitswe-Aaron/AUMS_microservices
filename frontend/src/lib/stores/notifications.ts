import { writable } from 'svelte/store';

export type Notification = {
  id: string;
  userId: string;
  message: string;
  isRead: boolean;
  createdAt: string;
};

function createNotificationStore() {
  const { subscribe, update } = writable<Notification[]>([]);
  let socket: WebSocket | null = null;

  return {
    subscribe,
    connect: (userId: string) => {
      if (socket) return;
      socket = new WebSocket(`ws://localhost:8080/ws/notifications?userId=${userId}`);
      
      socket.onmessage = (event) => {
        try {
          const notification: Notification = JSON.parse(event.data);
          update(n => [notification, ...n]);
        } catch (e) {
          console.error('Failed to parse notification', e);
        }
      };

      socket.onclose = () => {
        socket = null;
        // Optionally implement reconnect logic
      };
    },
    disconnect: () => {
      if (socket) {
        socket.close();
        socket = null;
      }
      update(() => []);
    },
    markAsRead: (id: string) => {
      update(notifications => 
        notifications.map(n => n.id === id ? { ...n, isRead: true } : n)
      );
    }
  };
}

export const notifications = createNotificationStore();
