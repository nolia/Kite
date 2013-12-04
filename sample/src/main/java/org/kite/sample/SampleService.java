package org.kite.sample;


import org.kite.wire.Provided;
import org.kite.wire.WiredService;

/**
 * TODO
 *
 * @author Nikolay Soroka
 */
public class SampleService extends WiredService {

    private CalcInterface calculator;

    public SampleService() {
        super("SampleService");
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
}
