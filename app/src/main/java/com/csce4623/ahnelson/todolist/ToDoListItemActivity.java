package com.csce4623.ahnelson.todolist;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;

import static com.csce4623.ahnelson.todolist.HomeActivity.EXTRA_MESSAGE_ID;

public class ToDoListItemActivity extends AppCompatActivity implements View.OnClickListener, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    EditText EditTextContent;
    EditText EditTextTitle;
    CheckBox CompletedCheckBox;
    TextView timeText;
    TextView dateText;
    Button AlarmButton;

    private String id = "";
    private int idInt;
    private int hourOfDayAlarm;
    private int minuteAlarm;
    private int yearAlarm;
    private int monthAlarm;
    private int dayAlarm;
    private boolean isAlarmSet;

    public static final String EXTRA_MESSAGE_TITLE = "com.csce4623.ahnelson.todolist.TITLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_activity);
        initializeComponents();
        Intent intent = getIntent();

        //Get the ID from Home activity
        id = intent.getStringExtra(EXTRA_MESSAGE_ID);
        idInt = Integer.parseInt(id);

        String[] projection = {
                ToDoProvider.TODO_TABLE_COL_ID,
                ToDoProvider.TODO_TABLE_COL_TITLE,
                ToDoProvider.TODO_TABLE_COL_CONTENT,
                ToDoProvider.TODO_TABLE_COL_TASKDONE,
                ToDoProvider.TODO_TABLE_COL_ALARMDATE,
                ToDoProvider.TODO_TABLE_COL_ALARMTIME,
                ToDoProvider.TODO_TABLE_COL_ISALARMSET };

        //Point the cursor to the row where _ID = id
        Cursor myCursor = getContentResolver().query(ToDoProvider.CONTENT_URI,projection,"_ID=" + id,null,null);

        //Move the cursor to each column and save the value to a local variable
        int titleIndex = myCursor.getColumnIndex("TITLE");
        Log.d("booty", String.valueOf(titleIndex));
        myCursor.moveToFirst();
        String title = myCursor.getString(titleIndex);

        int contentIndex = myCursor.getColumnIndex("CONTENT");
        myCursor.moveToFirst();
        String content = myCursor.getString(contentIndex);

        int taskdoneIndex = myCursor.getColumnIndex("TASKDONE");
        myCursor.moveToFirst();
        String taskDone = myCursor.getString(taskdoneIndex);

        int alarmDateIndex = myCursor.getColumnIndex("ALARMDATE");
        myCursor.moveToFirst();
        String alarmDate = myCursor.getString(alarmDateIndex);

        int alarmTimeIndex = myCursor.getColumnIndex("ALARMTIME");
        myCursor.moveToFirst();
        String alarmTime = myCursor.getString(alarmTimeIndex);

        int isAlarmSetIndex = myCursor.getColumnIndex("ISALARMSET");
        myCursor.moveToFirst();
        String isAlarmSetString = myCursor.getString(isAlarmSetIndex);

        if (isAlarmSetString.equals("false")) {
            isAlarmSet = false;
        }
        else {
            isAlarmSet = true;
        }

        myCursor.close();

        //Sets the task completed checkbox in the view
        CompletedCheckBox = (CheckBox) findViewById(R.id.completedBox);
        if (taskDone.equals("false")) {
            CompletedCheckBox.setChecked(false);
        }
        else {
            CompletedCheckBox.setChecked(true);
        }

        //Set the title in the view
        EditTextTitle = (EditText) findViewById(R.id.tvNoteTitle);
        EditTextTitle.setInputType(InputType.TYPE_CLASS_TEXT);
        EditTextTitle.setText(title);

        //Set the content in the view
        EditTextContent = (EditText) findViewById(R.id.etNoteContent);
        EditTextContent.setInputType(InputType.TYPE_CLASS_TEXT);
        EditTextContent.setText(content);

        //Set the time in the view
        timeText = (TextView) findViewById(R.id.etTimePicker);
        timeText.setText(alarmTime);

        //Set the date in the view
        dateText = (TextView) findViewById(R.id.etDatePicker);
        dateText.setText(alarmDate);
    }

    //Set the OnClick Listener for buttons
    void initializeComponents(){
        findViewById(R.id.btnSave).setOnClickListener(this);
        findViewById(R.id.btnDelete).setOnClickListener(this);
        findViewById(R.id.etTimePicker).setOnClickListener(this);
        findViewById(R.id.etDatePicker).setOnClickListener(this);
        findViewById(R.id.btnAlarm).setOnClickListener(this);
        findViewById(R.id.completedBox).setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()) {
            case R.id.btnSave:
                //Save button writes all the local variables to the content provider
                ContentValues myCV = new ContentValues();
                myCV.put(ToDoProvider.TODO_TABLE_COL_CONTENT, EditTextContent.getText().toString());
                myCV.put(ToDoProvider.TODO_TABLE_COL_TITLE, EditTextTitle.getText().toString());
                myCV.put(ToDoProvider.TODO_TABLE_COL_ALARMDATE, dateText.getText().toString());
                myCV.put(ToDoProvider.TODO_TABLE_COL_ALARMTIME, timeText.getText().toString());
                String tempChecked;
                if (CompletedCheckBox.isChecked()) {
                    tempChecked = "true";
                }
                else {
                    tempChecked = "false";
                }
                myCV.put(ToDoProvider.TODO_TABLE_COL_TASKDONE, tempChecked);
                String tempAlarm;
                if (isAlarmSet) {
                    tempAlarm = "true";
                }
                else {
                    tempAlarm = "false";
                }
                myCV.put(ToDoProvider.TODO_TABLE_COL_ISALARMSET, tempAlarm);
                getContentResolver().update(ToDoProvider.CONTENT_URI, myCV, "_ID=" + id, null);

                //And goes back to home activity
                Intent intentSave = new Intent(ToDoListItemActivity.this, HomeActivity.class);
                startActivity(intentSave);
                break;

            case R.id.btnDelete:
                //Deletes the item, cancels the alarm, and goes back to home
                deleteNote();
                cancelAlarm();
                Intent intentDelete = new Intent(ToDoListItemActivity.this, HomeActivity.class);
                startActivity(intentDelete);
                break;

            case R.id.completedBox:
                //Checking the task complete box cancels the alarm
                if (CompletedCheckBox.isChecked()) {
                    CompletedCheckBox.setChecked(true);
                    cancelAlarm();
                    Toast.makeText(this, "Alarm Canceled!", Toast.LENGTH_LONG).show();
                    isAlarmSet = false;
                }
                else {
                    CompletedCheckBox.setChecked(false);
                }
                break;

            case R.id.etTimePicker:
                //Opens the time picker dialog and sets the time for the alarm
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
                break;

            case R.id.etDatePicker:
                //Opens the calendar dialog and sets the date for the alarm
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
                break;

            case R.id.btnAlarm:
                AlarmButton = (Button) findViewById(R.id.btnAlarm);

                //If the alarm is set, the same button cancles
                //TODO -- make this its own button... would be way less clunky
                if (isAlarmSet) {
                    cancelAlarm();
                    Toast.makeText(this, "Alarm Canceled!", Toast.LENGTH_LONG).show();
                    isAlarmSet = false;
                }
                //If the user hasn't specified a date and time, don't set alarm
                else if (timeText.toString().equals("TIME") || dateText.toString().equals("DATE")) {
                    break;
                }
                else {
                    //If no alarm, set the alarm with date and time
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.YEAR, yearAlarm);
                    cal.set(Calendar.MONTH, monthAlarm);
                    cal.set(Calendar.DAY_OF_MONTH, dayAlarm);
                    cal.set(Calendar.HOUR_OF_DAY, hourOfDayAlarm);
                    cal.set(Calendar.MINUTE, minuteAlarm);
                    startAlarm(cal);
                    isAlarmSet = true;

                    Toast.makeText(this, "Alarm Set!", Toast.LENGTH_LONG).show();
                    break;
                }

            default:
                break;
        }
    }

    private void startAlarm(Calendar c) {
        //Creates a pending intent that runs the broadcast receiver that sends the alarm notification
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, AlertReceiver.class);
        alarmIntent.putExtra(EXTRA_MESSAGE_TITLE, EditTextTitle.getText().toString());
        alarmIntent.putExtra(EXTRA_MESSAGE_ID, id);
        //Pending intent ID is id of row
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, idInt, alarmIntent, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, AlertReceiver.class);
        //Same id for easy cancellation
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, idInt, alarmIntent, 0);

        alarmManager.cancel(pendingIntent);
    }

    //Time from time picker dialog is returned here
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String amOrPm;
        String minuteDisplay;
        hourOfDayAlarm = hourOfDay;
        minuteAlarm = minute;

        if (hourOfDay < 12) {
            amOrPm = "AM";
        }
        else {
            amOrPm = "PM";
        }
        switch (hourOfDay) {
            case 0:
                hourOfDay = 12;
                break;
            case 13:
                hourOfDay = 1;
                break;
            case 14:
                hourOfDay = 2;
                break;
            case 15:
                hourOfDay = 3;
                break;
            case 16:
                hourOfDay = 4;
                break;
            case 17:
                hourOfDay = 5;
                break;
            case 18:
                hourOfDay = 6;
                break;
            case 19:
                hourOfDay = 7;
                break;
            case 20:
                hourOfDay = 8;
                break;
            case 21:
                hourOfDay = 9;
                break;
            case 22:
                hourOfDay = 10;
                break;
            case 23:
                hourOfDay = 11;
                break;
        }

        if (minute < 10) {
            minuteDisplay = "0" + minute;
        }
        else {
            minuteDisplay = String.valueOf(minute);
        }
        timeText.setText(hourOfDay + ":" + minuteDisplay + " " + amOrPm);
    }

    //Date from calendar dialog is sent here
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        yearAlarm = year;
        monthAlarm = month;
        dayAlarm = dayOfMonth;

        dateText.setText(month + "/" + dayOfMonth + "/" + year);
    }

    //Delete the currently viewed item in the ToDOList
    void deleteNote(){
        //Create the projection for the query
        String[] projection = {
                ToDoProvider.TODO_TABLE_COL_ID,
                ToDoProvider.TODO_TABLE_COL_TITLE,
                ToDoProvider.TODO_TABLE_COL_CONTENT,
                ToDoProvider.TODO_TABLE_COL_TASKDONE,
                ToDoProvider.TODO_TABLE_COL_ALARMDATE,
                ToDoProvider.TODO_TABLE_COL_ALARMTIME };

        //Perform the query, with ID Descending
        Cursor myCursor = getContentResolver().query(ToDoProvider.CONTENT_URI,projection,"_ID=" + id,null,null);
        if(myCursor != null & myCursor.getCount() > 0) {
            //Delete the note
            getContentResolver().delete(Uri.parse(ToDoProvider.CONTENT_URI + "/" + id), null, null);
        }
    }
}
