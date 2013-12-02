package org.wire.sample;

import org.wire.Provided;
import org.wire.WiredService;

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
    public CalcInterface getCalculator(){
        return this.calculator;
    }
}
