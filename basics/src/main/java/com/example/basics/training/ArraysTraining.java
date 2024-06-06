package com.example.basics.training;

/**
 * Набор тренингов по работе с массивами в java.
 * <p>
 * Задания определены в комментариях методов.
 * <p>
 * Проверка может быть осуществлена запуском тестов.
 * <p>
 * Доступна проверка тестированием @see ArraysTrainingTest.
 */
public class ArraysTraining {

    /**
     * Метод должен сортировать входящий массив
     * по возрастранию пузырьковым методом
     *
     * @param valuesArray массив для сортировки
     * @return отсортированный массив
     */
    public int[] sort(int[] valuesArray) {
        int buffer;
        for (int i = 0; i < valuesArray.length; i++) {
            for (int j = 0; j < valuesArray.length - 1; j++) {
                if (valuesArray[j] > valuesArray[j + 1]) {
                    buffer = valuesArray[j];
                    valuesArray[j] = valuesArray[j + 1];
                    valuesArray[j + 1] = buffer;
                }
            }
        }

        return valuesArray;
    }

    /**
     * Метод должен возвращать максимальное
     * значение из введенных. Если входящие числа
     * отсутствуют - вернуть 0
     *
     * @param values входящие числа
     * @return максимальное число или 0
     */
    public int maxValue(int... values) {
        if (values.length == 0) {
            return 0;
        }

        int max = Integer.MIN_VALUE;
        for (int value : values) {
            if (value > max) {
                max = value;
            }
        }

        return max;
    }

    /**
     * Переставить элементы массива
     * в обратном порядке
     *
     * @param array массив для преобразования
     * @return входящий массив в обратном порядке
     */
    public int[] reverse(int[] array) {
        int buffer;
        int reversedIndex;

        for (int i = 0; i < array.length / 2; i++) {
            reversedIndex = array.length - 1 - i;
            buffer = array[i];
            array[i] = array[reversedIndex];
            array[reversedIndex] = buffer;
        }

        return array;
    }

    /**
     * Метод должен вернуть массив,
     * состоящий из чисел Фибоначчи
     *
     * @param numbersCount количество чисел Фибоначчи,
     *                     требуемое в исходящем массиве.
     *                     Если numbersCount < 1, исходный
     *                     массив должен быть пуст.
     * @return массив из чисел Фибоначчи
     */
    public int[] fibonacciNumbers(int numbersCount) {
        if (numbersCount < 1) {
            return new int[]{};
        }
        if (numbersCount == 1) {
            return new int[]{1};
        }

        int[] array = new int[numbersCount];
        array[0] = 1;
        array[1] = 1;

        for (int i = 2; i < numbersCount; i++) {
            array[i] = array[i - 1] + array[i - 2];
        }

        return array;
    }

    /**
     * В данном массиве найти максимальное
     * количество одинаковых элементов.
     *
     * @param array массив для выборки
     * @return количество максимально встречающихся
     * элементов
     */
    public int maxCountSymbol(int[] array) {
        if (array.length == 0) {
            return 0;
        }
        if (array.length == 1) {
            return 1;
        }

        int maxValue = 1;
        int counter = 1;
        int[] sortedArray = sort(array);

        for (int i = 1; i < sortedArray.length; i++) {
            if (sortedArray[i] == sortedArray[i - 1]) {
                counter++;
            } else {
                if (counter > maxValue) {
                    maxValue = counter;
                }
                counter = 1;
            }
        }

        return maxValue;
    }
}
