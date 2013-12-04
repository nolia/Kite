package org.kite.sample;

/**
 * TODO
 *
 * @author Nikolay Soroka
 */
public class Calculator implements CalcInterface {

    @Override
    public int add(int first, int second) {
        return first + second;
    }

    @Override
    public int addAsync(int first, int second, long delay) {
        int res = first + second;
        return res;
    }
}
