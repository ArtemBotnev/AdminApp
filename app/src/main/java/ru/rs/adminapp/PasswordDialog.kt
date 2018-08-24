package ru.rs.adminapp

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText

/**
 * For creating password alert dialog
 *
 * Created by Artem Botnev on 08/24/2018
 */
class PasswordDialog(private val context: Context, private val attemptEnable: Boolean) {
    companion object {
        private const val MIN_PASSWORD_LENGTH = 5
    }

    private val resolvable = context as Resolvable

    private lateinit var root: View
    private lateinit var passwordView: EditText
    private lateinit var confirmView: EditText

    fun showPasswordDialog(isRegistration: Boolean): Dialog {
        // get root view
        root = LayoutInflater.from(context).inflate(R.layout.password_dialog, null)
        passwordView = root.findViewById(R.id.password)
        confirmView = root.findViewById(R.id.confirm_password)

        if (!isRegistration) confirmView.visibility = View.GONE

        return AlertDialog.Builder(context)
                .setTitle(if (isRegistration) R.string.create_password else R.string.enter_password)
                .setView(root)
                .setPositiveButton(android.R.string.ok)
                { dialog, which ->  if (isRegistration) setPassword(dialog) else checkPassword(dialog) }
                .setNegativeButton(android.R.string.cancel)
                { dialog, which ->  dialog.dismiss() }
                .create()
    }

    private fun setPassword(dialog: DialogInterface) {
        if (passwordView.text.length < MIN_PASSWORD_LENGTH) {
            showShortToast(context, R.string.short_password)
            return
        } else if (passwordView.text.contains(" ")) {
            showShortToast(context, R.string.password_space)
            return
        } else if (passwordView.text != confirmView.text) {
            showShortToast(context, R.string.password_confirm_mismatch)
            return
        } else {
            PreferenceManager.getDefaultSharedPreferences(context)
                    .edit()
                    .putString(PASSWORD, passwordView.text.toString())
                    .apply()

            resolvable.doChange(attemptEnable)
            dialog.dismiss()
        }
    }

    private fun checkPassword(dialog: DialogInterface) {
        val password = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PASSWORD, PASSWORD_DEFAULT_VAL)

        if (password == PASSWORD_DEFAULT_VAL || passwordView.text.toString() != password) {
            showShortToast(context, R.string.password_confirm_mismatch)
        } else {
            resolvable.doChange(attemptEnable)
            dialog.dismiss()
        }
    }

    /**
     * Allows change smth protected by password
     */
    internal interface Resolvable {
        fun doChange(enable: Boolean)
    }
}