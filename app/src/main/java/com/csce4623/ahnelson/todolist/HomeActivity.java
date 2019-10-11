package com.csce4623.ahnelson.todolist;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

//GIT COMMIT 2:30 AM - FROM HOME

//Create HomeActivity and implement the OnClick listener
public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    RecyclerView recyclerView;
    ArrayList<String> recyclerViewList = new ArrayList<>();
    ArrayList<String> recyclerViewIDList = new ArrayList<>();
    MyAdapter adapter;

    public static final String EXTRA_MESSAGE_ID = "com.csce4623.ahnelson.todolist.ID";
    //public static final String EXTRA_MESSAGE_TITLE = "com.csce4623.ahnelson.todlist.TITLE";
    private String userText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initializeComponents();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        String[] projection = {
                ToDoProvider.TODO_TABLE_COL_ID,
                ToDoProvider.TODO_TABLE_COL_TITLE,
                ToDoProvider.TODO_TABLE_COL_CONTENT,
                ToDoProvider.TODO_TABLE_COL_TASKDONE,
                ToDoProvider.TODO_TABLE_COL_ALARMDATE,
                ToDoProvider.TODO_TABLE_COL_ALARMTIME };

        Cursor myCursor = getContentResolver().query(ToDoProvider.CONTENT_URI,projection,null,null,null);

        if (myCursor != null && myCursor.getCount() > 0) {
            int titleIndex = myCursor.getColumnIndex("TITLE");
            for (myCursor.moveToFirst(); !myCursor.isAfterLast(); myCursor.moveToNext()) {
                recyclerViewList.add(myCursor.getString(titleIndex));
            }
            int idIndex = myCursor.getColumnIndex("_ID");
            for (myCursor.moveToFirst(); !myCursor.isAfterLast(); myCursor.moveToNext()) {
                recyclerViewIDList.add(myCursor.getString(idIndex));
            }
        }

        adapter = new MyAdapter(this, recyclerViewList,
                new OnMyAdapterItemClickListener() {
                    @Override
                    public void onItemClicked(int position) {
                        Intent intent = new Intent(HomeActivity.this, ToDoListItemActivity.class);
                        intent.putExtra(EXTRA_MESSAGE_ID, recyclerViewIDList.get(position));
                        //intent.putExtra(EXTRA_MESSAGE_TITLE, recyclerViewList.get(position));
                        startActivity(intent);
                    }
                }
         );
        recyclerView.setAdapter(adapter);
    }

    //Set the OnClick Listener for buttons
    void initializeComponents(){
        findViewById(R.id.btnNewNote).setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            //If new Note, call createNewNote()
            case R.id.btnNewNote:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Enter the name of the ToDoList Item:");

                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userText = input.getText().toString();
                        recyclerViewList.add(userText);
                        recyclerViewIDList.add(createNewNote(userText));
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

                break;
            default:
                break;
        }
    }

    //Create a new note with the title "New Note" and content "Note Content"
    String createNewNote(String title){
        //Create a ContentValues object
        ContentValues myCV = new ContentValues();
        //Put key_value pairs based on the column names, and the values
        myCV.put(ToDoProvider.TODO_TABLE_COL_TITLE, title);
        myCV.put(ToDoProvider.TODO_TABLE_COL_CONTENT,"Insert content here.");
        myCV.put(ToDoProvider.TODO_TABLE_COL_TASKDONE, "false");
        myCV.put(ToDoProvider.TODO_TABLE_COL_ALARMDATE, "DATE");
        myCV.put(ToDoProvider.TODO_TABLE_COL_ALARMTIME, "TIME");
        //Perform the insert function using the ContentProvider
        String listItemID = getContentResolver().insert(ToDoProvider.CONTENT_URI,myCV).getLastPathSegment();
        //Set the projection for the columns to be returned
        /*String[] projection = {
                ToDoProvider.TODO_TABLE_COL_ID,
                ToDoProvider.TODO_TABLE_COL_TITLE,
                ToDoProvider.TODO_TABLE_COL_CONTENT,
                ToDoProvider.TODO_TABLE_COL_COMPLETED};
        //Perform a query to get all rows in the DB
        Cursor myCursor = getContentResolver().query(ToDoProvider.CONTENT_URI,projection,null,null,null);*/
        return listItemID;
    }
}
