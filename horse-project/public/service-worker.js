const CACHE_NAME = 'dead-hand-cache-v1';
const OFFLINE_URL = '/offline.html';

self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => {
      return cache.addAll([
        '/',
        '/offline.html',
        '/manifest.json',
        '/icon-192x192.png',
        '/icon-512x512.png',
        '/styles.css',
        '/app.js'
      ]);
    })
  );
});

self.addEventListener('fetch', (event) => {
  event.respondWith(
    caches.match(event.request).then((response) => {
      return response || fetch(event.request).catch(() => {
        return caches.match(OFFLINE_URL);
      });
    })
  );
});

self.addEventListener('push', (event) => {
  const options = event.data.json();
  event.waitUntil(
    self.registration.showNotification('Dead Hand Alert', options)
  );
});
