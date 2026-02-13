package com.example.paymyfine.screens.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import org.jetbrains.compose.resources.painterResource
import paymyfine.composeapp.generated.resources.*
import com.example.paymyfine.data.session.SessionStore
import com.example.paymyfine.ui.ResponsiveScreenShell

class AboutScreen(
    private val sessionStore: SessionStore
) : Screen {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.current

        val userName =
            sessionStore.getFullName() ?: "there"

        // ✅ NO sessionStore parameter here
        ResponsiveScreenShell {

            var selectedTab by remember { mutableStateOf(0) }

            val tabs = listOf(
                "About",
                "FAQs",
                "Services",
                "Contact Us"
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {

                ////////////////// GREETING //////////////////

                Text(
                    text = "Hi, $userName",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0)
                )

                Spacer(Modifier.height(16.dp))

                ////////////////// BANNER //////////////////

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {

                    Image(
                        painter = painterResource(Res.drawable.about_fines),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(20.dp)
                    ) {

                        Text(
                            "View and Pay your Fine",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = { navigator?.pop() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1565C0)
                            ),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text("Go to Home", color = Color.White)
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                ////////////////// TABS //////////////////

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = Color(0xFF1565C0)
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    title,
                                    color =
                                        if (selectedTab == index)
                                            Color(0xFF1565C0)
                                        else
                                            Color.Gray
                                )
                            }
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                ////////////////// CONTENT //////////////////

                when (selectedTab) {
                    0 -> AboutContent()
                    1 -> FAQContent()
                    2 -> ServicesContent()
                    3 -> ContactContent()
                }

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}




@Composable
fun AboutContent() {

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        Text(
            "About PayMyFines",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Text(
            "PayMyFines makes managing traffic fines simple and stress-free.",
            fontSize = 14.sp,
            color = Color.Gray,
            lineHeight = 20.sp
        )

        Text(
            "With our app you can quickly view, manage and pay your fines in one secure place. We work directly with official authorities to ensure your payments are processed correctly.",
            fontSize = 14.sp,
            color = Color.Gray,
            lineHeight = 20.sp
        )

        Text(
            "Our mission is to save you time, reduce paperwork, and give you peace of mind when dealing with fines.",
            fontSize = 14.sp,
            color = Color.Gray,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun FAQContent() {

    val faq = listOf(
        "Why can't I log in even after entering the new password?" to
                "Ensure there are no extra spaces before or after the password.",

        "Why is the password so long?" to
                "Longer passwords improve security and protect your account.",

        "I did not receive the password reset email." to
                "• Check your spam folder\n" +
                "• Verify your email address\n" +
                "• Wait a few minutes",

        "What should I do if iForce is down?" to
                "Try again after a few hours or contact support.",

        "What should I do if my account is blocked?" to
                "Your account will unlock after two hours.",

        "What should I do if my payment fails?" to
                "• Check card details\n" +
                "• Try another payment method\n" +
                "• Contact support if the issue persists",

        "Can I pay fines without logging in?" to
                "Yes. Search by ID or Notice Number and proceed to checkout.",

        "Can I view payment history without logging in?" to
                "No. Payment history requires an account.",

        "How do I register an account?" to
                "Click the Register option.\n" +
                "Complete all required fields:\n" +
                "Name, Surname, RSA ID Number, Email Address, Cellphone Number.\n" +
                "Confirm you are not a robot and submit.",

        "Will my ticket close immediately after payment?" to
                "Yes, once payment is received, the ticket is closed immediately.",

        "I paid but my ticket is still open. What should I do?" to
                "Contact us at 021 126 0600\n" +
                "or visit:\n" +
                "https://www.paymyfines.co.za/contactus",

        "I already paid but the ticket still shows outstanding." to
                "Please contact support for verification and resolution."
    )

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

        Text(
            "Frequently Asked Questions",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        faq.forEach { (question, answer) ->

            Text(
                text = question,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.Black
            )

            Text(
                text = answer,
                fontSize = 14.sp,
                color = Color.Gray,
                lineHeight = 20.sp
            )
        }
    }
}



@Composable
fun ServicesContent() {

    val services = listOf(
        "View and search traffic fines",
        "Pay outstanding fines securely",
        "Family profile management",
        "Payment history tracking",
        "Notifications and reminders",
        "Secure online payments",
        "Direct authority integration"
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        Text(
            "Our Services",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        services.forEach {
            Text(
                "• $it",
                fontSize = 14.sp,
                color = Color.Gray,
                lineHeight = 20.sp
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            "PayMyFines simplifies motoring compliance so you can drive with peace of mind.",
            fontSize = 14.sp,
            color = Color.Gray,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun ContactContent() {

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        Text(
            "Contact Us",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Text(
            "Queries",
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
        )

        Text(
            "Phone: +27 21 126 0600",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(Modifier.height(6.dp))

        Text(
            "Website",
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
        )

        Text(
            "www.paymyfines.co.za",
            fontSize = 14.sp,
            color = Color(0xFF1565C0) // link-style blue
        )

        Spacer(Modifier.height(6.dp))

        Text(
            "Address",
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
        )

        Text(
            "PO Box 234\nCentury City\nCape Town\nSouth Africa\n7446",
            fontSize = 14.sp,
            color = Color.Gray,
            lineHeight = 20.sp
        )
    }
}

