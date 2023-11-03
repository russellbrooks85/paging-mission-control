package com.test.enlightenchallenge.russellbrooks;

import com.enlightenchallenge.russellbrooks.PagingMissionControl;
import com.enlightenchallenge.russellbrooks.model.AlarmOutput;
import com.enlightenchallenge.russellbrooks.service.AlarmsService;
import com.enlightenchallenge.russellbrooks.util.AppConstants;
import com.enlightenchallenge.russellbrooks.util.CommonsUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;


public class Tests {

    private static final Logger logger
            = LoggerFactory.getLogger(Tests.class);



    @Test
    public void givenTest() throws Exception {
        AlarmsService as = new AlarmsService();
        String filename= "input.txt";

        File input = new File(filename);


        long startTime = Instant.now().toEpochMilli();
        List<AlarmOutput> out = as.processAlarms(input);
        assertEquals(2, out.size());
        assertEquals("TSTAT", out.get(0).getComponent());
        assertEquals(1000, out.get(0).getSatelliteId());
        assertEquals("RED HIGH", out.get(0).getSeverity());
        assertEquals("BATT", out.get(1).getComponent());
        assertEquals(1000, out.get(1).getSatelliteId());
        assertEquals("RED LOW", out.get(1).getSeverity());
        assertTrue((Instant.now().toEpochMilli() - startTime) < 10000);

    }

    @Test
    public void loadTest1() throws Exception {
        // this generates a huge file that will generally throw about 6-8 alarms
        generateLoadTest(10000000l, 11, 3);
        long startTime = Instant.now().toEpochMilli();
        PagingMissionControl.main(new String[]{"LoadTest.txt"});
        assertTrue((Instant.now().toEpochMilli() - startTime) < 60000);

    }

    @Test
    public void highTest1() throws Exception {
        AlarmsService as = new AlarmsService();
        String filename= "test.txt";

        File input = new File(filename);
        if (!input.createNewFile()) {
            input.delete();
            input.createNewFile();
        }
        FileWriter fw = new FileWriter(input);
        generateTest(1000,4, "TSTAT", 10, 106, fw);
        long startTime = Instant.now().toEpochMilli();
        List<AlarmOutput> out = as.processAlarms(input);
        assertEquals(1, out.size());
        assertEquals("TSTAT", out.get(0).getComponent());
        assertEquals(1000, out.get(0).getSatelliteId());
        assertEquals("RED HIGH", out.get(0).getSeverity());
        assertTrue((Instant.now().toEpochMilli() - startTime) < 10000);

    }

    @Test
    public void highTest2() throws Exception {
        AlarmsService as = new AlarmsService();
        String filename= "test2.txt";

        File input = new File(filename);
        if (!input.createNewFile()) {
            input.delete();
            input.createNewFile();
        }
        FileWriter fw = new FileWriter(input);
        generateTest(1000,4, "TSTAT", 10, 106, fw);
        generateTest(1000,4, "BATT", 10, 106, fw);

        long startTime = Instant.now().toEpochMilli();
        List<AlarmOutput> out = as.processAlarms(input);
        assertEquals(2, out.size());
        assertEquals("TSTAT", out.get(0).getComponent());
        assertEquals(1000, out.get(0).getSatelliteId());
        assertEquals("RED HIGH", out.get(0).getSeverity());
        assertEquals("BATT", out.get(1).getComponent());
        assertEquals(1000, out.get(1).getSatelliteId());
        assertEquals("RED HIGH", out.get(1).getSeverity());
        assertTrue((Instant.now().toEpochMilli() - startTime) < 10000);

    }

    @Test
    public void highlowTest3() throws Exception {
        AlarmsService as = new AlarmsService();
        String filename= "test3.txt";

        File input = new File(filename);
        if (!input.createNewFile()) {
            input.delete();
            input.createNewFile();
        }
        FileWriter fw = new FileWriter(input);
        generateTest(1000, 4,"TSTAT", 10, 106, fw);
        generateTest(1000,4, "BATT", 10, 10, fw);

        long startTime = Instant.now().toEpochMilli();
        List<AlarmOutput> out = as.processAlarms(input);
        assertEquals(2, out.size());
        assertEquals("TSTAT", out.get(0).getComponent());
        assertEquals(1000, out.get(0).getSatelliteId());
        assertEquals("RED HIGH", out.get(0).getSeverity());
        assertEquals("BATT", out.get(1).getComponent());
        assertEquals(1000, out.get(1).getSatelliteId());
        assertEquals("RED LOW", out.get(1).getSeverity());
        assertTrue((Instant.now().toEpochMilli() - startTime) < 10000);

    }

    @Test
    public void highlowNegetiveTest4() throws Exception {
        AlarmsService as = new AlarmsService();
        String filename= "test4.txt";

        File input = new File(filename);
        if (!input.createNewFile()) {
            input.delete();
            input.createNewFile();
        }
        FileWriter fw = new FileWriter(input);
        generateTest(1000,4, "TSTAT", 10, 101, fw);
        generateTest(1000,4, "BATT", 10, 39, fw);

        long startTime = Instant.now().toEpochMilli();
        List<AlarmOutput> out = as.processAlarms(input);
        assertEquals(0, out.size());

        assertTrue((Instant.now().toEpochMilli() - startTime) < 10000);

    }

    @Test
    public void highlowTest5() throws Exception {
        AlarmsService as = new AlarmsService();
        String filename= "test5.txt";

        File input = new File(filename);
        if (!input.createNewFile()) {
            input.delete();
            input.createNewFile();
        }
        FileWriter fw = new FileWriter(input);
        generateTest(1000,40, "TSTAT", 10, 106, fw);
        generateTest(1000,40, "BATT", 10, 10, fw);
        generateTest(1001,40, "TSTAT", 10, 106, fw);
        generateTest(1001,40, "BATT", 10, 10, fw);
        generateTest(1001,40, "TSTAT", 10, 10, fw);
        generateTest(1001,40, "BATT", 10, 109, fw);

        long startTime = Instant.now().toEpochMilli();
        List<AlarmOutput> out = as.processAlarms(input);
        assertEquals(6, out.size());
        assertEquals("TSTAT", out.get(0).getComponent());
        assertEquals(1000, out.get(0).getSatelliteId());
        assertEquals("RED HIGH", out.get(0).getSeverity());
        assertEquals("BATT", out.get(1).getComponent());
        assertEquals(1000, out.get(1).getSatelliteId());
        assertEquals("RED LOW", out.get(1).getSeverity());
        assertEquals("TSTAT", out.get(2).getComponent());
        assertEquals(1001, out.get(2).getSatelliteId());
        assertEquals("RED HIGH", out.get(2).getSeverity());
        assertEquals("BATT", out.get(3).getComponent());
        assertEquals(1001, out.get(3).getSatelliteId());
        assertEquals("RED LOW", out.get(3).getSeverity());
        assertEquals("TSTAT", out.get(4).getComponent());
        assertEquals(1001, out.get(4).getSatelliteId());
        assertEquals("RED LOW", out.get(4).getSeverity());
        assertEquals("BATT", out.get(5).getComponent());
        assertEquals(1001, out.get(5).getSatelliteId());
        assertEquals("RED HIGH", out.get(5).getSeverity());
        assertTrue((Instant.now().toEpochMilli() - startTime) < 10000);

    }


    public static void generateLoadTest(long lines, float standardDeviation, int interval) throws IOException {
        Random rand = new Random();
        Instant inst = Instant.now();
        long startTime = inst.toEpochMilli();
        StringBuilder sb;
        File input = new File("LoadTest.txt");
        if (!input.createNewFile()) {
            input.delete();
            input.createNewFile();
        }
        FileWriter fw = new FileWriter(input);

        for (int x = 0; x < lines; x++) {
            sb = new StringBuilder();
            inst = inst.plusSeconds(rand.nextInt(interval));
            sb.append(CommonsUtil.dtf.format(inst)).append("|")
                    .append(rand.nextInt(1000, 1002)).append("|")
                    .append("105|100|40|35|")
                    .append(rand.nextGaussian(70, standardDeviation)).append("|")
                    .append(AppConstants.Components.valueOf(rand.nextInt(2)))
                    .append("\n");

            fw.write(sb.toString());
            fw.flush();

        }
        logger.info("LoadTest.txt generation  completed in time={} ms ", (Instant.now().toEpochMilli() - startTime));

    }

    public static void generateTest(int satelite, int violations, String component, long interval, float raw, FileWriter fw) throws IOException {
        Random rand = new Random();
        Instant inst = Instant.now();
        long startTime = inst.toEpochMilli();
        StringBuilder sb;



        for (int x = 0; x < violations; x++) {
            sb = new StringBuilder();
            inst = inst.plusSeconds(interval);
            sb.append(CommonsUtil.dtf.format(inst)).append("|")
                    .append(satelite)
                    .append("|105|100|40|35|")
                    .append(raw).append("|")
                    .append(component)
                    .append("\n");

            fw.write(sb.toString());
            fw.flush();

        }


        logger.info("Test file generation  completed in time={} ms ", (Instant.now().toEpochMilli() - startTime));

    }
}
