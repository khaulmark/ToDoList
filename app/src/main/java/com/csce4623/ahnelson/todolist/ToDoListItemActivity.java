package com.csce4623.ahnelson.todolist;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import 	android.view.inputmethod.EditorInfo;

public class ToDoListItemActivity extends AppCompatActivity implements View.OnClickListener{

    EditText EditTextContent;
    EditText EditTextTitle;
    CheckBox CompletedCheckBox;
    private String id = "";

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
                ToDoProvider.TODO_TABLE_COL_TASKDONE};

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


    }

    //Set the OnClick Listener for buttons
    void initializeComponents(){
        findViewById(R.id.btnSave).setOnClickListener(this);
        findViewById(R.id.btnDelete).setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()) {
            case R.id.etNoteContent:


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
        }
    }

    //Delete the newest note placed into the database
    void deleteNote(){
        //Create the projection for the query
        String[] projection = {
                ToDoProvider.TODO_TABLE_COL_ID,
                ToDoProvider.TODO_TABLE_COL_TITLE,
                ToDoProvider.TODO_TABLE_COL_CONTENT,
                ToDoProvider.TODO_TABLE_COL_TASKDONE };

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
