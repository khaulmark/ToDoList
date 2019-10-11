package com.csce4623.ahnelson.todolist;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.InputMismatchException;
import java.util.List;
import 	android.view.inputmethod.EditorInfo;

import static com.csce4623.ahnelson.todolist.App.CHANNEL_1_ID;

public class ToDoListItemActivity extends AppCompatActivity implements View.OnClickListener, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    EditText EditTextContent;
    EditText EditTextTitle;
    CheckBox CompletedCheckBox;
    TextView timeText;
    TextView dateText;

    private String id = "";
    private int hourOfDayAlarm;
    private int minuteAlarm;
    private int yearAlarm;
    private int monthAlarm;
    private int dayAlarm;
    //private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_activity);
        initializeComponents();
        Intent intent = getIntent();
        id = intent.getStringExtra(HomeActivity.EXTRA_MESSAGE_ID);

        String[] projection = {
                ToDoProvider.TODO_TABLE_COL_ID,
                ToDoProvider.TODO_TABLE_COL_TITLE,
                ToDoProvider.TODO_TABLE_COL_CONTENT,
                ToDoProvider.TODO_TABLE_COL_TASKDONE,
                ToDoProvider.TODO_TABLE_COL_ALARMDATE,
                ToDoProvider.TODO_TABLE_COL_ALARMTIME};

        Cursor myCursor = getContentResolver().query(ToDoProvider.CONTENT_URI,projection,"_ID=" + id,null,null);

        int titleIndex = myCursor.getColumnIndex("TITLE");
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

        CompletedCheckBox = (CheckBox) findViewById(R.id.completedBox);
        if (taskDone.equals("false")) {
            CompletedCheckBox.setChecked(false);
        }
        else {
            CompletedCheckBox.setChecked(true);
        }

        EditTextTitle = (EditText) findViewById(R.id.tvNoteTitle);
        EditTextTitle.setInputType(InputType.TYPE_CLASS_TEXT);
        EditTextTitle.setText(title);

        EditTextContent = (EditText) findViewById(R.id.etNoteContent);
        EditTextContent.setInputType(InputType.TYPE_CLASS_TEXT);
        //Load description from content provider
        EditTextContent.setText(content);

        timeText = (TextView) findViewById(R.id.etTimePicker);
        timeText.setText(alarmTime);

        dateText = (TextView) findViewById(R.id.etDatePicker);
        dateText.setText(alarmDate);

        //notificationManager = NotificationManagerCompat.from(this);
    }

    //Set the OnClick Listener for buttons
    void initializeComponents(){
        findViewById(R.id.btnSave).setOnClickListener(this);
        findViewById(R.id.btnDelete).setOnClickListener(this);
        findViewById(R.id.etTimePicker).setOnClickListener(this);
        findViewById(R.id.etDatePicker).setOnClickListener(this);
        findViewById(R.id.setAlarmButton).setOnClickListener(this);
        findViewById(R.id.cancelAlarmButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()) {
            case R.id.btnSave:
                ContentValues myCV = new ContentValues();
                myCV.put(ToDoProvider.TODO_TABLE_COL_CONTENT, EditTextContent.getText().toString());
                myCV.put(ToDoProvider.TODO_TABLE_COL_TITLE, EditTextTitle.getText().toString());
                String tempChecked;
                if (CompletedCheckBox.isChecked()) {
                    tempChecked = "true";
                }
                else {
                    tempChecked = "false";
                }
                myCV.put(ToDoProvider.TODO_TABLE_COL_TASKDONE, tempChecked);
                getContentResolver().update(ToDoProvider.CONTENT_URI, myCV, "_ID=" + id, null);

                Intent intentSave = new Intent(ToDoListItemActivity.this, HomeActivity.class);
                startActivity(intentSave);
                break;

            case R.id.btnDelete:
                deleteNote();
                Intent intentDelete = new Intent(ToDoListItemActivity.this, HomeActivity.class);
                startActivity(intentDelete);
                break;

            case R.id.etTimePicker:
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
                break;

            case R.id.etDatePicker:
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
                break;

            //TODO - Make set alarm and cancel alarm one button
            //TODO - ADD one more column to database that lets me know if the user has an alarm set for this item
            //TODO - If Alarm is set, send Toast message and set background color to green.. if not set, leave blue
            //TODO - Make Date/Time look nicer
            case R.id.setAlarmButton:
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, yearAlarm);
                cal.set(Calendar.MONTH, monthAlarm);
                cal.set(Calendar.DAY_OF_MONTH, dayAlarm);
                cal.set(Calendar.HOUR_OF_DAY, hourOfDayAlarm);
                cal.set(Calendar.MINUTE, minuteAlarm);
                startAlarm(cal);
                break;

            //TODO - One more issue... if not calendar value isn't exactly the same, it won't cancel the alarm
            //TODO - Use boolean alarmSet value from DB to prevent user from altering time/date picker??
            case R.id.cancelAlarmButton:
                cancelAlarm();
                break;

            default:
                break;
        }
    }

    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, alarmIntent, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, alarmIntent, 0);

        alarmManager.cancel(pendingIntent);
    }

    /*public void sendOnChannel1(View v) {
        Notification notification = new NotificationCompat.Builder(this,CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_access_alarms_black_24dp)
                .setContentTitle("test")
                .setContentText("test content")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .build();

        notificationManager.notify(1, notification);
    }*/

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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        yearAlarm = year;
        monthAlarm = month;
        dayAlarm = dayOfMonth;

        dateText.setText(month + "/" + dayOfMonth + "/" + year);
    }

    //Delete the newest note placed into the database
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
            //Move the cursor to the beginning
            //myCursor.moveToFirst();
            //Get the ID (int) of the newest note (column 0)
           // int newestId = myCursor.getInt(0);
            //Delete the note
            int didWork = getContentResolver().delete(Uri.parse(ToDoProvider.CONTENT_URI + "/" + id), null, null);
            //If deleted, didWork returns the number of rows deleted (should be 1)
            if (didWork == 1) {
                //If it didWork, then create a Toast Message saying that the note was deleted
                //Toast.makeText(getApplicationContext(), "Deleted Note " + newestId, Toast.LENGTH_LONG).show();
            }
        } else{
            //Toast.makeText(getApplicationContext(), "No Note to delete!", Toast.LENGTH_LONG).show();

        }
    }
}
