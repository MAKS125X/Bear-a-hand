package com.example.simbirsoftmobile.collections;


import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

/**
 * Набор тренингов по работе со строками в java.
 * <p>
 * Задания определены в комментариях методов.
 * <p>
 * Проверка может быть осуществлена запуском тестов.
 * <p>
 * Доступна проверка тестированием @see CollectionsBlockTest.
 */
public class CollectionsBlock<T extends Comparable> {

    /**
     * Даны два упорядоченных по убыванию списка.
     * Объедините их в новый упорядоченный по убыванию список.
     * Исходные данные не проверяются на упорядоченность в рамках данного задания
     *
     * @param firstList  первый упорядоченный по убыванию список
     * @param secondList второй упорядоченный по убыванию список
     * @return объединенный упорядоченный список
     * @throws NullPointerException если один из параметров null
     */
    public List<T> collectionTask0(@NonNull List<T> firstList, @NonNull List<T> secondList) {
        if (firstList == null || secondList == null) {
            throw new NullPointerException("Параметры не могут быть null");
        }

        int firstCounter = 0;
        int secondCounter = 0;
        int compareResult;
        LinkedList<T> thirdList = new LinkedList<>();

        while (firstCounter < firstList.size() && secondCounter < secondList.size()) {
            compareResult = firstList.get(firstCounter).compareTo(secondList.get(secondCounter));

            if (compareResult >= 0) {
                thirdList.add(firstList.get(firstCounter));
                firstCounter++;
            } else {
                thirdList.add(secondList.get(secondCounter));
                secondCounter++;
            }
        }

        if (firstCounter < firstList.size()) {
            thirdList.addAll(firstList.subList(firstCounter, firstList.size()));
        }
        if (secondCounter < secondList.size()) {
            thirdList.addAll(secondList.subList(secondCounter, secondList.size()));
        }

        return thirdList;
    }

    /**
     * Дан список. После каждого элемента добавьте предшествующую ему часть списка.
     *
     * @param inputList с исходными данными
     * @return измененный список
     * @throws NullPointerException если один из параметров null
     */
    public List<T> collectionTask1(@NonNull List<T> inputList) {
        if (inputList == null) {
            throw new NullPointerException("Список не может быть null");
        }

        LinkedList<T> result = new LinkedList<>();

        for (int i = 0; i < inputList.size(); i++) {
            result.add(inputList.get(i));
            result.addAll(inputList.subList(0, i));
        }

        return result;
    }

    /**
     * Даны два списка. Определите, совпадают ли множества их элементов.
     *
     * @param firstList  первый список элементов
     * @param secondList второй список элементов
     * @return <tt>true</tt> если множества списков совпадают
     * @throws NullPointerException если один из параметров null
     */
    public boolean collectionTask2(@NonNull List<T> firstList, @NonNull List<T> secondList) {
        if (firstList == null || secondList == null) {
            throw new NullPointerException("Параметры не могут быть null");
        }

        Set<T> firstSet = new HashSet<>(firstList);
        Set<T> secondSet = new HashSet<>(secondList);

        return firstSet.equals(secondSet);
    }

    /**
     * Создать список из заданного количества элементов.
     * Выполнить циклический сдвиг этого списка на N элементов вправо или влево.
     * Если N > 0 циклический сдвиг вправо.
     * Если N < 0 циклический сдвиг влево.
     *
     * @param inputList список, для которого выполняется циклический сдвиг влево
     * @param n         количество шагов циклического сдвига N
     * @return список inputList после циклического сдвига
     * @throws NullPointerException если один из параметров null
     */
    public List<T> collectionTask3(@NonNull List<T> inputList, int n) {
        if (inputList == null) {
            throw new NullPointerException("Список не может быть null");
        }
        if (inputList.isEmpty()) {
            return inputList;
        }

        List<T> list = new ArrayList<>(inputList);
        Collections.rotate(list, n);

        return list;
    }

    /**
     * Элементы списка хранят слова предложения.
     * Замените каждое вхождение слова A на B.
     *
     * @param inputList список со словами предложения и пробелами для разделения слов
     * @param a         слово, которое нужно заменить
     * @param b         слово, на которое нужно заменить
     * @return список после замены каждого вхождения слова A на слово В
     * @throws NullPointerException если один из параметров null
     */
    public List<String> collectionTask4(@NonNull List<String> inputList, @NonNull String a,
                                        @NonNull String b) {
        if (inputList == null || a == null || b == null) {
            throw new NullPointerException("Параметры не могут быть null");
        }

        ArrayList<String> list = new ArrayList<>(inputList);
        for (int i = 0; i < list.size(); i++) {
            if (Objects.equals(list.get(i), a)) {
                list.set(i, b);
            }
        }

        return list;
    }

    /*
      Задание подразумевает создание класса(ов) для выполнения задачи.

      Дан список студентов. Элемент списка содержит фамилию, имя, отчество, год рождения,
      курс, номер группы, оценки по пяти предметам. Заполните список и выполните задание.
      Упорядочите студентов по курсу, причем студенты одного курса располагались
      в алфавитном порядке. Найдите средний балл каждой группы по каждому предмету.
      Определите самого старшего студента и самого младшего студентов.
      Для каждой группы найдите лучшего с точки зрения успеваемости студента.
     */
    public class Student {
        String firstName;
        String lastName;
        String patronymic;
        int birthYear;
        int course;
        String groupName;
        Grade[] grades;

        public Student(String lastName, String firstName, String patronymic, int birthYear,
                       int course, String groupName, Grade[] grades) {
            if (grades.length != 5) {
                throw new IllegalArgumentException("У студента должно быть 5 оценок");
            }

            setFirstName(firstName);
            setLastName(lastName);
            setPatronymic(patronymic);
            setBirthYear(birthYear);
            setCourse(course);
            setGroupName(groupName);
            setGrades(grades);
        }

        public String getFullName() {
            return String.format("%s %s %s", getLastName(), getFirstName(), getPatronymic());
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getPatronymic() {
            return patronymic;
        }

        public void setPatronymic(String patronymic) {
            this.patronymic = patronymic;
        }

        public int getBirthYear() {
            return birthYear;
        }

        public void setBirthYear(int birthYear) {
            this.birthYear = birthYear;
        }

        public int getCourse() {
            return course;
        }

        public void setCourse(int course) {
            this.course = course;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public Grade[] getGrades() {
            return grades;
        }

        public void setGrades(Grade[] grades) {
            this.grades = grades;
        }
    }

    public class Grade {
        Subject subject;
        int value;

        public Grade(Subject subject, int value) {
            this.subject = subject;
            this.value = value;
        }

        public Subject getSubject() {
            return subject;
        }

        public void setSubject(Subject subject) {
            this.subject = subject;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public class Subject {
        String name;

        public Subject(String name) {
            setName(name);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * Упорядочите студентов по курсу,
     * причем студенты одного курса располагались в алфавитном порядке
     *
     * @param inputList список студентов
     * @throws NullPointerException если список null
     */
    public void sortByGroupAndNames(@NonNull List<Student> inputList) {
        if (inputList == null) {
            throw new NullPointerException("Параметры не могут быть null");
        }

        Comparator<Student> comparator = Comparator.comparing(Student::getCourse)
                .thenComparing(Student::getLastName)
                .thenComparing(Student::getFirstName)
                .thenComparing(Student::getPatronymic);
        inputList.sort(comparator);
    }

    /**
     * Найти средний балл каждой группы по каждому предмету.
     *
     * @param inputList список студентов
     * @return словарь, в котором ключом является группа, а значением другой словарь,
     * хранящий пары предмета и средней оценки по нему
     * @throws NullPointerException если список null
     */
    public Map<String, Map<String, Double>> getAverageGradesByGroupAndSubject(List<CollectionsBlock.Student> inputList) {
        if (inputList == null) {
            throw new NullPointerException("Параметры не могут быть null");
        }

        Map<String, Map<String, Double>> averageGradesByGroupAndSubject = new HashMap<>();

        for (Student student : inputList) {
            String groupName = student.getGroupName();

            Map<String, Double> subjectGradeMap = averageGradesByGroupAndSubject
                    .computeIfAbsent(groupName, k -> new HashMap<>());

            for (Grade grade : student.getGrades()) {
                String subjectName = grade.getSubject().getName();
                int gradeValue = grade.getValue();

                subjectGradeMap.compute(subjectName, (k, v) -> (v == null)
                        ? grade.getValue()
                        : v + grade.getValue()
                );
            }
        }

        for (Map.Entry<String, Map<String, Double>> entry : averageGradesByGroupAndSubject.entrySet()) {
            int groupSize = (int) inputList.stream()
                    .filter(student -> student.getGroupName().equals(entry.getKey()))
                    .count();

            for (Map.Entry<String, Double> innerEntry : entry.getValue().entrySet()) {
                innerEntry.setValue(innerEntry.getValue() / groupSize);
            }
        }

        return averageGradesByGroupAndSubject;
    }

    /**
     * Найти самого старшего студента.
     *
     * @param inputList список студентов
     * @throws NullPointerException   если список null
     * @throws NoSuchElementException если один из параметров null
     */
    public Student findOldestStudent(@NonNull List<CollectionsBlock.Student> inputList) {
        if (inputList == null) {
            throw new NullPointerException();
        }
        if (inputList.isEmpty()) {
            throw new NoSuchElementException();
        }

        Student oldestStudent = inputList.get(0);

        for (Student student : inputList) {
            if (student.getBirthYear() < oldestStudent.getBirthYear()) {
                oldestStudent = student;
            }
        }

        return oldestStudent;
    }

    /**
     * Найти самого младшего студента.
     *
     * @param inputList список студентов
     * @throws NullPointerException   если список null
     * @throws NoSuchElementException если один из параметров null
     */
    public Student findYoungestStudent(@NonNull List<CollectionsBlock.Student> inputList) {
        if (inputList == null) {
            throw new NullPointerException();
        }
        if (inputList.isEmpty()) {
            throw new NoSuchElementException();
        }

        Student youngestStudent = inputList.get(0);

        for (Student student : inputList) {
            if (student.getBirthYear() > youngestStudent.getBirthYear()) {
                youngestStudent = student;
            }
        }

        return youngestStudent;
    }

    /**
     * Найти лучшего с точки зрения успеваемости студента для каждой группы
     *
     * @param inputList список студентов
     * @throws NullPointerException   если список null
     * @throws NoSuchElementException если один из параметров null
     */
    public Map<String, CollectionsBlock.Student> findBestStudentsByGroup(List<CollectionsBlock.Student> inputList) {
        if (inputList == null) {
            throw new NullPointerException();
        }
        if (inputList.isEmpty()) {
            throw new NoSuchElementException();
        }

        Map<String, CollectionsBlock.Student> bestStudentsByGroup = new HashMap<>();

        for (Student student : inputList) {
            String groupName = student.getGroupName();

            Student bestStudent = bestStudentsByGroup.getOrDefault(groupName, null);

            if (bestStudent == null || calculateAverageGrade(student) > calculateAverageGrade(bestStudent)) {
                bestStudentsByGroup.put(groupName, student);
            }
        }

        return bestStudentsByGroup;
    }

    private double calculateAverageGrade(Student student) {
        double sum = 0.0;
        for (Grade grade : student.getGrades()) {
            sum += grade.getValue();
        }

        return sum / student.getGrades().length;
    }
}

class StudentsProgram {
    public static void main(String[] args) {
        CollectionsBlock<Integer> col = new CollectionsBlock<Integer>();
        ArrayList<CollectionsBlock.Student> students = new ArrayList<>();
        CollectionsBlock.Grade[] grades1 =
                new CollectionsBlock.Grade[]{
                        col.new Grade(col.new Subject("Математика"), 5),
                        col.new Grade(col.new Subject("Информатика"), 5),
                        col.new Grade(col.new Subject("Русский язык"), 4),
                        col.new Grade(col.new Subject("Физика"), 5),
                        col.new Grade(col.new Subject("Биология"), 4)
                };
        CollectionsBlock.Grade[] grades2 =
                new CollectionsBlock.Grade[]{
                        col.new Grade(col.new Subject("Математика"), 3),
                        col.new Grade(col.new Subject("Информатика"), 3),
                        col.new Grade(col.new Subject("Русский язык"), 3),
                        col.new Grade(col.new Subject("Физика"), 3),
                        col.new Grade(col.new Subject("Биология"), 3)
                };
        CollectionsBlock.Grade[] grades3 =
                new CollectionsBlock.Grade[]{
                        col.new Grade(col.new Subject("Математика"), 4),
                        col.new Grade(col.new Subject("Информатика"), 3),
                        col.new Grade(col.new Subject("Русский язык"), 4),
                        col.new Grade(col.new Subject("Физика"), 3),
                        col.new Grade(col.new Subject("Биология"), 4)
                };
        students.add(col.new Student("Иванов", "Иван", "Иванович",
                2004, 2, "614", grades1));
        students.add(col.new Student("Петрова", "Мария", "Александровна",
                2005, 2, "614", grades2));
        students.add(col.new Student("Борисов", "Валерий", "Максимович",
                2003, 2, "614", grades3));
        students.add(col.new Student("Игнатьева", "Дарья", "Олеговна",
                2003, 2, "613", grades1.clone()));

        Map<String, Map<String, Double>> gradesByGroupAndSubject =
                col.getAverageGradesByGroupAndSubject(students);
        System.out.println("Средние оценки по группам и предметам: ");
        for (Map.Entry<String, Map<String, Double>> entry : gradesByGroupAndSubject.entrySet()) {
            System.out.println("Группа:" + entry.getKey());
            for (Map.Entry<String, Double> innerEntry : entry.getValue().entrySet()) {
                System.out.println(innerEntry.getKey() + ": " + innerEntry.getValue());
            }
            System.out.println();
        }

        System.out.println("Самый младший студент: ");
        System.out.println(col.findYoungestStudent(students).getFullName());
        System.out.println();

        System.out.println("Самый старший студент: ");
        col.findOldestStudent(students).getFullName();
        System.out.println();

        System.out.println("Лучший студент: ");
        Map<String, CollectionsBlock.Student> bestStudentsByGroup =
                col.findBestStudentsByGroup(students);
        for (Map.Entry<String, CollectionsBlock.Student> entry : bestStudentsByGroup.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue().getFullName());
        }
    }
}