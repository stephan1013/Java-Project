package eu.epitech.nagamouttou.stephane.todolist;

/**
 * Created by Nagamo_s on 03/02/2018.
 */

public class TaskData {
    public String name;
    public String content;
    public String date;
    public String statut;
    public int id;

    public TaskData(String Name, String Content, String Date, String Statut, int Id) {
        this.name = Name;
        this.content = Content;
        this.date = Date;
        this.statut = Statut;
        this.id = Id;
    }
}
