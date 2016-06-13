package com.conestogac.mytask;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;


/**
 * Cursor Adapter to show cursor value on list item by reading from Database
 * This will be bind to listview which layout is defined at task_item.xml
 *
 */
public class TaskCursorAdapter extends CursorAdapter{
    private static final String TAG = TaskCursorAdapter.class.getSimpleName();
    private Context curConext;

    // Default constructor
    public TaskCursorAdapter(Context context, Cursor cursor, int cflags) {
        super(context, cursor, 0);
        curConext = context;
    }


    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, final ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.task_item, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the data on a TextView.
    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        // Find fields to populate in inflated template
        CheckBox cbIsCompleted = (CheckBox) view.findViewById(R.id.cbIsCompleted);
        Button btPriority = (Button) view.findViewById(R.id.btPrority);
        TextView tvTodo = (TextView) view.findViewById(R.id.tvTodo);
        TextView tvDateTime = (TextView) view.findViewById(R.id.tvDateTime);

        // Read value with cursor and set value to widget
        // Id is set to invisible text, to make easy to read data from database
        curConext = context;
        btPriority.setBackgroundColor(getColorFromPriority(cursor.getInt(cursor.getColumnIndexOrThrow(TaskDatabaseHelper.KEY_TASKS_PRIORITY))));
        tvTodo.setText(String.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(TaskDatabaseHelper.KEY_TASKS_TODO))));

        //Set tag with id of item and  get id when checkbox clicked to delete the checked task
        tvTodo.setTag(cursor.getInt(0));
        tvDateTime.setText(String.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(TaskDatabaseHelper.KEY_TASKS_DUEDATE))));
    }

    public Integer getColorFromPriority(Integer priority) {
        Integer retColor;

        Log.i(TAG,"getColorFromPriority() Priority Color:" + priority);

        switch (priority) {
            case 1:
                retColor = android.R.color.holo_green_light;
                break;
            case 2:
                retColor = android.R.color.holo_orange_light;
                break;
            case 3:
                retColor = android.R.color.holo_red_light;
                break;
            default:
                retColor = android.R.color.holo_blue_light;
                break;

        }
        return (curConext.getResources().getColor(retColor));
    }

}
