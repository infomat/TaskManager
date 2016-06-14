package com.conestogac.mytask;

import android.app.AlarmManager;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private TaskCursorAdapter taskAdapter;
    private TaskDatabaseHelper mytaskdb;
    private ListView listView;
    private Integer sortSel = 0;
    private Integer orderSel = 0;
    private Task taskItem = new Task();
    private AlarmManager alarms=null;

//List View are bind to Cursor Adapter to support for long list of data
//Additionally, to support taking long time to load database data,
//separate thread is used to load data into adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mytaskdb = new TaskDatabaseHelper(this);
        listView = (ListView) findViewById(R.id.list_task);

        //In most cases, by adding empty_list_item, this text view will be shown, if list has
        //no item to display. However, for some reason it is not displayed automatically.
        //So, set Empty View Manually
        listView.setEmptyView(findViewById(R.id.empty_list_item));

        //read cursor from DB and set to cursor adapter
        //cursor adapter will be connected to list view
        readFromDB();

        alarms = (AlarmManager)getSystemService(ALARM_SERVICE);
        //Add listener on listview
        //Important!!! To get this work, android:clickable="false", android:focusable="false",
        // android:focusableInTouchMode="false" are defined within all candiate widget
        //that consumes click event. Without adding this, click event does not work
        //for the list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                Log.d(TAG, "onItemClick");

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
                intent.putExtra(NewTaskActivity.EXTRA_DB_COMMAND, NewTaskActivity.DbOperation[1]);
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

        mytaskdb.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.main_activity_bar, menu);
        return (super.onCreateOptionsMenu(menu));
    }


    // When user select action bar menu, this will be invoked
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, NewTaskActivity.class);
        // Handle Add button on the action bar items
        switch (item.getItemId()) {

            //Add task
            case R.id.addtask:
                intent.putExtra(NewTaskActivity.EXTRA_DB_COMMAND, NewTaskActivity.DbOperation[0]);
                // when AddTask button is selected, go to another activity
                startActivity(new Intent(this, NewTaskActivity.class));
                return true;

            //Order by. For each touch, order by will be changed and reload list from db
            case R.id.sort:
                sortSel = (sortSel+1) % (TaskDatabaseHelper.sortConditon).length;
                Toast.makeText(getApplicationContext(), "Order By "+
                        TaskDatabaseHelper.sortConditon[sortSel], Toast.LENGTH_SHORT).show();
                readFromDB();
                return true;

            //Asc or Dsc. For each touch, ASC/DESC will be changed and reload list from db
            case R.id.order:
                orderSel = (orderSel + 1) %  (TaskDatabaseHelper.orderConditon).length;

                Toast.makeText(getApplicationContext(), "Sort with "+
                        TaskDatabaseHelper.orderConditon[orderSel], Toast.LENGTH_SHORT).show();
                readFromDB();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClick(View view) {
        Integer index;

        //Navigate through view hierarchy to get textview's tag which was set at Cursor Adapter
        // during creating cursor adapter
        ViewGroup parent = (ViewGroup) view.getParent();

        //Get index of checked task to delete
        Log.d(TAG, "Checked _ID: "+parent.getChildAt(2).getTag());
        index = (Integer) parent.getChildAt(2).getTag();

        //Cancel Alarm first incase of problem at DB
        AlarmReceiver.cancelAlarm(this, alarms, index);

        //delete task
        if (mytaskdb.deleteTask(index) > 0) {
            Toast.makeText(getApplicationContext(), "Task is deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Error with delete!!! Please try again.", Toast.LENGTH_SHORT).show();
        }

        //Todo:This should refactored later just updating modifed list
        //to update screenm call readFromDB()
        readFromDB();
    }

    private void readFromDB() {
        // Database query can be a time consuming task ..
        // so its safe to call database query in another thread
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                //get cursor and load data into adapter
                taskAdapter = new TaskCursorAdapter(MainActivity.this, mytaskdb.getAllTasks(sortSel, orderSel),0);

                //set cursor adapter to listview
                listView.setAdapter(taskAdapter);
            }
        });
    }

}
