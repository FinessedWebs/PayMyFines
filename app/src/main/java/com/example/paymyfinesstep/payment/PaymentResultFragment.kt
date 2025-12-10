package com.example.paymyfinesstep.payment

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.paymyfinesstep.R
import com.example.paymyfinesstep.cart.CartManager
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class PaymentResultFragment : Fragment(R.layout.fragment_payment_result) {

    private val args: PaymentResultFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val icon = view.findViewById<ImageView>(R.id.iconResult)
        val title = view.findViewById<TextView>(R.id.textResultTitle)
        val msg = view.findViewById<TextView>(R.id.textResultMessage)
        val amount = view.findViewById<TextView>(R.id.textAmountPaid)
        val ref = view.findViewById<TextView>(R.id.textReference)
        val btnHome = view.findViewById<MaterialButton>(R.id.btnGoHome)

        val isSuccess = args.success
        val amtRands = args.amountCents / 100.0
        val reference = args.reference

        if (isSuccess) {
            icon.setImageResource(R.drawable.ic_success)
            title.text = "Payment Successful"
            msg.text = "Thank you! Your payment was completed."

            amount.text = "Amount Paid: R%.2f".format(amtRands)
            ref.text = "Reference: $reference"

            // Clear cart after success
            lifecycleScope.launch {
                CartManager.clear(requireContext())
            }


        } else {
            icon.setImageResource(R.drawable.ic_failed)
            title.text = "Payment Failed"
            msg.text = "Unfortunately the transaction did not complete. Please try again."

            amount.text = ""
            ref.text = ""
        }

        btnHome.setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }
    }
}
