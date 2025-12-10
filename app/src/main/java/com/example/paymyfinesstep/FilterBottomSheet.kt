package com.example.paymyfinesstep

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.example.paymyfinesstep.R
import com.example.paymyfinesstep.FilterOptions

class FilterBottomSheet(
    private val currentFilters: FilterOptions,
    private val onApply: (FilterOptions) -> Unit

) : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.filter_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // STATUS CHIPS
        val chipStatusNotice = view.findViewById<Chip>(R.id.chipStatusNotice)
        val chipStatusSummons = view.findViewById<Chip>(R.id.chipStatusSummons)
        val chipStatusWarrant = view.findViewById<Chip>(R.id.chipStatusWarrant)
        val chipStatusCamera = view.findViewById<Chip>(R.id.chipStatusCamera)
        val chipStatusCourt = view.findViewById<Chip>(R.id.chipStatusCourt)

        // SEVERITY
        val chipSeverityHigh = view.findViewById<Chip>(R.id.chipSeverityHigh)
        val chipSeverityMed = view.findViewById<Chip>(R.id.chipSeverityMed)
        val chipSeverityLow = view.findViewById<Chip>(R.id.chipSeverityLow)

        // PAYMENT
        val chipPayAllowed = view.findViewById<Chip>(R.id.chipPayAllowed)
        val chipPayNotAllowed = view.findViewById<Chip>(R.id.chipPayNotAllowed)

        // DATE
        val chipDate30 = view.findViewById<Chip>(R.id.chipDate30)
        val chipDate6 = view.findViewById<Chip>(R.id.chipDate6months)
        val chipDate12 = view.findViewById<Chip>(R.id.chipDate12months)
        val chipDateAll = view.findViewById<Chip>(R.id.chipDateAll)

        // LOCATION
        val chipMetro = view.findViewById<Chip>(R.id.chipMetro)
        val chipProvincial = view.findViewById<Chip>(R.id.chipProvincial)
        val chipSAPS = view.findViewById<Chip>(R.id.chipSAPS)

        val btnApply = view.findViewById<Button>(R.id.btnApplyFilters)
        val btnReset = view.findViewById<Button>(R.id.btnResetFilters)

        // ===========================
        // RESTORE CURRENT FILTER STATE
        // ===========================
        chipStatusNotice.isChecked = "Notice" in currentFilters.statuses
        chipStatusSummons.isChecked = "Summons" in currentFilters.statuses
        chipStatusWarrant.isChecked = "Warrant" in currentFilters.statuses
        chipStatusCamera.isChecked = "Camera" in currentFilters.statuses
        chipStatusCourt.isChecked = "Court" in currentFilters.statuses

        chipSeverityHigh.isChecked = "High" in currentFilters.severities
        chipSeverityMed.isChecked = "Medium" in currentFilters.severities
        chipSeverityLow.isChecked = "Low" in currentFilters.severities

        chipPayAllowed.isChecked = "Allowed" in currentFilters.paymentFlags
        chipPayNotAllowed.isChecked = "NotAllowed" in currentFilters.paymentFlags

        when (currentFilters.dateRange) {
            "30"  -> chipDate30.isChecked = true
            "180" -> chipDate6.isChecked = true
            "365" -> chipDate12.isChecked = true
            else  -> chipDateAll.isChecked = true
        }

        chipMetro.isChecked = "Metro" in currentFilters.issuingAuthorities
        chipProvincial.isChecked = "Provincial" in currentFilters.issuingAuthorities
        chipSAPS.isChecked = "SAPS" in currentFilters.issuingAuthorities


        // ===========================
        // APPLY FILTERS
        // ===========================
        btnApply.setOnClickListener {

            val f = FilterOptions()

            // STATUS
            if (chipStatusNotice.isChecked) f.statuses.add("Notice")
            if (chipStatusSummons.isChecked) f.statuses.add("Summons")
            if (chipStatusWarrant.isChecked) f.statuses.add("Warrant")
            if (chipStatusCamera.isChecked) f.statuses.add("Camera")
            if (chipStatusCourt.isChecked) f.statuses.add("Court")

            // SEVERITY
            if (chipSeverityHigh.isChecked) f.severities.add("High")
            if (chipSeverityMed.isChecked) f.severities.add("Medium")
            if (chipSeverityLow.isChecked) f.severities.add("Low")

            // PAYMENT
            if (chipPayAllowed.isChecked) f.paymentFlags.add("Allowed")
            if (chipPayNotAllowed.isChecked) f.paymentFlags.add("NotAllowed")

            // DATE RANGE
            f.dateRange = when {
                chipDate30.isChecked -> "30"
                chipDate6.isChecked -> "180"
                chipDate12.isChecked -> "365"
                else -> null
            }

            // ISSUING AUTHORITY
            if (chipMetro.isChecked) f.issuingAuthorities.add("Metro")
            if (chipProvincial.isChecked) f.issuingAuthorities.add("Provincial")
            if (chipSAPS.isChecked) f.issuingAuthorities.add("SAPS")

            onApply(f)
            dismiss()
        }

        // ===========================
        // RESET
        // ===========================
        btnReset.setOnClickListener {
            onApply(FilterOptions()) // return empty filters
            dismiss()
        }
    }
}
