package eu.epitech.nagamouttou.stephane.todolist;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class TaskEdit extends AppCompatActivity {

    String task;
    String content;
    String dateTime;
    String time;
    String date;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);

        Intent intent = getIntent();
        task = intent.getStringExtra("Task");
        content = intent.getStringExtra("Content");
        id = intent.getStringExtra("Id");
        dateTime = intent.getStringExtra("Date");

        String[] tmp = dateTime.split(" ");
        date = tmp[0];
        time = tmp[1];
        TextView textView = findViewById(R.id.editText);
        textView.setText(task);
        textView = findViewById(R.id.editText2);
        textView.setText(content);
        textView = findViewById(R.id.editText3);
        textView.setText(date);
        textView = findViewById(R.id.editText4);
        textView.setText(time);
    }

    public void sendEdit(View view) {
        Intent intent = new Intent(this, TaskEditAdd.class);

        intent.putExtra("TTask", task);
        intent.putExtra("TContent", content);
        intent.putExtra("TDate", dateTime);
        intent.putExtra("Id", id);
        String time =(((EditText)findViewById(R.id.editText4)).getText().toString()).length() >= 7 ? ((EditText)findViewById(R.id.editText4)).getText().toString() : ((EditText)findViewById(R.id.editText4)).getText().toString() + ":00";
        intent.putExtra("Task", ((EditText)findViewById(R.id.editText)).getText().toString());
        intent.putExtra("Content", ((EditText)findViewById(R.id.editText2)).getText().toString());
        intent.putExtra("Date", ((EditText)findViewById(R.id.editText3)).getText().toString() + " " + time);

        startActivity(intent);
    }

    public void onStart(){
        super.onStart();
        EditText txtDate=(EditText)findViewById(R.id.editText3);
        txtDate.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View view, boolean hasfocus){
                if(hasfocus){
                    DateDialog dialog = new DateDialog(view);
                    FragmentTransaction ft =getFragmentManager().beginTransaction();
                    dialog.show(ft, "DatePicker");
                }
            }
        });
        EditText txtTime = (EditText)findViewById(R.id.editText4);
        txtTime.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View view, boolean hasfocus){
                if(hasfocus){
                    TimeDialog dialog = new TimeDialog(view);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    dialog.show(ft, "TimePicker");
                }
            }
        });
    }

}
