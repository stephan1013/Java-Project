package eu.epitech.nagamouttou.stephane.todolist;

import android.app.DatePickerDialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskForm extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "Form";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_form);

    }

    public void sendForm(View view) {


        EditText task = (EditText)findViewById(R.id.Task);
        EditText content = (EditText)findViewById(R.id.Content);
        EditText date = (EditText)findViewById(R.id.txtdate);
        EditText time = (EditText)findViewById(R.id.txttime);
        if (!validationText(task.getText().toString()))
        {
            task.setError("Tâche invalide");
            task.requestFocus();
        }
        else if (!validationText(content.getText().toString()))
        {
            content.setError("Description invalide");
            content.requestFocus();
        }
        else if (!validationDate(date.getText().toString()))
        {
            date.setError("Date invalide");
            date.setText("");
        }
        else if (!validationTime(time.getText().toString()))
        {
            time.setError("Heure invalide");
            time.setText("");
        }
        else
        {
            Intent intent = new Intent(this, TaskAdd.class);

            intent.putExtra("Task", ((EditText) findViewById(R.id.Task)).getText().toString());
            intent.putExtra("Content", ((EditText) findViewById(R.id.Content)).getText().toString());
            intent.putExtra("DateTime", ((EditText) findViewById(R.id.txtdate)).getText().toString() + " " + ((EditText) findViewById(R.id.txttime)).getText().toString() + ":00");
            startActivity(intent);
        }
    }

    private boolean validationTime(String time) {
        String parse = "^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]";
        Pattern pattern = Pattern.compile(parse);
        Matcher matcher = pattern.matcher(time);
        return matcher.matches();
    }

    private boolean validationDate(String date) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setLenient(false);
        try {
               Date dt = formatter.parse(date);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean validationText(String text) {
        if (text != null && text.length() > 0) {
            String parse = "^[a-zA-Z0-9]*$";
            Pattern pattern = Pattern.compile(parse);
            Matcher matcher = pattern.matcher(text);
            return matcher.matches();
        }
        return false;
    }

    public void onStart(){
        super.onStart();
        EditText txtDate=(EditText)findViewById(R.id.txtdate);
        txtDate.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View view, boolean hasfocus){
                if(hasfocus){
                    DateDialog dialog = new DateDialog(view);
                    FragmentTransaction ft =getFragmentManager().beginTransaction();
                    dialog.show(ft, "DatePicker");
                }
            }
        });
        EditText txtTime = (EditText)findViewById(R.id.txttime);
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