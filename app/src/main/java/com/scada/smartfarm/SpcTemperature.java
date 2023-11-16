package com.scada.smartfarm;

import com.scada.smartfarm.response.Temperature;

public class SpcTemperature {
    public Double temperature;
    public Double percentage;

    public SpcTemperature(Double temp, Double percent) {
        this.temperature = temp;
        this.percentage = percent;
    }
}
