package com.example.paymyfinesstep.ui.about

import android.content.Context
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.paymyfinesstep.R
import com.google.android.material.tabs.TabLayout

class AboutFragment : Fragment(R.layout.fragment_about) {

    private lateinit var tabLayout: TabLayout
    private lateinit var textContent: TextView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext()
            .getSharedPreferences("paymyfines_prefs", Context.MODE_PRIVATE)

        val username = prefs.getString("fullName", "there") ?: "there"

        val textGreeting = view.findViewById<TextView>(R.id.textGreeting)
        val btnGoHome = view.findViewById<Button>(R.id.btnGoHome)

        textGreeting.text = "Hi, $username"

        btnGoHome.setOnClickListener {
            findNavController().navigate(
                R.id.action_aboutFragment_to_homeFragment
            )
        }

        tabLayout = view.findViewById(R.id.tabAbout)
        textContent = view.findViewById(R.id.textAboutContent)

// Default tab
        showAboutContent()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> showAboutContent()
                    1 -> showFaqContent()
                    2 -> showServicesContent()
                    3 -> showContactContent()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })


    }

    private fun showAboutContent() {
        textContent.text = """
            About Paymyfines
            
            Welcome to the Paymyfines online payment platform.
            
            Access your fine history through our quick search button, pay your outstanding fines in a few easy clicks, and in no time you will be on your way again with trouble-free motoring.
            
            We know that paying motoring fines can at times be difficult and inconvenient, with days off work and long queues. Frustrating indeed.
            
            At Paymyfines, we have simplified the process with cutting-edge simplicity and ease of use to ensure the best and quickest way to get you back on the road again.
            
            Paymyfines has secured direct relationships with national road traffic authorities, allowing us to streamline your payment options and facilitate queries and concerns at the click of a button.
            
            We are confident that Paymyfines will ease the burden and get you back to the joy of driving.
            
            It is the best way to pay.
""".trimIndent()
    }

    private fun showFaqContent() {

        val html = """
        <b>Frequently Asked Questions (FAQ)</b><br><br>

        <b>Why can't I log in even after entering the new password?</b><br>
        <font color="#888888">
        Ensure there are no extra spaces before or after the password.
        </font><br><br>

        <b>Why is the password so long?</b><br>
        <font color="#888888">
        Longer passwords improve security and protect your account.
        </font><br><br>

        <b>I did not receive the password reset email.</b><br>
        <font color="#888888">
        • Check your spam folder<br>
        • Verify your email address<br>
        • Wait a few minutes
        </font><br><br>

        <b>What should I do if iForce is down?</b><br>
        <font color="#888888">
        Try again after a few hours or contact support.
        </font><br><br>

        <b>What should I do if my account is blocked?</b><br>
        <font color="#888888">
        Your account will unlock after two hours.
        </font><br><br>

        <b>What should I do if my payment fails?</b><br>
        <font color="#888888">
        • Check card details<br>
        • Try another payment method<br>
        • Contact support if the issue persists
        </font><br><br>

        <b>Can I pay fines without logging in?</b><br>
        <font color="#888888">
        Yes. Search by ID or Notice Number and proceed to checkout.
        </font><br><br>

        <b>Can I view payment history without logging in?</b><br>
        <font color="#888888">
        No. Payment history requires an account.
        </font><br><br>

        <b>How do I register an account?</b><br>
        <font color="#888888">
        Click the Register option.<br>
        Complete all required fields:<br>
        Name, Surname, RSA ID Number, Email Address, Cellphone Number.<br>
        Confirm you are not a robot and submit.
        </font><br><br>

        <b>Will my ticket close immediately after payment?</b><br>
        <font color="#888888">
        Yes, once payment is received, the ticket is closed immediately.
        </font><br><br>

        <b>I paid but my ticket is still open. What should I do?</b><br>
        <font color="#888888">
        Contact us at 021 126 0600 or visit<br>
        https://www.paymyfines.co.za/contactus
        </font><br><br>

        <b>I already paid but the ticket still shows outstanding.</b><br>
        <font color="#888888">
        Please contact support for verification and resolution.
        </font>
    """.trimIndent()

        textContent.text = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
    }


    private fun showServicesContent() {
        textContent.text = """
            Our Services
            
            • View and search traffic fines
            • Pay outstanding fines securely
            • Family profile management
            • Payment history tracking
            • Notifications and reminders
            • Secure online payments
            • Direct authority integration
            
            Paymyfines simplifies motoring compliance so you can drive with peace of mind.
            """.trimIndent()
    }

    private fun showContactContent() {
        textContent.text = """
            Contact Us
            
            Queries
            
            Call us:
            021 126 0600
            
            Email:
            https://www.paymyfines.co.za/contactus
            
            Postal Address:
            PO Box 234
            Century City
            Cape Town
            South Africa
            7446
            """.trimIndent()
    }





}
