package main;

import main.models.Group;
import main.models.Student;
import main.models.Task;
import main.models.Variant;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ExcelHandler {

    public static ArrayList<Group> importStudents(File file) throws IOException, InvalidFormatException {
        XSSFWorkbook book = new XSSFWorkbook(file);
        ArrayList<Group> groups = new ArrayList<>();
        for (int i = 0; i<book.getNumberOfSheets(); i++){
            XSSFSheet sheet = book.getSheetAt(i);
            Group group = new Group();
            ArrayList<Student> students = new ArrayList<>();
            for (int j = 1; j<=sheet.getLastRowNum(); j++) {
                Student student = new Student();
                XSSFRow row = sheet.getRow(j);
                student.setVar((int) row.getCell(0).getNumericCellValue());
                student.setFio(row.getCell(1).getStringCellValue());
                students.add(student);
            }
            group.setStudents(students);
            group.setName(sheet.getSheetName());
            groups.add(group);
        }
        return groups;
    }

    public static Variant importVar(String fileName) throws IOException {
        InputStream input = ExcelHandler.class.getResourceAsStream(fileName);
        XSSFWorkbook book = new XSSFWorkbook(fileName);
        Variant variant = new Variant();
        ArrayList<Task> tasks = new ArrayList<>();
        for (int i = 0; i<book.getNumberOfSheets()-1; i++){
            Task task = importTask(book, i);
            XSSFSheet lastSheet = book.getSheetAt(book.getNumberOfSheets()-1);
            getCondition(lastSheet, task);
            tasks.add(task);
        }
        variant.setTasks(tasks);
        return variant;
    }

    public static Task getCondition(XSSFSheet lastSheet, Task task){
        int grade = 0;
        String condition = "";
        for (int i = 0; i<lastSheet.getLastRowNum(); i++){
            XSSFRow row = lastSheet.getRow(i);
            if (row.getCell(0).getStringCellValue().equals("Задание "+(task.getTaskNumber()+1))){
                XSSFRow gradeRow = lastSheet.getRow(i+3);
                grade = Integer.parseInt(gradeRow.getCell(1).getStringCellValue());
                XSSFRow conditionRow = lastSheet.getRow(i+6);
                condition = conditionRow.getCell(0).getStringCellValue();
            }
        }
        task.setCondition(condition);
        task.setMaxGrade(grade);
        return task;
    }

    public static Task importTask(XSSFWorkbook book, int taskNumber) {
        XSSFSheet sheet = book.getSheetAt(taskNumber);
        Task task = new Task();
        task.setTaskNumber(taskNumber);
        ArrayList<ArrayList<String>> data = new ArrayList<>();
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            int j = 0;
            ArrayList<String> dataRow = new ArrayList<>();
            while (row.getCell(j) != null) {
                String rec = null;
                if (row.getCell(j).getCellType() == CellType.STRING) {
                    rec = row.getCell(j).getStringCellValue();
                } else {
                    rec = String.valueOf(row.getCell(j).getNumericCellValue());
                }
                dataRow.add(rec);
                j++;
            }
            data.add(dataRow);
        }
        task.setData(data);
        return task;
    }

}
