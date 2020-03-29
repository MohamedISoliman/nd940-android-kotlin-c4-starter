package com.udacity.project4.locationreminders.reminderslist

import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.util.*

/**
 * data class acts as a data mapper between the DB and the UI
 */
@Keep
data class ReminderDataItem(
    var title: String?,
    var description: String?,
    var location: String?,
    var latitude: Double?,
    var longitude: Double?,
    val id: String = UUID.randomUUID().toString()
) : Serializable