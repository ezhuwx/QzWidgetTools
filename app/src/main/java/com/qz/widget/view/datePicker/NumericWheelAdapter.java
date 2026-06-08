package com.qz.widget.view.datePicker;


import com.contrarywind.adapter.WheelAdapter;

/**
 * Numeric Wheel adapter.
 */
public class NumericWheelAdapter implements WheelAdapter {
    /**
     * 间隔
     */
    private int interval = 1;
    private int minValue;
    private int maxValue;

    /**
     * Constructor
     *
     * @param minValue the wheel min value
     * @param maxValue the wheel max value
     */
    public NumericWheelAdapter(int minValue, int maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    /**
     * Constructor
     *
     * @param minValue the wheel min value
     * @param maxValue the wheel max value
     */
    public NumericWheelAdapter(int minValue, int maxValue, int interval) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.interval = interval;
    }

    @Override
    public Object getItem(int index) {
        if (index >= 0 && index < getItemsCount()) {
            return minValue + index * interval;
        }
        return 0;
    }

    @Override
    public int getItemsCount() {
        return ((maxValue - minValue) / interval) + 1;
    }

    @Override
    public int indexOf(Object o) {
        try {
            return ((int) o - minValue) / interval;
        } catch (Exception e) {
            return -1;
        }

    }
}
