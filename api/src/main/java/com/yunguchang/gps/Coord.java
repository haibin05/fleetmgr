package com.yunguchang.gps;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by 禕 on 2015/9/10.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Coord {
    private double x;
    private double y;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Coord(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Coord(){}
}
