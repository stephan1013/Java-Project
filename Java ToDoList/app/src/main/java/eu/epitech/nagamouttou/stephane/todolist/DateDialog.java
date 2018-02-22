package eu.epitech.nagamouttou.stephane.todolist;
import java.util.Calendar;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
/**
 * Created by Nagamo_s on 03/02/2018.
 */
@SuppressLint("ValidFragment")
public class DateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    EditText txtdate;
    public DateDialog(View view){
        txtdate=(EditText)view;
    }
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int Year, int Month, int Day) {
        String day = String.valueOf(Day).length() == 1 ? "0" + String.valueOf(Day) : String.valueOf(Day);
        String month = String.valueOf(Month + 1).length() == 1 ? "0" + String.valueOf(Month + 1) : String.valueOf(Month + 1);
        String year = String.valueOf(Year).length() == 1 ? "0" + String.valueOf(Year) : String.valueOf(Year);
        String date= year + "-" + month + "-" + day;
        txtdate.setText(date);
    }
}
