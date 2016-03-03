package com.example.velkapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.velkapp.validator.Validator;
import com.example.velkapp.db.AccountContract;
import com.example.velkapp.db.AccountDBHelper;
import com.example.velkapp.helper.Helper;

public class MainActivity extends AppCompatActivity {

    private AccountDBHelper dbHelper;
    private Helper helper;
    private Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = AccountDBHelper.getInstance(MainActivity.this);
        helper = new Helper(MainActivity.this);
        validator = new Validator(MainActivity.this);
        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    private void updateUI() {
        Cursor cursor = dbHelper.list();
        ListAdapter listAdapter = new SimpleCursorAdapter(
                this,
                R.layout.view_account,
                cursor,
                new String[] { AccountContract.Columns.NAME,
                        AccountContract.Columns.ACCOUNT},
                new int[] { R.id.nameTextView, R.id.accountTextView },
                0
        );
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(listAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // If plus button is pressed on menu bar
            case R.id.action_add_account:
                LayoutInflater inflater = this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.dialog_account, null);
                // Create alertDialog from dialog_account.xml and add title to it
                final AlertDialog addDialog = helper.getAlertDialog(dialogView,
                        getString(R.string.account_add), getString(R.string.button_add));
                // Use showListener so dialog doesn't go away when positiveButton is pressed
                // and input can be validated
                addDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        final EditText nameInput = (EditText) dialogView.findViewById(R.id.name);
                        final EditText accountInput = (EditText) dialogView.findViewById(R.id.account);
                        // Show keyboard when showing alertDialog
                        helper.showKeyboard(nameInput);
                        Button b = addDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            // When positive (ADD) button is pressed
                            @Override
                            public void onClick(View view) {
                                // Get user input
                                String name = nameInput.getText().toString().trim();
                                String account = helper.makeAccountReadable(accountInput.getText().toString());
                                // Validate input
                                if (validator.inputValid(nameInput, accountInput, name, account, false)) {
                                    // Let dialog go away
                                    addDialog.dismiss();
                                    // Add values to database
                                    dbHelper.insert(name, account);
                                    updateUI();
                                }
                            }
                        });
                    }
                });
                addDialog.show();
                return true;
            default:
                return false;
        }
    }

    public void onDeleteButtonClick(final View view) {
        // Build dialog to get user acceptation for delete
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
            .setTitle(getString(R.string.account_delete))
            .setPositiveButton(R.string.button_delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    View v = (View) view.getParent();
                    TextView accountView = (TextView) v.findViewById(R.id.accountTextView);
                    String account = accountView.getText().toString();

                    // Delete account from database
                    dbHelper.delete(account);

                    updateUI();
                }
            })
            .setNegativeButton(getString(R.string.button_cancel), null);
        builder.create().show();
    }

    public void onEditButtonClick(View view) {
        // Get name and account to modify
        View v = (View)view.getParent();
        TextView nameView = (TextView)v.findViewById(R.id.nameTextView);
        TextView accountView = (TextView)v.findViewById(R.id.accountTextView);
        final String name = nameView.getText().toString();
        final String account = accountView.getText().toString();
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_account, null);
        // Set name and account values to inputs so user doesn't have to type
        // them
        final EditText nameInput = (EditText) dialogView.findViewById(R.id.name);
        nameInput.setText(name);
        final EditText accountInput = (EditText) dialogView.findViewById(R.id.account);
        accountInput.setText(account);
        // Create alertDialog from dialog_account.xml and add title to it
        final AlertDialog editDialog = helper.getAlertDialog(dialogView,
                getString(R.string.account_edit), getString(R.string.button_apply));
        // Use showListener so dialog doesn't go away when positiveButton is pressed
        // and input can be validated
        editDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {
                // Show keyboard when showing alertDialog
                helper.showKeyboard(nameInput);
                Button b = editDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String newName = nameInput.getText().toString().trim();
                        String newAccount = helper.makeAccountReadable(accountInput.getText().toString());
                        // If name or account has changed
                        if (!name.equals(newName) || !account.equals(newAccount)) {
                            // Validate input
                            if (validator.inputValid(nameInput, accountInput, newName, newAccount, account.equals(newAccount))) {
                                // Let dialog go away
                                editDialog.dismiss();
                                // Update account to database
                                dbHelper.update(account, newName, newAccount);
                                updateUI();
                            }
                        } else {
                            editDialog.dismiss();
                        }
                    }
                });
            }
        });
        editDialog.show();
    }
}
