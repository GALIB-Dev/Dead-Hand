package com.example.deadhand

import android.os.Bundle
import android.app.Activity
import android.content.Intent
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import com.example.deadhand.database.DatabaseHelper
import com.example.deadhand.database.UserData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {
    
    private var currentUser: UserData? = null
    private var loginAttempts = 0
    private val maxAttempts = 3
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize database
        databaseHelper = DatabaseHelper(this)
        
        if (currentUser == null) {
            showLoginScreen()
        } else {
            showMainSystem()
        }
    }
    
    private fun showLoginScreen() {
        val scrollView = ScrollView(this)
        scrollView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        scrollView.isFillViewport = true
        
        val loginLayout = LinearLayout(this)
        loginLayout.orientation = LinearLayout.VERTICAL
        loginLayout.setBackgroundColor(android.graphics.Color.parseColor("#0D0D0D"))
        loginLayout.setPadding(16, 24, 16, 24)
        loginLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        loginLayout.gravity = android.view.Gravity.CENTER_VERTICAL
        
        // Spy Terminal Header
        val headerLayout = LinearLayout(this)
        headerLayout.orientation = LinearLayout.VERTICAL
        headerLayout.setBackgroundColor(android.graphics.Color.parseColor("#151515"))
        headerLayout.setPadding(20, 30, 20, 30)
        
        // Terminal border effect
        val headerBorder = android.graphics.drawable.GradientDrawable()
        headerBorder.setColor(android.graphics.Color.parseColor("#151515"))
        headerBorder.setStroke(2, android.graphics.Color.parseColor("#00FF41"))
        headerBorder.cornerRadius = 8f
        headerLayout.background = headerBorder
        
        // System logo/title
        val systemTitle = TextView(this)
        systemTitle.text = "◉ SHADOWNET"
        systemTitle.setTextColor(android.graphics.Color.parseColor("#00FF41"))
        systemTitle.textSize = 28f
        systemTitle.gravity = android.view.Gravity.CENTER
        systemTitle.setTypeface(android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.BOLD)
        headerLayout.addView(systemTitle)
        
        val systemSubtitle = TextView(this)
        systemSubtitle.text = "CLASSIFIED INTELLIGENCE NETWORK"
        systemSubtitle.setTextColor(android.graphics.Color.parseColor("#B0B0B0"))
        systemSubtitle.textSize = 12f
        systemSubtitle.gravity = android.view.Gravity.CENTER
        systemSubtitle.setTypeface(android.graphics.Typeface.MONOSPACE)
        val subtitleParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        subtitleParams.setMargins(0, 8, 0, 0)
        systemSubtitle.layoutParams = subtitleParams
        headerLayout.addView(systemSubtitle)
        
        val securityNotice = TextView(this)
        securityNotice.text = "⚠ AUTHORIZED PERSONNEL ONLY ⚠"
        securityNotice.setTextColor(android.graphics.Color.parseColor("#FF6B00"))
        securityNotice.textSize = 10f
        securityNotice.gravity = android.view.Gravity.CENTER
        securityNotice.setTypeface(android.graphics.Typeface.MONOSPACE)
        val noticeParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        noticeParams.setMargins(0, 8, 0, 0)
        securityNotice.layoutParams = noticeParams
        headerLayout.addView(securityNotice)
        
        val headerParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        headerParams.setMargins(0, 0, 0, 30)
        headerLayout.layoutParams = headerParams
        loginLayout.addView(headerLayout)
        
        // Terminal Login Form
        val formLayout = LinearLayout(this)
        formLayout.orientation = LinearLayout.VERTICAL
        formLayout.setBackgroundColor(android.graphics.Color.parseColor("#1E1E1E"))
        formLayout.setPadding(25, 25, 25, 25)
        
        // Terminal border effect
        val border = android.graphics.drawable.GradientDrawable()
        border.setColor(android.graphics.Color.parseColor("#1E1E1E"))
        border.setStroke(1, android.graphics.Color.parseColor("#404040"))
        border.cornerRadius = 4f
        formLayout.background = border
        
        // Terminal prompt
        val promptText = TextView(this)
        promptText.text = "> AGENT AUTHENTICATION REQUIRED"
        promptText.setTextColor(android.graphics.Color.parseColor("#00FF41"))
        promptText.textSize = 12f
        promptText.setTypeface(android.graphics.Typeface.MONOSPACE)
        val promptParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        promptParams.setMargins(0, 0, 0, 20)
        promptText.layoutParams = promptParams
        formLayout.addView(promptText)
        
        // Agent ID label
        val agentIdLabel = TextView(this)
        agentIdLabel.text = "AGENT_ID:"
        agentIdLabel.setTextColor(android.graphics.Color.parseColor("#00D4FF"))
        agentIdLabel.textSize = 14f
        agentIdLabel.setTypeface(android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.BOLD)
        val agentIdLabelParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        agentIdLabelParams.setMargins(0, 0, 0, 8)
        agentIdLabel.layoutParams = agentIdLabelParams
        formLayout.addView(agentIdLabel)
        
        // Agent ID input
        val usernameInput = EditText(this)
        usernameInput.hint = "Enter agent identifier..."
        usernameInput.setTextColor(android.graphics.Color.parseColor("#FFFFFF"))
        usernameInput.setHintTextColor(android.graphics.Color.parseColor("#808080"))
        usernameInput.setBackgroundColor(android.graphics.Color.parseColor("#2D2D2D"))
        usernameInput.setPadding(15, 15, 15, 15)
        usernameInput.setTypeface(android.graphics.Typeface.MONOSPACE)
        val inputBorder = android.graphics.drawable.GradientDrawable()
        inputBorder.setColor(android.graphics.Color.parseColor("#2D2D2D"))
        inputBorder.setStroke(1, android.graphics.Color.parseColor("#404040"))
        inputBorder.cornerRadius = 2f
        usernameInput.background = inputBorder
        val usernameParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        usernameParams.setMargins(0, 0, 0, 20)
        usernameInput.layoutParams = usernameParams
        formLayout.addView(usernameInput)
        
        // Password label
        val passLabel = TextView(this)
        passLabel.text = "ACCESS_CODE:"
        passLabel.setTextColor(android.graphics.Color.parseColor("#00D4FF"))
        passLabel.textSize = 14f
        passLabel.setTypeface(android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.BOLD)
        val passLabelParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        passLabelParams.setMargins(0, 0, 0, 8)
        passLabel.layoutParams = passLabelParams
        formLayout.addView(passLabel)
        
        // Password input
        val passwordInput = EditText(this)
        passwordInput.hint = "Enter encrypted access code..."
        passwordInput.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        passwordInput.setTextColor(android.graphics.Color.parseColor("#FFFFFF"))
        passwordInput.setHintTextColor(android.graphics.Color.parseColor("#808080"))
        passwordInput.setBackgroundColor(android.graphics.Color.parseColor("#2D2D2D"))
        passwordInput.setPadding(15, 15, 15, 15)
        passwordInput.setTypeface(android.graphics.Typeface.MONOSPACE)
        val passBorder = android.graphics.drawable.GradientDrawable()
        passBorder.setColor(android.graphics.Color.parseColor("#2D2D2D"))
        passBorder.setStroke(1, android.graphics.Color.parseColor("#404040"))
        passBorder.cornerRadius = 2f
        passwordInput.background = passBorder
        val passwordParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        passwordParams.setMargins(0, 0, 0, 25)
        passwordInput.layoutParams = passwordParams
        formLayout.addView(passwordInput)
        
        // Execute button
        val loginButton = Button(this)
        loginButton.text = "▶ EXECUTE"
        loginButton.setBackgroundColor(android.graphics.Color.parseColor("#00FF41"))
        loginButton.setTextColor(android.graphics.Color.parseColor("#0D0D0D"))
        loginButton.textSize = 14f
        loginButton.setTypeface(android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.BOLD)
        val buttonBorder = android.graphics.drawable.GradientDrawable()
        buttonBorder.setColor(android.graphics.Color.parseColor("#00FF41"))
        buttonBorder.setStroke(1, android.graphics.Color.parseColor("#00D4FF"))
        buttonBorder.cornerRadius = 2f
        loginButton.background = buttonBorder
        val loginButtonParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            120
        )
        loginButton.layoutParams = loginButtonParams
        loginButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "⚠ ACCESS DENIED - CREDENTIALS REQUIRED", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Authenticate with database
            val user = databaseHelper.authenticateUser(username, password)
            if (user != null) {
                currentUser = user
                Toast.makeText(this, "✓ ACCESS GRANTED - AGENT ${user.getCodename()}", Toast.LENGTH_SHORT).show()
                showMainSystem()
            } else {
                loginAttempts++
                if (loginAttempts >= maxAttempts) {
                    Toast.makeText(this, "⚠ SECURITY BREACH DETECTED - TERMINAL LOCKED", Toast.LENGTH_LONG).show()
                    loginButton.isEnabled = false
                    loginButton.text = "◉ LOCKED"
                    val lockedBorder = android.graphics.drawable.GradientDrawable()
                    lockedBorder.setColor(android.graphics.Color.parseColor("#FF0040"))
                    lockedBorder.cornerRadius = 2f
                    loginButton.background = lockedBorder
                    loginButton.setTextColor(android.graphics.Color.WHITE)
                } else {
                    Toast.makeText(this, "✗ ACCESS DENIED - ${maxAttempts - loginAttempts} attempts remaining", Toast.LENGTH_LONG).show()
                    usernameInput.text.clear()
                    passwordInput.text.clear()
                }
            }
        }
        formLayout.addView(loginButton)
        
        val formParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        formParams.setMargins(0, 0, 0, 60)
        formLayout.layoutParams = formParams
        loginLayout.addView(formLayout)
        
        // Test credentials info
        val demoInfo = TextView(this)
        demoInfo.text = "TEST ACCESS CREDENTIALS:\n[AGENT_ID: DEMO | ACCESS_CODE: demo]\n\n[AGENT_ID: S01D020 | ACCESS_CODE: defective]"
        demoInfo.setTextColor(android.graphics.Color.parseColor("#808080"))
        demoInfo.textSize = 10f
        demoInfo.gravity = android.view.Gravity.CENTER
        demoInfo.setTypeface(android.graphics.Typeface.MONOSPACE)
        demoInfo.setBackgroundColor(android.graphics.Color.parseColor("#2D2D2D"))
        demoInfo.setPadding(15, 12, 15, 12)
        val demoBorder = android.graphics.drawable.GradientDrawable()
        demoBorder.setColor(android.graphics.Color.parseColor("#2D2D2D"))
        demoBorder.setStroke(1, android.graphics.Color.parseColor("#404040"))
        demoBorder.cornerRadius = 2f
        demoInfo.background = demoBorder
        val demoParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        demoParams.setMargins(0, 25, 0, 0)
        demoInfo.layoutParams = demoParams
        loginLayout.addView(demoInfo)
        
        // Footer
        val footerText = TextView(this)
        footerText.text = "SHADOWNET v2.7.3 | CLASSIFIED SYSTEM\nUNAUTHORIZED ACCESS MONITORED"
        footerText.setTextColor(android.graphics.Color.parseColor("#404040"))
        footerText.textSize = 8f
        footerText.gravity = android.view.Gravity.CENTER
        footerText.setTypeface(android.graphics.Typeface.MONOSPACE)
        val footerParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        footerParams.setMargins(0, 25, 0, 0)
        footerText.layoutParams = footerParams
        loginLayout.addView(footerText)
        
        scrollView.addView(loginLayout)
        setContentView(scrollView)
    }
    
    private fun showUserProfile() {
        val scrollView = ScrollView(this)
        scrollView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        scrollView.isFillViewport = true
        
        val profileLayout = LinearLayout(this)
        profileLayout.orientation = LinearLayout.VERTICAL
        profileLayout.setBackgroundColor(android.graphics.Color.parseColor("#0D0D0D"))
        profileLayout.setPadding(12, 12, 12, 12)
        profileLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        
        // Header
        val headerBar = LinearLayout(this)
        headerBar.orientation = LinearLayout.HORIZONTAL
        headerBar.setBackgroundColor(android.graphics.Color.parseColor("#151515"))
        headerBar.setPadding(15, 20, 15, 20)
        
        val headerBorder = android.graphics.drawable.GradientDrawable()
        headerBorder.setColor(android.graphics.Color.parseColor("#151515"))
        headerBorder.setStroke(1, android.graphics.Color.parseColor("#00D4FF"))
        headerBorder.cornerRadius = 4f
        headerBar.background = headerBorder
        
        val backIcon = TextView(this)
        backIcon.text = "◂"
        backIcon.setTextColor(android.graphics.Color.parseColor("#00D4FF"))
        backIcon.textSize = 20f
        backIcon.setTypeface(android.graphics.Typeface.MONOSPACE)
        backIcon.setOnClickListener { showMainSystem() }
        headerBar.addView(backIcon)
        
        val headerTitle = TextView(this)
        headerTitle.text = "AGENT DOSSIER"
        headerTitle.setTextColor(android.graphics.Color.parseColor("#FFFFFF"))
        headerTitle.textSize = 16f
        headerTitle.setTypeface(android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.BOLD)
        val headerTitleParams = LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        )
        headerTitleParams.setMargins(15, 0, 0, 0)
        headerTitle.layoutParams = headerTitleParams
        headerBar.addView(headerTitle)
        
        val headerParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        headerParams.setMargins(0, 0, 0, 15)
        headerBar.layoutParams = headerParams
        profileLayout.addView(headerBar)
        
        currentUser?.let { user ->
            
            // Agent profile card
            val profileCard = LinearLayout(this)
            profileCard.orientation = LinearLayout.VERTICAL
            profileCard.setBackgroundColor(android.graphics.Color.parseColor("#1E1E1E"))
            profileCard.setPadding(20, 20, 20, 20)
            
            val cardBorder = android.graphics.drawable.GradientDrawable()
            cardBorder.setColor(android.graphics.Color.parseColor("#1E1E1E"))
            cardBorder.setStroke(1, user.getClearanceColor())
            cardBorder.cornerRadius = 4f
            profileCard.background = cardBorder
            
            // Agent picture placeholder and basic info
            val profileHeader = LinearLayout(this)
            profileHeader.orientation = LinearLayout.HORIZONTAL
            profileHeader.gravity = android.view.Gravity.CENTER_VERTICAL
            
            val avatar = TextView(this)
            avatar.text = "◈"
            avatar.setTextColor(android.graphics.Color.parseColor("#00D4FF"))
            avatar.textSize = 40f
            avatar.setTypeface(android.graphics.Typeface.MONOSPACE)
            avatar.setBackgroundColor(android.graphics.Color.parseColor("#2D2D2D"))
            avatar.setPadding(15, 15, 15, 15)
            val avatarBorder = android.graphics.drawable.GradientDrawable()
            avatarBorder.setColor(android.graphics.Color.parseColor("#2D2D2D"))
            avatarBorder.setStroke(1, android.graphics.Color.parseColor("#404040"))
            avatarBorder.cornerRadius = 4f
            avatar.background = avatarBorder
            profileHeader.addView(avatar)
            
            val basicInfo = LinearLayout(this)
            basicInfo.orientation = LinearLayout.VERTICAL
            val basicInfoParams = LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            )
            basicInfoParams.setMargins(15, 0, 0, 0)
            basicInfo.layoutParams = basicInfoParams
            
            val agentName = TextView(this)
            agentName.text = user.getCodename()
            agentName.setTextColor(android.graphics.Color.parseColor("#FFFFFF"))
            agentName.textSize = 20f
            agentName.setTypeface(android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.BOLD)
            basicInfo.addView(agentName)
            
            val agentId = TextView(this)
            agentId.text = user.getAgentId()
            agentId.setTextColor(android.graphics.Color.parseColor("#B0B0B0"))
            agentId.textSize = 14f
            agentId.setTypeface(android.graphics.Typeface.MONOSPACE)
            basicInfo.addView(agentId)
            
            val clearanceInfo = TextView(this)
            clearanceInfo.text = "CLEARANCE: ${user.getSecurityClearance()}"
            clearanceInfo.setTextColor(user.getClearanceColor())
            clearanceInfo.textSize = 12f
            clearanceInfo.setTypeface(android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.BOLD)
            basicInfo.addView(clearanceInfo)
            
            profileHeader.addView(basicInfo)
            profileCard.addView(profileHeader)
            
            // Operational statistics
            val opsTitle = TextView(this)
            opsTitle.text = "> OPERATIONAL DATA"
            opsTitle.setTextColor(android.graphics.Color.parseColor("#00FF41"))
            opsTitle.textSize = 14f
            opsTitle.setTypeface(android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.BOLD)
            val opsTitleParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            opsTitleParams.setMargins(0, 25, 0, 12)
            opsTitle.layoutParams = opsTitleParams
            profileCard.addView(opsTitle)
            
            val statsGrid = LinearLayout(this)
            statsGrid.orientation = LinearLayout.HORIZONTAL
            
            val missionCard = createSpyStatCard("MISSIONS", "${user.getMissionCount()}", android.graphics.Color.parseColor("#00FF41"))
            val missionParams = LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            )
            missionParams.setMargins(0, 0, 8, 0)
            missionCard.layoutParams = missionParams
            statsGrid.addView(missionCard)
            
            val successCard = createSpyStatCard("SUCCESS %", "${user.getSuccessRate()}%", android.graphics.Color.parseColor("#00D4FF"))
            val successParams = LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            )
            successParams.setMargins(8, 0, 0, 0)
            successCard.layoutParams = successParams
            statsGrid.addView(successCard)
            
            profileCard.addView(statsGrid)
            
            // Additional agent details
            val detailsSection = LinearLayout(this)
            detailsSection.orientation = LinearLayout.VERTICAL
            val detailsParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            detailsParams.setMargins(0, 20, 0, 0)
            detailsSection.layoutParams = detailsParams
            
            val divisionDetail = createAgentDetail("DIVISION", user.getDivision())
            detailsSection.addView(divisionDetail)
            
            val statusDetail = createAgentDetail("OPERATIONAL STATUS", user.getOperationalStatus())
            detailsSection.addView(statusDetail)
            
            val threatDetail = createAgentDetail("THREAT ASSESSMENT", user.getThreatLevel())
            detailsSection.addView(threatDetail)
            
            val loginDetail = createAgentDetail("LOGIN COUNT", "${user.loginCount}")
            detailsSection.addView(loginDetail)
            
            profileCard.addView(detailsSection)
            
            val profileCardParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            profileCardParams.setMargins(0, 0, 0, 20)
            profileCard.layoutParams = profileCardParams
            profileLayout.addView(profileCard)
            
            // Activity history section
            val activityCard = LinearLayout(this)
            activityCard.orientation = LinearLayout.VERTICAL
            activityCard.setBackgroundColor(android.graphics.Color.WHITE)
            activityCard.setPadding(20, 20, 20, 20)
            
            val activityBorder = android.graphics.drawable.GradientDrawable()
            activityBorder.setColor(android.graphics.Color.WHITE)
            activityBorder.setStroke(1, android.graphics.Color.parseColor("#E0E0E0"))
            activityBorder.cornerRadius = 12f
            activityCard.background = activityBorder
            
            val activityHeader = TextView(this)
            activityHeader.text = "� Recent Activity"
            activityHeader.setTextColor(android.graphics.Color.parseColor("#2196F3"))
            activityHeader.textSize = 18f
            activityHeader.setTypeface(android.graphics.Typeface.DEFAULT_BOLD)
            activityCard.addView(activityHeader)
            
            // Get login history from database
            val loginHistory = databaseHelper.getLoginHistory(user.userId)
            
            if (loginHistory.isEmpty()) {
                val noHistory = TextView(this)
                noHistory.text = "No recent activity"
                noHistory.setTextColor(android.graphics.Color.parseColor("#757575"))
                noHistory.textSize = 14f
                val noHistoryParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                noHistoryParams.setMargins(0, 15, 0, 0)
                noHistory.layoutParams = noHistoryParams
                activityCard.addView(noHistory)
            } else {
                loginHistory.take(3).forEach { session ->
                    val sessionLayout = LinearLayout(this)
                    sessionLayout.orientation = LinearLayout.HORIZONTAL
                    sessionLayout.setPadding(0, 10, 0, 10)
                    
                    val statusIcon = TextView(this)
                    statusIcon.text = when (session.status) {
                        "ACTIVE" -> "🟢"
                        "LOGGED_OUT" -> "⚪"
                        else -> "🔴"
                    }
                    statusIcon.textSize = 16f
                    sessionLayout.addView(statusIcon)
                    
                    val sessionInfo = LinearLayout(this)
                    sessionInfo.orientation = LinearLayout.VERTICAL
                    val sessionInfoParams = LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                    )
                    sessionInfoParams.setMargins(15, 0, 0, 0)
                    sessionInfo.layoutParams = sessionInfoParams
                    
                    val sessionTime = TextView(this)
                    sessionTime.text = session.loginTime
                    sessionTime.setTextColor(android.graphics.Color.parseColor("#212121"))
                    sessionTime.textSize = 14f
                    sessionInfo.addView(sessionTime)
                    
                    val sessionDetails = TextView(this)
                    sessionDetails.text = "${session.deviceInfo} • ${session.status}"
                    sessionDetails.setTextColor(android.graphics.Color.parseColor("#757575"))
                    sessionDetails.textSize = 12f
                    sessionInfo.addView(sessionDetails)
                    
                    sessionLayout.addView(sessionInfo)
                    activityCard.addView(sessionLayout)
                }
            }
            
            val activityParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            activityParams.setMargins(0, 0, 0, 20)
            activityCard.layoutParams = activityParams
            profileLayout.addView(activityCard)
            

        }
        
        scrollView.addView(profileLayout)
        setContentView(scrollView)
    }
    
    private fun showMainSystem() {
        val scrollView = ScrollView(this)
        scrollView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        scrollView.isFillViewport = true
        
        val mainLayout = LinearLayout(this)
        mainLayout.orientation = LinearLayout.VERTICAL
        mainLayout.setBackgroundColor(android.graphics.Color.parseColor("#0D0D0D"))
        mainLayout.setPadding(12, 12, 12, 12)
        mainLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        
        // Spy Command Header
        val headerBar = LinearLayout(this)
        headerBar.orientation = LinearLayout.HORIZONTAL
        headerBar.setBackgroundColor(android.graphics.Color.parseColor("#151515"))
        headerBar.setPadding(15, 20, 15, 20)
        
        val headerBorder = android.graphics.drawable.GradientDrawable()
        headerBorder.setColor(android.graphics.Color.parseColor("#151515"))
        headerBorder.setStroke(1, android.graphics.Color.parseColor("#00FF41"))
        headerBorder.cornerRadius = 4f
        headerBar.background = headerBorder
        
        val systemIcon = TextView(this)
        systemIcon.text = "◉"
        systemIcon.setTextColor(android.graphics.Color.parseColor("#00FF41"))
        systemIcon.textSize = 20f
        systemIcon.setTypeface(android.graphics.Typeface.MONOSPACE)
        headerBar.addView(systemIcon)
        
        val headerInfo = LinearLayout(this)
        headerInfo.orientation = LinearLayout.VERTICAL
        val headerInfoParams = LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        )
        headerInfoParams.setMargins(12, 0, 0, 0)
        headerInfo.layoutParams = headerInfoParams
        
        val systemTitle = TextView(this)
        systemTitle.text = "SHADOWNET COMMAND CENTER"
        systemTitle.setTextColor(android.graphics.Color.parseColor("#FFFFFF"))
        systemTitle.textSize = 16f
        systemTitle.setTypeface(android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.BOLD)
        headerInfo.addView(systemTitle)
        
        val currentTime = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault()).format(Date())
        val timeText = TextView(this)
        timeText.text = "TIMESTAMP: $currentTime"
        timeText.setTextColor(android.graphics.Color.parseColor("#B0B0B0"))
        timeText.textSize = 10f
        timeText.setTypeface(android.graphics.Typeface.MONOSPACE)
        headerInfo.addView(timeText)
        
        headerBar.addView(headerInfo)
        
        // Status indicator
        val statusIndicator = TextView(this)
        statusIndicator.text = "SECURE"
        statusIndicator.setTextColor(android.graphics.Color.parseColor("#00FF41"))
        statusIndicator.textSize = 10f
        statusIndicator.setTypeface(android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.BOLD)
        statusIndicator.setBackgroundColor(android.graphics.Color.parseColor("#2D2D2D"))
        statusIndicator.setPadding(8, 4, 8, 4)
        val statusBorder = android.graphics.drawable.GradientDrawable()
        statusBorder.setColor(android.graphics.Color.parseColor("#2D2D2D"))
        statusBorder.setStroke(1, android.graphics.Color.parseColor("#00FF41"))
        statusBorder.cornerRadius = 2f
        statusIndicator.background = statusBorder
        headerBar.addView(statusIndicator)
        
        val headerParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        headerParams.setMargins(0, 0, 0, 10)
        headerBar.layoutParams = headerParams
        mainLayout.addView(headerBar)
        
        // Agent info section removed - using unified Mohammad Al Galib profile below
        
        // Command functions grid
        val actionsTitle = TextView(this)
        actionsTitle.text = "> AVAILABLE FUNCTIONS"
        actionsTitle.setTextColor(android.graphics.Color.parseColor("#00FF41"))
        actionsTitle.textSize = 14f
        actionsTitle.setTypeface(android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.BOLD)
        val actionsTitleParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        actionsTitleParams.setMargins(0, 0, 0, 15)
        actionsTitle.layoutParams = actionsTitleParams
        mainLayout.addView(actionsTitle)
        
        val actionsGrid = LinearLayout(this)
        actionsGrid.orientation = LinearLayout.HORIZONTAL
        
        // Single main action button
        val intelButton = createSpyActionButton("🔍", "CLASSIFIED INTEL SEARCH", android.graphics.Color.parseColor("#00FF41"))
        val intelParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 120
        )
        intelParams.setMargins(0, 0, 0, 0)
        intelButton.layoutParams = intelParams
        intelButton.setOnClickListener {
            Toast.makeText(this, "🔍 ACCESSING CLASSIFIED INTELLIGENCE DATABASE...", Toast.LENGTH_SHORT).show()
        }
        actionsGrid.addView(intelButton)
        
        val actionsGridParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        actionsGridParams.setMargins(0, 0, 0, 20)
        actionsGrid.layoutParams = actionsGridParams
        mainLayout.addView(actionsGrid)
        
        // Agent Profile Section
        val profileCard = LinearLayout(this)
        profileCard.orientation = LinearLayout.VERTICAL
        profileCard.setBackgroundColor(android.graphics.Color.parseColor("#1E1E1E"))
        profileCard.setPadding(20, 20, 20, 20)
        
        val profileBorder = android.graphics.drawable.GradientDrawable()
        profileBorder.setColor(android.graphics.Color.parseColor("#1E1E1E"))
        profileBorder.setStroke(2, android.graphics.Color.parseColor("#00FF41"))
        profileBorder.cornerRadius = 8f
        profileCard.background = profileBorder
        
        // Profile Header with Image
        val profileHeaderLayout = LinearLayout(this)
        profileHeaderLayout.orientation = LinearLayout.HORIZONTAL
        profileHeaderLayout.gravity = android.view.Gravity.CENTER_VERTICAL
        
        // Profile Image
        val profileImage = android.widget.ImageView(this)
        try {
            val bitmap = android.graphics.BitmapFactory.decodeStream(assets.open("profile_image.jpg"))
            profileImage.setImageBitmap(bitmap)
        } catch (e: Exception) {
            // Fallback to a simple colored rectangle if image fails to load
            profileImage.setBackgroundColor(android.graphics.Color.parseColor("#00FF41"))
        }
        profileImage.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
        profileImage.setPadding(4, 4, 4, 4)
        val imageBorder = android.graphics.drawable.GradientDrawable()
        imageBorder.setColor(android.graphics.Color.parseColor("#2D2D2D"))
        imageBorder.setStroke(2, android.graphics.Color.parseColor("#00FF41"))
        imageBorder.cornerRadius = 6f
        profileImage.background = imageBorder
        val imageParams = LinearLayout.LayoutParams(80, 80)
        imageParams.setMargins(0, 0, 16, 0)
        profileImage.layoutParams = imageParams
        profileHeaderLayout.addView(profileImage)
        
        // Profile Title
        val profileHeaderText = LinearLayout(this)
        profileHeaderText.orientation = LinearLayout.VERTICAL
        val headerTextParams = LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        )
        profileHeaderText.layoutParams = headerTextParams
        
        val profileTitle = TextView(this)
        profileTitle.text = "👤 AGENT PROFILE"
        profileTitle.setTextColor(android.graphics.Color.parseColor("#00FF41"))
        profileTitle.textSize = 16f
        profileTitle.setTypeface(android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.BOLD)
        profileHeaderText.addView(profileTitle)
        
        val profileSubtitle = TextView(this)
        profileSubtitle.text = "CLASSIFIED PERSONNEL FILE"
        profileSubtitle.setTextColor(android.graphics.Color.parseColor("#B0B0B0"))
        profileSubtitle.textSize = 10f
        profileSubtitle.setTypeface(android.graphics.Typeface.MONOSPACE)
        profileHeaderText.addView(profileSubtitle)
        
        profileHeaderLayout.addView(profileHeaderText)
        profileCard.addView(profileHeaderLayout)
        
        // Agent Details
        val agentName = TextView(this)
        agentName.text = "NAME: Mohammad Al Galib"
        agentName.setTextColor(android.graphics.Color.parseColor("#FFFFFF"))
        agentName.textSize = 12f
        agentName.setTypeface(android.graphics.Typeface.MONOSPACE)
        val nameParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        nameParams.setMargins(0, 12, 0, 4)
        agentName.layoutParams = nameParams
        profileCard.addView(agentName)
        
        val agentAge = TextView(this)
        agentAge.text = "AGE: 17 Years (Born January 1, 2007)"
        agentAge.setTextColor(android.graphics.Color.parseColor("#00FFFF"))
        agentAge.textSize = 12f
        agentAge.setTypeface(android.graphics.Typeface.MONOSPACE)
        val ageParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        ageParams.setMargins(0, 4, 0, 4)
        agentAge.layoutParams = ageParams
        profileCard.addView(agentAge)
        
        val agentLocation = TextView(this)
        agentLocation.text = "LOCATION: Bangladesh"
        agentLocation.setTextColor(android.graphics.Color.parseColor("#FFFFFF"))
        agentLocation.textSize = 12f
        agentLocation.setTypeface(android.graphics.Typeface.MONOSPACE)
        val locationParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        locationParams.setMargins(0, 4, 0, 4)
        agentLocation.layoutParams = locationParams
        profileCard.addView(agentLocation)
        
        val agentBlood = TextView(this)
        agentBlood.text = "BLOOD TYPE: O+ (Universal Donor)"
        agentBlood.setTextColor(android.graphics.Color.parseColor("#FF6B6B"))
        agentBlood.textSize = 12f
        agentBlood.setTypeface(android.graphics.Typeface.MONOSPACE)
        val bloodParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        bloodParams.setMargins(0, 4, 0, 4)
        agentBlood.layoutParams = bloodParams
        profileCard.addView(agentBlood)
        
        val agentHealth = TextView(this)
        agentHealth.text = "MEDICAL STATUS: No Conditions - Combat Ready"
        agentHealth.setTextColor(android.graphics.Color.parseColor("#00FF41"))
        agentHealth.textSize = 12f
        agentHealth.setTypeface(android.graphics.Typeface.MONOSPACE)
        val healthParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        healthParams.setMargins(0, 4, 0, 4)
        agentHealth.layoutParams = healthParams
        profileCard.addView(agentHealth)
        
        val agentClearance = TextView(this)
        agentClearance.text = "CLEARANCE: TOP SECRET - NUCLEAR AUTHORIZED"
        agentClearance.setTextColor(android.graphics.Color.parseColor("#FF0000"))
        agentClearance.textSize = 12f
        agentClearance.setTypeface(android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.BOLD)
        val clearanceParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        clearanceParams.setMargins(0, 8, 0, 4)
        agentClearance.layoutParams = clearanceParams
        profileCard.addView(agentClearance)
        
        val profileCardParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        profileCardParams.setMargins(0, 0, 0, 20)
        profileCard.layoutParams = profileCardParams
        mainLayout.addView(profileCard)
        
        // Update age in real-time
        val ageHandler = android.os.Handler(android.os.Looper.getMainLooper())
        val updateAge = object : Runnable {
            override fun run() {
                val birthDate = java.util.Calendar.getInstance()
                birthDate.set(2007, 0, 1, 0, 0, 0) // January 1, 2007
                
                val now = java.util.Calendar.getInstance()
                var years = now.get(java.util.Calendar.YEAR) - birthDate.get(java.util.Calendar.YEAR)
                var months = now.get(java.util.Calendar.MONTH) - birthDate.get(java.util.Calendar.MONTH)
                var days = now.get(java.util.Calendar.DAY_OF_MONTH) - birthDate.get(java.util.Calendar.DAY_OF_MONTH)
                var hours = now.get(java.util.Calendar.HOUR_OF_DAY) - birthDate.get(java.util.Calendar.HOUR_OF_DAY)
                var minutes = now.get(java.util.Calendar.MINUTE) - birthDate.get(java.util.Calendar.MINUTE)
                var seconds = now.get(java.util.Calendar.SECOND) - birthDate.get(java.util.Calendar.SECOND)
                
                // Adjust for negative values
                if (seconds < 0) { seconds += 60; minutes-- }
                if (minutes < 0) { minutes += 60; hours-- }
                if (hours < 0) { hours += 24; days-- }
                if (days < 0) { 
                    val prevMonth = java.util.Calendar.getInstance()
                    prevMonth.set(now.get(java.util.Calendar.YEAR), now.get(java.util.Calendar.MONTH) - 1, 1)
                    days += prevMonth.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
                    months--
                }
                if (months < 0) { months += 12; years-- }
                
                agentAge.text = "AGE: ${years}Y ${months}M ${days}D ${hours}H ${minutes}M ${seconds}S"
                
                ageHandler.postDelayed(this, 1000)
            }
        }
        ageHandler.post(updateAge)
        
        // Minimalist Clock
        val clockCard = LinearLayout(this)
        clockCard.orientation = LinearLayout.VERTICAL
        clockCard.setBackgroundColor(android.graphics.Color.parseColor("#1E1E1E"))
        clockCard.setPadding(15, 15, 15, 15)
        clockCard.gravity = android.view.Gravity.CENTER
        
        val clockBorder = android.graphics.drawable.GradientDrawable()
        clockBorder.setColor(android.graphics.Color.parseColor("#1E1E1E"))
        clockBorder.setStroke(1, android.graphics.Color.parseColor("#00FF41"))
        clockBorder.cornerRadius = 4f
        clockCard.background = clockBorder
        
        val clockDisplay = TextView(this)
        clockDisplay.setTextColor(android.graphics.Color.parseColor("#00FF41"))
        clockDisplay.textSize = 18f
        clockDisplay.setTypeface(android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.BOLD)
        clockDisplay.gravity = android.view.Gravity.CENTER
        clockCard.addView(clockDisplay)
        
        val clockParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        clockParams.setMargins(0, 0, 0, 20)
        clockCard.layoutParams = clockParams
        mainLayout.addView(clockCard)
        
        // Update clock every second
        val clockHandler = android.os.Handler(android.os.Looper.getMainLooper())
        val updateClock = object : Runnable {
            override fun run() {
                val currentTime = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
                clockDisplay.text = currentTime
                clockHandler.postDelayed(this, 1000)
            }
        }
        clockHandler.post(updateClock)
        
        // System alerts
        val alertsCard = LinearLayout(this)
        alertsCard.orientation = LinearLayout.VERTICAL
        alertsCard.setBackgroundColor(android.graphics.Color.parseColor("#1E1E1E"))
        alertsCard.setPadding(15, 15, 15, 15)
        
        val alertsBorder = android.graphics.drawable.GradientDrawable()
        alertsBorder.setColor(android.graphics.Color.parseColor("#1E1E1E"))
        alertsBorder.setStroke(1, android.graphics.Color.parseColor("#FF6B00"))
        alertsBorder.cornerRadius = 4f
        alertsCard.background = alertsBorder
        
        val alertsHeader = TextView(this)
        alertsHeader.text = "⚠ SYSTEM ALERTS"
        alertsHeader.setTextColor(android.graphics.Color.parseColor("#FF6B00"))
        alertsHeader.textSize = 12f
        alertsHeader.setTypeface(android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.BOLD)
        alertsCard.addView(alertsHeader)
        
        val alert1 = TextView(this)
        alert1.text = "• NEW ASSIGNMENT: Operation Nightfall"
        alert1.setTextColor(android.graphics.Color.parseColor("#B0B0B0"))
        alert1.textSize = 11f
        alert1.setTypeface(android.graphics.Typeface.MONOSPACE)
        val alert1Params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        alert1Params.setMargins(0, 12, 0, 4)
        alert1.layoutParams = alert1Params
        alertsCard.addView(alert1)
        
        val alert2 = TextView(this)
        alert2.text = "• SECURITY UPDATE: Clearance verification required"
        alert2.setTextColor(android.graphics.Color.parseColor("#B0B0B0"))
        alert2.textSize = 11f
        alert2.setTypeface(android.graphics.Typeface.MONOSPACE)
        val alert2Params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        alert2Params.setMargins(0, 4, 0, 4)
        alert2.layoutParams = alert2Params
        alertsCard.addView(alert2)
        
        val alert3 = TextView(this)
        alert3.text = "• NETWORK STATUS: All channels secure"
        alert3.setTextColor(android.graphics.Color.parseColor("#00FF41"))
        alert3.textSize = 11f
        alert3.setTypeface(android.graphics.Typeface.MONOSPACE)
        val alert3Params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        alert3Params.setMargins(0, 4, 0, 4)
        alert3.layoutParams = alert3Params
        alertsCard.addView(alert3)
        
        val alert4 = TextView(this)
        alert4.text = "• SURVEILLANCE: Perimeter monitoring active"
        alert4.setTextColor(android.graphics.Color.parseColor("#B0B0B0"))
        alert4.textSize = 11f
        alert4.setTypeface(android.graphics.Typeface.MONOSPACE)
        val alert4Params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        alert4Params.setMargins(0, 4, 0, 0)
        alert4.layoutParams = alert4Params
        alertsCard.addView(alert4)
        
        val alertsParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        alertsParams.setMargins(0, 0, 0, 20)
        alertsCard.layoutParams = alertsParams
        mainLayout.addView(alertsCard)
        
        // Control panel
        val controlNav = LinearLayout(this)
        controlNav.orientation = LinearLayout.HORIZONTAL
        controlNav.setBackgroundColor(android.graphics.Color.parseColor("#2D2D2D"))
        controlNav.setPadding(8, 12, 8, 12)
        
        val controlNavBorder = android.graphics.drawable.GradientDrawable()
        controlNavBorder.setColor(android.graphics.Color.parseColor("#2D2D2D"))
        controlNavBorder.setStroke(1, android.graphics.Color.parseColor("#404040"))
        controlNavBorder.cornerRadius = 2f
        controlNav.background = controlNavBorder
        
        // Agent profile button (removed - using main grid button instead)
        
        // System config button  
        val configButton = createSpyNavButton("⚡", "SYSTEM\nCONFIG")
        val configParams = LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        )
        configButton.layoutParams = configParams
        configButton.setOnClickListener {
            Toast.makeText(this, "◉ SYSTEM CONFIG ACCESS RESTRICTED", Toast.LENGTH_SHORT).show()
        }
        controlNav.addView(configButton)
        
        // Logout button
        val logoutButton = createSpyNavButton("X", "LOGOUT")
        val logoutParams = LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        )
        logoutButton.layoutParams = logoutParams
        logoutButton.setOnClickListener {
            currentUser?.let { user ->
                databaseHelper.logoutUser(user.userId)
            }
            currentUser = null
            loginAttempts = 0
            Toast.makeText(this, "◉ SESSION TERMINATED", Toast.LENGTH_SHORT).show()
            showLoginScreen()
        }
        controlNav.addView(logoutButton)
        
        mainLayout.addView(controlNav)
        
        // System status footer
        val statusFooter = LinearLayout(this)
        statusFooter.orientation = LinearLayout.VERTICAL
        statusFooter.setBackgroundColor(android.graphics.Color.parseColor("#151515"))
        statusFooter.setPadding(12, 10, 12, 10)
        
        val footerBorder = android.graphics.drawable.GradientDrawable()
        footerBorder.setColor(android.graphics.Color.parseColor("#151515"))
        footerBorder.setStroke(1, android.graphics.Color.parseColor("#404040"))
        footerBorder.cornerRadius = 2f
        statusFooter.background = footerBorder
        
        val systemStatus = TextView(this)
        systemStatus.text = "SYSTEM STATUS: OPERATIONAL | ENCRYPTION: AES-256 | UPTIME: 99.7%"
        systemStatus.setTextColor(android.graphics.Color.parseColor("#808080"))
        systemStatus.textSize = 8f
        systemStatus.setTypeface(android.graphics.Typeface.MONOSPACE)
        systemStatus.gravity = android.view.Gravity.CENTER
        statusFooter.addView(systemStatus)
        
        val versionInfo = TextView(this)
        versionInfo.text = "SHADOWNET v2.7.3 | CLASSIFIED SYSTEM ACCESS"
        versionInfo.setTextColor(android.graphics.Color.parseColor("#404040"))
        versionInfo.textSize = 7f
        versionInfo.setTypeface(android.graphics.Typeface.MONOSPACE)
        versionInfo.gravity = android.view.Gravity.CENTER
        val versionParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        versionParams.setMargins(0, 4, 0, 0)
        versionInfo.layoutParams = versionParams
        statusFooter.addView(versionInfo)
        
        val footerParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        footerParams.setMargins(0, 10, 0, 0)
        statusFooter.layoutParams = footerParams
        mainLayout.addView(statusFooter)
        
        scrollView.addView(mainLayout)
        setContentView(scrollView)
    }
    
    private fun createActionButton(icon: String, text: String, color: Int): LinearLayout {
        val button = LinearLayout(this)
        button.orientation = LinearLayout.VERTICAL
        button.gravity = android.view.Gravity.CENTER
        button.setPadding(20, 20, 20, 20)
        
        val border = android.graphics.drawable.GradientDrawable()
        border.setColor(color)
        border.cornerRadius = 16f
        button.background = border
        
        val iconText = TextView(this)
        iconText.text = icon
        iconText.textSize = 32f
        iconText.gravity = android.view.Gravity.CENTER
        button.addView(iconText)
        
        val labelText = TextView(this)
        labelText.text = text
        labelText.setTextColor(android.graphics.Color.WHITE)
        labelText.textSize = 12f
        labelText.gravity = android.view.Gravity.CENTER
        labelText.setTypeface(android.graphics.Typeface.DEFAULT_BOLD)
        val labelParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        labelParams.setMargins(0, 10, 0, 0)
        labelText.layoutParams = labelParams
        button.addView(labelText)
        
        return button
    }
    
    private fun createNavButton(icon: String, text: String): LinearLayout {
        val button = LinearLayout(this)
        button.orientation = LinearLayout.VERTICAL
        button.gravity = android.view.Gravity.CENTER
        button.setPadding(10, 10, 10, 10)
        
        val iconText = TextView(this)
        iconText.text = icon
        iconText.textSize = 24f
        iconText.gravity = android.view.Gravity.CENTER
        button.addView(iconText)
        
        val labelText = TextView(this)
        labelText.text = text
        labelText.setTextColor(android.graphics.Color.parseColor("#757575"))
        labelText.textSize = 12f
        labelText.gravity = android.view.Gravity.CENTER
        val labelParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        labelParams.setMargins(0, 5, 0, 0)
        labelText.layoutParams = labelParams
        button.addView(labelText)
        
        return button
    }
    
    private fun createStatCard(title: String, value: String, color: Int): LinearLayout {
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.setPadding(15, 15, 15, 15)
        card.gravity = android.view.Gravity.CENTER
        
        val border = android.graphics.drawable.GradientDrawable()
        border.setColor(android.graphics.Color.parseColor("#F5F5F5"))
        border.setStroke(2, color)
        border.cornerRadius = 12f
        card.background = border
        
        val valueText = TextView(this)
        valueText.text = value
        valueText.setTextColor(color)
        valueText.textSize = 24f
        valueText.setTypeface(android.graphics.Typeface.DEFAULT_BOLD)
        valueText.gravity = android.view.Gravity.CENTER
        card.addView(valueText)
        
        val titleText = TextView(this)
        titleText.text = title
        titleText.setTextColor(android.graphics.Color.parseColor("#757575"))
        titleText.textSize = 12f
        titleText.gravity = android.view.Gravity.CENTER
        val titleParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        titleParams.setMargins(0, 5, 0, 0)
        titleText.layoutParams = titleParams
        card.addView(titleText)
        
        return card
    }
    
    private fun createSpyActionButton(icon: String, text: String, color: Int): LinearLayout {
        val button = LinearLayout(this)
        button.orientation = LinearLayout.VERTICAL
        button.gravity = android.view.Gravity.CENTER
        button.setBackgroundColor(android.graphics.Color.parseColor("#2D2D2D"))
        button.setPadding(15, 15, 15, 15)
        
        val border = android.graphics.drawable.GradientDrawable()
        border.setColor(android.graphics.Color.parseColor("#2D2D2D"))
        border.setStroke(1, color)
        border.cornerRadius = 2f
        button.background = border
        
        val iconText = TextView(this)
        iconText.text = icon
        iconText.setTextColor(color)
        iconText.textSize = 28f
        iconText.gravity = android.view.Gravity.CENTER
        iconText.setTypeface(android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.BOLD)
        button.addView(iconText)
        
        val labelText = TextView(this)
        labelText.text = text
        labelText.setTextColor(android.graphics.Color.parseColor("#FFFFFF"))
        labelText.textSize = 10f
        labelText.gravity = android.view.Gravity.CENTER
        labelText.setTypeface(android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.BOLD)
        val labelParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        labelParams.setMargins(0, 8, 0, 0)
        labelText.layoutParams = labelParams
        button.addView(labelText)
        
        return button
    }
    
    private fun createSpyNavButton(icon: String, text: String): LinearLayout {
        val button = LinearLayout(this)
        button.orientation = LinearLayout.VERTICAL
        button.gravity = android.view.Gravity.CENTER
        button.setPadding(8, 8, 8, 8)
        
        val iconText = TextView(this)
        iconText.text = icon
        iconText.setTextColor(android.graphics.Color.parseColor("#00D4FF"))
        iconText.textSize = 16f
        iconText.gravity = android.view.Gravity.CENTER
        iconText.setTypeface(android.graphics.Typeface.MONOSPACE)
        button.addView(iconText)
        
        val labelText = TextView(this)
        labelText.text = text
        labelText.setTextColor(android.graphics.Color.parseColor("#B0B0B0"))
        labelText.textSize = 9f
        labelText.gravity = android.view.Gravity.CENTER
        labelText.setTypeface(android.graphics.Typeface.MONOSPACE)
        val labelParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        labelParams.setMargins(0, 4, 0, 0)
        labelText.layoutParams = labelParams
        button.addView(labelText)
        
        return button
    }
    
    private fun createSpyStatCard(title: String, value: String, color: Int): LinearLayout {
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.setPadding(12, 12, 12, 12)
        card.gravity = android.view.Gravity.CENTER
        card.setBackgroundColor(android.graphics.Color.parseColor("#2D2D2D"))
        
        val border = android.graphics.drawable.GradientDrawable()
        border.setColor(android.graphics.Color.parseColor("#2D2D2D"))
        border.setStroke(1, color)
        border.cornerRadius = 2f
        card.background = border
        
        val valueText = TextView(this)
        valueText.text = value
        valueText.setTextColor(android.graphics.Color.parseColor("#FFFFFF"))
        valueText.textSize = 20f
        valueText.setTypeface(android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.BOLD)
        valueText.gravity = android.view.Gravity.CENTER
        card.addView(valueText)
        
        val titleText = TextView(this)
        titleText.text = title
        titleText.setTextColor(color)
        titleText.textSize = 10f
        titleText.setTypeface(android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.BOLD)
        titleText.gravity = android.view.Gravity.CENTER
        val titleParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        titleParams.setMargins(0, 4, 0, 0)
        titleText.layoutParams = titleParams
        card.addView(titleText)
        
        return card
    }
    
    private fun createAgentDetail(label: String, value: String): LinearLayout {
        val detailLayout = LinearLayout(this)
        detailLayout.orientation = LinearLayout.HORIZONTAL
        detailLayout.setPadding(0, 8, 0, 8)
        
        val labelText = TextView(this)
        labelText.text = "$label:"
        labelText.setTextColor(android.graphics.Color.parseColor("#B0B0B0"))
        labelText.textSize = 11f
        labelText.setTypeface(android.graphics.Typeface.MONOSPACE)
        val labelParams = LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        )
        labelText.layoutParams = labelParams
        detailLayout.addView(labelText)
        
        val valueText = TextView(this)
        valueText.text = value
        valueText.setTextColor(android.graphics.Color.parseColor("#FFFFFF"))
        valueText.textSize = 11f
        valueText.setTypeface(android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.BOLD)
        valueText.gravity = android.view.Gravity.END
        detailLayout.addView(valueText)
        
        return detailLayout
    }
}