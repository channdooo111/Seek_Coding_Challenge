package com.seek.challenge;

import java.time.LocalDateTime;

public class TrafficDataPoint implements Comparable<TrafficDataPoint>{

    private LocalDateTime time;    
    private Integer count;

    public TrafficDataPoint(LocalDateTime time, Integer count) {
        super();
        this.time = time;
        this.count = count;
    }   


    @Override
    public int compareTo(TrafficDataPoint o) {
        return this.count.compareTo(o.count);
    }

    @Override
    public String toString() {
        return time.toString() + " " + count;
    }

    public int getCount() {
        return this.count;
    }

    public LocalDateTime getTime() {
        return this.time;
    }
}

