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
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

public class ToDoListItemActivity extends AppCompatActivity implements View.OnClickListener{

    TextView TextView;
    EditText EditText;
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
                ToDoProvider.TODO_TABLE_COL_CONTENT};

        Cursor myCursor = getContentResolver().query(ToDoProvider.CONTENT_URI,projection,"_ID=" + id,null,null);

        int titleIndex = myCursor.getColumnIndex("TITLE");
        myCursor.moveToFirst();
        String title = myCursor.getString(titleIndex);

        int contentIndex = myCursor.getColumnIndex("CONTENT");
        myCursor.moveToFirst();
        String content = myCursor.getString(contentIndex);

        TextView = (TextView) findViewById(R.id.tvNoteTitle);
        TextView.setText(title);

        EditText = (EditText) findViewById(R.id.etNoteContent);
        EditText.setInputType(InputType.TYPE_CLASS_TEXT);
        //Load description from content provider
        EditText.setText(content);
    }

    //Set the OnClick Listener for buttons
    void initializeComponents(){
        findViewById(R.id.btnSave).setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()) {
            case R.id.etNoteContent:


            case R.id.btnSave:
                ContentValues myCV = new ContentValues();
                myCV.put(ToDoProvider.TODO_TABLE_COL_CONTENT, EditText.getText().toString());
                getContentResolver().update(ToDoProvider.CONTENT_URI, myCV, "_ID=" + id, null);

                Intent intent = new Intent(ToDoListItemActivity.this, HomeActivity.class);
                startActivity(intent);
        }
    }
}
