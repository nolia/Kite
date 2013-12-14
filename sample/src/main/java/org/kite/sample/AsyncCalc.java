package org.kite.sample;

import org.kite.annotations.AsyncMethod;

/**
 * TODO
 *
 * @author Nikolay Soroka
 */
public interface AsyncCalc {

    int ADD_RESULT = 100;

    @AsyncMethod(ADD_RESULT)
    Integer asyncAdd(int a, int b);
}
