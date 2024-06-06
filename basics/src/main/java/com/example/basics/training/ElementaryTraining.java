package com.example.basics.training;

/**
 * Набор тренингов по работе с примитивными типами java.
 * <p>
 * Задания определены в комментариях методов.
 * <p>
 * Проверка может быть осуществлена запуском тестов.
 * <p>
 * Доступна проверка тестированием @see ElementaryTrainingTest.
 */
public class ElementaryTraining {

    /**
     * Метод должен возвращать среднее значение
     * для введенных параметров
     *
     * @param firstValue  первый элемент
     * @param secondValue второй элемент
     * @return среднее значение для введенных чисел
     */
    public double averageValue(int firstValue, int secondValue) {
        return (double) (firstValue + secondValue) / 2;
    }

    /**
     * Пользователь вводит три числа.
     * Произвести манипуляции и вернуть сумму новых чисел
     *
     * @param firstValue  увеличить в два раза
     * @param secondValue уменьшить на три
     * @param thirdValue  возвести в квадрат
     * @return сумма новых трех чисел
     */
    public double complicatedAmount(int firstValue, int secondValue, int thirdValue) {
        return firstValue * 2 + (secondValue - 3) + thirdValue * thirdValue;
    }

    /**
     * Метод должен поменять значение в соответствии с условием.
     * Если значение больше 3, то увеличить
     * на 10, иначе уменьшить на 10.
     *
     * @param value число для изменения
     * @return новое значение
     */
    public int changeValue(int value) {
        int changedValue = value;

        if (changedValue > 3) {
            changedValue += 10;
        } else {
            changedValue -= 10;
        }

        return changedValue;
    }

    /**
     * Метод должен менять местами первую
     * и последнюю цифру числа.
     * Обрабатывать максимум пятизначное число.
     * Если число < 10, вернуть
     * то же число
     *
     * @param value число для перестановки
     * @return новое число
     */
    public int swapNumbers(int value) {
        if ((value / 100000 != 0)
                || value < 10) {
            return value;
        }

        int sign = Integer.signum(value);
        int lastNumber = value % 10;
        int firstNumbers = value / 10;  // число без последней цифры
        int sum = 0;
        int currentNumber;

        for (int i = 1; firstNumbers > 10; firstNumbers /= 10, lastNumber *= 10, i *= 10) {
            currentNumber = firstNumbers % 10;
            sum += i * currentNumber;
        }

        return sign * (10 * (lastNumber + sum) + firstNumbers);
    }

    /**
     * Изменить значение четных цифр числа на ноль.
     * Счет начинать с единицы.
     * Обрабатывать максимум пятизначное число.
     * Если число < 10 вернуть
     * то же число.
     *
     * @param value число для изменения
     * @return новое число
     */
    public int zeroEvenNumber(int value) {
        if ((value / 100_000 != 0) || value < 10) {
            return value;
        }

        int newValue = 0;
        int currentNumber;
        int digitValue = 1;

        for (int position = 1; value > 0; value /= 10, position += 1, digitValue *= 10) {
            if (position % 2 != 0) {
                currentNumber = value % 10;
                newValue += digitValue * (currentNumber);
            }
        }

        return newValue;
    }
}
