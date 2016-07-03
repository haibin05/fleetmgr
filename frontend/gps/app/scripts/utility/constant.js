'use strict';

angular.module('gpsApp').constant('constants', {

    BASE_URL: '/api',

    //车辆列表位置更新间隔时间
    CARS_GROUP_GPS_POLL_INTERVAL: 30000,

    //车辆位置更新间隔时间
    CAR_GPS_POLL_INTERVAL: 15000,

    ALARMS_TYPE: [{
        name: '超速驾驶',
        value: 'SPEEDING'
    }, {
        name: '疲劳驾驶',
        value: 'TIRED'
    }, {
        name: '无任务出车',
        value: 'NOSCHEDULE'
    }, {
        name: '超违章出车',
        value: 'VIOLATION'
    }, {
        name: '修理车辆出车',
        value: 'INREPAIRING'
    }, {
        name: '准驾车型不符',
        value: 'LICENSE_MISMATCH'
    }, {
        name: 'GPS数据异常',
        value: 'NOGPSPOINTS'
    },{
        name: '提前出厂',
        value: 'GO_OUT_EARLY'
    },{
        name: '延迟回厂',
        value: 'COME_BACK_LATE'
    }],



    CAR_STATUS: [{
        name: '待命',
        value: 'IDLE',
        icon: 'images/car_idle_64x64.png',
        cls: 'yellow',
        color: '#ff9300'
    }, {
        name: '出车',
        value: 'INUSE',
        icon: 'images/car_busy_64x64.png',
        cls: 'blue',
        color: '#2ea9df'
    }, {
        name: '待修',
        value: 'AWAITING_REPAIRE',
        icon: 'images/car_repair_64x64.png',
        cls: 'red',
        color: '#fd1000'
    }, {
        name: '维修',
        value: 'REPAIRING',
        icon: 'images/car_repair_64x64.png',
        cls: 'red',
        color: '#fd1000'
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

    BROADCAST_OPTIONS: [
        '您已超速，请注意控制车速！',
        '您已连续驾车较长时间，疲劳驾驶危险，请及时休息！',
        '请您核查出车任务单信息，严禁无任务单出车！',
        '您有多张违章单未处理，请尽快处理！',
        '系统显示车辆状态尚在修理，严禁无任务单出车！',
        '请核对您所驾驶车辆与准驾车型是否一致，严禁证驾不符！',
        'GPS数据异常，请及时报修检查！',
        '请您核对任务单的出车时间，提前出车请告知车队！',
        '工作结束请及时回场，延误请及时与车队联系！'
    ]
});