import express from 'express';
import mongoose from 'mongoose';
import twilio from 'twilio';
import dotenv from 'dotenv';
import helmet from 'helmet';
import rateLimit from 'express-rate-limit';
import path from 'path';
import { Server } from 'socket.io';
import http from 'http';
import winston from 'winston';
import * as prometheusClient from 'prom-client';
import { z } from 'zod';
import { createTerminus } from '@godaddy/terminus';
import webpush from 'web-push';
import cors from 'cors';
import CircuitBreaker from 'opossum';

dotenv.config();

// Validate required environment variables
const requiredEnvVars = [
    'TWILIO_ACCOUNT_SID',
    'TWILIO_AUTH_TOKEN',
    'TO_WHATSAPP_NUMBER',
    'DEAD_HAND_AUTH_KEY',
    'VERIFICATION_PIN',
    'VAPID_PUBLIC_KEY',
    'VAPID_PRIVATE_KEY'
];

for (const envVar of requiredEnvVars) {
    if (!process.env[envVar]) {
        throw new Error(`Missing required environment variable: ${envVar}`);
    }
}

// Setup express app and server
const app = express();
const server = http.createServer(app);
const io = new Server(server);

// Move ALL middleware to the top BEFORE any route handlers
app.use(express.json());
app.use(helmet({
  contentSecurityPolicy: {
    directives: {
      defaultSrc: ["'self'"],
      scriptSrc: ["'self'", "'unsafe-inline'"],
      styleSrc: ["'self'", "'unsafe-inline'"],
      imgSrc: ["'self'", 'data:', 'https:'],
      connectSrc: ["'self'", 'wss:', 'https:']
    }
  }
}));
app.use(cors({
  origin: ['http://localhost:3000','http://192.168.0.1:3000', 'https://yourapp.com'],
  credentials: true
}));
app.use(rateLimit({
  windowMs: 15 * 60 * 1000,
  max: 100
}));

// Static file serving
app.use(express.static(path.join(__dirname, '../src/public')));
app.use('/socket.io', express.static(path.join(__dirname, '../node_modules/socket.io/client-dist')));

// Add API test routes BEFORE other route handlers
app.get('/api/test', (_, res) => {
  console.log('Test endpoint hit'); // Debug log
  res.json({ 
    status: 'API is working',
    time: new Date().toISOString()
  });
});

app.get('/api/status', (req, res) => {
  try {
    const now = Date.now();
    const timeSinceVerification = now - lastVerification.getTime();
    const remainingTime = Math.max(0, VERIFICATION_THRESHOLD - timeSinceVerification);
    
    console.log('Status endpoint hit:', { // Debug log
      lastVerification: lastVerification.toISOString(),
      remainingTime,
      countdownExpired
    });

    res.json({
      lastVerification: lastVerification.toISOString(),
      remainingTime,
      countdownExpired,
      isActive: remainingTime > 0,
      connectedClients: connectedClients.size,
      serverTime: new Date().toISOString()
    });
  } catch (error) {
    console.error('Status check failed:', error);
    res.status(500).json({ error: 'Failed to get status' });
  }
});

// Root route handler
app.get('/', (req, res) => {
  res.sendFile(path.join(__dirname, '../src/public/index.html'));
});

const accountSid = process.env.TWILIO_ACCOUNT_SID;
const authToken = process.env.TWILIO_AUTH_TOKEN;

if (!accountSid || !authToken) {
  throw new Error('TWILIO_ACCOUNT_SID and TWILIO_AUTH_TOKEN must be set');
}

if (!accountSid.startsWith('AC')) {
  throw new Error('TWILIO_ACCOUNT_SID must start with "AC"');
}

// Move client declaration to module scope
let client: twilio.Twilio;

try {
  client = twilio(accountSid, authToken);
} catch (error) {
  console.error('Failed to initialize Twilio client:', error);
  process.exit(1);
}

const fromWhatsAppNumber = 'whatsapp:+14155238886';

// MongoDB connection with retry logic
const connectDB = async () => {
  const MONGO_URI = process.env.MONGO_URI || 'mongodb://127.0.0.1:27017/horse-db';
  
  const connectionOptions: mongoose.ConnectOptions = {
    serverSelectionTimeoutMS: 5000,
    socketTimeoutMS: 30000,
    maxPoolSize: 10,
    // Remove writeConcern and retryWrites as they're causing type issues
  };

  try {
    await mongoose.connect(MONGO_URI, connectionOptions);
    logger.info('Connected to MongoDB');
    return true;
  } catch (err) {
    logger.error('MongoDB connection error:', err);
    console.log('Trying to start MongoDB service...');
    try {
      await new Promise((resolve, reject) => {
        const { exec } = require('child_process');
        // Use mongodb instead of mongod for Kali
        exec('sudo systemctl start mongodb', (error: Error | null) => {
          if (error) {
            console.error('Failed to start MongoDB:', error);
            reject(error);
          } else {
            console.log('MongoDB service started');
            resolve(true);
          }
        });
      });
      // Retry connection after starting service
      await mongoose.connect(MONGO_URI);
      logger.info('Connected to MongoDB after service start');
      return true;
    } catch (startError) {
      logger.error('Failed to recover MongoDB connection:', startError);
      return false;
    }
  }
};

// Replace heartbeat monitoring with last verification check
let lastVerification = new Date();
let countdownExpired = false;
const VERIFICATION_THRESHOLD = 7 * 24 * 60 * 60 * 1000; // 7 days

// Enhance dead-hand route with alert levels
app.post('/dead-hand', async (req, res) => {
  const { authKey } = req.body;
  const toNumber = process.env.TO_WHATSAPP_NUMBER;
  const timeSinceHeartbeat = Date.now() - lastVerification.getTime();
  
  if (!toNumber) {
    return res.status(500).json({ error: 'TO_WHATSAPP_NUMBER not configured' });
  }

  if (authKey !== process.env.DEAD_HAND_AUTH_KEY) {
    return res.status(401).json({ error: 'Unauthorized' });
  }

  try {
    const alertLevel = timeSinceHeartbeat > VERIFICATION_THRESHOLD ? 'CRITICAL' : 'WARNING';
    const message = `${alertLevel}: Dead hand system alert triggered at ${new Date().toISOString()}. 
Last verification: ${lastVerification.toISOString()} (${Math.round(timeSinceHeartbeat/60000)} minutes ago)
Please check on the system owner immediately. 
Location: ${process.env.LOCATION_INFO || 'Not specified'}
Emergency Contact: ${process.env.EMERGENCY_CONTACT || 'Not specified'}`;

    await client.messages.create({
      from: fromWhatsAppNumber,
      to: toNumber,
      body: message
    });

    res.status(200).json({ 
      success: true, 
      timestamp: new Date().toISOString(),
      alertLevel,
      lastVerification: lastVerification.toISOString()
    });
  } catch (error: unknown) {
    console.error('Error sending message:', error);
    const errorMessage = error instanceof Error ? error.message : 'Unknown error';
    res.status(500).json({ error: 'Failed to send message', details: errorMessage });
  }
});

// Simple verification endpoint
app.post('/api/verify', async (req, res) => {
    try {
        const { password } = req.body;
        
        if (!password) {
            return res.status(400).json({
                success: false,
                error: 'Password is required'
            });
        }

        if (password === process.env.VERIFICATION_PIN) {
            const now = new Date();
            lastVerification = now;
            countdownExpired = false;

            // Log successful verification
            logger.info('Verification successful', {
                timestamp: now,
                clientIp: req.ip
            });

            return res.json({
                success: true,
                timestamp: now,
                nextCheckIn: new Date(now.getTime() + VERIFICATION_THRESHOLD),
                token: generateToken() // Add token generation if needed
            });
        }

        // Log failed attempt
        logger.warn('Failed verification attempt', {
            clientIp: req.ip,
            timestamp: new Date()
        });

        return res.status(401).json({
            success: false,
            error: 'Invalid password'
        });
    } catch (error) {
        logger.error('Verification error:', error);
        res.status(500).json({ 
            success: false,
            error: 'Server error'
        });
    }
});

// Check for expired verification every hour
setInterval(async () => {
  const timeSinceVerification = Date.now() - lastVerification.getTime();
  
  if (timeSinceVerification > VERIFICATION_THRESHOLD && !countdownExpired) {
    countdownExpired = true;
    try {
      await client.messages.create({
        from: fromWhatsAppNumber,
        to: process.env.TO_WHATSAPP_NUMBER!,
        body: `🚨 ALERT: System not verified for 7 days!\nLast verification: ${lastVerification.toISOString()}`
      });
    } catch (error) {
      console.error('Failed to send alert:', error);
    }
  }
}, 60 * 60 * 1000);

// Check for expired verification
setInterval(async () => {
  const now = Date.now();
  const timeSinceVerification = now - lastVerification.getTime();
  
  if (timeSinceVerification > VERIFICATION_THRESHOLD) {
    console.log('Verification expired, sending alert...');
    try {
      await client.messages.create({
        from: fromWhatsAppNumber,
        to: process.env.TO_WHATSAPP_NUMBER!,
        body: `🚨 ALERT: System not verified for 7 days!\nLast verification: ${lastVerification.toISOString()}\nEmergency Contact: ${process.env.EMERGENCY_CONTACT}`
      });
    } catch (error) {
      console.error('Failed to send alert:', error);
    }
  }
}, 60 * 60 * 1000); // Check every hour

// Generate a secure random token
function generateToken(): string {
  const crypto = require('crypto');
  return crypto.randomBytes(32).toString('hex');
}

// Get verification status
app.get('/api/status', (req, res) => {
  try {
    const now = Date.now();
    const timeSinceVerification = now - lastVerification.getTime();
    const remainingTime = Math.max(0, VERIFICATION_THRESHOLD - timeSinceVerification);
    
    console.log('Status check:', { // Debug log
      lastVerification: lastVerification.toISOString(),
      remainingTime,
      countdownExpired
    });

    res.json({
      lastVerification: lastVerification.toISOString(),
      remainingTime,
      countdownExpired,
      isActive: remainingTime > 0,
      connectedClients: connectedClients.size,
      serverTime: new Date().toISOString()
    });
  } catch (error) {
    logger.error('Status check failed:', error);
    res.status(500).json({ error: 'Failed to get status' });
  }
});

// Add socket.io connection handling
// Track connected clients
let connectedClients = new Set<string>();

io.on('connection', (socket) => {
  console.log('Client connected:', socket.id);
  connectedClients.add(socket.id);
  
  // Send initial countdown status
  const now = Date.now();
  const timeSinceVerification = now - lastVerification.getTime();
  const remainingSeconds = Math.max(0, 
      Math.floor((VERIFICATION_THRESHOLD - timeSinceVerification) / 1000));
  
  socket.emit('countdown-status', {
      isRunning: remainingSeconds > 0,
      remainingSeconds: remainingSeconds
  });
  
  io.emit('activeConnections', connectedClients.size);

  socket.on('check-countdown', () => {
    try {
      const now = Date.now();
      const timeSinceVerification = now - lastVerification.getTime();
      const remainingSeconds = Math.max(0, 
          Math.floor((VERIFICATION_THRESHOLD - timeSinceVerification) / 1000));
      
      socket.emit('countdown-status', {
          isRunning: !countdownExpired && remainingSeconds > 0,
          remainingSeconds: remainingSeconds
      });
    } catch (error) {
      console.error('Error in check-countdown:', error);
      socket.emit('error', { message: 'Failed to check countdown status' });
    }
  });

  socket.on('verified', async (data) => {
    try {
      if (!data || typeof data !== 'object') {
        throw new Error('Invalid verification data');
      }

      lastVerification = new Date();
      countdownExpired = false;
      
      await client.messages.create({
          from: fromWhatsAppNumber,
          to: process.env.TO_WHATSAPP_NUMBER!,
          body: `✅ System verified at ${lastVerification.toISOString()}\nNext check-in required within 7 days.`
      });

      // Notify all connected clients
      io.emit('countdown-status', {
          isRunning: true,
          remainingSeconds: VERIFICATION_THRESHOLD / 1000
      });
    } catch (error) {
      console.error('Failed to process verification:', error);
      socket.emit('error', { message: 'Verification processing failed' });
    }
  });

  socket.on('countdown-expired', async () => {
    if (countdownExpired) return; // Prevent multiple alerts
    
    countdownExpired = true;
    try {
        await client.messages.create({
            from: fromWhatsAppNumber,
            to: process.env.TO_WHATSAPP_NUMBER!,
            body: `🚨 EMERGENCY ALERT: Verification period expired!\nLast verification: ${lastVerification.toISOString()}\nLocation: ${process.env.LOCATION_INFO}\nEmergency Contact: ${process.env.EMERGENCY_CONTACT}`
        });
    } catch (error) {
        console.error('Failed to send expiration alert:', error);
    }
  });

  socket.on('disconnect', () => {
    console.log(`Client disconnected: ${socket.id}`);
    connectedClients.delete(socket.id);
    io.emit('activeConnections', connectedClients.size);
  });

  // Add room support for multiple monitoring stations
  socket.on('join-station', (stationId: string) => {
    socket.join(`station-${stationId}`);
    io.to(`station-${stationId}`).emit('station-update', {
      connected: true,
      stationId
    });
  });

  // Enhanced real-time monitoring
  socket.on('monitor-stats', async () => {
    const stats = await getSystemHealth();
    socket.emit('system-stats', stats);
  });
});

// Add verification route for mobile
app.get('/verify/:sessionId', (req, res) => {
  res.sendFile(path.join(__dirname, 'public/index.html'));
});

// Add better port handling and error recovery
const startServer = async (retryCount = 0) => {
  const maxRetries = 3;
  const basePort = process.env.PORT ? parseInt(process.env.PORT) : 3000;
  const port = basePort + retryCount;

  try {
    await new Promise((resolve, reject) => {
      const serverInstance = server.listen(port, () => {
        console.log(`Server running on port ${port}`);
        resolve(serverInstance);
      });

      serverInstance.on('error', (error: NodeJS.ErrnoException) => {
        if (error.code === 'EADDRINUSE' && retryCount < maxRetries) {
          console.log(`Port ${port} is busy, trying port ${port + 1}`);
          serverInstance.close();
          startServer(retryCount + 1);
        } else {
          console.error('Server failed to start:', error);
          process.exit(1);
        }
      });
    });
  } catch (error) {
    console.error('Failed to start server:', error);
    process.exit(1);
  }
};

// Initialize server
(async () => {
  const dbConnected = await connectDB();
  if (!dbConnected) {
    console.error('Could not establish MongoDB connection. Exiting...');
    process.exit(1);
  }
  await startServer();
})().catch(error => {
  console.error('Startup failed:', error);
  process.exit(1);
});

// Add error handling for MongoDB connection issues
mongoose.connection.on('error', (error) => {
  console.error('MongoDB connection error:', error);
  // Attempt to reconnect
  setTimeout(() => connectDB(), 5000);
});

mongoose.connection.on('disconnected', () => {
  console.log('MongoDB disconnected, attempting to reconnect...');
  setTimeout(() => connectDB(), 5000);
});

interface SystemHealth {
    cpuUsage: number;
    memoryUsage: number;
    uptime: number;
    lastVerification: Date;
}

// Add system health monitoring
async function getSystemHealth(): Promise<SystemHealth> {
    const os = require('os');
    return {
        cpuUsage: os.loadavg()[0],
        memoryUsage: 1 - (os.freemem() / os.totalmem()),
        uptime: os.uptime(),
        lastVerification: lastVerification
    };
}

// Add type for verification methods
type VerificationMethod = 'pin' | 'fingerprint' | 'face';

// Mock implementations of biometric verification functions
async function verifyFingerprint(credential: string): Promise<boolean> {
    // Implement actual fingerprint verification logic here
    return credential === 'valid-fingerprint';
}

async function verifyFaceId(credential: string): Promise<boolean> {
    // Implement actual face ID verification logic here
    return credential === 'valid-faceid';
}

// Add verifyCredential function
async function verifyCredential(method: VerificationMethod, credential: string): Promise<boolean> {
  switch (method) {
    case 'pin':
      return credential === process.env.VERIFICATION_PIN;
    case 'fingerprint':
      return await verifyFingerprint(credential);
    case 'face':
      return await verifyFaceId(credential);
    default:
      return false;
  }
}

// Advanced logging configuration
const logger = winston.createLogger({
  level: 'info',
  format: winston.format.combine(
    winston.format.timestamp(),
    winston.format.json()
  ),
  transports: [
    new winston.transports.File({ filename: 'error.log', level: 'error' }),
    new winston.transports.File({ filename: 'combined.log' })
  ]
});

// Prometheus metrics
const metrics = {
  verificationCounter: new prometheusClient.Counter({
    name: 'verifications_total',
    help: 'Total number of verifications'
  }),
  lastVerificationGauge: new prometheusClient.Gauge({
    name: 'last_verification_timestamp',
    help: 'Timestamp of last verification'
  })
};

// Input validation schemas
const verificationSchema = z.object({
  method: z.enum(['pin', 'fingerprint', 'face']),
  credential: z.string().min(1)
});

// Enhanced security middleware
app.use(helmet({
  contentSecurityPolicy: {
    directives: {
      defaultSrc: ["'self'"],
      scriptSrc: ["'self'", "'unsafe-inline'"],
      styleSrc: ["'self'", "'unsafe-inline'"],
      imgSrc: ["'self'", 'data:', 'https:'],
      connectSrc: ["'self'", 'wss:', 'https:']
    }
  }
}));

// Rate limiting with Redis
const limiter = rateLimit({
  windowMs: 15 * 60 * 1000,
  max: 100,
  standardHeaders: true,
  legacyHeaders: false,
  keyGenerator: (req) => req.ip ?? 'unknown'
});

// Enhanced alert system
type AlertLevel = 'WARNING' | 'CRITICAL' | 'INFO';

async function sendEnhancedAlert(level: AlertLevel, message: string) {
    const health = await getSystemHealth();
    const enhancedMessage = `
🚨 ${level} ALERT 🚨
${message}

System Status:
CPU Load: ${(health.cpuUsage * 100).toFixed(1)}%
Memory: ${(health.memoryUsage * 100).toFixed(1)}%
Uptime: ${Math.floor(health.uptime / 3600)}h ${Math.floor((health.uptime % 3600) / 60)}m
Last Verification: ${health.lastVerification.toISOString()}

${process.env.LOCATION_INFO}
${process.env.EMERGENCY_CONTACT}`;

    await client.messages.create({
        from: fromWhatsAppNumber,
        to: process.env.TO_WHATSAPP_NUMBER!,
        body: enhancedMessage
    });
}

// Enhanced WebSocket handling with rooms and authentication
io.use(async (socket, next) => {
  // Allow initial connection without auth
  next();
}).on('connection', (socket) => {
  console.log('Client connected:', socket.id);
  connectedClients.add(socket.id);
  
  // Send initial countdown status
  const now = Date.now();
  const timeSinceVerification = now - lastVerification.getTime();
  const remainingSeconds = Math.max(0, 
      Math.floor((VERIFICATION_THRESHOLD - timeSinceVerification) / 1000));
  
  socket.emit('countdown-status', {
      isRunning: remainingSeconds > 0,
      remainingSeconds: remainingSeconds
  });
  
  io.emit('activeConnections', connectedClients.size);

  socket.on('check-countdown', () => {
    try {
      const now = Date.now();
      const timeSinceVerification = now - lastVerification.getTime();
      const remainingSeconds = Math.max(0, 
          Math.floor((VERIFICATION_THRESHOLD - timeSinceVerification) / 1000));
      
      socket.emit('countdown-status', {
          isRunning: !countdownExpired && remainingSeconds > 0,
          remainingSeconds: remainingSeconds
      });
    } catch (error) {
      console.error('Error in check-countdown:', error);
      socket.emit('error', { message: 'Failed to check countdown status' });
    }
  });

  socket.on('verified', async (data) => {
    try {
      if (!data || typeof data !== 'object') {
        throw new Error('Invalid verification data');
      }

      lastVerification = new Date();
      countdownExpired = false;
      
      await client.messages.create({
          from: fromWhatsAppNumber,
          to: process.env.TO_WHATSAPP_NUMBER!,
          body: `✅ System verified at ${lastVerification.toISOString()}\nNext check-in required within 7 days.`
      });

      // Notify all connected clients
      io.emit('countdown-status', {
          isRunning: true,
          remainingSeconds: VERIFICATION_THRESHOLD / 1000
      });
    } catch (error) {
      console.error('Failed to process verification:', error);
      socket.emit('error', { message: 'Verification processing failed' });
    }
  });

  socket.on('countdown-expired', async () => {
    if (countdownExpired) return; // Prevent multiple alerts
    
    countdownExpired = true;
    try {
        await client.messages.create({
            from: fromWhatsAppNumber,
            to: process.env.TO_WHATSAPP_NUMBER!,
            body: `🚨 EMERGENCY ALERT: Verification period expired!\nLast verification: ${lastVerification.toISOString()}\nLocation: ${process.env.LOCATION_INFO}\nEmergency Contact: ${process.env.EMERGENCY_CONTACT}`
        });
    } catch (error) {
        console.error('Failed to send expiration alert:', error);
    }
  });

  socket.on('disconnect', () => {
    console.log(`Client disconnected: ${socket.id}`);
    connectedClients.delete(socket.id);
    io.emit('activeConnections', connectedClients.size);
  });

  // Add room support for multiple monitoring stations
  socket.on('join-station', (stationId: string) => {
    socket.join(`station-${stationId}`);
    io.to(`station-${stationId}`).emit('station-update', {
      connected: true,
      stationId
    });
  });

  // Enhanced real-time monitoring
  socket.on('monitor-stats', async () => {
    const stats = await getSystemHealth();
    socket.emit('system-stats', stats);
  });
});

// Enhanced verification endpoint with circuit breaker
const verificationCircuitBreaker = new CircuitBreaker(
  async (method: string, credential: string) => {
    const verification = await verifyCredential(method as VerificationMethod, credential);
    return verification;
  },
  {
    timeout: 5000,
    errorThresholdPercentage: 50,
    resetTimeout: 30000
  }
);

app.post('/api/verify', async (req, res) => {
    try {
        const { password } = req.body;
        
        console.log('Verification attempt:', {
            password,
            expectedPin: process.env.VERIFICATION_PIN
        });

        if (password === process.env.VERIFICATION_PIN) {
            const now = new Date();
            lastVerification = now;
            countdownExpired = false;

            console.log('Verification successful');
            // Generate a simple auth token
            const token = Math.random().toString(36).substring(2);
            
            return res.json({
                success: true,
                timestamp: now,
                token,
                nextCheckIn: new Date(now.getTime() + VERIFICATION_THRESHOLD)
            });
        }

        console.log('Verification failed - invalid password');
        return res.status(401).json({
            success: false,
            error: 'Invalid password'
        });
    } catch (error) {
        console.error('Verification error:', error);
        res.status(500).json({ 
            success: false,
            error: 'Server error'
        });
    }
});

// Graceful shutdown handling
createTerminus(server, {
  signal: 'SIGINT',
  healthChecks: {
    '/healthcheck': async () => {
      const health = await getSystemHealth();
      if (health.cpuUsage > 0.9 || health.memoryUsage > 0.9) {
        throw new Error('System resources critical');
      }
      return health;
    }
  },
  onSignal: async () => {
    logger.info('Server is shutting down');
    await mongoose.connection.close();
    await new Promise((resolve) => io.close(resolve));
  }
});

// Enable CORS for mobile apps
app.use(cors({
  origin: ['http://localhost:3000','http://192.168.0.1:3000', 'https://yourapp.com'],
  credentials: true
}));

// Configure Web Push
if (process.env.VAPID_PUBLIC_KEY && process.env.VAPID_PRIVATE_KEY) {
    webpush.setVapidDetails(
        'mailto:admin@yourapp.com', // Replace with your email
        process.env.VAPID_PUBLIC_KEY,
        process.env.VAPID_PRIVATE_KEY
    );
} else {
    console.warn('VAPID keys not set, web push notifications will not work');
}

// Mobile-specific verification endpoint
app.post('/api/mobile/verify', async (req, res) => {
  try {
    const { pin, deviceId } = req.body;
    
    if (pin !== process.env.VERIFICATION_PIN) {
      return res.status(401).json({ 
        success: false, 
        error: 'Invalid PIN',
        errorCode: 'INVALID_PIN'
      });
    }

    const now = new Date();
    lastVerification = now;

    // Store device token for push notifications
    if (deviceId) {
      await storeDeviceToken(deviceId);
    }

    await sendEnhancedAlert('INFO', 'Mobile verification successful');

    res.json({
      success: true,
      timestamp: now,
      nextCheckIn: new Date(now.getTime() + VERIFICATION_THRESHOLD),
      remainingTime: VERIFICATION_THRESHOLD,
      offlineSupport: true
    });
  } catch (error) {
    logger.error('Mobile verification failed:', error);
    res.status(500).json({
      error: 'Verification failed',
      errorCode: 'SERVER_ERROR',
      retry: true
    });
  }
});

// PWA manifest route
app.get('/manifest.json', (req, res) => {
  res.json({
    name: 'Dead Hand Verification',
    short_name: 'DeadHand',
    start_url: '/',
    display: 'standalone',
    background_color: '#ffffff',
    theme_color: '#000000',
    icons: [
      {
        src: '/icon-192x192.png',
        sizes: '192x192',
        type: 'image/png'
      },
      {
        src: '/icon-512x512.png',
        sizes: '512x512',
        type: 'image/png'
      }
    ]
  });
});

// Service Worker route
app.get('/service-worker.js', (req, res) => {
  res.sendFile(path.join(__dirname, '../public/service-worker.js'));
});

// Mobile status check endpoint
app.get('/api/mobile/status', async (req, res) => {
  try {
    const now = Date.now();
    const timeSinceVerification = now - lastVerification.getTime();
    const remainingTime = Math.max(0, VERIFICATION_THRESHOLD - timeSinceVerification);

    res.json({
      isActive: remainingTime > 0,
      remainingTime,
      lastVerification: lastVerification.toISOString(),
      nextCheckIn: new Date(lastVerification.getTime() + VERIFICATION_THRESHOLD).toISOString(),
      systemHealth: await getSystemHealth(),
      offlineCapable: true
    });
  } catch (error) {
    res.status(500).json({
      error: 'Status check failed',
      errorCode: 'STATUS_ERROR',
      fallbackMode: true
    });
  }
});

// Offline support endpoint
app.get('/api/mobile/offline-data', (req, res) => {
  res.json({
    verificationThreshold: VERIFICATION_THRESHOLD,
    emergencyContacts: process.env.EMERGENCY_CONTACT,
    locationInfo: process.env.LOCATION_INFO,
    offlineVerificationEnabled: true,
    syncRequired: false
  });
});

// Enhanced error handling for mobile
app.use((err: Error, req: express.Request, res: express.Response, next: express.NextFunction) => {
  logger.error('Mobile API error:', err);
  res.status(500).json({
    error: process.env.NODE_ENV === 'production' ? 'Internal server error' : err.message,
    errorCode: 'API_ERROR',
    retry: true,
    offline: {
      available: true,
      lastSync: lastVerification
    }
  });
});

// Store device token for push notifications
async function storeDeviceToken(deviceId: string) {
  try {
    await mongoose.model('Device').findOneAndUpdate(
      { deviceId },
      { lastSeen: new Date() },
      { upsert: true }
    );
  } catch (error) {
    logger.error('Failed to store device token:', error);
  }
}

// Send push notification to all registered devices
async function sendPushNotifications(message: string) {
  try {
    const devices = await mongoose.model('Device').find({
      lastSeen: { $gte: new Date(Date.now() - 30 * 24 * 60 * 60 * 1000) }
    });

    for (const device of devices) {
      try {
        await webpush.sendNotification(device.subscription, JSON.stringify({
          title: 'Dead Hand Alert',
          body: message,
          icon: '/icon-192x192.png',
          badge: '/badge.png',
          timestamp: Date.now()
        }));
      } catch (error) {
        logger.error(`Failed to send push to device ${device.deviceId}:`, error);
      }
    }
  } catch (error) {
    logger.error('Failed to send push notifications:', error);
  }
}

// Add debugging route to check current verification status
app.get('/api/status', (req, res) => {
  try {
    const now = Date.now();
    const timeSinceVerification = now - lastVerification.getTime();
    const remainingTime = Math.max(0, VERIFICATION_THRESHOLD - timeSinceVerification);
    
    console.log('Status check:', { // Debug log
      lastVerification: lastVerification.toISOString(),
      remainingTime,
      countdownExpired
    });

    res.json({
      lastVerification: lastVerification.toISOString(),
      remainingTime,
      countdownExpired,
      isActive: remainingTime > 0,
      connectedClients: connectedClients.size,
      serverTime: new Date().toISOString()
    });
  } catch (error) {
    logger.error('Status check failed:', error);
    res.status(500).json({ error: 'Failed to get status' });
  }
});

// Update socket connection handler to immediately send status
io.on('connection', (socket) => {
  console.log('Client connected:', socket.id);
  connectedClients.add(socket.id);
  
  // Send immediate status update
  const now = Date.now();
  const timeSinceVerification = now - lastVerification.getTime();
  const remainingSeconds = Math.max(0, 
    Math.floor((VERIFICATION_THRESHOLD - timeSinceVerification) / 1000));
  
  // Debug log
  console.log('Sending initial status:', {
    isRunning: !countdownExpired && remainingSeconds > 0,
    remainingSeconds,
    lastVerification: lastVerification.toISOString()
  });

  socket.emit('countdown-status', {
    isRunning: !countdownExpired && remainingSeconds > 0,
    remainingSeconds,
    lastVerification: lastVerification.toISOString()
  });
  
  // ...rest of socket handling code...
});

// Add error handling middleware
app.use((err: Error, req: express.Request, res: express.Response, next: express.NextFunction) => {
  logger.error('API Error:', err);
  res.status(500).json({
    success: false,
    error: process.env.NODE_ENV === 'production' ? 'Internal server error' : err.message,
    message: 'Something went wrong, please try again'
  });
});

// Add a test endpoint to verify routes are working
app.get('/api/test', (_, res) => {
  res.json({ status: 'API is working' });
});

