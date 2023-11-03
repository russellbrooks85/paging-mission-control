/***
 * @author Russell Brooks
 * Thank you for your consideration.
 */
package com.enlightenchallenge.russellbrooks.service;

import com.enlightenchallenge.russellbrooks.model.AlarmOutput;
import com.enlightenchallenge.russellbrooks.model.TelemetryInput;




import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlarmsService {

    private static final Logger logger
            = LoggerFactory.getLogger(AlarmsService.class);

    private final Map<String,LinkedList<TelemetryInput>> alarmMap = new HashMap<String,LinkedList<TelemetryInput>>();

    /**
     * Ingest status telemetry data and create alert messages for the following violation conditions:
     * If for the same satellite there are three battery voltage readings that are under the red low limit within a five-minute interval.
     * If for the same satellite there are three thermostat readings that exceed the red high limit within a five-minute interval.
     * NOTE: this was done very efficiently. if I was favoring readability over efficiency, I would have split the file reading/parsing into a new method like readFile below.
     * @param file input file with telemetry data
     * @return List<AlarmOutput> of alarms as defined in the problem statement
     */
    public List<AlarmOutput> processAlarms(File file) {

        long startTime = Instant.now().toEpochMilli();
        long rows = 0;
        long totalRedAlarms = 0;
        long totalYellowAlarms = 0;

        BufferedReader reader;
        List<AlarmOutput> outputs = new ArrayList<AlarmOutput>();

        LinkedList<TelemetryInput> currentAlarm = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            TelemetryInput telemetryData = null;
            while (line != null) {
                rows++;
                telemetryData = new TelemetryInput(line.split("\\|"));
                // first test if the row meets the red threshold
                if (telemetryData.getRawValue()>=telemetryData.getRedHighLimit()){
                    logger.trace("Red High Alarm : Condition : {} <= {} <= {} <= {} <= {}",
                            telemetryData.getRedLowLimit(),telemetryData.getYellowLowLimit(),telemetryData.getRawValue(),telemetryData.getYellowHighLimit(), telemetryData.getRedHighLimit());
                    totalRedAlarms++;
                    outputs=checkAlarm(currentAlarm,telemetryData,outputs,"RED HIGH");

                } else if (telemetryData.getRawValue()<=telemetryData.getRedLowLimit()) {

                    logger.trace("Red Low Alarm : Condition : {} <= {} <= {} <= {} <= {}",
                            telemetryData.getRedLowLimit(),telemetryData.getYellowLowLimit(),telemetryData.getRawValue(),telemetryData.getYellowHighLimit(), telemetryData.getRedHighLimit());
                    totalRedAlarms++;
                    outputs=checkAlarm(currentAlarm,telemetryData,outputs,"RED LOW");

                } // yellow appears to be totally irrelevant according to the problem statement
                else if (telemetryData.getRawValue()>=telemetryData.getYellowHighLimit()
                        || telemetryData.getRawValue()<=telemetryData.getYellowLowLimit()
                ) {
                    totalYellowAlarms++;
                    logger.trace("Yellow Alarm ignored : Condition : {} <= {} <= {}",
                            telemetryData.getYellowLowLimit(),telemetryData.getRawValue(),telemetryData.getYellowHighLimit());
                } else {
                    logger.trace("Non-Alarm Ignored : Condition : {} <= {} <= {} <= {} <= {}",
                            telemetryData.getRedLowLimit(),telemetryData.getYellowLowLimit(),telemetryData.getRawValue(),telemetryData.getYellowHighLimit(), telemetryData.getRedHighLimit());

                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            logger.error("Error reading file filename={}, error={}",file.getName(),e.getLocalizedMessage());
        } finally {
            logger.info("Operation completed in time={} ms : rows={} : alarm_response_count={} : total_red={} : total_yellow={}",(Instant.now().toEpochMilli()-startTime),rows, outputs.size(),totalRedAlarms,totalYellowAlarms);
        }


        return outputs;
    }

    /**
     * Efficiently compare values in for the current satellite, component and alarm type.
     * This can handle any case with matching alarm type, component, and satellite
     * @param currentAlarm - linked list specific to current satellite, component and alarm type
     * @param telemetryData - one row of telemetry data matching current satellite, component and alarm type
     * @param outputs - alarms to output back to the user
     * @param severity - RED HIGH or RED LOW
     * @return outputs
     */
    private List<AlarmOutput> checkAlarm(LinkedList<TelemetryInput> currentAlarm, TelemetryInput telemetryData, List<AlarmOutput> outputs, String severity){
        // separate into a linked list for each satellite, component, and alarm type (high/low)
        // in theory this is O(n), but the problem actually specifies a max of 8 entries in the map, making this very fast
        currentAlarm = alarmMap.computeIfAbsent(telemetryData.getSatelliteId() + telemetryData.getComponent() + severity, k -> new LinkedList<TelemetryInput>());
        currentAlarm.addLast(telemetryData);

        // remove any entries older than 5 min in a way that is O(1)
        // after the alarm has been added, we stop checking since size will be >3
        while (currentAlarm.size() <=3
                && currentAlarm.getFirst().getTimestamp().plusSeconds(300).isBefore(currentAlarm.getLast().getTimestamp())) {
            currentAlarm.removeFirst();
        }
        //add the first alarm timestamp like in the example, and no longer add alarms for this since the list will be >3 next time.
        if (currentAlarm.size()==3) {
            outputs.add(new AlarmOutput(telemetryData.getSatelliteId(),severity, telemetryData.getComponent(), currentAlarm.getFirst().getTimestamp()));
        }
        return outputs;
    }


    // if we weren't concerned about efficiency, I would separate out this part.  this adds an extra loop though
//    private List<TelemetryInput> readFile(File file) {
//        BufferedReader reader;
//
//        List<TelemetryInput> inputs = new ArrayList<TelemetryInput>();
//        try {
//            reader = new BufferedReader(new FileReader(file));
//            String line = reader.readLine();
//            TelemetryInput telemetryData = null;
//            while (line != null) {
//                System.out.println(line);
//                telemetryData = new TelemetryInput(line.split("\\|"));
//                // filter out rows that don't me the threshold from the start. yellow appears to be totally irrelevant
//                if (telemetryData.getRawValue()>=telemetryData.getRedHighLimit()
//                    || telemetryData.getRawValue()<=telemetryData.getRedLowLimit()){
//                    inputs.add(telemetryData);
//                } else if (telemetryData.getRawValue()>=telemetryData.getYellowHighLimit()
//                        || telemetryData.getRawValue()<=telemetryData.getYellowLowLimit()
//                ) {
//                    logger.warn("Yellow Alarm ignored : Condition : {} <= {} <= {}",
//                            telemetryData.getYellowLowLimit(),telemetryData.getRawValue(),telemetryData.getYellowHighLimit());
//                } else {
//                    logger.info("Ignored non-Alarm : Condition : {} <= {} <= {} <= {} <= {}",
//                            telemetryData.getRedLowLimit(),telemetryData.getYellowLowLimit(),telemetryData.getRawValue(),telemetryData.getYellowHighLimit(), telemetryData.getRedHighLimit());
//
//                }
//                line = reader.readLine();
//            }
//
//            reader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return inputs;
//    }
}
