package eu.epitech.nagamouttou.stephane.todolist;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Nagamo_s on 03/02/2018.
 */
@SuppressLint("ValidFragment")
public class TimeDialog extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    EditText txtdate;
    public TimeDialog(View view){
        txtdate=(EditText)view;
    }
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return  new TimePickerDialog(getActivity(), this, hour, minute, true);
    }

    public void onTimeSet(TimePicker view, int Hour, int Minute) {
        String hour = String.valueOf(Hour).length() == 1 ? "0" + String.valueOf(Hour) : String.valueOf(Hour);
        String minute = String.valueOf(Minute).length() == 1 ? "0" + String.valueOf(Minute) : String.valueOf(Minute);
        String time = hour + ":" + minute;
        txtdate.setText(time);
    }
}
