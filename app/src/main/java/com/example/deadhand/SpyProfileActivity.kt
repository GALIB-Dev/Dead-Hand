package com.example.deadhand

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.*
import java.text.SimpleDateFormat

class SpyProfileActivity : AppCompatActivity() {
    
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editName: EditText
    private lateinit var editAge: EditText
    private lateinit var editLocation: EditText
    private lateinit var editPhone: EditText
    private lateinit var editEmail: EditText
    private lateinit var saveButton: Button
    private lateinit var ageDisplay: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spy_profile)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // Initialize SharedPreferences for saving profile data
        sharedPreferences = getSharedPreferences("SpyProfile", MODE_PRIVATE)
        
        try {
            // Initialize views safely
            editName = findViewById(R.id.editName)
            editAge = findViewById(R.id.editAge)
            editLocation = findViewById(R.id.editLocation)
            editPhone = findViewById(R.id.editPhone)
            editEmail = findViewById(R.id.editEmail)
            saveButton = findViewById(R.id.saveProfileButton)
            ageDisplay = findViewById(R.id.ageDisplay)
            
            // Load saved profile data
            loadProfileData()
            
            // Calculate and display real-time age
            updateAge()
            
            // Set up save button
            saveButton.setOnClickListener {
                saveProfileData()
            }
            
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading profile: ${e.message}", Toast.LENGTH_SHORT).show()
            finish() // Close activity if initialization fails
        }
    }
    
    private fun loadProfileData() {
        editName.setText(sharedPreferences.getString("name", "Mohammad Al Galib"))
        editAge.setText(sharedPreferences.getString("age", "17"))
        editLocation.setText(sharedPreferences.getString("location", "Bangladesh"))
        editPhone.setText(sharedPreferences.getString("phone", "+880 XXX-XXX-XXXX"))
        editEmail.setText(sharedPreferences.getString("email", "mohammad.galib@classified.gov"))
    }
    
    private fun updateAge() {
        try {
            // Birth date: January 1, 2007
            val birthCalendar = Calendar.getInstance()
            birthCalendar.set(2007, Calendar.JANUARY, 1, 0, 0, 0)
            birthCalendar.set(Calendar.MILLISECOND, 0)
            
            val currentCalendar = Calendar.getInstance()
            
            val birthTimeMillis = birthCalendar.timeInMillis
            val currentTimeMillis = currentCalendar.timeInMillis
            val diffMillis = currentTimeMillis - birthTimeMillis
            
            // Calculate exact age - simplified to prevent crashes
            val totalSeconds = diffMillis / 1000
            val years = (totalSeconds / (365.25 * 24 * 60 * 60)).toInt()
            val remainingSeconds = totalSeconds % (365.25 * 24 * 60 * 60).toLong()
            val days = (remainingSeconds / (24 * 60 * 60)).toInt()
            val hours = ((remainingSeconds % (24 * 60 * 60)) / (60 * 60)).toInt()
            val minutes = ((remainingSeconds % (60 * 60)) / 60).toInt()
            val seconds = (remainingSeconds % 60).toInt()
            
            val ageText = "🎂 AGE: $years Years, $days Days, $hours Hours, $minutes Minutes, $seconds Seconds"
            
            // Safe UI update
            runOnUiThread {
                ageDisplay?.text = ageText
            }
            
            // Update every second with error handling
            ageDisplay?.postDelayed({ 
                if (!isFinishing && !isDestroyed) {
                    updateAge()
                }
            }, 1000)
            
        } catch (e: Exception) {
            // Fallback if calculation fails
            runOnUiThread {
                ageDisplay?.text = "🎂 AGE: 17 Years (Born January 1, 2007)"
            }
        }
    }
    
    private fun saveProfileData() {
        val editor = sharedPreferences.edit()
        editor.putString("name", editName.text.toString())
        editor.putString("age", editAge.text.toString())
        editor.putString("location", editLocation.text.toString())
        editor.putString("phone", editPhone.text.toString())
        editor.putString("email", editEmail.text.toString())
        editor.apply()
        
        Toast.makeText(this, "🔐 PROFILE SAVED TO SECURE DATABASE", Toast.LENGTH_SHORT).show()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Stop age updates when activity is destroyed
        ageDisplay?.removeCallbacks(null)
    }
}