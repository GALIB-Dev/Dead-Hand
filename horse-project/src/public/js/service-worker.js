const CACHE_NAME = 'v1';
const VALID_SCHEMES = ['http:', 'https:'];
const ESSENTIAL_FILES = [
    '/',
    '/index.html',
    '/offline.html',
    '/js/verification.js',
    '/js/matrix.js',
    '/socket.io/socket.io.js'
];

function isValidUrl(url) {
    if (!url || url.startsWith('chrome-extension:')) return false;
    try {
        const parsedUrl = new URL(url);
        return VALID_SCHEMES.includes(parsedUrl.protocol) && 
               parsedUrl.origin === self.location.origin;
    } catch {
        return false;
    }
}

self.addEventListener('fetch', event => {
    // Skip non-GET and invalid requests
    if (event.request.method !== 'GET' || !isValidUrl(event.request.url)) {
        return;
    }

    event.respondWith(
        caches.match(event.request)
            .then(response => {
                if (response) return response;
                
                return fetch(event.request)
                    .then(response => {
                        if (!response || response.status !== 200) return response;

                        try {
                            const responseToCache = response.clone();
                            caches.open(CACHE_NAME)
                                .then(cache => {
                                    if (isValidUrl(event.request.url)) {
                                        cache.put(event.request, responseToCache)
                                            .catch(() => {});
                                    }
                                });
                        } catch (error) {
                            console.warn('Cache operation failed:', error);
                        }

                        return response;
                    });
            })
            .catch(() => {
                return caches.match('/offline.html');
            })
    );
});

self.addEventListener('install', event => {
    event.waitUntil(
        caches.open(CACHE_NAME)
            .then(cache => {
                return Promise.allSettled(
                    ESSENTIAL_FILES.map(url => {
                        return fetch(url)
                            .then(response => {
                                if (response.ok) {
                                    return cache.put(url, response);
                                }
                                return Promise.resolve();
                            })
                            .catch(() => Promise.resolve())
                    })
                );
            })
    );
});
