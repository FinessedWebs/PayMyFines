package com.example.paymyfine.data.payment

import com.example.paymyfine.data.cart.CartManager
import io.ktor.util.date.getTimeMillis
import kotlinx.datetime.Instant

class PaymentRepository(
    private val service: PaymentService,
    private val cartManager: CartManager
) {

    suspend fun checkout(): PaymentRegisterResponse {

        val cart = cartManager.getCart()
        require(cart.isNotEmpty()) { "Cart empty" }

        val totalCents = cart.sumOf { it.amountInCents }
        val notices = cart.map { it.noticeNumber }

        val nowMillis = getTimeMillis()

        // ✅ Multiplatform ISO string
        val isoDate =
            Instant.fromEpochMilliseconds(nowMillis)
                .toString()

        val request = PaymentRegisterRequest(
            freshFine = false,
            issuingAuthorityCode = "TMT",
            noticeNumber = notices.first(),
            amountInCents = totalCents,

            // ✅ FIXED
            paymentDate = isoDate,

            paymentProvider = 1,
            terminalId = 101,

            requestId = nowMillis.toString(),
            receiptNumber = "APP-$nowMillis",
            paidNoticeNumbers = notices
        )

        println("SENDING PAYMENT DATE → $isoDate")

        val result = service.registerPayment(request)

        if (result.isSuccessful) {
            cartManager.clear()
        }

        return result
    }
}
