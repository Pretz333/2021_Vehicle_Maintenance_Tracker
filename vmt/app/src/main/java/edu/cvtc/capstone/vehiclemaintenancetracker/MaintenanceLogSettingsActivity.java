package edu.cvtc.capstone.vehiclemaintenancetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MaintenanceLogSettingsActivity extends AppCompatActivity {
    public static final String TAG = "MaintenanceLogSettingsActivity_CLASS";

    //Class variables
    DBHelper dbHelper = new DBHelper(MaintenanceLogSettingsActivity.this);
    MaintenanceLog log = null;
    int vehicleId;
    Toolbar toolbar;

    private EditText mTitle, mDescription, mMaintenanceDate, mCost, mTime, mMileage;
    private TextInputLayout eTitle, eDescription, eMaintenanceDate, eCost, eTime, eMileage;
    private Spinner mSystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_log_settings);

        toolbar = findViewById(R.id.maintenanceLogSettings_toolbar);

        // Set support for the toolbar
        setSupportActionBar(toolbar);

        // Back button, better than the Manifest way for reasons... - Alexander
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setNavigationOnClickListener(v -> MaintenanceLogSettingsActivity.super.finish());

        //Set the save button's onClickListener
        findViewById(R.id.maintenanceLogSettings_buttonSave).setOnClickListener(
                v -> {
                    if (v.getId() == R.id.maintenanceLogSettings_buttonSave) {


                        if (hasMinimumRequirements()) {

                            //updateLogWithValues();
                            //If we found a vehicle from the id passed, update it. If not, make one
                            if (log == null) {
                                //It couldn't be made as it didn't have the minimum properties needed to be functional
                                Snackbar.make(v, "The log must have a title", Snackbar.LENGTH_SHORT).show();
                            } else if (log.getId() == -1) {
                                dbHelper.insertMaintenanceLog(log);
                                MaintenanceLogSettingsActivity.super.finish();
                            } else {
                                dbHelper.updateLog(log);
                                MaintenanceLogSettingsActivity.super.finish();
                            }


                        } else {
                            // Something went wrong, lets alert the user!
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

                        // Create the LayoutInflater to use the custom layout.
                        LayoutInflater inflater = getLayoutInflater();
                        View view = MaintenanceLogSettingsActivity.this.getLayoutInflater().inflate(R.layout.alert_dialog, null);

                        // Get a reference to the buttons in the alert dialog
                        Button yesButton = view.findViewById(R.id.alertDialog_buttonYes);
                        Button noButton = view.findViewById(R.id.alertDialog_buttonNo);

                        builder.setView(view);
                        AlertDialog alert = builder.create();

                        // Set the text for the message in the alert dialog
                        TextView alertDialogText = view.findViewById(R.id.alertDialog_message);
                        alertDialogText.setText(R.string.alertDialog_messageDeleteMaintenanceLog);

                        // The yes button was clicked.
                        yesButton.setOnClickListener(v1 -> {
                            // Delete maintenance log from the database
                            dbHelper.deleteMaintenanceLog(log);

                            // Close the alert dialog box
                            alert.cancel();

                            // Display a toast that the maintenance log was deleted
                            Toast.makeText(getApplicationContext(), "The maintenance log was deleted.", Toast.LENGTH_SHORT).show();
                            MaintenanceLogSettingsActivity.super.finish();
                        });

                        // The no button was clicked.
                        noButton.setOnClickListener(v12 -> {
                            // Close the alert dialog
                            alert.cancel();
                        });

                        // Display the alert dialog
                        alert.show();
                    }
                }
        );

        // Get a reference to the member objects
        mTitle = findViewById(R.id.maintenanceLogSettings_editTextTitle);
        mDescription = findViewById(R.id.maintenanceLogSettings_editTextDescription);
        mMaintenanceDate = findViewById(R.id.maintenanceLogSettings_editTextMaintenanceDate);
        mCost = findViewById(R.id.maintenanceLogSettings_editTextCost);
        mTime = findViewById(R.id.maintenanceLogSettings_editTextTime);
        mMileage = findViewById(R.id.maintenanceLogSettings_editTextMileage);
        mSystem = findViewById(R.id.maintenanceLogSettings_spinnerSystems);
        Button buttonDelete = findViewById(R.id.maintenanceLogSettings_buttonDelete);

        // Reference to the EditText containers (We use these to highlight
        // them red if there is an error in parsing in any way
        eTitle = findViewById(R.id.maintenanceLogSettings_textInputTitle);
        eDescription = findViewById(R.id.maintenanceLogSettings_textInputDescription);
        eMaintenanceDate = findViewById(R.id.maintenanceLogSettings_textInputMaintenanceDate);
        eCost = findViewById(R.id.maintenanceLogSettings_textInputCost);
        eTime = findViewById(R.id.maintenanceLogSettings_textInputTime);
        eMileage = findViewById(R.id.maintenanceLogSettings_textInputMileage);

        // Set up the systems spinner if there are systems in the db
        List<System> systems = dbHelper.getAllSystems();
        if(systems.size() > 0) {
            //These default to "gone"
            mSystem.setVisibility(View.VISIBLE);
            findViewById(R.id.maintenanceLogSettings_textViewSystemLabel).setVisibility(View.VISIBLE);
            //TODO: Fix this
            ArrayAdapter<System> dataAdapter = new ArrayAdapter(MaintenanceLogSettingsActivity.this,
                    android.R.layout.simple_spinner_item, systems);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSystem.setAdapter(dataAdapter);
        }

        // Get the logId from the intent
        Intent receivedIntent = getIntent();
        int logId = receivedIntent.getIntExtra(LogActivity.EXTRA_LOG_ID, -1);
        vehicleId = receivedIntent.getIntExtra(VehicleOptionActivity.EXTRA_VEHICLE_ID, -1);

        // If a valid logId was passed to this activity, we want to pre-populate the fields
        if (logId != -1) {
            //Since a logId was passed, we also want to grab the log from the db for later modification
            log = dbHelper.getLogByLogId(logId);
            populateFieldsByObject(log);

            // Since the log was already in the database, the delete button should be visible.
            buttonDelete.setVisibility(View.VISIBLE);

            //And we want to change the title to say "Edit Log" instead of "Add Log"
            TextView title = findViewById(R.id.maintenanceLogSettings_textViewTitle);
            title.setText(getResources().getString(R.string.maintenanceLogSettings_titleDisplay_edit));
        }
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
        mSystem.setSelection(maintenanceLog.getSystemId());
    }

    // This checks every editText field if its empty, or contains invalid data.
    //
    // If it catches any issues, it will highlight the editText container field and return false.
    private boolean hasMinimumRequirements() {
        // Whether the values from the fields can be inserted into the database without conflict.
        boolean retVal = true;


        if (log == null){
            log = new MaintenanceLog(mTitle.getText().toString(), vehicleId);
        } else if (!mTitle.getText().toString().equals("")) {
            //since the other option adds the title to new logs, we'll set the title for edited logs
            log.setTitle(mTitle.getText().toString());
        }

        // Convert the EditText fields to strings
        String checkTitle = mTitle.getText().toString();
        String checkDescription = mDescription.getText().toString();
        String checkDate = mMaintenanceDate.getText().toString();


        // Check if each field is not blank, null, or contains invalid data.
        // If it does, retVal will be false.

        // Check if the title's empty
        if (isStringEmpty(checkTitle)) {
            retVal = false;
            eTitle.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessage));
        } else if (!VerifyUtil.isStringSafe(checkTitle)) {
            retVal = false;
            eTitle.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessageInvalidCharacters));
        } else {
            eTitle.setError(null);
            // Set this attribute to the vehicle object
            log.setTitle(checkTitle);
        }

        // Again...
        if (isStringEmpty(checkDescription)) {
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
        // And again...
        if (isStringEmpty(checkDate)) {
            retVal = false;
            eMaintenanceDate.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessage));
        } else if (verifyDate == null) { //TODO: This isn't verified in A's version
            retVal = false;
            eMaintenanceDate.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessageInvalidDate));
        } else {
            eMaintenanceDate.setError(null);
            log.setDate(verifyDate);
        }

        // Simple checks for non-required fields
        // which also sets the log object too
        try {
            double maintenanceCost = Double.parseDouble(mCost.getText().toString());
            log.setCost(maintenanceCost);
            eCost.setError(null);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            log.setCost(0.00);
            eCost.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessageDifferentValue));
            retVal = false;
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

    // Simplified function to check if a string is empty
    private boolean isStringEmpty(String string) {
        return (string.isEmpty() || string.equals("") || string.equals(" "));
    }
}