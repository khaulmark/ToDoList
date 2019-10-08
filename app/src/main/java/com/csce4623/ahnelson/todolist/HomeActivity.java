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

//Create HomeActivity and implement the OnClick listener
public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    RecyclerView recyclerView;
    ArrayList<String> recyclerViewList = new ArrayList<>();
    ArrayList<String> recyclerViewIDList = new ArrayList<>();
    MyAdapter adapter;

    public static final String EXTRA_MESSAGE_ID = "com.csce4623.ahnelson.todolist.ID";
    public static final String EXTRA_MESSAGE_TITLE = "com.csce4623.ahnelson.todlist.TITLE";
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
                ToDoProvider.TODO_TABLE_COL_CONTENT};

        Cursor myCursor = getContentResolver().query(ToDoProvider.CONTENT_URI,projection,null,null,null);

        if (myCursor.getCount() != 0) {
            int titleIndex = myCursor.getColumnIndex("TITLE");
            for (myCursor.moveToFirst(); !myCursor.isAfterLast(); myCursor.moveToNext()) {
                recyclerViewList.add(myCursor.getString(titleIndex));
            }
            int idIndex = myCursor.getColumnIndex("_ID");
            for (myCursor.moveToFirst(); !myCursor.isAfterLast(); myCursor.moveToNext()) {
                recyclerViewIDList.add(myCursor.getString(idIndex));
            }
            //change
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
        findViewById(R.id.btnDeleteNote).setOnClickListener(this);
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
                        createNewNote(userText);
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
            //If delete note, call deleteNewestNote()
            case R.id.btnDeleteNote:
                if (recyclerViewList.isEmpty()) {
                    break;
                }

                else {
                    deleteNewestNote();
                    int position = recyclerViewList.size() - 1;
                    recyclerViewList.remove(position);
                    recyclerViewIDList.remove(position);
                    recyclerView.getAdapter().notifyItemChanged(position);
                    break;
                }
            //This shouldn't happen
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
        //Perform the insert function using the ContentProvider
        String listItemID = getContentResolver().insert(ToDoProvider.CONTENT_URI,myCV).getLastPathSegment();
        //Set the projection for the columns to be returned
        String[] projection = {
                ToDoProvider.TODO_TABLE_COL_ID,
                ToDoProvider.TODO_TABLE_COL_TITLE,
                ToDoProvider.TODO_TABLE_COL_CONTENT};
        //Perform a query to get all rows in the DB
        Cursor myCursor = getContentResolver().query(ToDoProvider.CONTENT_URI,projection,null,null,null);
        //Create a toast message which states the number of rows currently in the database
        //Toast.makeText(getApplicationContext(),Integer.toString(myCursor.getCount()),Toast.LENGTH_SHORT).show();
        return listItemID;
    }

    //Delete the newest note placed into the database
    void deleteNewestNote(){
        //Create the projection for the query
        String[] projection = {
                ToDoProvider.TODO_TABLE_COL_ID,
                ToDoProvider.TODO_TABLE_COL_TITLE,
                ToDoProvider.TODO_TABLE_COL_CONTENT};

        //Perform the query, with ID Descending
        Cursor myCursor = getContentResolver().query(ToDoProvider.CONTENT_URI,projection,null,null,"_ID DESC");
        if(myCursor != null & myCursor.getCount() > 0) {
            //Move the cursor to the beginning
            myCursor.moveToFirst();
            //Get the ID (int) of the newest note (column 0)
            int newestId = myCursor.getInt(0);
            //Delete the note
            int didWork = getContentResolver().delete(Uri.parse(ToDoProvider.CONTENT_URI + "/" + newestId), null, null);
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
