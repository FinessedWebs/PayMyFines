
package com.example.paymyfinesstep.ui.home

import android.app.AlertDialog
import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paymyfinesstep.FilterBottomSheet
import com.example.paymyfinesstep.FilterOptions
import com.example.paymyfinesstep.FinesAdapter
import com.example.paymyfinesstep.HomeGroupedAdapter
import com.example.paymyfinesstep.InfringementsApi
import com.example.paymyfinesstep.R
import com.example.paymyfinesstep.api.ApiBackend
import com.example.paymyfinesstep.api.FamilyApi
import com.example.paymyfinesstep.api.FamilyMember
import com.example.paymyfinesstep.api.IForceItem
import com.example.paymyfinesstep.cart.CartManager
import com.example.paymyfinesstep.databinding.FragmentHomeBinding

import com.example.paymyfinesstep.ui.family.AddFamilyMemberDialogFragment
import com.example.paymyfinesstep.ui.family.FamilyAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt


class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val api by lazy { ApiBackend.create(requireContext(), InfringementsApi::class.java) }

    private val familyApi by lazy { ApiBackend.create(requireContext(),FamilyApi::class.java) }

    private val prefs by lazy {
        requireContext().getSharedPreferences("paymyfines_prefs", 0)
    }

    private lateinit var imageProfileAvatar: ImageView
    private lateinit var textProfileName: TextView
    private lateinit var textProfileEmail: TextView
    private lateinit var textProfileId: TextView

    private lateinit var textTopFineCount: TextView
    private lateinit var recyclerFines: RecyclerView
    private lateinit var progress: View

    private lateinit var btnUnpaid: TextView
    private lateinit var btnPaid: TextView
    private lateinit var slidingPill: View
    private lateinit var toggleContainer: FrameLayout

    private lateinit var adapter: FinesAdapter

    private var showUnpaid = true   // toggle setting
    private var allFines: List<IForceItem> = emptyList()
    private lateinit var dropdownProfileMode: MaterialAutoCompleteTextView
    private lateinit var inputLayoutProfileMode: com.google.android.material.textfield.TextInputLayout

    private var currentMode: ProfileMode = ProfileMode.INDIVIDUAL

    private enum class ProfileMode { INDIVIDUAL, FAMILY }
    private var familyMembers: List<FamilyMember> = emptyList()


    private lateinit var recyclerFamilyUsers: RecyclerView
    private lateinit var textUsersFound: TextView
    /*private lateinit var textExpandAll: TextView*/
    private lateinit var editSearch: TextInputEditText
    private var searchQuery = ""



    // import at top of file:
// import com.example.paymyfinesstep.ui.family.FamilyAdapter
    private lateinit var familyAdapter: com.example.paymyfinesstep.ui.family.FamilyAdapter

    private var fullFamilyList: List<FamilyMember> = emptyList()
    private var activeFilters = FilterOptions()

    private fun saveMode(mode: ProfileMode) {
        prefs.edit().putString("profile_mode", mode.name).apply()
    }

    private fun loadSavedMode(): ProfileMode {
        val saved = prefs.getString("profile_mode", ProfileMode.INDIVIDUAL.name)
        return ProfileMode.valueOf(saved!!)
    }

    private val profileModes = listOf("Individual", "Family")


    private lateinit var textCartBadge: TextView


    /*companion object {
        private var savedMode: ProfileMode = ProfileMode.INDIVIDUAL
    }*/






    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        binding.fabAddMember.setOnClickListener {
            AddFamilyMemberDialogFragment {
                loadFines()   // refresh family list after adding
            }.show(childFragmentManager, "add_family_dialog")
        }



        // ------------------------------------------------------
        // 1. RESTORE SAVED MODE IMMEDIATELY
        // ------------------------------------------------------
        /*currentMode = savedMode*/
        currentMode = loadSavedMode()

        // ------------------------------------------------------
        // 2. MENU HOST
        // ------------------------------------------------------
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_top, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_apply_reduction -> {
                        findNavController().navigate(R.id.applyReductionFragment)
                        return true
                    }
                    R.id.action_apply_redirection -> {
                        findNavController().navigate(R.id.applyRedirectionFragment)
                        return true
                    }
                    R.id.action_settings -> {
                        findNavController().navigate(R.id.settingsFragment)
                        return true
                    }
                    R.id.action_logout -> {
                        logoutUser()
                        return true
                    }
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)


        // ------------------------------------------------------
        // 3. BASIC UI REFERENCES
        // ------------------------------------------------------
        imageProfileAvatar = view.findViewById(R.id.imageProfileAvatar)
        textProfileName = view.findViewById(R.id.textProfileName)
        textProfileEmail = view.findViewById(R.id.textProfileEmail)
        textProfileId = view.findViewById(R.id.textProfileId)

        textTopFineCount = view.findViewById(R.id.textTopFineCount)

        recyclerFines = view.findViewById(R.id.recyclerFines)
        progress = view.findViewById(R.id.progressBar)

        btnUnpaid = view.findViewById(R.id.btnUnpaid)
        btnPaid = view.findViewById(R.id.btnPaid)
        slidingPill = view.findViewById(R.id.viewSlidingPill)
        toggleContainer = view.findViewById(R.id.togglePaidState)

        recyclerFamilyUsers = view.findViewById(R.id.recyclerFamilyUsers)
        editSearch = view.findViewById(R.id.editSearch)
        textUsersFound = view.findViewById(R.id.textUsersFound)
        textCartBadge = view.findViewById(R.id.textCartBadge)

        val btnSearch = view.findViewById<ImageButton>(R.id.btnSearch)
        val btnFilter = view.findViewById<ImageButton>(R.id.btnFilter)
        val btnCart = view.findViewById<ImageButton>(R.id.btnCart)


        // ------------------------------------------------------
        // 4. DROPDOWN SETUP
        // ------------------------------------------------------
        inputLayoutProfileMode = binding.inputLayoutProfileMode
        dropdownProfileMode = binding.dropdownProfileMode

        val profileModes = arrayOf("Individuals", "Family")

        dropdownProfileMode.setSimpleItems(profileModes)

        // Set dropdown text based on restored mode
        dropdownProfileMode.setText(
            if (currentMode == ProfileMode.INDIVIDUAL) "Individuals" else "Family",
            false
        )

        inputLayoutProfileMode.setEndIconOnClickListener { dropdownProfileMode.showDropDown() }
        dropdownProfileMode.setOnClickListener { dropdownProfileMode.showDropDown() }

        dropdownProfileMode.setOnItemClickListener { _, _, position, _ ->
            currentMode = if (position == 0) ProfileMode.INDIVIDUAL else ProfileMode.FAMILY
            saveMode(currentMode)


            updateModeUI()
            loadFines()
            updateFabVisibility(currentMode)
        }


        // ------------------------------------------------------
        // 5. SEARCH
        // ------------------------------------------------------
        editSearch.addTextChangedListener {
            val q = it.toString().trim()

            if (currentMode == ProfileMode.FAMILY) {
                searchFamilyMembers(q)
            } else {
                searchFines(q)
            }
        }

        btnSearch.setOnClickListener { showSearchBottomSheet() }


        // ------------------------------------------------------
        // 6. FILTERS
        // ------------------------------------------------------
        btnFilter.setOnClickListener {
            val sheet = FilterBottomSheet(activeFilters) { newFilters ->
                activeFilters = newFilters
                updateList()
            }
            sheet.show(childFragmentManager, "filters")
            updateFilterBadge()
        }


        // ------------------------------------------------------
        // 7. CART
        // ------------------------------------------------------
        btnCart.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToCartFragment()
            findNavController().navigate(action)
        }

        updateCartBadge()


        // ------------------------------------------------------
        // 8. FAMILY ADAPTER
        // ------------------------------------------------------
        familyAdapter = FamilyAdapter(
            items = mutableListOf(),
            allFines = emptyList(),
            onFineClick = { fine ->
                val action = HomeFragmentDirections
                    .actionHomeFragmentToFineDetailsFragment(fine)
                findNavController().navigate(action)
            },
            onMemberClick = { },
            onDelete = { },
            onEdit = { }
        )

        recyclerFamilyUsers.layoutManager = LinearLayoutManager(requireContext())
        recyclerFamilyUsers.adapter = familyAdapter


        // ------------------------------------------------------
        // 9. LOAD DATA + INIT UI + FAB
        // ------------------------------------------------------
        setupProfile()
        setupRecycler()
        setupToggleAnimation()

        resetFabMenu()           // Ensure FAB menu starts closed
        setupFabMenu()
        updateFabVisibility(currentMode)

        loadFines()
    }


    private var menuOpen = false

    private fun resetFabMenu() {
        binding.fabAddMember.apply {
            visibility = View.GONE
            alpha = 0f
            translationY = 0f
        }

        binding.fabDeleteMember.apply {
            visibility = View.GONE
            alpha = 0f
            translationY = 0f
        }
    }


    private fun setupFabMenu() {
        var isOpen = false

        binding.fabMain.setOnClickListener {

            isOpen = !isOpen

            if (isOpen) {
                // Expand menu
                binding.fabAddMember.visibility = View.VISIBLE
                binding.fabDeleteMember.visibility =
                    if (currentMode == ProfileMode.FAMILY) View.VISIBLE else View.GONE

                binding.fabAddMember.animate().translationY(-10f).alpha(1f).setDuration(200).start()
                binding.fabDeleteMember.animate().translationY(-25f).alpha(1f).setDuration(200).start()

            } else {
                // Collapse
                binding.fabAddMember.animate().translationY(0f).alpha(0f)
                    .setDuration(150)
                    .withEndAction { binding.fabAddMember.visibility = View.GONE }
                    .start()

                binding.fabDeleteMember.animate().translationY(0f).alpha(0f)
                    .setDuration(150)
                    .withEndAction { binding.fabDeleteMember.visibility = View.GONE }
                    .start()
            }
        }

        /*binding.fabAddMember.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_addFamily)
        }*/

        binding.fabDeleteMember.setOnClickListener {
            showDeleteMemberDialog()
        }
    }

    private fun updateFabVisibility(mode: ProfileMode) {
        binding.fabAddMember.visibility = View.VISIBLE // always visible

        if (mode == ProfileMode.INDIVIDUAL) {
            binding.fabDeleteMember.visibility = View.GONE
        } else {
            binding.fabDeleteMember.visibility = View.VISIBLE
        }
    }



    /*private fun updateFabVisibility(mode: String) {
        if (mode == "Individual") {
            binding.fabDeleteMember.visibility = View.GONE
            binding.fabAddMember.visibility = View.VISIBLE
            binding.fabMain.text = "Add"
        } else if (mode == "Family") {
            binding.fabDeleteMember.visibility = View.VISIBLE
            binding.fabAddMember.visibility = View.VISIBLE
            binding.fabMain.text = "Actions"
        }
    }*/



    override fun onResume() {
        super.onResume()
        updateCartBadge()
    }

    private fun showDeleteMemberDialog() {

        if (fullFamilyList.isEmpty()) {
            Toast.makeText(requireContext(), "No family members available", Toast.LENGTH_SHORT).show()
            return
        }

        val names = fullFamilyList.map { "${it.fullName} ${it.surname}" }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle("Select Member to Delete")
            .setItems(names) { _, index ->
                val selected = fullFamilyList[index]
                confirmFamilyDelete(selected)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun confirmFamilyDelete(member: FamilyMember) {
        AlertDialog.Builder(requireContext())
            .setTitle("Remove Family Member")
            .setMessage("Are you sure you want to remove ${member.fullName}?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->
                performFamilyDelete(member)
            }
            .show()
    }

    private fun performFamilyDelete(member: FamilyMember) {
        lifecycleScope.launch {
            try {
                val resp = withContext(Dispatchers.IO) {
                    familyApi.deleteFamilyMember(member.id)
                }

                if (resp.isSuccessful) {
                    Toast.makeText(requireContext(), "Member removed", Toast.LENGTH_SHORT).show()
                    loadFines() // reload family list + fines
                } else {
                    Toast.makeText(requireContext(), "Delete failed (${resp.code()})", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }



    private fun updateCartBadge() {
        lifecycleScope.launch {
            val list = CartManager.getCart(requireContext())

            if (list.isEmpty()) {
                textCartBadge.visibility = View.GONE
            } else {
                textCartBadge.visibility = View.VISIBLE
                textCartBadge.text = list.size.toString()
            }
        }
    }




    private fun logoutUser() {
        val prefs = requireContext().getSharedPreferences("paymyfines_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()

        findNavController().navigate(
            R.id.loginFragment,
            null,
            NavOptions.Builder()
                .setPopUpTo(R.id.nav_graph, true) // pop entire stack
                .build()
        )
    }




    private fun isWithinRange(dateStr: String, range: String?): Boolean {
        if (range == null) return true   // no date filter applied

        return try {
            val year = dateStr.substring(0, 4).toInt()
            val month = dateStr.substring(4, 6).toInt()
            val day = dateStr.substring(6, 8).toInt()

            val fineDate = Calendar.getInstance().apply {
                set(year, month - 1, day)
            }.timeInMillis

            val now = System.currentTimeMillis()
            val diff = now - fineDate

            when (range) {
                "30" -> diff <= 30L * 24 * 60 * 60 * 1000
                "180" -> diff <= 180L * 24 * 60 * 60 * 1000
                "365" -> diff <= 365L * 24 * 60 * 60 * 1000
                else -> true   // all time
            }
        } catch (e: Exception) {
            false
        }
    }



    private fun updateFilterBadge() {
        val badge = view?.findViewById<TextView>(R.id.textFilterBadge)
        if (badge != null) {
            badge.visibility = if (activeFilters.isEmpty) View.GONE else View.VISIBLE
        }
    }


    private fun searchFamilyMembers(query: String) {

        if (currentMode != ProfileMode.FAMILY) return

        if (query.isBlank()) {
            // reset list
            familyAdapter.update(fullFamilyList.toMutableList(), allFines)
            textUsersFound.text = "${fullFamilyList.size} Users Found"
            return
        }

        val filtered = fullFamilyList.filter { member ->
            member.fullName.contains(query, ignoreCase = true) ||
                    member.surname.contains(query, ignoreCase = true) ||
                    member.idNumber.contains(query)
        }

        familyMembers = filtered

        familyAdapter.update(filtered.toMutableList(), allFines)



        textUsersFound.text = "${filtered.size} Users Found"
    }


    private fun showSearchBottomSheet() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottomsheet_search, null)
        val bottomSheet = BottomSheetDialog(requireContext(), R.style.BottomSheetTheme)
        bottomSheet.setContentView(bottomSheetView)

        val edit = bottomSheetView.findViewById<TextInputEditText>(R.id.bottomSearchEdit)

        // Auto-focus + show keyboard
        edit.requestFocus()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT)

        // Live filtering
        edit.addTextChangedListener {
            searchFines(it.toString().trim())
        }

        bottomSheet.show()
    }


    private fun searchFines(query: String) {
        val list = if (currentMode == ProfileMode.INDIVIDUAL) {
            allFines
        } else {
            allFines   // family fines + userIdNumber already attached
        }

        val filtered = list.filter { fine ->
            fine.noticeNumber?.contains(query, ignoreCase = true) == true ||
                    fine.offenceLocation?.contains(query, ignoreCase = true) == true ||
                    fine.vehicleLicenseNumber?.contains(query, ignoreCase = true) == true ||
                    fine.chargeDescriptions?.any { it.contains(query, ignoreCase = true) } == true
        }

        adapter.update(filtered)

        textTopFineCount.text = "${filtered.size} results"
    }


    private fun onFamilyMemberClicked(member: FamilyMember) {
        // Clear current fine list
        allFines = emptyList()
        updateList()

        lifecycleScope.launch {
            val response = withContext(Dispatchers.IO) {
                api.getFamilyInfringements(member.idNumber)
            }

            allFines = response.iForce ?: emptyList()
            updateList()
        }
    }



    private fun updateModeUI() {
        val isFamily = currentMode == ProfileMode.FAMILY

        val cardProfileInfo = view?.findViewById<View>(R.id.cardProfileInfo)
        val cardSummary = view?.findViewById<View>(R.id.cardSummary)
        val familyHeader = view?.findViewById<View>(R.id.familyHeaderRow)
        val textUsersFound = view?.findViewById<TextView>(R.id.textUsersFound)
        /*val textExpandAll = view?.findViewById<TextView>(R.id.textExpandAll)*/
        val recyclerFamilyUsers = view?.findViewById<RecyclerView>(R.id.recyclerFamilyUsers)

        // 🔥 THIS VIEW was the cause of hiding Family Mode fines
        val recyclerIndividual = view?.findViewById<RecyclerView>(R.id.recyclerFines)

        if (!isFamily) {
            // -------------------
            // INDIVIDUAL MODE
            // -------------------
            cardProfileInfo?.visibility = View.VISIBLE
            cardSummary?.visibility = View.VISIBLE
            recyclerIndividual?.visibility = View.VISIBLE

            // Hide family mode views
            familyHeader?.visibility = View.GONE
            recyclerFamilyUsers?.visibility = View.GONE
            textUsersFound?.visibility = View.GONE
            /*textExpandAll?.visibility = View.GONE*/

            // Do NOT reset adapter every time → it breaks expand states
            return
        }

        // -------------------
        // FAMILY MODE
        // -------------------
        cardProfileInfo?.visibility = View.GONE
        cardSummary?.visibility = View.GONE
        recyclerIndividual?.visibility = View.GONE  // 🔥 REQUIRED FIX

        familyHeader?.visibility = View.VISIBLE
        recyclerFamilyUsers?.visibility = View.VISIBLE
        textUsersFound?.visibility = View.VISIBLE
        /*textExpandAll?.visibility = View.VISIBLE*/

        textUsersFound?.text = "0 Users Found"
        /*textExpandAll?.text = "Expand all ▼"*/
    }






    // -----------------------------------------------------------
    // PROFILE BAR
    // -----------------------------------------------------------
    private fun setupProfile() {
        val name = prefs.getString("fullName", "You") ?: "You"
        val email = prefs.getString("email", "") ?: ""
        val idNumber = prefs.getString("idNumber", "") ?: ""

        textProfileName.text = name
        textProfileEmail.text = email
        textProfileId.text = "ID: $idNumber"

        applyProfileIconFromId(idNumber)
    }



    // -----------------------------------------------------------
    // RECYCLER
    // -----------------------------------------------------------
    private fun setupRecycler() {
        adapter = FinesAdapter(emptyList()) { fine ->
            onFineClicked(fine)
        }

        recyclerFines.layoutManager = LinearLayoutManager(requireContext())
        recyclerFines.adapter = adapter
    }


    // -----------------------------------------------------------
    // LOAD FINES (ONLY INDIVIDUAL)
    // -----------------------------------------------------------
    private fun loadFines() {
        progress.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val fines = withContext(Dispatchers.IO) {

                    // ---------------------------- INDIVIDUAL ----------------------------
                    if (currentMode == ProfileMode.INDIVIDUAL) {
                        val response = api.getInfringements()

                        val error = response.errorDetails?.firstOrNull()
                        if (error?.statusCode == 99) {
                            withContext(Dispatchers.Main) { showDailyLimitError() }
                            return@withContext emptyList<IForceItem>()
                        }

                        return@withContext response.iForce ?: emptyList()
                    }

                    // ---------------------------- FAMILY ----------------------------
                    // ----------------------------
// FAMILY MODE
// ----------------------------
                    val familyList = familyApi.getFamilyMembers()
                    fullFamilyList = familyList
                    familyMembers = familyList

// Update UI with family list
                    withContext(Dispatchers.Main) {
                        textUsersFound.text = "${familyList.size} Users Found"
                        familyAdapter.update(familyList.toMutableList(), emptyList())
                    }

                    var dailyLimitReached = false
                    val collectedFines = mutableListOf<IForceItem>()

                    for (member in familyList) {
                        val resp = api.getFamilyInfringements(member.idNumber)

                        val err = resp.errorDetails?.firstOrNull()

                        val isDailyLimit = err?.statusCode == 99 &&
                                err.message?.contains("daily limit", ignoreCase = true) == true

                        if (isDailyLimit) {
                            dailyLimitReached = true
                            continue
                        }

                        // Do NOT treat "No results found" as a limit error.
                        // Just skip fines for that member.
                        if (err != null) {
                            continue
                        }

                        resp.iForce?.forEach { fine ->
                            collectedFines.add(fine.copy(userIdNumber = member.idNumber))
                        }
                    }


                    // Update UI with whatever fines were successfully retrieved
                    withContext(Dispatchers.Main) {
                        familyAdapter.update(familyList.toMutableList(), collectedFines)
                    }

                        // After loading everything, show the limit warning once
                    if (dailyLimitReached) {
                        withContext(Dispatchers.Main) {
                            showDailyLimitError()
                        }
                    }

                    return@withContext collectedFines

                }

                allFines = fines
                updateList()

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading fines: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                progress.visibility = View.GONE
            }
        }
    }

    private fun showDailyLimitError() {
        Toast.makeText(
            requireContext(),
            "Daily lookup limit reached. Try again tomorrow.",
            Toast.LENGTH_LONG
        ).show()
    }









    private fun applyProfileIconFromId(idNumber: String) {
        if (idNumber.length < 10) return

        // ----- GENDER -----
        val genderDigits = idNumber.substring(6, 10).toInt()
        val isMale = genderDigits >= 5000
        val isFemale = genderDigits < 5000

        // ----- AGE (Calendar version — works on API 24) -----
        try {
            val yy = idNumber.substring(0, 2).toInt()
            val mm = idNumber.substring(2, 4).toInt() - 1  // Calendar months start at 0
            val dd = idNumber.substring(4, 6).toInt()

            // Get today's date
            val today = java.util.Calendar.getInstance()

            // Convert yy to full year (1900s or 2000s)
            val currentYearTwoDigits = today.get(java.util.Calendar.YEAR) % 100
            val fullYear = if (yy <= currentYearTwoDigits) 2000 + yy else 1900 + yy

            // Birthdate Calendar
            val birth = java.util.Calendar.getInstance()
            birth.set(fullYear, mm, dd)

            // Compute age
            var age = today.get(java.util.Calendar.YEAR) - birth.get(java.util.Calendar.YEAR)

            // Adjust age if birthday hasn't happened yet this year
            if (
                today.get(java.util.Calendar.DAY_OF_YEAR) <
                birth.get(java.util.Calendar.DAY_OF_YEAR)
            ) {
                age--
            }

            // ----- SELECT ICON -----
            val iconRes = when {
                isMale && age < 18 -> R.drawable.ic_boy
                isMale && age < 65 -> R.drawable.ic_father
                isMale -> R.drawable.ic_grandpa

                isFemale && age < 18 -> R.drawable.ic_girl
                isFemale && age < 65 -> R.drawable.ic_mother
                else -> R.drawable.ic_grandma
            }

            imageProfileAvatar.setImageResource(iconRes)

        } catch (e: Exception) {
            // If anything fails (invalid ID format), do nothing
            e.printStackTrace()
        }
    }




    // -----------------------------------------------------------
    // FILTER LIST
    // -----------------------------------------------------------
    private fun updateList() {

        // -------------------------------
        // STEP 1 — FILTER UNPAID / PAID
        // -------------------------------
        val filteredPaid = if (showUnpaid) {
            allFines.filter { (it.amountDueInCents ?: 0) > 0 }
        } else {
            allFines.filter {
                (it.amountDueInCents ?: 0) <= 0 ||
                        (it.status?.lowercase()?.contains("paid") == true)
            }
        }


        // -------------------------------
        // STEP 2 — SEARCH
        // -------------------------------
        val afterSearch = filteredPaid.filter {
            val q = searchQuery.lowercase()

            it.chargeDescriptions?.joinToString(" ")?.lowercase()?.contains(q) == true ||
                    (it.offenceLocation?.lowercase()?.contains(q) == true) ||
                    (it.vehicleLicenseNumber?.lowercase()?.contains(q) == true) ||
                    (it.noticeNumber?.lowercase()?.contains(q) == true)
        }


        // -------------------------------
        // STEP 3 — FILTER OPTIONS
        // -------------------------------
        val afterFilters = afterSearch.filter { fine ->

            // --- STATUS ---
            if (activeFilters.statuses.isNotEmpty()) {
                val statusMatch = activeFilters.statuses.any { selected ->
                    fine.status?.lowercase()?.contains(selected.lowercase()) == true
                }
                if (!statusMatch) return@filter false
            }

            // --- SEVERITY ---
            if (activeFilters.severities.isNotEmpty()) {
                val amount = (fine.amountDueInCents ?: 0)

                val severity = when {
                    amount >= 100_000 -> "High"
                    amount >= 50_000 -> "Medium"
                    amount > 0 -> "Low"
                    else -> "Low"
                }

                if (!activeFilters.severities.contains(severity)) return@filter false
            }

            // --- PAYMENT FLAGS ---
            if (activeFilters.paymentFlags.isNotEmpty()) {
                val eligible = if (fine.paymentAllowed == true) "Allowed" else "NotAllowed"

                if (!activeFilters.paymentFlags.contains(eligible)) return@filter false
            }

            // --- DATE RANGE ---
            if (activeFilters.dateRange != null) {
                if (!isWithinRange(fine.offenceDate ?: "", activeFilters.dateRange))
                    return@filter false
            }

            // --- ISSUING AUTHORITY ---
            if (activeFilters.issuingAuthorities.isNotEmpty()) {

                val authority = fine.issuingAuthority?.lowercase() ?: ""

                val match = activeFilters.issuingAuthorities.any { sel ->
                    authority.contains(sel.lowercase())
                }

                if (!match) return@filter false
            }

            true
        }


        // -------------------------------
        // STEP 4 — APPLY RESULTS
        // -------------------------------
        if (currentMode == ProfileMode.INDIVIDUAL) {

            textTopFineCount.text =
                "${afterFilters.size} ${if (showUnpaid) "unpaid" else "paid"} fines found"

            adapter.update(afterFilters)
        } else {
            familyAdapter.update(
                familyMembers.toMutableList(),
                afterFilters
            )


        }
    }





    // -----------------------------------------------------------
    // TOGGLE (UNPAID / PAID)
    // -----------------------------------------------------------
    private fun setupToggleAnimation() {

        toggleContainer.post {
            slidingPill.layoutParams.width = toggleContainer.width / 2
            slidingPill.requestLayout()
        }

        btnUnpaid.setOnClickListener {
            showUnpaid = true
            animatePill(left = true)

            btnUnpaid.setTextColor(requireContext().getColor(R.color.segmentActive))
            btnPaid.setTextColor(requireContext().getColor(R.color.segmentInactive))

            updateList()
        }

        btnPaid.setOnClickListener {
            showUnpaid = false
            animatePill(left = false)

            btnPaid.setTextColor(requireContext().getColor(R.color.segmentActive))
            btnUnpaid.setTextColor(requireContext().getColor(R.color.segmentInactive))

            updateList()
        }
    }

    private fun animatePill(left: Boolean) {
        val pillWidth = slidingPill.width
        val target = if (left) 0 else pillWidth

        slidingPill.animate()
            .translationX(target.toFloat())
            .setDuration(250)
            .start()
    }


    // -----------------------------------------------------------
    // NAVIGATE TO FINE DETAILS
    // -----------------------------------------------------------
    private fun onFineClicked(fine: IForceItem) {
        val action = HomeFragmentDirections
            .actionHomeFragmentToFineDetailsFragment(fine)
        findNavController().navigate(action)
    }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun openAddFamilyDialog() {
        Toast.makeText(requireContext(), "Add Family Member dialog coming soon", Toast.LENGTH_SHORT).show()
    }




}
