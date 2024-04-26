package com.example.simbirsoftmobile.classes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Набор заданий по работе с классами в java.
 * <p>
 * Задания подразумевают создание класса(ов), выполняющих задачу.
 * <p>
 * Проверка осуществляется ментором.
 */
public interface ClassesBlock {

    /*
      I

      Создать класс с двумя переменными. Добавить функцию вывода на экран
      и функцию изменения этих переменных. Добавить функцию, которая находит
      сумму значений этих переменных, и функцию, которая находит наибольшее
      значение из этих двух переменных.
     */
    class Pair {
        int first;
        int second;

        public Pair(int first, int second) {
            setFirst(first);
            setSecond(second);
        }

        public int getSum() {
            return first + second;
        }

        public int getMax() {
            return Math.max(getFirst(), getSecond());
        }

        public void print() {
            System.out.printf("(%d; %d)", getFirst(), getSecond());
        }

        public int getFirst() {
            return first;
        }

        public void setFirst(int first) {
            this.first = first;
        }

        public int getSecond() {
            return second;
        }

        public void setSecond(int second) {
            this.second = second;
        }
    }

    /*
      II

      Создать класс, содержащий динамический массив и количество элементов в нем.
      Добавить конструктор, который выделяет память под заданное количество элементов.
      Добавить методы, позволяющие заполнять массив случайными числами,
      переставлять в данном массиве элементы в случайном порядке, находить количество
      различных элементов в массиве, выводить массив на экран.
     */
    class DynamicArray {
        ArrayList<Integer> array;

        public DynamicArray(int size) {
            array = new ArrayList<>(size);
        }

        public int getElementCount() {
            return array.size();
        }

        public void fillRandom() {
            Random random = new Random();
            array.replaceAll(ignored -> random.nextInt(100));
        }

        public void shuffle() {
            Collections.shuffle(array);
        }

        public int getOccurrencesOfValue(int value) {
            return Collections.frequency(array, value);
        }

        public void print() {
            StringBuilder builder = new StringBuilder("[");
            for (int i = 0; i < array.size() - 1; i++) {
                builder.append(array.get(i)).append(", ");
            }
            builder.append(array.get(array.size() - 1)).append("]");
            System.out.println(builder.toString());
        }
    }

    /*
      III

      Описать класс, представляющий треугольник. Предусмотреть методы для создания объектов,
      вычисления площади, периметра и точки пересечения медиан.
      Описать свойства для получения состояния объекта.
     */
    class Point {
        private double x;
        private double y;

        public Point() {
            this.x = 0;
            this.y = 0;
        }

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public void sum(Point point) {
            setX(getX() + point.getX());
            setY(getY() + point.getY());
        }
    }

    class Triangular {
        private Point a;
        private Point b;
        private Point c;

        public Triangular(Point a, Point b, Point c) {
            setA(a);
            setB(b);
            setC(c);
        }

        public Point getA() {
            return a;
        }

        public void setA(Point a) {
            this.a = a;
        }

        public Point getB() {
            return b;
        }

        public void setB(Point b) {
            this.b = b;
        }

        public Point getC() {
            return c;
        }

        public void setC(Point c) {
            this.c = c;
        }

        public double getPerimeter() {
            double first = (b.getX() - a.getX()) * (b.getX() - a.getX())
                    + (b.getY() - a.getY()) * (b.getY() - a.getY());
            double second = (c.getX() - b.getX()) * (c.getX() - b.getX())
                    + (c.getY() - b.getY()) * (c.getY() - b.getY());
            double third = (a.getX() - c.getX()) * (a.getX() - c.getX())
                    + (a.getY() - c.getY()) * (a.getY() - c.getY());

            return first + second + third;
        }

        public double getSquare() {
            double s = a.getX() * (b.getY() - c.getY()) + b.getX() * (c.getY() - a.getY())
                    + c.getX() * (a.getY() - b.getY());
            s /= 2;

            return s;
        }

        public Point getMediansIntersection() {
            Point point = new Point();
            point.setX((a.getX() + b.getX() + c.getX()) / 3);
            point.setY((a.getY() + b.getY() + c.getY()) / 3);

            return point;
        }
    }


    /*
      IV

      Составить описание класса для представления времени.
      Предусмотреть возможности установки времени и изменения его отдельных полей
      (час, минута, секунда) с проверкой допустимости вводимых значений.
      В случае недопустимых значений полей выбрасываются исключения.
      Создать методы изменения времени на заданное количество часов, минут и секунд.
     */
    class Timestamp {
        private int hours;
        private int minutes;
        private int seconds;

        public Timestamp(int hours, int minutes, int seconds) {
            setTime(hours, minutes, seconds);
        }

        public void addHours(int hours) {
            setHours(getHours() + hours);
        }

        public void addMinutes(int minutes) {
            setMinutes(getMinutes() + minutes);
        }

        public void addSeconds(int seconds) {
            setSeconds(getSeconds() + seconds);
        }

        public void setTime(int hours, int minutes, int seconds) {
            setHours(hours);
            setMinutes(minutes);
            setSeconds(seconds);
        }

        public int getHours() {
            return hours;
        }

        public void setHours(int hours) {
            if (hours >= 24 || hours < 0) {
                throw new IllegalArgumentException("Количество часов должно быть в пределе [0;23]");
            }

            this.hours = hours;
        }

        public int getMinutes() {
            return minutes;
        }

        public void setMinutes(int minutes) {
            if (minutes >= 60 || minutes < 0) {
                throw new IllegalArgumentException("Количество минут должно быть в пределе [0;59]");
            }

            this.minutes = minutes;
        }

        public int getSeconds() {
            return seconds;
        }

        public void setSeconds(int seconds) {
            if (seconds >= 60 || seconds < 0) {
                throw new IllegalArgumentException("Количество секунд должно быть в пределе [0;59]");
            }

            this.seconds = seconds;
        }
    }

    /*
      V

      Класс Абонент: Идентификационный номер, Фамилия, Имя, Отчество, Адрес,
      Номер кредитной карточки, Дебет, Кредит, Время междугородных и городских переговоров;
      Конструктор; Методы: установка значений атрибутов, получение значений атрибутов,
      вывод информации. Создать массив объектов данного класса.
      Вывести сведения относительно абонентов, у которых время городских переговоров
      превышает заданное.  Сведения относительно абонентов, которые пользовались
      междугородной связью. Список абонентов в алфавитном порядке.
     */
    class Subscriber {
        private int id;
        private String firstName;
        private String lastName;
        private String patronymic;
        private String address;
        private String creditCardNumber;
        private BigDecimal debit;
        private BigDecimal credit;
        private int longDistanceCallsInSeconds;
        private int localCallsInSeconds;

        public String getFullName() {
            return String.format("%s %s %s", getLastName(), getFirstName(), getPatronymic());
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCreditCardNumber() {
            return creditCardNumber;
        }

        public void setCreditCardNumber(String creditCardNumber) {
            this.creditCardNumber = creditCardNumber;
        }

        public BigDecimal getDebit() {
            return debit;
        }

        public void setDebit(BigDecimal debit) {
            this.debit = debit;
        }

        public BigDecimal getCredit() {
            return credit;
        }

        public void setCredit(BigDecimal credit) {
            this.credit = credit;
        }

        public int getLongDistanceCallsInSeconds() {
            return longDistanceCallsInSeconds;
        }

        public void setLongDistanceCallsInSeconds(int longDistanceCallsInSeconds) {
            this.longDistanceCallsInSeconds = longDistanceCallsInSeconds;
        }

        public int getLocalCallsInSeconds() {
            return localCallsInSeconds;
        }

        public void setLocalCallsInSeconds(int localCallsInSeconds) {
            this.localCallsInSeconds = localCallsInSeconds;
        }
    }


    class SubscriberController {
        /*
         * Получить сведения относительно абонентов,
         * у которых время городских переговоров превышает заданное.
         */
        public static Subscriber[] getSubscribersWithExcessiveCityTalks(Subscriber[] subscribers, int limit) {
            return Arrays.stream(subscribers)
                    .filter(it -> it.localCallsInSeconds > limit)
                    .toArray(Subscriber[]::new);
        }

        /*
         * Получить сведения относительно абонентов,
         * которые пользовались междугородной связью.
         */
        public static Subscriber[] getSubscribersWithLongDistanceCalls(Subscriber[] subscribers) {
            return Arrays.stream(subscribers)
                    .filter(it -> it.longDistanceCallsInSeconds > 0)
                    .toArray(Subscriber[]::new);
        }


        /*
         * Список абонентов в алфавитном порядке
         */
        public static Subscriber[] sortAlphabetically(Subscriber[] subscribers) {
            Comparator<Subscriber> comparator = Comparator.comparing(Subscriber::getLastName)
                    .thenComparing(Subscriber::getFirstName)
                    .thenComparing(Subscriber::getPatronymic);
            return Arrays.stream(subscribers).sorted(comparator).toArray(Subscriber[]::new);
        }
    }

    /*
      VI

      Задача на взаимодействие между классами. Разработать систему «Вступительные экзамены».
      Абитуриент регистрируется на Факультет, сдает Экзамены. Преподаватель выставляет Оценку.
      Система подсчитывает средний бал и определяет Абитуриента, зачисленного в учебное заведение.
     */
    class ApplicantController {
        public boolean isAdmitted(Applicant applicant) {
            List<Subject> subjectList = Arrays.asList(applicant.faculty.examsSubjects);
            List<Exam> passedNecessaryExams = applicant.exams.stream()
                    .filter(it -> subjectList.contains(it.subject))
                    .collect(Collectors.toList());
            List<Subject> passedSubjects = passedNecessaryExams.stream()
                    .map(it -> it.subject)
                    .collect(Collectors.toList());
            if (passedSubjects.containsAll(subjectList)) {
                double sum = 0;
                double average;

                for (Exam exam : passedNecessaryExams) {
                    sum += exam.getGrade().getValue();
                }
                average = sum / passedNecessaryExams.size();

                return average > applicant.faculty.admissionThreshold;
            } else {
                return false;
            }
        }
    }

    class Applicant {
        private String firstName;
        private String lastName;
        private String patronymic;
        private Faculty faculty;
        private ArrayList<Exam> exams;

        public Applicant(String firstName, String lastName, String patronymic, Faculty faculty) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.patronymic = patronymic;
            this.faculty = faculty;
            exams = new ArrayList<>(faculty.examsSubjects.length);
        }

        public void addExam(Exam exam) {
            exams.add(exam);
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

        public Faculty getFaculty() {
            return faculty;
        }

        public void setFaculty(Faculty faculty) {
            this.faculty = faculty;
        }

        public ArrayList<Exam> getExams() {
            return exams;
        }

        public void setExams(ArrayList<Exam> exams) {
            this.exams = exams;
        }
    }

    class Faculty {
        private String name;
        private Subject[] examsSubjects;
        private double admissionThreshold;
    }

    class Exam {
        private Subject subject;
        private Grade grade;
        private Teacher examiner;

        public Teacher getExaminer() {
            return examiner;
        }

        public void setExaminer(Teacher examiner) {
            this.examiner = examiner;
        }

        public Exam(Subject subject, Grade grade, Teacher examiner) {
            this.subject = subject;
            this.grade = grade;
            this.examiner = examiner;
        }

        public Subject getSubject() {
            return subject;
        }

        public Grade getGrade() {
            return grade;
        }

        public void setGrade(Grade grade) {
            this.grade = grade;
        }
    }

    class Grade {
        private int value;

        public Grade(int value) {
            setValue(value);
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            if (value < 2 || value > 5) {
                throw new IllegalArgumentException("Оценкой является число на отрезке [2;5]");
            }
            this.value = value;
        }
    }

    enum Subject {
        MATH,
        PE,
        ENGLISH,
        COMPUTER_SCIENCE;
    }

    class Teacher {
        private String firstName;
        private String lastName;
        private String patronymic;
    }

    /*
      VII

      Задача на взаимодействие между классами. Разработать систему «Интернет-магазин».
      Товаровед добавляет информацию о Товаре. Клиент делает и оплачивает Заказ на Товары.
      Товаровед регистрирует Продажу и может занести неплательщика в «черный список».
     */
    class Client {
        private int id;
        private String name;

        public Client(int id, String name) {
            setName(name);
            setId(id);
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    class Merchandiser {
        private int id;
        private String name;

        public Merchandiser(int id, String name) {
            setId(id);
            setName(name);
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    class Product {
        private int id;
        private String name;
        private BigDecimal cost;
        private int count;
        private Merchandiser merchandiser;

        public Product(int id, String name, BigDecimal cost, int count, Merchandiser merchandiser) {
            setId(id);
            setName(name);
            setCost(cost);
            setCount(count);
            setMerchandiser(merchandiser);
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public BigDecimal getCost() {
            return cost;
        }

        public void setCost(BigDecimal cost) {
            this.cost = cost;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public Merchandiser getMerchandiser() {
            return merchandiser;
        }

        public void setMerchandiser(Merchandiser merchandiser) {
            this.merchandiser = merchandiser;
        }
    }


    class Order {
        private int id;
        private LinkedList<OrderPosition> orderPositions;
        private Client client;

        public Order(int id, LinkedList<OrderPosition> orderPositions, Client client) {
            Set<Integer> productIdSet = new HashSet<>();
            for (OrderPosition orderPosition : orderPositions) {
                int productId = orderPosition.getProduct().getId();
                if (!productIdSet.add(productId)) {
                    throw new IllegalArgumentException("В заказе присутствуют " +
                            "две разные позиции одного товара");
                }
            }

            setId(id);
            setOrderPositions(orderPositions);
            this.client = client;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public LinkedList<OrderPosition> getOrderPositions() {
            return orderPositions;
        }

        public void setOrderPositions(LinkedList<OrderPosition> orderPositions) {
            this.orderPositions = orderPositions;
        }

        public Client getClient() {
            return client;
        }
    }

    class OrderPosition {
        private Product product;
        private int count;

        public OrderPosition(Product product, int count) {
            setProduct(product);
            setCount(count);
        }

        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
            this.product = product;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    class Sale {
        private Order order;
        private Merchandiser merchandiser;

        public Sale(Order order, Merchandiser merchandiser) {
            this.order = order;
            this.merchandiser = merchandiser;
        }
    }


    /*
     * Класс магазина, позволяющий управлять отдельными списками объектов
     * Сигнатуры методов разрабатывались с учётом возможного преобразования в http-запросы в будущем
     * (наличие товароведов)
     */
    class ShopController {
        private LinkedList<Client> blackListed = new LinkedList<>();
        private LinkedList<Order> currentOrders = new LinkedList<>();
        private LinkedList<Sale> sales = new LinkedList<>();
        private LinkedList<Product> products = new LinkedList<>();

        public void addToBlackList(Merchandiser merchandiser, Client client) {
            blackListed.add(client);

            Order[] orders = currentOrders.stream()
                    .filter(it -> it.client.id == client.id)
                    .toArray(Order[]::new);

            for (Order order : orders) {
                cancelOrder(order);
            }
        }

        public void cancelOrder(Order order) {
            List<OrderPosition> positions = order.getOrderPositions();
            for (OrderPosition position : positions) {
                increaseProductCount(position.getProduct(), position.getCount());
            }

            currentOrders.removeIf(it -> it.id == order.id);
        }

        public void addSale(Merchandiser merchandiser, Order order) {
            sales.add(new Sale(order, merchandiser));
            currentOrders.removeIf(it -> it.id == order.id);
        }

        public void addOrder(Order order) {
            int i = 0;
            List<OrderPosition> positions = order.getOrderPositions();

            while (i < order.orderPositions.size()
                    && checkProductEnough(positions.get(i).getProduct(), positions.get(i).getCount())) {
                i++;
            }

            if (i < order.orderPositions.size()) {
                throw new IllegalArgumentException("В магазине недостаточно товара "
                        + positions.get(i).getProduct().getName());
            }

            currentOrders.add(order);

            for (OrderPosition position : positions) {
                reduceProductCount(position.getProduct(), position.getCount());
            }
        }

        private boolean checkProductEnough(Product product, int count) {
            for (Product p : products) {
                if (p.getId() == product.getId()) {
                    return p.getCount() >= count;
                }
            }

            throw new IllegalArgumentException("Товар " + product.getName() + " не найден");
        }

        private void reduceProductCount(Product product, int count) {
            for (Product p : products) {
                if (p.getId() == product.getId()) {
                    p.setCount(p.getCount() - count);
                }
            }
        }

        private void increaseProductCount(Product product, int count) {
            for (Product p : products) {
                if (p.getId() == product.getId()) {
                    p.setCount(p.getCount() + count);
                }
            }
        }
    }
}
