package com.scada.smartfarm;

import com.scada.smartfarm.response.Temperature;

import java.util.ArrayList;
import java.util.List;

public class SpcTemperatureCollection {
    public List<SpcTemperature> listTemperature;

    public SpcTemperatureCollection(Temperature temperature) {
        List<Double> listTemp = temperature.getBins();
        List<Double> listPercent = temperature.getHist();
        listTemperature = new ArrayList<SpcTemperature>();
        int count = listPercent.size();
        for (int i=0;i<count;i++) {
            Double x = listTemp.get(i);
            Double y = listPercent.get(i);
            SpcTemperature t = new SpcTemperature(x, y);
            listTemperature.add(t);
        }
    }
}
