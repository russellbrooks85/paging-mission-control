package com.enlightenchallenge.russellbrooks.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data @AllArgsConstructor
public class AlarmOutput {

    int satelliteId;
    String severity;
    String component;
    Instant timestamp;


}
