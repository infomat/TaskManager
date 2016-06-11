package com.conestogac.mytask;

import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private TaskCursorAdapter taskAdapter;
    private TaskDatabaseHelper dbHelper;
    private ListView listView;
    private Task taskItem = new Task();

//List View are bind to Cursor Adapter to support for long list of data
//Additionally, to support taking long time to load database data,
//separate thread is used to load data into adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new TaskDatabaseHelper(this);
        listView = (ListView) findViewById(R.id.list_task);

        //In most cases, by adding empty_list_item, this text view will be shown, if list has
        //no item to display. However, for some reason it is not displayed automatically.
        //So, set Empty View Manually
        listView.setEmptyView(findViewById(R.id.empty_list_item));

        //read cursor from DB and set to cursor adapter
        //cursor adapter will be connected to list view
        readFromDB();


        //Add listener on listview
        //Important!!! To get this work, android:clickable="false", android:focusable="false",
        // android:focusableInTouchMode="false" are defined within all candiate widget
        //that consumes click event. Without adding this, click event does not work
        //for the list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                // Get the cursor, positioned to the corresponding row in the result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                // Get the selected item and activate newtask activity
                Log.d(TAG, cursor.getString(cursor.getColumnIndexOrThrow(TaskDatabaseHelper.KEY_TASKS_TODO)));

                // To resolve scope, MainActivity.this is used, instead of this
                Intent intent = new Intent(MainActivity.this, NewTaskActivity.class);

                //Read value from DB using cursor
                taskItem.setId(cursor.getInt(cursor.getColumnIndexOrThrow(TaskDatabaseHelper.KEY_TASKS_ID)));
                taskItem.setTodo(cursor.getString(cursor.getColumnIndexOrThrow(TaskDatabaseHelper.KEY_TASKS_TODO)));
                taskItem.setPriority(cursor.getInt(cursor.getColumnIndexOrThrow(TaskDatabaseHelper.KEY_TASKS_PRIORITY)));
                taskItem.setDueDateTime(cursor.getString(cursor.getColumnIndexOrThrow(TaskDatabaseHelper.KEY_TASKS_DUEDATE)));

                //Assign data to send - which implements Parcel interface to send
                intent.putExtra(NewTaskActivity.EXTRA_DB_COMMAND, "UPDATE");
                intent.putExtra(NewTaskActivity.EXTRA_SELECTED_ITEM, taskItem);

                //To resolve scope, MainActivity.this is used, instead of this
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onResume() {

        super.onResume();
        readFromDB();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "Ondestory()");

        dbHelper.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.main_activity_bar, menu);
        return (super.onCreateOptionsMenu(menu));
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, NewTaskActivity.class);
        // Handle Add button on the action bar items
        switch (item.getItemId()) {
            case R.id.addtask:
                intent.putExtra(NewTaskActivity.EXTRA_DB_COMMAND, "CREATE");
                // when AddTask button is selected, go to another activity
                startActivity(new Intent(this, NewTaskActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void readFromDB() {
        // Database query can be a time consuming task ..
        // so its safe to call database query in another thread
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                //get cursor and load data into adapter
                taskAdapter = new TaskCursorAdapter(MainActivity.this, dbHelper.getAllTasks(),0);

                //set cursor adapter to listview
                listView.setAdapter(taskAdapter);
            }
        });
    }

}
