package main.models;

public class Student {

    int var;
    String fio;
    StudentReport report;

    public StudentReport getReport() {
        return report;
    }

    public void setReport(StudentReport report) {
        this.report = report;
    }

    public int getVar() {
        return var;
    }

    public void setVar(int var) {
        this.var = var;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    @Override
    public String toString() {
        String rate;
        if (report==null){
            rate = "�� ������";
        } else if (report.noWork){
            rate = "��� ������";
        } else rate = "������";
        return "" + fio + ", " + var + " �������, " + rate;
    }
}
