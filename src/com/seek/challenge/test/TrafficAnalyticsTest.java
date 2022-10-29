package com.seek.challenge.test;

import org.junit.Assert;
import org.junit.Test;


import com.seek.challenge.TrafficDataPoint;
import com.seek.challenge.Main;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
  
public class TrafficAnalyticsTest {

    @Test
    public void testTotalCount() {
        List<TrafficDataPoint> testData = new ArrayList<>();
        // Testing empty data
        Assert.assertEquals(0, Main.getTotalCars(testData));

        LocalDateTime startTime = LocalDateTime.now().plusDays(-10);
        int count = 0;
        Random random = new Random();
        for(int i = 0; i < 10; i++) {
          int carCount = random.nextInt(1000 - 1) + 1;
          startTime.plusMinutes(30);
          testData.add(new TrafficDataPoint(startTime, carCount));
          count += carCount;
        }

        // Testing non empty data
        Assert.assertEquals(count, Main.getTotalCars(testData));
    }

    @Test
    public void testTopK() {
        List<TrafficDataPoint> testData = new ArrayList<>();
        // Testing empty data
        Assert.assertEquals(0, Main.getTopKHours(testData, 3).size());

        LocalDateTime startTime = LocalDateTime.now().plusDays(-10);

        testData.add(new TrafficDataPoint(startTime.plusMinutes(30), 100));
        testData.add(new TrafficDataPoint(startTime.plusMinutes(30), 200));
        Assert.assertEquals(2, Main.getTopKHours(testData, 3).size());

        Random random = new Random();
        for(int i = 0; i < 10; i++) {
            int carCount = random.nextInt(1000 - 1) + 1;
            startTime.plusMinutes(30);
            testData.add(new TrafficDataPoint(startTime, carCount));
        }

        testData.add(new TrafficDataPoint(startTime.plusMinutes(30), 1200));
        testData.add(new TrafficDataPoint(startTime.plusMinutes(30), 2100));
        testData.add(new TrafficDataPoint(startTime.plusMinutes(30), 1900));

        List<TrafficDataPoint> result = Main.getTopKHours(testData, 3);
        Assert.assertEquals(3, result.size());
        Assert.assertTrue(result.stream().anyMatch(e -> e.getCount() == 1200));
        Assert.assertTrue(result.stream().anyMatch(e -> e.getCount() == 2100));
        Assert.assertTrue(result.stream().anyMatch(e -> e.getCount() == 1900));
    }

    @Test
    public void testDayAggregate() {
        List<TrafficDataPoint> testData = new ArrayList<>();
        // Testing empty data
        Assert.assertEquals(0, Main.getAggregateByDate(testData).size());

        LocalDateTime day1startTime = LocalDateTime.now().plusDays(-10).with(LocalTime.MIN);

        for(int i = 0; i < 5; i++) {
            day1startTime.plusHours(4);
            testData.add(new TrafficDataPoint(day1startTime, 100));
        }

        LocalDateTime day2startTime = LocalDateTime.now().plusDays(-8).with(LocalTime.MIN);

        for(int i = 0; i < 3; i++) {
            day2startTime.plusHours(4);
            testData.add(new TrafficDataPoint(day2startTime, 200));
        }

        TreeMap<LocalDate, Integer> result = Main.getAggregateByDate(testData);
        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.containsKey(day1startTime.toLocalDate()));
        Assert.assertEquals(500, (long)result.get(day1startTime.toLocalDate()));
        Assert.assertTrue(result.containsKey(day2startTime.toLocalDate()));
        Assert.assertEquals(600, (long) result.get(day2startTime.toLocalDate()));
    }

    @Test
    public void testSilentHours() {
        List<TrafficDataPoint> testData = new ArrayList<>();
        // Testing empty data
        Assert.assertEquals(0, Main.getSilentPeriod(testData, 3).size());

        LocalDateTime startTime = LocalDateTime.now().plusDays(-10);

        testData.add(new TrafficDataPoint(startTime.plusMinutes(30), 100));
        testData.add(new TrafficDataPoint(startTime.plusMinutes(30), 200));
        Assert.assertEquals(2, Main.getTopKHours(testData, 3).size());

        Random random = new Random();
        for(int i = 0; i < 10; i++) {
            int carCount = random.nextInt(1000 - 500) + 500;
            startTime.plusMinutes(30);
            testData.add(new TrafficDataPoint(startTime, carCount));
        }

        LocalDateTime silentTimeStart = startTime.plusMinutes(30);
        testData.add(new TrafficDataPoint(silentTimeStart, 10));
        testData.add(new TrafficDataPoint(startTime.plusMinutes(30), 25));
        LocalDateTime silentTimeEnd = startTime.plusMinutes(30);
        testData.add(new TrafficDataPoint(silentTimeEnd, 30));

        for(int i = 0; i < 10; i++) {
            int carCount = random.nextInt(1000 - 500) + 500;
            startTime.plusMinutes(30);
            testData.add(new TrafficDataPoint(startTime, carCount));
        }

        List<LocalDateTime> result = Main.getSilentPeriod(testData, 3);
        Assert.assertEquals(silentTimeStart, result.get(0));
        Assert.assertEquals(silentTimeEnd, result.get(1));
    }
}
