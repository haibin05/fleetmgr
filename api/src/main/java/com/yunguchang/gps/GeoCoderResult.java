package com.yunguchang.gps;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by gongy on 9/10/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoCoderResult {
    //    "status": 0,
//    "result": {
//        "location": {
//            "lng": 116.32298703399,
//                    "lat": 39.983424051248
//        },
//        "formatted_address": "北京市海淀区中关村大街27号1101-08室",
//                "business": "中关村,人民大学,苏州街",
//                "addressComponent": {
//            "city": "北京市",
//                    "country": "中国",
//                    "direction": "附近",
//                    "distance": "7",
//                    "district": "海淀区",
//                    "province": "北京市",
//                    "street": "中关村大街",
//                    "street_number": "27号1101-08室",
//                    "country_code": 0
//        },
//        "poiRegions": [],
//        "sematic_description": "北京远景国际公寓(中关村店)内0米",
//                "cityCode": 131
//    }
    private int status;
    private Result result;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {

        @JsonProperty("formatted_address")
        private String formattedAddress;
        @JsonProperty("sematic_description")
        private String sematicDescription;
        public String getFormattedAddress() {
            return formattedAddress;
        }

        public void setFormattedAddress(String formattedAddress) {
            this.formattedAddress = formattedAddress;
        }

        public String getSematicDescription() {
            return sematicDescription;
        }

        public void setSematicDescription(String sematicDescription) {
            this.sematicDescription = sematicDescription;
        }

        public AddressComponent getAddressComponent() {
            return addressComponent;
        }

        public void setAddressComponent(AddressComponent addressComponent) {
            this.addressComponent = addressComponent;
        }

        private AddressComponent addressComponent;

        @JsonIgnoreProperties(ignoreUnknown = true)

        public static class AddressComponent {
            private String city;


            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }


        }

    }


}
