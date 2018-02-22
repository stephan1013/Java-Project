package eu.epitech.nagamouttou.stephane.todolist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Nagamo_s on 03/02/2018.
 */

public class CustomAdapter extends ArrayAdapter<TaskData> {
    public CustomAdapter(Context context, ArrayList<TaskData> task) {
        super(context, 0, task);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TaskData task = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_todo, parent, false);
        }
        TextView name = (TextView) convertView.findViewById(R.id.task_title);
        TextView content = (TextView) convertView.findViewById(R.id.task_content);
        TextView date = (TextView) convertView.findViewById(R.id.task_date);
        Button statut = (Button) convertView.findViewById(R.id.task_statut);
        TextView id = (TextView) convertView.findViewById(R.id.task_id);

        name.setText(task.name);
        content.setText(task.content);
        date.setText(task.date);
        statut.setText(task.statut);
        id.setText(String.valueOf(task.id));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date strDate = null;
        try {
            strDate = sdf.parse(task.date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (new Date().after(strDate))
            date.setTextColor(Color.parseColor("#FF0000"));
        else
            date.setTextColor(Color.parseColor("#72c972"));

        return convertView;
    }
}
