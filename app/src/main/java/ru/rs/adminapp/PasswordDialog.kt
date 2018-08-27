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

        val passwordDialog = AlertDialog.Builder(context)
                .setTitle(if (isRegistration) R.string.create_password else R.string.enter_password)
                .setView(root)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create()

        passwordDialog.setOnShowListener { dialog ->
            val button = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)

            button.setOnClickListener {
                if (isRegistration) setPassword(dialog) else checkPassword(dialog)
            }
        }

        return passwordDialog
    }

    private fun setPassword(dialog: DialogInterface) {
        if (passwordView.text.length < MIN_PASSWORD_LENGTH) {
            showLongToast(context, R.string.short_password)
            return
        } else if (passwordView.text.contains(" ")) {
            showLongToast(context, R.string.password_space)
            return
        } else if (passwordView.text.toString() != confirmView.text.toString()) {
            showLongToast(context, R.string.password_confirm_mismatch)
            return
        } else {
            showLongToast(context, R.string.password_set_success)
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
            showLongToast(context, R.string.wrong_password)
        } else {
            resolvable.doChange(attemptEnable)
            dialog.dismiss()
        }
    }

    /**
     * Allows change smth protected by password
     */
    internal interface Resolvable {
        /**
         * change smth if password is right
         *
         * @param enable - action is enabling or disabling
         */
        fun doChange(enable: Boolean)
    }
}