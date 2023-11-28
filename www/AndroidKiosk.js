var exec = require('cordova/exec');
var AndroidKiosk= {
    
    // 启用开机自动启动
    enableAutoStart: function(success, error){
        exec(success, error, 'AndroidKiosk', 'enableAutoStart', null);
    },
    
    // 取消开机自动启动
    disableAutoStart: function(success, error){
        exec(success, error, 'AndroidKiosk', 'disableAutoStart', null);
    },

    // 开始KIOSK模式
    startKiosk: function(success, error){
        exec(success, error, 'AndroidKiosk', 'startKiosk', null);
    },

    // 结束KIOSK模式
    endKiosk:function(success,error){
        exec(success, error, 'AndroidKiosk', 'endKiosk', null);
    },
}

module.exports = AndroidKiosk;