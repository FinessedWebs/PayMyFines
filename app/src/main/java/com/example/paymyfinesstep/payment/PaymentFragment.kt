package com.example.paymyfinesstep.payment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.paymyfinesstep.R
import com.example.paymyfinesstep.api.ApiBackend
import com.example.paymyfinesstep.api.PaymentApi
import com.example.paymyfinesstep.cart.CartItem
import com.example.paymyfinesstep.cart.CartManager
import com.example.paymyfinesstep.payment.PaymentItemRequest
import com.example.paymyfinesstep.payment.PaymentRegisterRequest
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.*

class PaymentFragment : Fragment(R.layout.fragment_payment) {

    private val paymentApi: PaymentApi by lazy {
        ApiBackend.create(requireContext(), PaymentApi::class.java)
    }

    private lateinit var progress: ProgressBar
    private lateinit var btnCancel: MaterialButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progress = view.findViewById(R.id.paymentProgress)
        btnCancel = view.findViewById(R.id.btnCancelPayment)

        btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        startPaymentFlow()
    }

    private fun startPaymentFlow() {
        val ctx = requireContext()

        lifecycleScope.launch {
            val cartItems = CartManager.getCart(ctx)
            if (cartItems.isEmpty()) {
                Toast.makeText(ctx, "Your cart is empty", Toast.LENGTH_LONG).show()
                findNavController().popBackStack()
                return@launch
            }

            processPayment(cartItems)
        }
    }

    private suspend fun processPayment(cartItems: List<CartItem>) {
        if (cartItems.isEmpty()) {
            Toast.makeText(requireContext(), "Cart is empty", Toast.LENGTH_LONG).show()
            return
        }

        val item = cartItems.first()

        val now = getCurrentIso8601()
        val requestId = java.util.UUID.randomUUID().toString()
        val receipt = "APP-${System.currentTimeMillis()}"

        val request = PaymentRegisterRequest(
            freshFine = false,
            issuingAuthorityCode = "TMT",
            noticeNumber = item.noticeNumber,
            amountInCents = item.amountInCents,
            paymentDate = now,
            paymentProvider = 1,
            terminalId = 101,
            requestId = requestId,
            receiptNumber = receipt
        )

        progress.visibility = View.VISIBLE

        try {
            val response = withContext(Dispatchers.IO) {
                paymentApi.registerPayment(request)
            }

            progress.visibility = View.GONE

            findNavController().navigate(
                PaymentFragmentDirections.actionPaymentFragmentToPaymentResultFragment(
                    success = response.isSuccessful,
                    amountCents = response.amountPaidInCents,
                    reference = response.receiptNumber
                )
            )

            if (response.isSuccessful) {
                CartManager.clear(requireContext())
            }

        } catch (ex: Exception) {
            progress.visibility = View.GONE
            Toast.makeText(requireContext(), "Payment failed: ${ex.message}", Toast.LENGTH_LONG).show()
        }
    }
    @SuppressLint("SimpleDateFormat")
    private fun getCurrentIso8601(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
        return sdf.format(java.util.Date())

    }}


