package com.example.basics.async_rx.rx;


import io.reactivex.rxjava3.core.Observable;

/**
 * @author Arthur Korchagin (artur.korchagin@simbirsoft.com)
 * @since 20.11.18
 */
public class RxErrorsTraining {

    /* Тренировочные методы */

    /**
     * В случае ошибки передавать значение по умолчанию
     *
     * @param intObservable {@link Observable} с произвольным количеством рандомных чисел, который
     *                      может передавать ошибку
     * @param defaultValue  значение по умолчанию, передавать в случае, если последовательность
     *                      {@code intObservable} завершилась с ошибкой {@link Throwable}
     * @return {@link Observable} который эммитит значения из {@code intObservable}, либо
     * defaultValue
     */
    public Observable<Integer> handleErrorsWithDefaultValue(Observable<Integer> intObservable, final Integer defaultValue) {
        return intObservable.onErrorReturn(e -> defaultValue);
    }

    /**
     * В случае ошибки переключаться на другую последовательность
     *
     * @param intObservable      {@link Observable} с произвольным количеством рандомных чисел, который
     *                           может передавать ошибку
     * @param fallbackObservable {@link Observable} последовательность, на которую нужно
     *                           переключиться в случае ошибки
     * @return {@link Observable} который эммитит значения из {@code intObservable}, либо
     * {@code fallbackObservable}
     */
    public Observable<Integer> handleErrorsWithFallbackObservable(Observable<Integer> intObservable, Observable<Integer> fallbackObservable) {
        return intObservable.onErrorResumeNext(it -> fallbackObservable);
    }
}
