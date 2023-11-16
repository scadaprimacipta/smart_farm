package com.scada.smartfarm;

import com.scada.smartfarm.databinding.ActivityMainBinding;

public class SpcImageParameter {
    public int temperatureMin;
    public int temperatureMax;
    public String imageLocation;

    public SpcImageParameter() {

    }

    public SpcImageParameter(String min, String max, String location) {
        temperatureMin = Integer.parseInt(min);
        temperatureMax = Integer.parseInt(max);
        this.imageLocation = location;
    }

    public SpcImageParameter(ActivityMainBinding activityMainBinding, String location) {
        String min = activityMainBinding.minInput.getText().toString();
        String max = activityMainBinding.maxInput.getText().toString();

        try {
            temperatureMin = Integer.parseInt(min);
        } catch (NumberFormatException e) {
            temperatureMin = -1;
        }

        try {
            temperatureMax = Integer.parseInt(max);
        } catch (NumberFormatException e) {
            temperatureMax = -1;
        }

        this.imageLocation = location;
    }

    public boolean checkDataReady() {
        boolean isReady = false;
        if (temperatureMin > 0 && temperatureMax > 0 && !imageLocation.trim().equals("")) {
            isReady = true;
        }
        return isReady;
    }

}
