package org.kite.sample;


import org.kite.annotations.Provided;
import org.kite.wire.Scope;
import org.kite.wire.WiredService;

/**
 * TODO
 *
 * @author Nikolay Soroka
 */
public class SampleService extends WiredService {

    public static final String ACTION_BIND_SUBSTRACTOR = "substractor";
    public SampleService() {
        super("SampleService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        calculator = new Calculator();
    }

    @Provided(scope = Scope.ACTION, action = ACTION_BIND_SUBSTRACTOR)
    public Substractor substractor = new Substractor() {
        @Override
        public int sub(int a, int b) {
            return a - b;
        }
    };

    @Provided
    public CalcInterface getCalculator() {
        return this.calculator;
    }

    private CalcInterface calculator;
}
