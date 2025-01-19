const socket = io({
    transports: ['websocket', 'polling'],
    reconnection: true,
    reconnectionAttempts: 3,
    reconnectionDelay: 2000,
    secure: false,
    rejectUnauthorized: false,
    timeout: 10000,
    auth: {
        autoConnect: false
    }
});

let isVerifying = false;
let retryCount = 0;
const MAX_RETRIES = 3;
let authTimeout;

document.addEventListener('DOMContentLoaded', function() {
    const passwordForm = document.getElementById('passwordForm');
    const statusElement = document.getElementById('status');
    const submitButton = passwordForm.querySelector('button[type="submit"]');

    function updateStatus(message, isError = false) {
        statusElement.textContent = message;
        statusElement.className = isError ? 'error' : 'status';
    }

    function setLoading(isLoading) {
        submitButton.disabled = isLoading;
        submitButton.textContent = isLoading ? 'Verifying...' : 'Submit';
        isVerifying = isLoading;
    }

    function handleAuthError() {
        clearTimeout(authTimeout);
        setLoading(false);
        localStorage.removeItem('auth_token');
        updateStatus('Authentication failed - Please try again', true);
        
        // Clean reconnection
        if (socket.connected) {
            socket.disconnect();
        }
        setTimeout(() => {
            socket.connect();
        }, 1000);
    }

    function reconnectWithBackoff() {
        if (retryCount >= MAX_RETRIES) {
            updateStatus('Maximum retry attempts reached. Please refresh the page.', true);
            return;
        }
        const backoffDelay = Math.min(1000 * Math.pow(2, retryCount), 10000);
        setTimeout(() => {
            if (!socket.connected) {
                socket.connect();
                retryCount++;
            }
        }, backoffDelay);
    }

    async function verifyPassword(password) {
        try {
            const response = await fetch('/api/verify', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ password })
            });

            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            const data = await response.json();

            if (data.success) {
                // Update socket auth with new token
                socket.auth = { token: data.token };
                if (socket.connected) {
                    socket.disconnect().connect(); // Reconnect with new auth
                }
                return { success: true, data };
            }
            return { success: false, error: data.error || 'Verification failed' };
        } catch (error) {
            console.error('API Error:', error);
            return { success: false, error: 'Network error' };
        }
    }

    passwordForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        if (isVerifying) return;

        const password = document.getElementById('passwordInput').value;
        if (!password.trim()) {
            updateStatus('Password cannot be empty', true);
            return;
        }

        setLoading(true);
        updateStatus('Verifying...');
        
        clearTimeout(authTimeout);
        authTimeout = setTimeout(() => {
            if (isVerifying) {
                handleAuthError();
            }
        }, 8000);

        try {
            const result = await verifyPassword(password);
            
            if (result.success) {
                handleSuccessfulVerification(result.data);
            } else {
                updateStatus(result.error || 'Invalid Password', true);
                passwordForm.reset();
            }
        } catch (error) {
            console.error('Verification error:', error);
            handleAuthError();
        } finally {
            setLoading(false);
        }
    });

    function handleSuccessfulVerification(result) {
        clearTimeout(authTimeout);
        setLoading(false);
        retryCount = 0;
        
        // Store auth token
        if (result.token) {
            localStorage.setItem('auth_token', result.token);
            socket.auth = { token: result.token };
            socket.connect(); // Reconnect with new token
        }

        updateStatus('Verification successful');
        document.getElementById('loginContainer').classList.add('hidden');
        document.getElementById('dashboard').classList.add('visible');
        addLoginHistory(new Date());
        startCountdown(7 * 24 * 60 * 60);

        // Notify server of successful verification
        socket.emit('verified', {
            timestamp: Date.now(),
            clientId: socket.id
        });
    }

    socket.on('connect', () => {
        updateStatus('Connected');
        retryCount = 0;
        document.getElementById('connectionId').textContent = socket.id;
        if (localStorage.getItem('auth_token')) {
            socket.emit('auth', { token: localStorage.getItem('auth_token') });
        }
    });

    socket.on('connect_error', (error) => {
        console.error('Connection error:', error);
        handleAuthError();
    });

    socket.on('disconnect', (reason) => {
        updateStatus(`Disconnected: ${reason}`, true);
    });

    socket.on('error', (error) => {
        setLoading(false);
        updateStatus(`Error: ${error.message}`, true);
    });

    socket.on('activeConnections', (count) => {
        document.getElementById('activeConnections').textContent = count;
    });

    socket.on('verificationResult', (result) => {
        clearTimeout(authTimeout);
        setLoading(false);
        retryCount = 0; // Reset retry counter on successful verification
        
        if (result.success) {
            if (result.token) {
                localStorage.setItem('auth_token', result.token);
                socket.auth.token = result.token;
            }
            updateStatus('Verification successful');
            document.getElementById('loginContainer').classList.add('hidden');
            document.getElementById('dashboard').classList.add('visible');
            addLoginHistory(new Date());
            startCountdown(7 * 24 * 60 * 60);
        } else {
            updateStatus(result.message || 'Invalid Password', true);
            passwordForm.reset();
            localStorage.removeItem('auth_token');
            socket.auth.token = null;
        }
    });

    // Add authentication specific error handler
    socket.on('auth_error', (error) => {
        handleAuthError();
    });

    // Initialize connection
    if (!socket.connected) {
        socket.connect();
    }
});

function startCountdown(seconds) {
    const countdownElement = document.getElementById('countdown');
    
    function updateDisplay() {
        const days = Math.floor(seconds / (24 * 60 * 60));
        const hours = Math.floor((seconds % (24 * 60 * 60)) / 3600);
        const minutes = Math.floor((seconds % 3600) / 60);
        
        countdownElement.textContent = `Time Remaining: ${days}d ${hours}h ${minutes}m`;
        
        if (seconds <= 0) {
            countdownElement.textContent = 'VERIFICATION REQUIRED';
            socket.emit('countdown-expired');
            return;
        }
        
        seconds--;
        setTimeout(updateDisplay, 1000);
    }

    updateDisplay();
}

function addLoginHistory(date) {
    const loginHistory = document.getElementById('loginHistory');
    const entry = document.createElement('div');
    entry.className = 'login-entry';
    entry.textContent = `Verified at ${date.toLocaleString()}`;
    loginHistory.insertBefore(entry, loginHistory.firstChild);
}
