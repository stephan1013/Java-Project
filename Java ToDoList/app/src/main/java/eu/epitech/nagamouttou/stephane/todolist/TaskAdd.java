package eu.epitech.nagamouttou.stephane.todolist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Calendar;

import eu.epitech.nagamouttou.stephane.todolist.db.TaskContract;
import eu.epitech.nagamouttou.stephane.todolist.db.TaskDbHelper;

public class TaskAdd extends AppCompatActivity {
    private TaskDbHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_add);

        mHelper = new TaskDbHelper(this);
        Intent intent;
        intent = getIntent();

        SQLiteDatabase db = mHelper.getWritableDatabase();
        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE, null,
                TaskContract.TaskEntry.COL_TASK_TITLE + " = " + "'" + intent.getStringExtra("Task") + "'" + " AND " +
                        TaskContract.TaskEntry.COL_TASK_CONTENT + " = " + "'" + intent.getStringExtra("Content") + "'" + " AND " +
                        TaskContract.TaskEntry.COL_TASK_DATE + " = " + "'" + intent.getStringExtra("DateTime") + "'",
                null, null, null, null);
        int i = 0;
        while (cursor.moveToNext()) {
            ++i;
        }
        if (i == 0) {
            ContentValues values = new ContentValues();
            values.put(TaskContract.TaskEntry.COL_TASK_TITLE, intent.getStringExtra("Task"));
            values.put(TaskContract.TaskEntry.COL_TASK_CONTENT, intent.getStringExtra("Content"));
            values.put(TaskContract.TaskEntry.COL_TASK_DATE, intent.getStringExtra("DateTime"));
            values.put(TaskContract.TaskEntry.COL_TASK_STATUT, "TODO");
            db.insertWithOnConflict(TaskContract.TaskEntry.TABLE,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE);
            setNotification(intent.getStringExtra("DateTime"));
        }
        else
            Toast.makeText(getApplicationContext(), "La tâche existe déja !!!", Toast.LENGTH_SHORT).show();
        db.close();

        Intent menu = new Intent(this, MainActivity.class);


        startActivity(menu);
    }

    private void setNotification(String Date) {

        String[] tmp = Date.split(" ");
        String[] date = tmp[0].split("-");
        String[] time = tmp[1].split(":");
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time[0]));
        calendar.set(Calendar.MINUTE, Integer.valueOf(time[1]));
        calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(date[2]));
        calendar.set(Calendar.MONTH, Integer.valueOf(date[1]) - 1);
        calendar.set(Calendar.YEAR, Integer.valueOf(date[0]));

        Intent intent = new Intent(getApplicationContext(), TaskNotification.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
