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

class SpyProfileActivitySimple : AppCompatActivity() {
    
    private lateinit var sharedPreferences: SharedPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spy_profile)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        try {
            // Initialize SharedPreferences
            sharedPreferences = getSharedPreferences("SpyProfile", MODE_PRIVATE)
            
            // Simple initialization
            val editName: EditText = findViewById(R.id.editName)
            val editAge: EditText = findViewById(R.id.editAge)
            val editLocation: EditText = findViewById(R.id.editLocation)
            val editPhone: EditText = findViewById(R.id.editPhone)
            val editEmail: EditText = findViewById(R.id.editEmail)
            val saveButton: Button = findViewById(R.id.saveProfileButton)
            val ageDisplay: TextView = findViewById(R.id.ageDisplay)
            
            // Load saved profile data
            editName.setText(sharedPreferences.getString("name", "Mohammad Al Galib"))
            editAge.setText(sharedPreferences.getString("age", "17"))
            editLocation.setText(sharedPreferences.getString("location", "Bangladesh"))
            editPhone.setText(sharedPreferences.getString("phone", "+880 XXX-XXX-XXXX"))
            editEmail.setText(sharedPreferences.getString("email", "mohammad.galib@classified.gov"))
            
            // Simple age display
            ageDisplay.text = "🎂 AGE: 17 Years (Born January 1, 2007)"
            
            // Set up save button
            saveButton.setOnClickListener {
                val editor = sharedPreferences.edit()
                editor.putString("name", editName.text.toString())
                editor.putString("age", editAge.text.toString())
                editor.putString("location", editLocation.text.toString())
                editor.putString("phone", editPhone.text.toString())
                editor.putString("email", editEmail.text.toString())
                editor.apply()
                
                Toast.makeText(this, "🔐 PROFILE SAVED", Toast.LENGTH_SHORT).show()
            }
            
        } catch (e: Exception) {
            Toast.makeText(this, "Profile loading error", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}