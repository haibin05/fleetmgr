package com.yunguchang.gps;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by 禕 on 2015/9/10.
 */
@JsonIgnoreProperties(ignoreUnknown = true)

public class GeoconvResult {
//    {
//        status : 0,
//                result :
//        [
//        {
//            x : 114.23075411589,
//                    y : 29.5790859441
//        },
//        {
//            x : 114.23075141142,
//                    y : 29.579086993255
//        }
//        ]
//    }

    private int status;
    private Coord [] result;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Coord[] getResult() {
        return result;
    }

    public void setResult(Coord[] result) {
        this.result = result;
    }

}
