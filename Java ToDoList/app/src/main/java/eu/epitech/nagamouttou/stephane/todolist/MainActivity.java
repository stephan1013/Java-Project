package eu.epitech.nagamouttou.stephane.todolist;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.provider.CalendarContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import eu.epitech.nagamouttou.stephane.todolist.db.TaskContract;
import eu.epitech.nagamouttou.stephane.todolist.db.TaskDbHelper;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TaskDbHelper mHelper;
    private ListView mTaskListView;
    public String OrderBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OrderBy = null;
        mHelper = new TaskDbHelper(this);
        mTaskListView = (ListView) findViewById(R.id.list_todo);
        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            Intent intent = new Intent(this, TaskForm.class);
            switch (item.getItemId()) {
                case R.id.add_task:
                    startActivity(intent);
                    return true;
                case R.id.Trie1:
                    OrderBy = TaskContract.TaskEntry.COL_TASK_DATE  + " ASC";
                    updateUI();
                    return true;
                case R.id.Trie2:
                    OrderBy = TaskContract.TaskEntry.COL_TASK_DATE  + " DESC";
                    updateUI();
                    return true;
                case R.id.Trie3:
                    OrderBy = TaskContract.TaskEntry.COL_TASK_TITLE  + " ASC";
                    updateUI();
                    return true;
                case R.id.Trie4:
                    OrderBy = TaskContract.TaskEntry.COL_TASK_TITLE  + " DESC";
                    updateUI();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }

    public void editTask(View view) {
        View parent = (View) view.getParent();

        Intent intent = new Intent(this, TaskEdit.class);
        intent.putExtra("Task", String.valueOf(((TextView) parent.findViewById(R.id.task_title)).getText()));
        intent.putExtra("Content", String.valueOf(((TextView) parent.findViewById(R.id.task_content)).getText()));
        intent.putExtra("Date", String.valueOf(((TextView) parent.findViewById(R.id.task_date)).getText()));
        intent.putExtra("Statut", String.valueOf(((TextView) parent.findViewById(R.id.task_statut)).getText()));
        intent.putExtra("Id", String.valueOf(((TextView) parent.findViewById(R.id.task_id)).getText()));
        startActivity(intent);
        updateUI();
    }

    public void deleteTask(View view) {
        View parent = (View) view.getParent();

        int id = Integer.valueOf(String.valueOf(((TextView) parent.findViewById(R.id.task_id)).getText()));
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(TaskContract.TaskEntry.TABLE,
                TaskContract.TaskEntry._ID + " = " + id, null);
        db.close();
        updateUI();
    }

    private void updateUI() {
        ArrayList<TaskData> arrayOfTask = new ArrayList<TaskData>();
        CustomAdapter adapter = new CustomAdapter(this, arrayOfTask);
        ListView listView = (ListView) findViewById(R.id.list_todo);
        listView.setAdapter(adapter);

        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE,
                new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COL_TASK_TITLE, TaskContract.TaskEntry.COL_TASK_CONTENT, TaskContract.TaskEntry.COL_TASK_DATE, TaskContract.TaskEntry.COL_TASK_STATUT},
                null, null, null, null, OrderBy);
        while (cursor.moveToNext()) {
            int idt = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);
            int idc = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_CONTENT);
            int idd = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_DATE);
            int id = cursor.getColumnIndex(TaskContract.TaskEntry._ID);
            int ids = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_STATUT);
            adapter.add(new TaskData(cursor.getString(idt), cursor.getString(idc), cursor.getString(idd), cursor.getString(ids), cursor.getInt(id)));
        }
        cursor.close();
        db.close();
    }

    public void dialog(View view) {

        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        final String[] grpname = {"TODO", "START", "DONE", "ERASE"};
        final View parent = (View) view.getParent();
        alt_bld.setTitle("Select a Statut");
        alt_bld.setSingleChoiceItems(grpname, -1, new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (!Objects.equals(grpname[item], "ERASE")) {
                    int id = Integer.valueOf(String.valueOf(((TextView) parent.findViewById(R.id.task_id)).getText()));
                    SQLiteDatabase db = mHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(TaskContract.TaskEntry.COL_TASK_STATUT, grpname[item]);
                    db.update(TaskContract.TaskEntry.TABLE, values, TaskContract.TaskEntry._ID + " = " + id, null);
                    db.close();
                    updateUI();
                } else
                    deleteTask(parent);
                dialog.dismiss();
            }
        });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }

}

/*                  Test Print
 Toast.makeText(getApplicationContext(), "Group Name = ", Toast.LENGTH_SHORT).show();

         */