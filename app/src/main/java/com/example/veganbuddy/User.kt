package com.example.veganbuddy

import androidx.annotation.DrawableRes
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import org.bson.types.ObjectId

open class User(
    var firstName: String = "",
    var lastName: String = "",
    var profileImage: Int? = null
): RealmObject()