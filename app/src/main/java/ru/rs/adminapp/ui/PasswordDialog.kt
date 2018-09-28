package ru.rs.adminapp.ui

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText

import ru.rs.adminapp.*
import ru.rs.adminapp.utils.PASSWORD
import ru.rs.adminapp.utils.PASSWORD_DEFAULT_VAL
import ru.rs.adminapp.utils.get256Sha
import ru.rs.adminapp.utils.showLongToast

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
                    .putString(PASSWORD, get256Sha(passwordView.text.toString()))
                    .apply()

            resolvable.doChange(attemptEnable)
            dialog.dismiss()
        }
    }

    private fun checkPassword(dialog: DialogInterface) {
        val password = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PASSWORD, PASSWORD_DEFAULT_VAL)
        val inputPasswordSha = get256Sha(passwordView.text.toString())

        if (passwordView.text.toString() == PASSWORD_DEFAULT_VAL || inputPasswordSha != password) {
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