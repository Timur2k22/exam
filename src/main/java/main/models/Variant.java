package main.models;

import main.models.Task;

import java.util.ArrayList;

public class Variant {

    ArrayList<Task> tasks;

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "Variant{" +
                "tasks=" + tasks +
                '}';
    }
}
