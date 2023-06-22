package main;

import java.util.ArrayList;

public class Task {

    int taskNumber;
    String condition;
    int maxGrade;
    ArrayList<ArrayList<String>> Data;

    public int getTaskNumber() {
        return taskNumber;
    }

    public void setTaskNumber(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public int getMaxGrade() {
        return maxGrade;
    }

    public void setMaxGrade(int maxGrade) {
        this.maxGrade = maxGrade;
    }

    public ArrayList<ArrayList<String>> getData() {
        return Data;
    }

    public void setData(ArrayList<ArrayList<String>> data) {
        Data = data;
    }



    @Override
    public String toString() {
        return "Task{" +
                "condition='" + condition + '\'' +
                ", maxGrade=" + maxGrade +
                ", Data=" + Data +
                '}';
    }
}
