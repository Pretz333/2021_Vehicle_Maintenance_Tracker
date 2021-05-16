package edu.cvtc.capstone.vehiclemaintenancetracker;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MaintenanceLogSettingsActivity extends AppCompatActivity {
    // This is exactly like the VehicleSettingsActivity, but for Maintenance Logs.
    // It is used to update/change a log and create new ones.
    public static final String TAG = "MaintenanceLogSettingsActivity_CLASS";

    DBHelper dbHelper = new DBHelper(MaintenanceLogSettingsActivity.this);
    MaintenanceLog log = null;
    int vehicleId;
    Toolbar toolbar;

    private EditText mTitle, mDescription, mMaintenanceDate, mCost, mTime, mMileage;
    private TextInputLayout eTitle, eDescription, eMaintenanceDate, eCost, eTime, eMileage;
    // private Spinner mSystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_log_settings);

        // Initialize the toolbar
        toolbar = findViewById(R.id.maintenanceLogSettings_toolbar);
        setSupportActionBar(toolbar);

        // Back button, better than the Manifest way for reasons... - Alexander
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setNavigationOnClickListener(v -> MaintenanceLogSettingsActivity.super.finish());

        // Get a reference to the member objects
        mTitle = findViewById(R.id.maintenanceLogSettings_editTextTitle);
        mDescription = findViewById(R.id.maintenanceLogSettings_editTextDescription);
        mMaintenanceDate = findViewById(R.id.maintenanceLogSettings_editTextMaintenanceDate);
        mCost = findViewById(R.id.maintenanceLogSettings_editTextCost);
        mTime = findViewById(R.id.maintenanceLogSettings_editTextTime);
        mMileage = findViewById(R.id.maintenanceLogSettings_editTextMileage);
        Button buttonDelete = findViewById(R.id.maintenanceLogSettings_buttonDelete);
        // mSystem = findViewById(R.id.maintenanceLogSettings_spinnerSystems);

        // Reference to the EditText containers
        eTitle = findViewById(R.id.maintenanceLogSettings_textInputTitle);
        eDescription = findViewById(R.id.maintenanceLogSettings_textInputDescription);
        eMaintenanceDate = findViewById(R.id.maintenanceLogSettings_textInputMaintenanceDate);
        eCost = findViewById(R.id.maintenanceLogSettings_textInputCost);
        eTime = findViewById(R.id.maintenanceLogSettings_textInputTime);
        eMileage = findViewById(R.id.maintenanceLogSettings_textInputMileage);

        /* We are no longer planning on implementing systems
        // Set up the systems spinner if there are systems in the db
        List<System> systems = dbHelper.getAllSystems();
        if(systems.size() > 0) {
            // These default to "gone"
            mSystem.setVisibility(View.VISIBLE);
            findViewById(R.id.maintenanceLogSettings_textViewSystemLabel).setVisibility(View.VISIBLE);
            ArrayAdapter<System> dataAdapter = new ArrayAdapter(MaintenanceLogSettingsActivity.this,
                    android.R.layout.simple_spinner_item, systems);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSystem.setAdapter(dataAdapter);
        }
         */

        // Get the logId and vehicleId from the intent
        Intent receivedIntent = getIntent();
        int logId = receivedIntent.getIntExtra(LogActivity.EXTRA_LOG_ID, -1);
        vehicleId = receivedIntent.getIntExtra(VehicleOptionActivity.EXTRA_VEHICLE_ID, -1);

        // If a valid logId was passed to this activity, we want to pre-populate the fields
        if (logId != -1) {
            // Since a logId was passed, we also want to grab the log from the db for later modification
            log = dbHelper.getLogByLogId(logId);
            populateFieldsByObject(log);

            // Since the log was already in the database, the delete button should be visible.
            buttonDelete.setVisibility(View.VISIBLE);

            // And we want to change the title to say "Edit Log" instead of "Add Log"
            TextView title = findViewById(R.id.maintenanceLogSettings_textViewTitle);
            title.setText(getResources().getString(R.string.maintenanceLogSettings_titleDisplay_edit));
        }

        // Set the save button's onClickListener
        findViewById(R.id.maintenanceLogSettings_buttonSave).setOnClickListener(
                v -> {
                    if (v.getId() == R.id.maintenanceLogSettings_buttonSave) {
                        // Check if all fields are valid
                        if (areFieldsValid()) {
                            // If we made a new log from the id passed, insert it, otherwise update it

                            // areFieldsValid() should catch this first check, so this
                            // means the log was unable to be created as it didn't
                            // have the minimum properties needed to be functional
                            if (log == null) {
                                Snackbar.make(v, "The log must have a title", Snackbar.LENGTH_SHORT).show();
                            } else if (log.getId() == -1) {
                                dbHelper.insertMaintenanceLog(log);
                                MaintenanceLogSettingsActivity.super.finish();
                            } else {
                                dbHelper.updateLog(log);
                                MaintenanceLogSettingsActivity.super.finish();
                            }
                        } else {
                            // The user made a typo/didn't fill out a field, lets alert them!
                            MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(this);
                            alert.setTitle(getResources().getString(R.string.vehicleSettingsActivity_errorValidationTitle));
                            alert.setMessage(getResources().getString(R.string.vehicleSettingsActivity_errorValidationMessage));
                            alert.setPositiveButton(getResources().getString(R.string.vehicleSettingsActivity_errorValidationButtonNeutral), null);
                            alert.show();
                        }
                    }
                }
        );

        // Set the delete button's onClickListener
        findViewById(R.id.maintenanceLogSettings_buttonDelete).setOnClickListener(
                v -> {
                    if (v.getId() == R.id.maintenanceLogSettings_buttonDelete) {
                        // Display the alert dialog to confirm the log delete.
                        AlertDialog.Builder builder = new AlertDialog.Builder(MaintenanceLogSettingsActivity.this);
                        View view = MaintenanceLogSettingsActivity.this.getLayoutInflater().inflate(R.layout.alert_dialog, null);
                        builder.setView(view);
                        AlertDialog alert = builder.create();

                        // Set the message of the alert dialog
                        ((TextView) view.findViewById(R.id.alertDialog_message)).setText(R.string.alertDialog_messageDeleteMaintenanceLog);

                        // Confirm button click handler
                        view.findViewById(R.id.alertDialog_buttonYes).setOnClickListener(v1 -> {
                            // Delete maintenance log from the database
                            dbHelper.deleteMaintenanceLog(log);

                            // Close the alert dialog box
                            alert.cancel();

                            // Display a toast that the maintenance log was deleted
                            Toast.makeText(getApplicationContext(), "The maintenance log was deleted.", Toast.LENGTH_SHORT).show();
                            MaintenanceLogSettingsActivity.super.finish();
                        });

                        // Cancel button click handler
                        view.findViewById(R.id.alertDialog_buttonNo).setOnClickListener(v12 -> {
                            // Close the alert dialog
                            alert.cancel();
                        });

                        // Display the alert dialog
                        alert.show();
                    }
                }
        );
    }

    private void populateFieldsByObject(MaintenanceLog maintenanceLog) {
        mTitle.setText(maintenanceLog.getTitle());
        mDescription.setText(maintenanceLog.getDescription());
        mCost.setText(String.format(Locale.US, "%.2f", maintenanceLog.getCost()));
        mTime.setText(String.valueOf(maintenanceLog.getTime()));
        mMileage.setText(String.valueOf(maintenanceLog.getMileage()));

        // Change the format for the date
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        mMaintenanceDate.setText(simpleDateFormat.format(maintenanceLog.getDate()));

        // Set the spinner to the id of the selected system
        // mSystem.setSelection(maintenanceLog.getSystemId());
    }

    // This checks every editText field to see if it's empty or contains invalid data.
    // If it catches any issues, it will highlight the editText container field and return false.
    private boolean areFieldsValid() {
        // Whether the values from the fields can be inserted into the database without conflict.
        boolean retVal = true;

        // If we're creating a new log, we need to create the log object
        if (log == null) {
            log = new MaintenanceLog(mTitle.getText().toString(), vehicleId);
        }

        // Convert the EditText fields to strings
        String checkTitle = mTitle.getText().toString().trim();
        String checkDescription = mDescription.getText().toString().trim();
        String checkDate = mMaintenanceDate.getText().toString().trim();

        if (checkTitle.isEmpty()) {
            retVal = false;
            eTitle.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessage));
        } else if (!VerifyUtil.isStringSafe(checkTitle)) {
            retVal = false;
            eTitle.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessageInvalidCharacters));
        } else {
            eTitle.setError(null);
            log.setTitle(checkTitle);
        }

        if (checkDescription.isEmpty()) {
            retVal = false;
            eDescription.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessage));
        } else if (!VerifyUtil.isTextSafe(checkDescription)) {
            retVal = false;
            eDescription.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessageInvalidCharacters));
        } else {
            eDescription.setError(null);
            log.setDescription(checkDescription);
        }

        Date verifyDate = VerifyUtil.parseStringToDate(checkDate);
        if (checkDate.isEmpty()) {
            log.setDate(null); // We set it to the current date in the DBHelper if it is null
        } else if (verifyDate == null) {
            retVal = false;
            eMaintenanceDate.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessageInvalidDate));
        } else {
            eMaintenanceDate.setError(null);
            log.setDate(verifyDate);
        }

        if (!mCost.getText().toString().isEmpty()) {
            try {
                log.setCost(Double.parseDouble(mCost.getText().toString()));
                eCost.setError(null);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                log.setCost(0.00);
                eCost.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessageDifferentValue));
                retVal = false;
            }
        }

        try {
            int totalTime = Integer.parseInt(mTime.getText().toString());
            log.setTime(totalTime);
            eTime.setError(null);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            log.setTime(0);
            eTime.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessageDifferentValue));
            retVal = false;
        }

        try {
            int mileageAtFix = Integer.parseInt(mMileage.getText().toString());
            log.setMileage(mileageAtFix);
            eMileage.setError(null);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            log.setMileage(0);
            eMileage.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessageDifferentValue));
            retVal = false;
        }

        return retVal;
    }
}