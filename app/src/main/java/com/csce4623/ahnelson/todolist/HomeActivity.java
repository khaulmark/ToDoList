package com.csce4623.ahnelson.todolist;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
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



//Create HomeActivity and implement the OnClick listener
public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    RecyclerView recyclerView;
    ArrayList<String> recyclerViewList = new ArrayList<>();
    ArrayList<String> recyclerViewIDList = new ArrayList<>();
    MyAdapter adapter;

    public static final String EXTRA_MESSAGE_ID = "com.csce4623.ahnelson.todolist.ID";
    private String userText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initializeComponents();

        //Recycler view holds the visible list of titles for the home activity
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String[] projection = {
                ToDoProvider.TODO_TABLE_COL_ID,
                ToDoProvider.TODO_TABLE_COL_TITLE,
                ToDoProvider.TODO_TABLE_COL_CONTENT,
                ToDoProvider.TODO_TABLE_COL_TASKDONE,
                ToDoProvider.TODO_TABLE_COL_ALARMDATE,
                ToDoProvider.TODO_TABLE_COL_ALARMTIME,
                ToDoProvider.TODO_TABLE_COL_ISALARMSET};

        //Queries the entire DB
        Cursor myCursor = getContentResolver().query(ToDoProvider.CONTENT_URI,projection,null,null,null);

        if (myCursor != null && myCursor.getCount() > 0) {
            //Iterates through the DB to get titles and IDs
            int titleIndex = myCursor.getColumnIndex("TITLE");
            for (myCursor.moveToFirst(); !myCursor.isAfterLast(); myCursor.moveToNext()) {
                recyclerViewList.add(myCursor.getString(titleIndex));
            }
            int idIndex = myCursor.getColumnIndex("_ID");
            for (myCursor.moveToFirst(); !myCursor.isAfterLast(); myCursor.moveToNext()) {
                recyclerViewIDList.add(myCursor.getString(idIndex));
            }
        }
        myCursor.close();

        //MyAdapter provides interface to override the click listener for recycler view
        //Also inflates the recycler view with additional rows
        adapter = new MyAdapter(this, recyclerViewList,
                new OnMyAdapterItemClickListener() {
                    @Override
                    public void onItemClicked(int position) {
                        //Intent starts the Item activity and passes it the ID of the row
                        Intent intent = new Intent(HomeActivity.this, ToDoListItemActivity.class);
                        intent.putExtra(EXTRA_MESSAGE_ID, recyclerViewIDList.get(position));
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
                //Create a pop-up dialog that the user can specify the title of the item
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
        myCV.put(ToDoProvider.TODO_TABLE_COL_ISALARMSET, "false");
        //Perform the insert function using the ContentProvider
        String listItemID = getContentResolver().insert(ToDoProvider.CONTENT_URI,myCV).getLastPathSegment();

        return listItemID;
    }
}
