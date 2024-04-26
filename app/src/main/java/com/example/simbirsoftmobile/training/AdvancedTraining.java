package com.example.simbirsoftmobile.training;

import java.util.LinkedList;

/**
 * Практическое задание по Блоку III. Java. Часть 2
 */
public class AdvancedTraining {

    /**
     * Задание 3.
     */
    public void task3() {
        Runnable runnable = () -> System.out.println("I love Java");

        System.out.println("Единоразовый запуск лямбды");
        runnable.run();

        System.out.println("Запуск лямбды заданное количество раз");
        repeatTask(10, runnable);
    }

    /**
     * Функция, запускающая лямбда выражение несколько раз
     */
    public void repeatTask(int times, Runnable task) {
        for (int i = 0; i < times; i++) {
            task.run();
        }
    }

    /**
     * Функциональный интерфейс задания 3,
     * предназначенный для работы с лямбда-выражениями
     */
    interface Runnable {
        void run();
    }

    /**
     * Задание 4
     */
    public void task4() {
        Position location = new Position(0, 0);
        System.out.println("Первоначальная позиция: " + location);

        Direction[] route = new Direction[]{Direction.UP, Direction.UP, Direction.LEFT,
                Direction.DOWN, Direction.LEFT, Direction.DOWN,
                Direction.DOWN, Direction.RIGHT, Direction.RIGHT,
                Direction.DOWN, Direction.RIGHT};

        for (Direction direction : route) {
            location = moveByStep(direction, location);
            System.out.println(location);
        }
    }

    enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT,
    }

    class Position {
        private int x;
        private int y;

        public Position(int x, int y) {
            setX(x);
            setY(y);
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        @Override
        public String toString() {
            return String.format("(%d;%d)", getX(), getY());
        }
    }

    public Position moveByStep(Direction direction, Position position) {
        switch (direction) {
            case UP:
                return new Position(position.getX(), position.getY() + 1);
            case DOWN:
                return new Position(position.getX(), position.getY() - 1);
            case LEFT:
                return new Position(position.getX() - 1, position.getY());
            case RIGHT:
                return new Position(position.getX() + 1, position.getY());
            default:
                return new Position(position.getX(), position.getY());
        }
    }

    /**
     * Задание 5.
     */
    public void task5() {
        LinkedList<Shape> shapes = new LinkedList<Shape>();
        shapes.add(new Rectangle(2, 3));
        shapes.add(new Square(3));
        shapes.add(new Circle(2));

        for (Shape shape : shapes) {
            System.out.println(shape.getClass().getSimpleName());
            System.out.printf("Периметр: %.3f\n", shape.perimeter());
            System.out.printf("Площадь: %.3f\n\n", shape.area());
        }
    }

    /**
     * Интерфейс фигуры, предоставляющий методы получения периметра и площади
     */
    interface Shape {
        double perimeter();

        double area();
    }

    class Rectangle implements Shape {
        private double width;
        private double length;

        public Rectangle(double width, double length) {
            setWidth(width);
            setLength(length);
        }

        @Override
        public double perimeter() {
            return getWidth() * 2 + getLength() * 2;
        }

        @Override
        public double area() {
            return getWidth() * getLength();
        }

        public double getWidth() {
            return width;
        }

        public void setWidth(double width) {
            this.width = width;
        }

        public double getLength() {
            return length;
        }

        public void setLength(double length) {
            this.length = length;
        }
    }

    class Square implements Shape {
        private double side;

        public Square(double side) {
            setSide(side);
        }

        @Override
        public double perimeter() {
            return getSide() * 4;
        }

        @Override
        public double area() {
            return getSide() * getSide();
        }

        public double getSide() {
            return side;
        }

        public void setSide(double side) {
            this.side = side;
        }
    }

    class Circle implements Shape {
        private double diameter;

        public Circle(double diameter) {
            setDiameter(diameter);
        }

        @Override
        public double perimeter() {
            return Math.PI * getDiameter();
        }

        @Override
        public double area() {
            return Math.PI * getDiameter() * getDiameter() / 4;
        }

        public double getDiameter() {
            return diameter;
        }

        public void setDiameter(double diameter) {
            this.diameter = diameter;
        }
    }
}

class Program {
    public static void main(String[] args) {
        new AdvancedTraining().task5();
    }
}
