package edu.cvtc.capstone.vehiclemaintenancetracker;

import androidx.appcompat.app.AppCompatActivity;

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

import com.google.android.material.snackbar.Snackbar;

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

    private EditText mTitle, mDescription, mMaintenanceDate, mCost, mTime, mMileage;
    private Spinner mSystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_log_settings);

        //Set the save button's onClickListener
        findViewById(R.id.maintenanceLogSettings_buttonSave).setOnClickListener(
                v -> {
                    if (v.getId() == R.id.maintenanceLogSettings_buttonSave) {
                        updateLogWithValues();
                        //If we found a vehicle from the id passed, update it. If not, make one
                        if (log == null || log.getId() == -1) {
                            dbHelper.insertMaintenanceLog(log);
                            Snackbar.make(v, "Successfully added the log!", Snackbar.LENGTH_SHORT).show();
                        } else {
                            dbHelper.updateLog(log);
                            Snackbar.make(v, "Successfully updated the log!", Snackbar.LENGTH_SHORT).show();
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

        // Set up the systems spinner if there are systems in the db
        List<System> systems = dbHelper.getAllSystems();
        if(systems.size() > 0) {
            mSystem.setVisibility(View.VISIBLE); //It defaults to invisible
            findViewById(R.id.maintenanceLogSettings_textViewSystemLabel).setVisibility(View.VISIBLE); //It defaults to invisible
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
        }
    }

    private void populateFieldsByObject(MaintenanceLog maintenanceLog) {
        mTitle.setText(maintenanceLog.getTitle());
        mDescription.setText(maintenanceLog.getDescription());
        mCost.setText(String.valueOf(maintenanceLog.getCost()));
        mTime.setText(String.valueOf(maintenanceLog.getTime()));
        mMileage.setText(String.valueOf(maintenanceLog.getMileage()));

        // Change the format for the date
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        mMaintenanceDate.setText(simpleDateFormat.format(maintenanceLog.getDate()));

        // Set the spinner to the id of the selected system
        mSystem.setSelection(maintenanceLog.getSystemId());
    }

    private void updateLogWithValues() {
        //Ensure they have the minimum values
        if((vehicleId != -1 || log.getVehicleId() != -1) && !mTitle.getText().toString().equals("")){

            //Make a new log if we're not editing one
            if(log == null){
                log = new MaintenanceLog(mTitle.getText().toString(), vehicleId);
            } else if (!mTitle.getText().toString().equals("")) {
                //since the other option adds the title to new logs, we'll set the title for edited logs
                log.setTitle(mTitle.getText().toString());
            }

            if(!mDescription.getText().toString().equals("")){
                log.setDescription(mDescription.getText().toString());
            }

            //TODO
            if(!mMaintenanceDate.getText().toString().equals("")){
                //date will be null if it fails any of the validity checks (there are several)
                Date date = VerifyUtil.parseStringToDate(mMaintenanceDate.getText().toString());
                if(date != null) {
                    log.setDate(date);
                }
                Log.d(TAG, "DATE: " + mMaintenanceDate.getText().toString());
            }

            if(!mCost.getText().toString().equals("")){
                log.setCost(Double.parseDouble(mCost.getText().toString()));
            }

            //TODO
            if(!mTime.getText().toString().equals("")){
                //log.setTime(new Time(mTime.getText().toString()));
                Log.d(TAG, "TIME: " + mTime.getText().toString());
            }

            if(!mMileage.getText().toString().equals("")){
                log.setMileage(Integer.parseInt(mMileage.getText().toString()));
            }

            //TODO: Test this
            //system spinner selection
            if(mSystem.getSelectedItem() != null){
                log.setSystemId(((System) mSystem.getSelectedItem()).getId());
            }
        }
    }
}