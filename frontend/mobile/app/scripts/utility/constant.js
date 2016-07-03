'use strict';

angular.module('mobileApp').constant('constants', {
    BASE_URL: '/api',

    CAR_STATUS: [{
        name: '待命',
        value: 'IDLE',
        icon: 'images/car_idle_64x64.png',
        cls: 'yellow'
    }, {
        name: '出车',
        value: 'INUSE',
        icon: 'images/car_busy_64x64.png',
        cls: 'blue'
    }, {
        name: '待修',
        value: 'AWAITING_REPAIRE',
        icon: 'images/car_repair_64x64.png',
        cls: 'red'
    }, {
        name: '维修',
        value: 'REPAIRING',
        icon: 'images/car_repair_64x64.png',
        cls: 'red'
    }],
    DRIVER_STATUS: [{
        name: '待命',
        value: 'IDLE',
        icon: 'images/driver_idle_64x64.png',
        cls: 'yellow',
        color: '#ff9300'
    }, {
        name: '出车',
        value: 'WORK',
        icon: 'images/driver_busy_64x64.png',
        cls: 'blue',
        color: '#2ea9df'
    }, {
        name: '请假',
        value: 'LEAVE',
        icon: 'images/driver_leave_64x64.png',
        cls: 'red',
        color: '#fd1000'
    }],

});