// This file can be used to define and export TypeScript types for the project.

export interface VerificationResult {
  success: boolean;
  timestamp: Date;
  nextCheckIn: Date;
}

export interface HeartbeatResponse {
  success: boolean;
  timestamp: Date;
}

export interface AlertConfig {
  message: string;
  lastVerification: Date;
}