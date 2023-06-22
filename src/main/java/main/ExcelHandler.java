package main;

import main.models.Group;
import main.models.Student;
import main.models.Task;
import main.models.Variant;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ExcelHandler {

    static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    // метод для импорта инфы из файлика со студентами
    public static ArrayList<Group> importStudents(File file) throws IOException, InvalidFormatException {
        //создание объекта книги
        XSSFWorkbook book = new XSSFWorkbook(file);
        ArrayList<Group> groups = new ArrayList<>();
        //цикл по обработке страниц файла
        for (int i = 0; i<book.getNumberOfSheets(); i++){
            XSSFSheet sheet = book.getSheetAt(i);
            Group group = new Group();
            ArrayList<Student> students = new ArrayList<>();
            //цикл по обработке строк страницы
            for (int j = 1; j<=sheet.getLastRowNum(); j++) {
                Student student = new Student();
                //строка эксель файла
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

    //импорт заданий из файлов с вариантами
    public static Variant importVar(String fileName) throws IOException {
        //объект книги, считываем эксель
        XSSFWorkbook book = new XSSFWorkbook(fileName);
        Variant variant = new Variant();
        ArrayList<Task> tasks = new ArrayList<>();
        //цикл по обработке страниц файла
        for (int i = 0; i<book.getNumberOfSheets()-1; i++){
            //импорт задания
            Task task = importTask(book, i);
            XSSFSheet lastSheet = book.getSheetAt(book.getNumberOfSheets()-1);
            //импорт условия
            getCondition(lastSheet, task);
            tasks.add(task);
        }
        variant.setTasks(tasks);
        return variant;
    }

    //записывает условия к заданиям из файла эксель
    public static void getCondition(XSSFSheet lastSheet, Task task){
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
    }

    //обрабатывает данные и записывает из экселя в объект
    public static Task importTask(XSSFWorkbook book, int taskNumber) {
        XSSFSheet sheet = book.getSheetAt(taskNumber);
        Task task = new Task();
        task.setTaskNumber(taskNumber);
        ArrayList<ArrayList<String>> data = new ArrayList<>();
        //цикл по обработке всех строк
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            int j = 0;
            ArrayList<String> dataRow = new ArrayList<>();
            //проверка на тип данных в ячейке и считывание данных и запись в объект
            while (row.getCell(j) != null) {
                String rec = null;
                if (row.getCell(j).getCellType() == CellType.STRING) {
                    rec = row.getCell(j).getStringCellValue();
                } else if (DateUtil.isCellDateFormatted(row.getCell(j))){
                    rec = dateFormat.format(row.getCell(j).getDateCellValue());
                } else if (row.getCell(j).getCellType() == CellType.NUMERIC){
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