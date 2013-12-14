package org.kite.sample;


import org.kite.annotations.Provided;
import org.kite.async.AsyncType;
import org.kite.wire.WiredService;

import java.util.concurrent.Executors;

/**
 * TODO
 *
 * @author Nikolay Soroka
 */
public class SampleService extends WiredService {

    public static final String ACTION_BIND_SUBSTRACTOR = "substractor";
    public SampleService() {
        super(Executors.newSingleThreadExecutor(), "SampleService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        calculator = new Calculator();
    }

    @Provided
    public CalcInterface getCalculator() {
        return this.calculator;
    }

    @Provided(scope = Provided.Scope.ACTION, action = ACTION_BIND_SUBSTRACTOR)
    public Substractor substractor = new Substractor() {
        @Override
        public int sub(int a, int b) {
            return a - b;
        }
    };

    @Provided(scope = Provided.Scope.ACTION, action = ACTION_BIND_SUBSTRACTOR, async = AsyncType.METHODS)
    private AsyncCalc asyncCalc = new AsyncCalc() {
        @Override
        public Integer asyncAdd(int a, int b) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return a + b;
        }
    };

    private CalcInterface calculator;
}
