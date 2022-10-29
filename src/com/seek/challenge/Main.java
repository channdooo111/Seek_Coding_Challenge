package com.seek.challenge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
        	Path currentRelativePath = Paths.get("");   
        	String s = currentRelativePath.toAbsolutePath().toString();
        	System.out.println("Current absolute path is: " + s);
        	
        	
            List<TrafficDataPoint> trafficData = GetTrafficData("src\\com\\seek\\challenge\\testdata.txt");
            System.out.println("Total cars : " + getTotalCars(trafficData));

            System.out.println("Top 3 busy hours");
            for (TrafficDataPoint dataPoint : getTopKHours(trafficData, 3)) {
                System.out.println(dataPoint);
            }

            System.out.println("Aggregate by day");
            for (Map.Entry<LocalDate, Integer> entry: getAggregateByDate(trafficData).entrySet()) {
                System.out.println(entry.getKey() + " " + entry.getValue());
            }

            System.out.println("Contiguous 1.5 hours with least cars");
            List<LocalDateTime> silentPeriod = getSilentPeriod(trafficData, 3);
            System.out.println("Start : " + silentPeriod.get(0) + ", End : " + silentPeriod.get(1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<LocalDateTime> getSilentPeriod(List<TrafficDataPoint> trafficData, int silentPeriodWidth) {
        LocalDateTime silentPeriodStartTime = null;
        LocalDateTime silentPeriodEndTime = null;
        int carsInSilentPeriod = Integer.MAX_VALUE;

        int currentPeriodCarCount = 0;  

        for(int i = 0; i < trafficData.size(); i++) {
            currentPeriodCarCount += trafficData.get(i).getCount();

            if(i >= silentPeriodWidth) {
                currentPeriodCarCount -= trafficData.get(i - silentPeriodWidth).getCount();
            }

            if(i >= silentPeriodWidth - 1 && currentPeriodCarCount < carsInSilentPeriod) {
                carsInSilentPeriod = currentPeriodCarCount;
                silentPeriodStartTime = trafficData.get(i - (silentPeriodWidth - 1)).getTime();
                silentPeriodEndTime = trafficData.get(i).getTime();
            }
        }

        if(silentPeriodStartTime == null || silentPeriodEndTime == null) {
            return Arrays.asList();
        }

        return Arrays.asList(silentPeriodStartTime, silentPeriodEndTime);
    }

    public static int getTotalCars(List<TrafficDataPoint> trafficData) {
        return trafficData.stream()
                .mapToInt(x -> x.getCount())
                .sum();
    }

    public static List<TrafficDataPoint> getTopKHours(List<TrafficDataPoint> trafficData, int k) {
        PriorityQueue<TrafficDataPoint> topKHours = new PriorityQueue<>();
        for (TrafficDataPoint dataPoint : trafficData) {
            if (topKHours.size() == k && dataPoint.compareTo(topKHours.peek()) > 0)
                topKHours.poll(); // remove the least element, current is better
            if (topKHours.size() < k) // we removed one or haven't filled up, so add
                topKHours.add(dataPoint);
        }

        return new ArrayList<>(topKHours);
    }

    public static TreeMap<LocalDate, Integer> getAggregateByDate(List<TrafficDataPoint> trafficData) {
        TreeMap<LocalDate, Integer> countByDay = new TreeMap<>();
        for (TrafficDataPoint dataPoint : trafficData) {
            LocalDate date = dataPoint.getTime().toLocalDate();
            countByDay.put(date, countByDay.getOrDefault(date, 0) +  dataPoint.getCount());
        }

        return countByDay;
    }

    private static List<TrafficDataPoint> GetTrafficData(String filePath) throws IOException {
        List<String> trafficDataStrings = readAllLines(filePath);
        List<TrafficDataPoint> trafficData = new ArrayList<>();
        for (String dataPoint : trafficDataStrings) {
            String[] parts = dataPoint.split(" ", 2);
            LocalDateTime dateTime = LocalDateTime.parse(parts[0], DateTimeFormatter.ISO_DATE_TIME);
            Integer count = Integer.parseInt(parts[1]);
            trafficData.add(new TrafficDataPoint(dateTime, count));
        }

        return trafficData;
    }

    private static List<String> readAllLines(String filePath) throws IOException {
    	List<String> fileContent = new ArrayList<>();
        Path currentRelativePath = Paths.get("");
        File file = new File(currentRelativePath.toAbsolutePath() + "\\" + filePath);

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                fileContent.add(line);
            }
        }
        finally {
            if (reader != null) {
                reader.close();
            }
        }
        return fileContent;
    }
}