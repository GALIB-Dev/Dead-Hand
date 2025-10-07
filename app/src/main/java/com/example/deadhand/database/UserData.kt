package com.example.deadhand.database

data class UserData(
    val userId: Int,
    val username: String,
    val clearanceLevel: Int,
    val fullName: String?,
    val department: String?,
    val lastLogin: String?,
    val loginCount: Int
) {
    // Agent identification and clearance data
    fun getAgentId(): String = "AGENT-${username.uppercase()}"
    fun getCodename(): String = fullName ?: "CLASSIFIED"
    fun getSecurityClearance(): String = when (clearanceLevel) {
        5 -> "ALPHA" // Highest clearance
        4 -> "BETA"  // High clearance
        3 -> "GAMMA" // Medium clearance
        2 -> "DELTA" // Low clearance
        1 -> "OMEGA" // Lowest clearance
        else -> "UNCLASSIFIED"
    }
    
    fun getDivision(): String = department ?: "UNKNOWN"
    fun getMissionCount(): Int = clearanceLevel * 25 // Missions completed
    fun getSuccessRate(): Int = when (clearanceLevel) {
        5 -> 98 + (loginCount % 3) // 98-100%
        4 -> 95 + (loginCount % 6) // 95-100%
        3 -> 90 + (loginCount % 11) // 90-100%
        2 -> 85 + (loginCount % 16) // 85-100%
        1 -> 80 + (loginCount % 21) // 80-100%
        else -> 75
    }
    
    fun getClearanceColor(): Int {
        return when (clearanceLevel) {
            5 -> android.graphics.Color.parseColor("#FF0040") // Red - ALPHA
            4 -> android.graphics.Color.parseColor("#FF6B00") // Orange - BETA
            3 -> android.graphics.Color.parseColor("#FFD700") // Gold - GAMMA
            2 -> android.graphics.Color.parseColor("#00FF41") // Green - DELTA
            1 -> android.graphics.Color.parseColor("#B300FF") // Purple - OMEGA
            else -> android.graphics.Color.parseColor("#808080") // Gray - UNCLASSIFIED
        }
    }
    
    fun getOperationalStatus(): String = if (loginCount > 10) "ACTIVE" else "PROBATIONARY"
    fun getThreatLevel(): String = when {
        getSuccessRate() >= 95 -> "MINIMAL"
        getSuccessRate() >= 85 -> "LOW"
        getSuccessRate() >= 75 -> "MODERATE"
        else -> "HIGH"
    }
}

data class LoginSession(
    val sessionId: Int,
    val userId: Int,
    val loginTime: String,
    val logoutTime: String?,
    val ipAddress: String?,
    val deviceInfo: String?,
    val status: String
) {
    fun getSessionInfo(): String {
        return if (logoutTime != null) {
            "TERMINATED"
        } else {
            "SECURE CONNECTION ACTIVE"
        }
    }
    
    fun getEncryptionLevel(): String = "AES-256"
    fun getConnectionType(): String = "ENCRYPTED TUNNEL"
    fun getNetworkProtocol(): String = "SHADOWNET"
    
    fun getStatusColor(): Int {
        return when (status) {
            "ACTIVE" -> android.graphics.Color.parseColor("#00FF41") // Spy green
            "LOGGED_OUT" -> android.graphics.Color.parseColor("#808080") // Gray
            "TIMEOUT" -> android.graphics.Color.parseColor("#FF6B00") // Orange
            "FORCED_LOGOUT" -> android.graphics.Color.parseColor("#FF0040") // Red
            "BREACH_DETECTED" -> android.graphics.Color.parseColor("#B300FF") // Purple
            else -> android.graphics.Color.parseColor("#FFFFFF") // White
        }
    }
    
    fun getSecurityAlert(): String {
        return when (status) {
            "ACTIVE" -> "ALL SYSTEMS SECURE"
            "LOGGED_OUT" -> "SESSION TERMINATED"
            "TIMEOUT" -> "AUTO-DISCONNECT ACTIVATED"
            "FORCED_LOGOUT" -> "EMERGENCY PROTOCOL ENGAGED"
            "BREACH_DETECTED" -> "SECURITY BREACH - LOCKDOWN INITIATED"
            else -> "STATUS UNKNOWN"
        }
    }
}

data class UserSettings(
    val theme: String = "dark",
    val autoLogout: Int = 300, // 5 minutes
    val notificationsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val lastSystemCheck: String? = null,
    val preferredLanguage: String = "EN"
)