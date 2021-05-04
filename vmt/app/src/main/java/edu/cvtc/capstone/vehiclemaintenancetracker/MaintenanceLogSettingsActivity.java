package edu.cvtc.capstone.vehiclemaintenancetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MaintenanceLogSettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static final String TAG = "MaintenanceLogSettingsActivity_CLASS";

    //Class variables
    DBHelper dbHelper = new DBHelper(MaintenanceLogSettingsActivity.this);
    MaintenanceLog log = null;

    private String mSelectedSystem;

    private EditText mTitle, mDescription, mMaintenanceDate, mCost, mTime, mMileage;
    private Spinner mSystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_log_settings);

        // Get a reference to the member objects
        mTitle = findViewById(R.id.maintenanceLogSettings_editTextTitle);
        mDescription = findViewById(R.id.maintenanceLogSettings_editTextDescription);
        mMaintenanceDate = findViewById(R.id.maintenanceLogSettings_editTextMaintenanceDate);
        mCost = findViewById(R.id.maintenanceLogSettings_editTextCost);
        mTime = findViewById(R.id.maintenanceLogSettings_editTextTime);
        mMileage = findViewById(R.id.maintenanceLogSettings_editTextMileage);
        mSystem = findViewById(R.id.maintenanceLogSettings_spinnerSystems);

        // Set up the systems spinner if there are systems in the db
        List<System> systems = dbHelper.getAllSystems();
        if(systems.size() > 0) {
            mSystem.setVisibility(View.VISIBLE); //It defaults to invisible
            mSystem.setOnItemSelectedListener(this);
            //TODO: Fix this
            ArrayAdapter<System> dataAdapter = new ArrayAdapter(MaintenanceLogSettingsActivity.this,
                    android.R.layout.simple_spinner_item, systems);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSystem.setAdapter(dataAdapter);
        }

        // Get the logId from the intent
        Intent receivedIntent = getIntent();
        int logID = receivedIntent.getIntExtra(LogActivity.EXTRA_LOG_ID, -1);

        // If a valid logID was passed to this activity, we want to pre-populate the fields
        if (logID != -1) {
            //Since a logID was passed, we also want to grab the log from the db for later modification
            log = dbHelper.getLogByLogId(logID);
            populateFieldsByObject(log);
        }

        // Create a reference to the save button
        Button saveButton = findViewById(R.id.maintenanceLogSettings_buttonSave);
        saveButton.setOnClickListener(v -> {
            //SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
            //db.close();

            // TODO: Validate maintenance log and update the record in the database
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSelectedSystem = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
}