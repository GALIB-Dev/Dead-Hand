package com.example.deadhand.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.analytics.FirebaseAnalytics
import android.content.Context

/**
 * Firebase Helper Class for Dead Hand Spy App
 * Manages all Firebase services and operations
 */
class FirebaseManager private constructor() {
    
    companion object {
        @Volatile
        private var INSTANCE: FirebaseManager? = null
        
        fun getInstance(): FirebaseManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FirebaseManager().also { INSTANCE = it }
            }
        }
    }
    
    // Firebase Services
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }
    private var analytics: FirebaseAnalytics? = null
    
    /**
     * Initialize Firebase Analytics
     */
    fun initializeAnalytics(context: Context) {
        analytics = FirebaseAnalytics.getInstance(context)
    }
    
    /**
     * Agent Authentication
     */
    fun authenticateAgent(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Agent authenticated successfully")
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }
    
    /**
     * Create new agent account
     */
    fun createAgentAccount(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Agent account created successfully")
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }
    
    /**
     * Save agent profile to Firestore
     */
    fun saveAgentProfile(agentData: Map<String, Any>, callback: (Boolean, String?) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("agents")
                .document(currentUser.uid)
                .set(agentData)
                .addOnSuccessListener {
                    callback(true, "Agent profile saved")
                }
                .addOnFailureListener { e ->
                    callback(false, e.message)
                }
        } else {
            callback(false, "No authenticated agent")
        }
    }
    
    /**
     * Get agent profile from Firestore
     */
    fun getAgentProfile(callback: (Map<String, Any>?, String?) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("agents")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        callback(document.data, null)
                    } else {
                        callback(null, "Agent profile not found")
                    }
                }
                .addOnFailureListener { e ->
                    callback(null, e.message)
                }
        } else {
            callback(null, "No authenticated agent")
        }
    }
    
    /**
     * Log mission activity for analytics
     */
    fun logMissionActivity(missionType: String, parameters: Map<String, String>? = null) {
        analytics?.let { analytics ->
            val bundle = android.os.Bundle()
            bundle.putString("mission_type", missionType)
            parameters?.forEach { (key, value) ->
                bundle.putString(key, value)
            }
            analytics.logEvent("spy_mission_activity", bundle)
        }
    }
    
    /**
     * Save intel data
     */
    fun saveIntelData(intelData: Map<String, Any>, callback: (Boolean, String?) -> Unit) {
        firestore.collection("intel")
            .add(intelData)
            .addOnSuccessListener {
                callback(true, "Intel data saved")
            }
            .addOnFailureListener { e ->
                callback(false, e.message)
            }
    }
    
    /**
     * Get current authenticated agent ID
     */
    fun getCurrentAgentId(): String? {
        return auth.currentUser?.uid
    }
    
    /**
     * Sign out agent
     */
    fun signOutAgent() {
        auth.signOut()
    }
    
    /**
     * Check if agent is authenticated
     */
    fun isAgentAuthenticated(): Boolean {
        return auth.currentUser != null
    }
}