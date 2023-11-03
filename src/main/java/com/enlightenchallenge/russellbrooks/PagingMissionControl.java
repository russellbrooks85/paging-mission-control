package com.enlightenchallenge.russellbrooks;

import com.enlightenchallenge.russellbrooks.model.AlarmOutput;
import com.enlightenchallenge.russellbrooks.service.AlarmsService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PagingMissionControl {
    private static final Logger logger
            = LoggerFactory.getLogger(PagingMissionControl.class);

    /**
     * Default run case for the problem.  Accepts a filename argument.
     * @param args  filename - input any valid filename, or defaults to input.txt
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        String filename = "input.txt";
        if (args !=null && args.length >0 && !StringUtils.isEmpty(args[0])) filename=args[0];
        File input = new File(filename);

        AlarmsService alarmsService = new AlarmsService();
        List<AlarmOutput> out = alarmsService.processAlarms(input);

        ObjectWriter ow = new ObjectMapper()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .registerModule(new JavaTimeModule())
                .writer()
                .withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(out);
        System.out.println(json);
    }


}