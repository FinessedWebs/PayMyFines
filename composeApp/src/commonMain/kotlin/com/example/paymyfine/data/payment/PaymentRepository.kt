package com.example.paymyfine.data.payment

import com.example.paymyfine.data.cart.CartProvider
import com.example.paymyfine.data.session.SessionStore
import io.ktor.util.date.getTimeMillis
import kotlinx.datetime.Instant

class PaymentRepository(
    private val service: PaymentService,
    private val sessionStore: SessionStore
) {

    suspend fun checkout(
        onProgress: (current: Int, total: Int) -> Unit
    ): List<PaymentRegisterResponse> {

        val cartManager = CartProvider.get(sessionStore)
        val cart = cartManager.getCart()

        require(cart.isNotEmpty()) { "Cart empty" }

        val total = cart.size

        // ✅ SAFE: No Clock.System used
        val nowMillis = getTimeMillis()

        // ✅ TRUE ISO-8601 string
        val iso = Instant
            .fromEpochMilliseconds(nowMillis)
            .toString()

        val results = mutableListOf<PaymentRegisterResponse>()

        cart.forEachIndexed { index, item ->

            onProgress(index + 1, total)

            val request = PaymentRegisterRequest(
                freshFine = false,
                issuingAuthorityCode = "TMT",
                noticeNumber = item.noticeNumber,
                amountInCents = item.amountInCents,
                paymentDate = iso,   // ✅ REAL ISO DATE
                paymentProvider = 1,
                terminalId = 101,
                requestId = "$nowMillis-${item.noticeNumber}",
                receiptNumber = "APP-$nowMillis-${item.noticeNumber}",
                paidNoticeNumbers = listOf(item.noticeNumber)
            )

            val result = service.registerPayment(request)

            if (result.isSuccessful) {
                PaymentHistoryStore.save(
                    PaymentHistoryItem(
                        noticeNumber = item.noticeNumber,
                        amount = item.amountInCents,
                        date = iso,
                        receipt = result.receiptNumber
                    )
                )
            }

            results.add(result)
        }

        if (results.all { it.isSuccessful }) {
            cartManager.clear()
        }

        return results
    }
}
