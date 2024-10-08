package com.example.basics.rx;

import static org.mockito.Mockito.reset;

import com.example.basics.async_rx.exceptions.ExpectedException;
import com.example.basics.async_rx.rx.RxErrorsTraining;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.observers.TestObserver;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.TestScheduler;


/**
 * @author Arthur Korchagin (artur.korchagin@simbirsoft.com)
 * @since 20.11.18
 */
public class RxErrorsTrainingTest {

    private RxErrorsTraining mRxCreatingTraining = Mockito.spy(new RxErrorsTraining());
    private TestScheduler mTestScheduler;

    @Before
    public void setUp() {
        reset(mRxCreatingTraining);
        mTestScheduler = new TestScheduler();
        RxJavaPlugins.setComputationSchedulerHandler(new Function<Scheduler, Scheduler>() {
            @Override
            public Scheduler apply(Scheduler scheduler) {
                return mTestScheduler;
            }
        });
    }

    @Test
    public void handleErrorsWithDefaultValue_withoutError() {
        TestObserver<Integer> testObserver = mRxCreatingTraining
                .handleErrorsWithDefaultValue(Observable.fromArray(1, 2, 3), 4)
                .test();
        testObserver.assertValues(1, 2, 3);
        testObserver.assertComplete();
        testObserver.assertNoErrors();
    }

    @Test
    public void handleErrorsWithDefaultValue_withError() {

        Observable<Integer> errorObservable = Observable.concat(
                Observable.fromArray(1, 2, 3),
                Observable.<Integer>error(new ExpectedException()));

        TestObserver<Integer> testObserver = mRxCreatingTraining
                .handleErrorsWithDefaultValue(errorObservable, 4)
                .test();
        testObserver.assertValues(1, 2, 3, 4);
        testObserver.assertComplete();
        testObserver.assertNoErrors();
    }

    @Test
    public void handleErrorsWithFallbackObservable_withoutError() {
        TestObserver<Integer> testObserver = mRxCreatingTraining
                .handleErrorsWithFallbackObservable(Observable.fromArray(1, 2, 3), Observable.fromArray(4))
                .test();
        testObserver.assertValues(1, 2, 3);
        testObserver.assertComplete();
        testObserver.assertNoErrors();
    }

    @Test
    public void handleErrorsWithFallbackObservable_withError() {
        Observable<Integer> errorObservable = Observable.concat(
                Observable.fromArray(1, 2, 3),
                Observable.<Integer>error(new ExpectedException()));
        TestObserver<Integer> testObserver = mRxCreatingTraining
                .handleErrorsWithFallbackObservable(errorObservable, Observable.fromArray(4))
                .test();
        testObserver.assertValues(1, 2, 3, 4);
        testObserver.assertComplete();
        testObserver.assertNoErrors();
    }
}