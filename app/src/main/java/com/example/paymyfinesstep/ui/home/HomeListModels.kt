package com.example.paymyfinesstep.ui.home

import com.example.paymyfinesstep.api.IForceItem

data class UserProfile(
    val key: String,            // unique key (self id or family id)
    val displayName: String,
    val idNumber: String?,
    val fines: List<IForceItem>
)

sealed class HomeListItem {
    data class UserHeader(val user: UserProfile, val expanded: Boolean) : HomeListItem()
    data class UserSummary(
        val user: UserProfile,
        val unpaidCount: Int,
        val allSelected: Boolean
    ) : HomeListItem()
    data class FineRow(
        val userKey: String,
        val fine: IForceItem
    ) : HomeListItem()
}

object FineSelectionManager {
    // we'll use noticeNumber as unique key
    private val selectedNoticeNumbers = mutableSetOf<String>()

    fun isSelected(noticeNumber: String?): Boolean {
        if (noticeNumber.isNullOrEmpty()) return false
        return selectedNoticeNumbers.contains(noticeNumber)
    }

    fun toggleSelected(noticeNumber: String?) {
        if (noticeNumber.isNullOrEmpty()) return
        if (!selectedNoticeNumbers.add(noticeNumber)) {
            selectedNoticeNumbers.remove(noticeNumber)
        }
    }

    fun setSelectedForUser(
        userKey: String,
        fines: List<IForceItem>,
        selected: Boolean
    ) {
        val notices = fines.mapNotNull { it.noticeNumber }
        if (selected) {
            selectedNoticeNumbers.addAll(notices)
        } else {
            selectedNoticeNumbers.removeAll(notices.toSet())
        }
    }

    fun clearAll() {
        selectedNoticeNumbers.clear()
    }

    fun getSelectedNoticeNumbers(): Set<String> = selectedNoticeNumbers.toSet()
}