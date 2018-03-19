package com.example.codebox.gisbms;

/**
 * Created by codebox on 7/8/17.
 */

public class JobLocation {
    private int jobId;
    private String jobTile;
    private String lat;
    private String lng;


    public JobLocation(int jobId, String jobTile, String lat, String lng) {
        this.jobId = jobId;
        this.jobTile = jobTile;
        this.lat = lat;
        this.lng = lng;
    }

    public int getId(){
        return jobId;
    }
    public String getJobTile(){
        return this.jobTile;
    }

    public String getLat(){
        return  this.lat;
    }

    public String getLng(){
        return this.lng;
    }
}
