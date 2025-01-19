const express = require('express');
const app = express();
const http = require('http').createServer(app);
const path = require('path');

// Add security middleware
app.use((req, res, next) => {
    res.setHeader('Cross-Origin-Opener-Policy', 'same-origin');
    res.setHeader('Origin-Agent-Cluster', '?1');
    res.setHeader('X-Content-Type-Options', 'nosniff');
    res.setHeader('X-Frame-Options', 'DENY');
    res.setHeader('Cross-Origin-Resource-Policy', 'same-origin');
    next();
});

// Updated Socket.IO configuration with more detailed CORS
const io = require('socket.io')(http, {
    cors: {
        origin: ["http://localhost:3000", "http://192.168.0.108:3000"],
        methods: ["GET", "POST"],
        credentials: true,
        transports: ['websocket', 'polling']
    },
    allowEIO3: true
});

// Add CORS middleware
app.use((req, res, next) => {
    res.header('Access-Control-Allow-Origin', '*');
    res.header('Access-Control-Allow-Methods', 'GET, POST');
    res.header('Access-Control-Allow-Headers', 'Content-Type');
    next();
});

// Serve static files from public directory
app.use(express.static(path.join(__dirname, 'public')));

// Add basic route handler
app.get('/', (req, res) => {
    res.setHeader('Content-Security-Policy', "default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; style-src 'self' 'unsafe-inline'; connect-src 'self' ws: wss:; form-action 'self';");
    res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

// Socket.IO connection handling
io.on('connection', (socket) => {
    console.log('New connection from:', socket.handshake.address);
    console.log('Client connected:', socket.id);
    
    socket.on('verify', (data) => {
        console.log('Verification attempt from:', socket.id);
        // Add your verification logic here
        const isValid = data.password === 'your-password'; // Replace with actual verification
        socket.emit('verificationResult', { success: isValid });
    });

    socket.on('disconnect', () => {
        console.log('Client disconnected:', socket.id);
    });

    socket.on('connect_error', (error) => {
        console.error('Connection error:', error);
    });
});

const PORT = process.env.PORT || 3000;
http.listen(PORT, '0.0.0.0', () => {
    console.log(`Server is running on port ${PORT}`);
    console.log(`Local access: http://localhost:${PORT}`);
    console.log(`Network access: http://192.168.0.108:${PORT}`);
});
