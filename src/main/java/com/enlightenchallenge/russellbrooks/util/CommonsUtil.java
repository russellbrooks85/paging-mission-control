package com.enlightenchallenge.russellbrooks.util;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class CommonsUtil {
    public static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS")
            .withZone(ZoneOffset.UTC);

}
