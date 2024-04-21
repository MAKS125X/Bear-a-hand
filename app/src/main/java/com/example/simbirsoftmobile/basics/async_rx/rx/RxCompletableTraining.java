package com.example.simbirsoftmobile.basics.async_rx.rx;


import com.example.simbirsoftmobile.basics.async_rx.exceptions.ExpectedException;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

/**
 * @author Arthur Korchagin (artur.korchagin@simbirsoft.com)
 * @since 20.11.18
 */
public class RxCompletableTraining {

    /* Тренировочные методы */

    /**
     * Выполнение метода {@link #havyMethod()} внутри {@link Completable} и вызов {@code onComplete}
     *
     * @return {@link Completable}, который вызывает {@link #havyMethod()}
     */
    public Completable callFunction() {
        return Completable.create(completableEmitter -> {
            havyMethod();
            completableEmitter.onComplete();
        });
    }

    /**
     * Завершить последовательность, если {@code checkSingle} эммитит {@code true} или эммитит
     * ошибку, если {@code checkSingle} эммитит {@code false}
     *
     * @param checkSingle @{link Single} который эммитит {@code true} или {@code false}
     * @return {@code Completable}
     */
    public Completable completeWhenTrue(Single<Boolean> checkSingle) {
        return checkSingle.flatMapCompletable(value -> {
            if (value) {
                return Completable.complete();
            } else {
                return Completable.error(ExpectedException::new);
            }
        });
    }

    /* Вспомогательные методы */

    /**
     * Тяжёлый метод
     * (Вспомогательный метод! Не изменять!)
     */
    void havyMethod() {
        // Выполнение операций
    }

}
