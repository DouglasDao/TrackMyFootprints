package com.footprints.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "geodata")
public class GeoData {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "geoId")
    private int id;
    @ColumnInfo(name = "latitude")
    private Double latit;
    @ColumnInfo(name = "longitude")
    private Double longit;
    @ColumnInfo(name = "address")
    private String address;

    public GeoData(Double latit, Double longit, String address) {
        this.latit = latit;
        this.longit = longit;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getLatit() {
        return latit;
    }

    public void setLatit(Double latit) {
        this.latit = latit;
    }

    public Double getLongit() {
        return longit;
    }

    public void setLongit(Double longit) {
        this.longit = longit;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
