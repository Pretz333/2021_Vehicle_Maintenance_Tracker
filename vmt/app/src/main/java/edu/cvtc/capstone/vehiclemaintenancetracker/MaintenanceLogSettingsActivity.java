package edu.cvtc.capstone.vehiclemaintenancetracker;

import androidx.appcompat.app.AppCompatActivity;

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

    private int mMaintenanceLogId;
    private String mSelectedSystem;

    private EditText mTitle;
    private EditText mDescription;
    private EditText mMaintenanceDate;
    private EditText mCost;
    private EditText mTime;
    private EditText mMileage;
    private Spinner mSystem;
    private DBHelper mDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_log_settings);

        mDbOpenHelper = new DBHelper(MaintenanceLogSettingsActivity.this);

        // Get a reference to the member objects
        mTitle = findViewById(R.id.maintenanceLogSettings_editTextTitle);
        mDescription = findViewById(R.id.maintenanceLogSettings_editTextDescription);
        mMaintenanceDate = findViewById(R.id.maintenanceLogSettings_editTextMaintenanceDate);
        mCost = findViewById(R.id.maintenanceLogSettings_editTextCost);
        mTime = findViewById(R.id.maintenanceLogSettings_editTextTime);
        mMileage = findViewById(R.id.maintenanceLogSettings_editTextMileage);
        mSystem = findViewById(R.id.maintenanceLogSettings_spinnerSystems);

        // Set the listener for the selected item if it is not empty
        if (mSystem != null) {
            mSystem.setOnItemSelectedListener(this);
        }

        // Use the system table data to populate the spinner
        populateSystemSpinner();

        // TODO: Get the intent from the LogActivity recycler view and populate the fields

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

    @Override
    protected void onDestroy() {
        mDbOpenHelper.close();
        super.onDestroy();
    }

    private void populateSystemSpinner() {
        List<System> systems = mDbOpenHelper.getAllSystems();

        ArrayAdapter<System> dataAdapter = new ArrayAdapter(MaintenanceLogSettingsActivity.this,
                android.R.layout.simple_spinner_item, systems);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSystem.setAdapter(dataAdapter);
    }

    private void populateFieldsWithMaintenanceLogInfo (MaintenanceLog maintenanceLog) {
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