package com.yunguchang.gps;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by 禕 on 2015/9/10.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class DrivingResult {
//    "status": 0,
//            "message": "ok",
//            "type": 2,
//            "info": {
//        "copyright": {
//            "text": "@2015 Baidu - Data",
//                    "imageUrl": "http://api.map.baidu.com/images/copyright_logo.png"
//        }
//    },


    private int status;
    private int type;

    private String originCity;
    private String destinationCity;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }



    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    private Result result;
    @JsonIgnoreProperties(ignoreUnknown = true)

    public static class Result {
        private Route[] routes;

        public Route[] getRoutes() {
            return routes;
        }

        public void setRoutes(Route[] routes) {
            this.routes = routes;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)

        public static class Route{

            private int duration;
            private Step[] steps;

            public int getDuration() {
                return duration;
            }

            public void setDuration(int duration) {
                this.duration = duration;
            }

            public Step[] getSteps() {
                return steps;
            }

            public void setSteps(Step[] steps) {
                this.steps = steps;
            }

            @JsonIgnoreProperties(ignoreUnknown = true)

            public static class Step{
                private String path;

                public String getPath() {
                    return path;
                }

                public void setPath(String path) {
                    this.path = path;
                }


            }
        }
    }

    public String getOriginCity() {
        return originCity;
    }

    public void setOriginCity(String originCity) {
        this.originCity = originCity;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }
}
