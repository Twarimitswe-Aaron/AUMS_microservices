import { writable } from 'svelte/store';

export type Notification = {
  id: string;
  userId: string;
  message: string;
  isRead: boolean;
  createdAt: string;
};

function createNotificationStore() {
  const { subscribe, set, update } = writable<Notification[]>([]);
  let socket: WebSocket | null = null;

  return {
    subscribe,

    /**
     * Connect to the WebSocket using the JWT token for authentication.
     * The server validates the token during the handshake.
     */
    connect: (token: string) => {
      if (socket) return;
      socket = new WebSocket(`ws://localhost:8080/ws/notifications?token=${token}`);

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
      };
    },

    /**
     * Loads historical notifications fetched from the REST endpoint
     * into the store (without duplicating ones already received via WS).
     */
    loadHistorical: (historical: Notification[]) => {
      update(current => {
        const existingIds = new Set(current.map(n => n.id));
        const newOnes = historical.filter(n => !existingIds.has(n.id));
        return [...current, ...newOnes];
      });
    },

    disconnect: () => {
      if (socket) {
        socket.close();
        socket = null;
      }
      set([]);
    },

    markAsRead: (id: string) => {
      update(notifications =>
        notifications.map(n => n.id === id ? { ...n, isRead: true } : n)
      );
    }
  };
}

export const notifications = createNotificationStore();
