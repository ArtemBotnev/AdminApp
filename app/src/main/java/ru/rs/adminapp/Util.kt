package ru.rs.adminapp

import android.content.Context
import android.widget.Toast

/**
 * Utils file
 *
 * Created by ArtemBotnev on 08/24/2018
 */
const val PASSWORD = "password_shed"
const val PASSWORD_DEFAULT_VAL = ""

fun showShortToast(context: Context, sourceId: Int) =
        Toast.makeText(context, sourceId, Toast.LENGTH_SHORT).show()