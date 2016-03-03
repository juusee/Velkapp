package com.example.velkapp.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.velkapp.R;

/**
 * Created by juusee on 02/03/16.
 */
public class Helper {

    private Context context;

    public Helper(Context context) {
        this.context = context;
    }

    public String makeAccountReadable(String account) {
        account = account.toUpperCase().trim().replaceAll(" +","");
        StringBuilder newAccount = new StringBuilder("");
        for (int i = 1; i <= account.length(); ++i) {
            newAccount.append(account.charAt(i-1));
            if (i % 4 == 0)
                newAccount.append(' ');
        }
        return newAccount.toString();
    }

    public void showKeyboard(EditText nameInput) {
        nameInput.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(nameInput, InputMethodManager.SHOW_IMPLICIT);
    }

    public AlertDialog getAlertDialog(View view, String title, String positiveButton) {
        return new AlertDialog.Builder(context)
                .setView(view)
                .setTitle(title)
                .setPositiveButton(positiveButton, null)
                .setNegativeButton(R.string.button_cancel, null)
                .create();
    }
}
