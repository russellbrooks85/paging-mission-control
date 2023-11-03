package com.enlightenchallenge.russellbrooks.model;

import com.enlightenchallenge.russellbrooks.util.CommonsUtil;
import lombok.Data;

import java.time.Instant;

@Data
public class TelemetryInput {
    Instant timestamp;
    int satelliteId;
    int redHighLimit;
    int yellowHighLimit;
    int yellowLowLimit;
    int redLowLimit;
    float rawValue;
    String component;

    public TelemetryInput(String[] input) {
        timestamp= CommonsUtil.dtf.parse(input[0],Instant::from);
        satelliteId=Integer.parseInt(input[1]);
        redHighLimit=Integer.parseInt(input[2]);
        yellowHighLimit=Integer.parseInt(input[3]);
        yellowLowLimit=Integer.parseInt(input[4]);
        redLowLimit=Integer.parseInt(input[5]);
        rawValue=Float.parseFloat(input[6]);
        component=input[7];
    }

}
