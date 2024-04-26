package com.example.simbirsoftmobile.training;

import java.util.LinkedList;

/**
 * Набор тренингов по работе со строками в java.
 * <p>
 * Задания определены в комментариях методов.
 * <p>
 * Проверка может быть осуществлена запуском тестов.
 * <p>
 * Доступна проверка тестированием @see StringsTrainingTest.
 */
public class StringsTraining {

    /**
     * Метод по созданию строки,
     * состоящей из нечетных символов
     * входной строки в том же порядке
     * (нумерация символов идет с нуля)
     *
     * @param text строка для выборки
     * @return новая строка из нечетных
     * элементов строки text
     */
    public String getOddCharacterString(String text) {
        if (text.length() <= 1) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < text.length(); i += 2) {
            builder.append(text.charAt(i));
        }

        return new String(builder);
    }

    /**
     * Метод для определения количества
     * символов, идентичных последнему
     * в данной строке
     *
     * @param text строка для выборки
     * @return массив с номерами символов,
     * идентичных последнему. Если таких нет,
     * вернуть пустой массив
     */
    public int[] getArrayLastSymbol(String text) {
        if (text.isEmpty() || text.length() == 1) {
            return new int[]{};
        }

        char lastChar = text.charAt(text.length() - 1);
        LinkedList<Integer> list = new LinkedList<>();

        for (int i = 0; i < text.length() - 1; i++) {
            if (lastChar == text.charAt(i)) {
                list.add(i);
            }
        }

        return list.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Метод по получению количества
     * цифр в строке
     *
     * @param text строка для выборки
     * @return количество цифр в строке
     */
    public int getNumbersCount(String text) {
        if (text.isEmpty()) {
            return 0;
        }

        int counter = 0;
        for (int i = 0; i < text.length(); i++) {
            if (Character.isDigit(text.charAt(i))) {
                counter++;
            }
        }

        return counter;
    }

    /**
     * Дан текст. Заменить все цифры
     * соответствующими словами.
     *
     * @param text текст для поиска и замены
     * @return текст, где цифры заменены словами
     */
    public String replaceAllNumbers(String text) {
        StringBuilder builder = new StringBuilder();
        char symbol;

        for (int i = 0; i < text.length(); i++) {
            symbol = text.charAt(i);
            switch (symbol) {
                case '1':
                    builder.append("one");
                    break;
                case '2':
                    builder.append("two");
                    break;
                case '3':
                    builder.append("three");
                    break;
                case '4':
                    builder.append("four");
                    break;
                case '5':
                    builder.append("five");
                    break;
                case '6':
                    builder.append("six");
                    break;
                case '7':
                    builder.append("seven");
                    break;
                case '8':
                    builder.append("eight");
                    break;
                case '9':
                    builder.append("nine");
                    break;
                case '0':
                    builder.append("zero");
                    break;
                default:
                    builder.append(symbol);
                    break;
            }
        }

        return new String(builder);
    }

    /**
     * Метод должен заменить заглавные буквы
     * на прописные, а прописные на заглавные
     *
     * @param text строка для изменения
     * @return измененная строка
     */
    public String capitalReverse(String text) {
        StringBuilder builder = new StringBuilder();
        char symbol;

        for (int i = 0; i < text.length(); i++) {
            symbol = text.charAt(i);
            if (Character.isUpperCase(symbol)) {
                builder.append(Character.toLowerCase(symbol));
            } else if (Character.isLowerCase(symbol)) {
                builder.append(Character.toUpperCase(symbol));
            } else {
                builder.append(symbol);
            }
        }

        return new String(builder);
    }
}
