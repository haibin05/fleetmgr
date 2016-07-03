package com.yunguchang.model.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gongy on 2015/11/9.
 */
public class OrderByParam {

    private OrderBy[] orderBies;

    public OrderBy[] getOrderBies() {
        return orderBies;
    }

    public void setOrderBies(OrderBy[] orderByList) {
        this.orderBies = orderByList;
    }

    public OrderByParam(OrderBy ... orderBies) {
        this.orderBies = orderBies;
    }

    @Override
    public String toString() {
        if (orderBies ==null || orderBies.length==0){
            return "";
        }
        StringBuilder sb=new StringBuilder();
        for (OrderBy orderBy : orderBies) {
            sb.append(orderBy).append(',');
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();

    }

    public  OrderByParam (String value){
        List<OrderBy> orderBies=new ArrayList<>();
        String[] orderbySegs = value.split(",");
        for (String orderbySeg : orderbySegs) {
            String[] orderbyField = orderbySeg.split("\\s+");
            OrderBy orderBy = new OrderBy();
            orderBy.setFiled(orderbyField[0]);
            if (orderbyField.length==2 && orderbyField[1].toLowerCase().equals("desc".toLowerCase())){
                orderBy.setAsc(false);
            }else{
                orderBy.setAsc(true);
            }
            orderBies.add(orderBy);
        }
        this.orderBies =orderBies.toArray(new OrderBy[]{});

    }

    public static  class OrderBy{
        private  String filed;
        private boolean asc;

        public String getFiled() {
            return filed;
        }

        public void setFiled(String filed) {
            this.filed = filed;
        }

        public boolean isAsc() {
            return asc;
        }

        public void setAsc(boolean asc) {
            this.asc = asc;
        }

        @Override
        public String toString() {
            return filed+' '+(asc?"asc":"desc");
        }
    }

}
