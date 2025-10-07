package com.example.deadhand.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.*

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    
    companion object {
        const val DATABASE_NAME = "DeadHandSystem.db"
        const val DATABASE_VERSION = 1
        
        // Users table
        const val TABLE_USERS = "users"
        const val COLUMN_USER_ID = "user_id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_CLEARANCE_LEVEL = "clearance_level"
        const val COLUMN_FULL_NAME = "full_name"
        const val COLUMN_DEPARTMENT = "department"
        const val COLUMN_LAST_LOGIN = "last_login"
        const val COLUMN_LOGIN_COUNT = "login_count"
        const val COLUMN_CREATED_DATE = "created_date"
        const val COLUMN_IS_ACTIVE = "is_active"
        
        // Login sessions table
        const val TABLE_LOGIN_SESSIONS = "login_sessions"
        const val COLUMN_SESSION_ID = "session_id"
        const val COLUMN_SESSION_USER_ID = "session_user_id"
        const val COLUMN_LOGIN_TIME = "login_time"
        const val COLUMN_LOGOUT_TIME = "logout_time"
        const val COLUMN_IP_ADDRESS = "ip_address"
        const val COLUMN_DEVICE_INFO = "device_info"
        const val COLUMN_SESSION_STATUS = "session_status"
        
        // System settings table
        const val TABLE_SYSTEM_SETTINGS = "system_settings"
        const val COLUMN_SETTING_KEY = "setting_key"
        const val COLUMN_SETTING_VALUE = "setting_value"
        const val COLUMN_SETTING_USER_ID = "setting_user_id"
        const val COLUMN_SETTING_UPDATED = "setting_updated"
    }
    
    override fun onCreate(db: SQLiteDatabase?) {
        // Create users table
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT UNIQUE NOT NULL,
                $COLUMN_PASSWORD TEXT NOT NULL,
                $COLUMN_CLEARANCE_LEVEL INTEGER DEFAULT 1,
                $COLUMN_FULL_NAME TEXT,
                $COLUMN_DEPARTMENT TEXT,
                $COLUMN_LAST_LOGIN TEXT,
                $COLUMN_LOGIN_COUNT INTEGER DEFAULT 0,
                $COLUMN_CREATED_DATE TEXT NOT NULL,
                $COLUMN_IS_ACTIVE INTEGER DEFAULT 1
            )
        """.trimIndent()
        
        // Create login sessions table
        val createSessionsTable = """
            CREATE TABLE $TABLE_LOGIN_SESSIONS (
                $COLUMN_SESSION_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_SESSION_USER_ID INTEGER NOT NULL,
                $COLUMN_LOGIN_TIME TEXT NOT NULL,
                $COLUMN_LOGOUT_TIME TEXT,
                $COLUMN_IP_ADDRESS TEXT,
                $COLUMN_DEVICE_INFO TEXT,
                $COLUMN_SESSION_STATUS TEXT DEFAULT 'ACTIVE',
                FOREIGN KEY($COLUMN_SESSION_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)
            )
        """.trimIndent()
        
        // Create system settings table
        val createSettingsTable = """
            CREATE TABLE $TABLE_SYSTEM_SETTINGS (
                $COLUMN_SETTING_KEY TEXT NOT NULL,
                $COLUMN_SETTING_VALUE TEXT NOT NULL,
                $COLUMN_SETTING_USER_ID INTEGER,
                $COLUMN_SETTING_UPDATED TEXT NOT NULL,
                PRIMARY KEY($COLUMN_SETTING_KEY, $COLUMN_SETTING_USER_ID),
                FOREIGN KEY($COLUMN_SETTING_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)
            )
        """.trimIndent()
        
        db?.execSQL(createUsersTable)
        db?.execSQL(createSessionsTable)
        db?.execSQL(createSettingsTable)
        
        // Insert default users
        insertDefaultUsers(db)
    }
    
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_LOGIN_SESSIONS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SYSTEM_SETTINGS")
        onCreate(db)
    }
    
    private fun insertDefaultUsers(db: SQLiteDatabase?) {
        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        
        val users = listOf(
            // Class A Students
            mapOf(
                COLUMN_USERNAME to "S01A001",
                COLUMN_PASSWORD to "anhs2025",
                COLUMN_CLEARANCE_LEVEL to 5,
                COLUMN_FULL_NAME to "Sakayanagi Arisu",
                COLUMN_DEPARTMENT to "Class A",
                COLUMN_CREATED_DATE to currentTime
            ),
            mapOf(
                COLUMN_USERNAME to "S01A015",
                COLUMN_PASSWORD to "princess",
                COLUMN_CLEARANCE_LEVEL to 4,
                COLUMN_FULL_NAME to "Katsuragi Kohei",
                COLUMN_DEPARTMENT to "Class A",
                COLUMN_CREATED_DATE to currentTime
            ),
            // Class B Students
            mapOf(
                COLUMN_USERNAME to "S01B001",
                COLUMN_PASSWORD to "ichinose",
                COLUMN_CLEARANCE_LEVEL to 4,
                COLUMN_FULL_NAME to "Ichinose Honami",
                COLUMN_DEPARTMENT to "Class B",
                COLUMN_CREATED_DATE to currentTime
            ),
            // Class C Students
            mapOf(
                COLUMN_USERNAME to "S01C001",
                COLUMN_PASSWORD to "dragon",
                COLUMN_CLEARANCE_LEVEL to 3,
                COLUMN_FULL_NAME to "Ryuen Kakeru",
                COLUMN_DEPARTMENT to "Class C",
                COLUMN_CREATED_DATE to currentTime
            ),
            // Class D Students
            mapOf(
                COLUMN_USERNAME to "S01D020",
                COLUMN_PASSWORD to "defective",
                COLUMN_CLEARANCE_LEVEL to 5,
                COLUMN_FULL_NAME to "Ayanokoji Kiyotaka",
                COLUMN_DEPARTMENT to "Class D",
                COLUMN_CREATED_DATE to currentTime
            ),
            mapOf(
                COLUMN_USERNAME to "S01D011",
                COLUMN_PASSWORD to "horikita",
                COLUMN_CLEARANCE_LEVEL to 4,
                COLUMN_FULL_NAME to "Horikita Suzune",
                COLUMN_DEPARTMENT to "Class D",
                COLUMN_CREATED_DATE to currentTime
            ),
            mapOf(
                COLUMN_USERNAME to "S01D013",
                COLUMN_PASSWORD to "kushida",
                COLUMN_CLEARANCE_LEVEL to 3,
                COLUMN_FULL_NAME to "Kushida Kikyo",
                COLUMN_DEPARTMENT to "Class D",
                COLUMN_CREATED_DATE to currentTime
            ),
            // Demo account
            mapOf(
                COLUMN_USERNAME to "DEMO",
                COLUMN_PASSWORD to "demo",
                COLUMN_CLEARANCE_LEVEL to 3,
                COLUMN_FULL_NAME to "Demo Student",
                COLUMN_DEPARTMENT to "Class D",
                COLUMN_CREATED_DATE to currentTime
            )
        )
        
        users.forEach { userData ->
            val values = ContentValues().apply {
                userData.forEach { (key, value) ->
                    when (value) {
                        is String -> put(key, value)
                        is Int -> put(key, value)
                    }
                }
            }
            db?.insert(TABLE_USERS, null, values)
        }
    }
    
    fun authenticateUser(username: String, password: String): UserData? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            null,
            "$COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ? AND $COLUMN_IS_ACTIVE = 1",
            arrayOf(username.uppercase(), password),
            null, null, null
        )
        
        return if (cursor.moveToFirst()) {
            val userData = UserData(
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                clearanceLevel = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CLEARANCE_LEVEL)),
                fullName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME)),
                department = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEPARTMENT)),
                lastLogin = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_LOGIN)),
                loginCount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LOGIN_COUNT))
            ).also {
                cursor.close()
                updateLastLogin(it.userId)
                createLoginSession(it.userId)
            }
            userData
        } else {
            cursor.close()
            null
        }
    }
    
    private fun updateLastLogin(userId: Int) {
        val db = writableDatabase
        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val values = ContentValues().apply {
            put(COLUMN_LAST_LOGIN, currentTime)
            put(COLUMN_LOGIN_COUNT, "$COLUMN_LOGIN_COUNT + 1")
        }
        db.update(TABLE_USERS, values, "$COLUMN_USER_ID = ?", arrayOf(userId.toString()))
    }
    
    private fun createLoginSession(userId: Int): Long {
        val db = writableDatabase
        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val values = ContentValues().apply {
            put(COLUMN_SESSION_USER_ID, userId)
            put(COLUMN_LOGIN_TIME, currentTime)
            put(COLUMN_IP_ADDRESS, "192.168.1.100") // Simulated IP
            put(COLUMN_DEVICE_INFO, "Android Terminal")
            put(COLUMN_SESSION_STATUS, "ACTIVE")
        }
        return db.insert(TABLE_LOGIN_SESSIONS, null, values)
    }
    
    fun getLoginHistory(userId: Int): List<LoginSession> {
        val db = readableDatabase
        val sessions = mutableListOf<LoginSession>()
        val cursor = db.query(
            TABLE_LOGIN_SESSIONS,
            null,
            "$COLUMN_SESSION_USER_ID = ?",
            arrayOf(userId.toString()),
            null, null,
            "$COLUMN_LOGIN_TIME DESC",
            "10" // Last 10 sessions
        )
        
        while (cursor.moveToNext()) {
            sessions.add(
                LoginSession(
                    sessionId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SESSION_ID)),
                    userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SESSION_USER_ID)),
                    loginTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOGIN_TIME)),
                    logoutTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOGOUT_TIME)),
                    ipAddress = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IP_ADDRESS)),
                    deviceInfo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEVICE_INFO)),
                    status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SESSION_STATUS))
                )
            )
        }
        cursor.close()
        return sessions
    }
    
    fun logoutUser(userId: Int) {
        val db = writableDatabase
        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val values = ContentValues().apply {
            put(COLUMN_LOGOUT_TIME, currentTime)
            put(COLUMN_SESSION_STATUS, "LOGGED_OUT")
        }
        db.update(
            TABLE_LOGIN_SESSIONS, 
            values, 
            "$COLUMN_SESSION_USER_ID = ? AND $COLUMN_SESSION_STATUS = 'ACTIVE'", 
            arrayOf(userId.toString())
        )
    }
    
    fun saveUserSetting(userId: Int, key: String, value: String) {
        val db = writableDatabase
        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val values = ContentValues().apply {
            put(COLUMN_SETTING_KEY, key)
            put(COLUMN_SETTING_VALUE, value)
            put(COLUMN_SETTING_USER_ID, userId)
            put(COLUMN_SETTING_UPDATED, currentTime)
        }
        db.insertWithOnConflict(TABLE_SYSTEM_SETTINGS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }
    
    fun getUserSetting(userId: Int, key: String): String? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_SYSTEM_SETTINGS,
            arrayOf(COLUMN_SETTING_VALUE),
            "$COLUMN_SETTING_KEY = ? AND $COLUMN_SETTING_USER_ID = ?",
            arrayOf(key, userId.toString()),
            null, null, null
        )
        
        val value = if (cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SETTING_VALUE))
        } else null
        
        cursor.close()
        return value
    }
}