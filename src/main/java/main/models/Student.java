package main.models;

public class Student {

    int var;
    String fio;

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
        return "Student{" +
                "var=" + var +
                ", fio='" + fio + '\'' +
                '}';
    }
}
