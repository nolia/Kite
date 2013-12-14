package org.kite.sample;

import org.kite.annotations.AsyncMethod;

public interface Substractor {

    final int ADD_RESULT = 100;

    int sub(int a, int b);

    @AsyncMethod(ADD_RESULT)
    Integer asyncAdd(int a, int b);

}
