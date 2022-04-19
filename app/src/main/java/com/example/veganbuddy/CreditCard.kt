package com.example.veganbuddy

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import org.bson.types.ObjectId
import java.util.*

open class CreditCard(
    var cardNumber: Long? = null,
    var expiryDate: String? = null
):RealmObject() {
}