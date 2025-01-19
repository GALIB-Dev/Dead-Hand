document.getElementById('authButton').addEventListener('click', async () => {
    try {
        const pin = document.getElementById('pinInput').value;
        const status = document.getElementById('status');
        
        if (!pin) {
            status.textContent = 'Please enter your PIN';
            status.className = 'error';
            return;
        }

        status.textContent = 'Verifying...';
        status.className = '';

        // Send verification to server
        const response = await fetch('/api/verify-alive', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                pin: pin
            })
        });

        const result = await response.json();
        
        if (result.success) {
            status.textContent = 'Verification successful! See you in 7 days.';
            document.getElementById('pinInput').value = '';
            startCountdown(7 * 24 * 60 * 60); // 7 days in seconds
            document.getElementById('authButton').disabled = true;
            document.getElementById('pinInput').disabled = true;
        } else {
            status.textContent = 'Verification failed: ' + (result.error || 'Invalid PIN');
            status.className = 'error';
        }
    } catch (error) {
        console.error('Verification failed:', error);
        document.getElementById('status').textContent = 'Verification failed: ' + error.message;
        document.getElementById('status').className = 'error';
    }
});

function startCountdown(seconds) {
    const countdownElement = document.getElementById('countdown');
    updateCountdown();

    function updateCountdown() {
        const days = Math.floor(seconds / (24 * 60 * 60));
        const hours = Math.floor((seconds % (24 * 60 * 60)) / 3600);
        const minutes = Math.floor((seconds % 3600) / 60);
        const remainingSeconds = seconds % 60;

        countdownElement.textContent = 
            `Next check-in in: ${days}d ${hours}h ${minutes}m ${remainingSeconds}s`;

        if (seconds > 0) {
            seconds--;
            setTimeout(updateCountdown, 1000);
        } else {
            countdownElement.textContent = 'Check-in required!';
            document.getElementById('authButton').disabled = false;
            document.getElementById('pinInput').disabled = false;
        }
    }
}
