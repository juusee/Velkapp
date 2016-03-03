package com.example.velkapp.validator;

import android.content.Context;
import android.widget.EditText;

import com.example.velkapp.R;
import com.example.velkapp.db.AccountDBHelper;
import com.example.velkapp.helper.Helper;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by juusee on 02/03/16.
 */
public class Validator {
    private static final int IBANNUMBER_MIN_SIZE = 15;
    private static final int IBANNUMBER_MAX_SIZE = 34;
    private static final BigInteger IBANNUMBER_MAGIC_NUMBER = new BigInteger("97");

    private static final String NAME_REGEX = "^[\\p{L} .'-]+$";
    private Pattern pattern;
    private Matcher matcher;

    private Context context;

    private AccountDBHelper dbHelper;

    public Validator(Context context) {
        // Save context so we can use string resources
        this.context = context;
        dbHelper = AccountDBHelper.getInstance(context);
    }

    public boolean inputValid(EditText nameInput, EditText accountInput, String name, String account,
                              boolean oldAccountSameAsNew) {

        // Validate input and show error message if input isn't valid
        if (!nameValid(name)) {
            nameInput.requestFocus();
            nameInput.setError(context.getString(R.string.error_name));
        } else if (!ibanValid(account)) {
            accountInput.requestFocus();
            accountInput.setError(context.getString(R.string.error_iban));
        } else if (!oldAccountSameAsNew && dbHelper.accountInDB(account)) {
            accountInput.requestFocus();
            accountInput.setError(context.getString(R.string.error_duplicate));
        } else {
            return true;
        }
        return false;
    }

    private boolean ibanValid(String accountNumber) {
        String newAccountNumber = accountNumber.replaceAll("\\s","");

        // Check that the total IBAN length is correct as per the country. If not, the IBAN is invalid. We could also check
        // for specific length according to country, but for now we won't
        if (newAccountNumber.length() < IBANNUMBER_MIN_SIZE || newAccountNumber.length() > IBANNUMBER_MAX_SIZE) {
            return false;
        }

        // Move the four initial characters to the end of the string.
        newAccountNumber = newAccountNumber.substring(4) + newAccountNumber.substring(0, 4);

        // Replace each letter in the string with two digits, thereby expanding the string, where A = 10, B = 11, ..., Z = 35.
        StringBuilder numericAccountNumber = new StringBuilder();
        int numericValue;
        for (int i = 0; i < newAccountNumber.length(); i++) {
            numericValue = Character.getNumericValue(newAccountNumber.charAt(i));
            if (-1 >= numericValue) {
                return false;
            } else {
                numericAccountNumber.append(numericValue);
            }
        }
        // Interpret the string as a decimal integer and compute the remainder of that number on division by 97.
        BigInteger ibanNumber = new BigInteger(numericAccountNumber.toString());
        return ibanNumber.mod(IBANNUMBER_MAGIC_NUMBER).intValue() == 1;
    }

    /*
     * Validated name so it can contain letters, spaces, dots, apostrophies and hyphens
     */
    private boolean nameValid(String name) {
        pattern = Pattern.compile(NAME_REGEX);
        matcher = pattern.matcher(name);
        return matcher.matches();
    }
}