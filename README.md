# Cordova Plugin Android Kiosk Mode

此插件，基于DPC(DevicePolicyManager),在使用中，需要DeviceOwner权限。适用于专用设备。
详细可见：https://developer.android.google.cn/work/dpc/dedicated-devices/lock-task-mode?hl=zh-cn

### 注意点

此插件，需要DeviceOwner权限。


### 设置DeviceOwner

```
adb shell dpm set-device-owner {your-app-name}/com.huayu.cordova.plugin.android.kiosk.KioskModeDeviceAdminReceiver
``` 

如果设置DeviceOwner报错，且提示内容为
```
java.lang.IllegalStateException: Not allowed to set the device owner because there are already some accounts on the device
```
可以通过
```
adb shell pm list users
```
查看用户数。有多个用户时，请删除非机主用户。如果仅显示只有一个机主用户，可以继续通过
```
adb shell dumpsys account
```
查看账号数量，如果有多个账号，可以退出对应的账号。

如果上述步骤都已做完，设置DeviceOwner依旧报错。最简单的方式就是恢复出厂设置。



### 使用方法

```typescript

  StartKioskMode(){
    AndroidKiosk.startKiosk((s: any)=>{
      console.log("start successfully");
    },(err:any)=>{
      console.log("start failed");
      alert('start failed');
    });
  }

  
  EndKioskMode(){
    AndroidKiosk.endKiosk((s: any)=>{
      console.log("end successfully");      
    },(err:any)=>{
      console.log("end failed");
      alert('end failed');      
    });
    
  }

  EnableAppAutoStart(){
    AndroidKiosk.enableAutoStart((s:any)=>{

    },(err:any)=>{ } );
  }

  DisableAppAutoStart(){
    AndroidKiosk.disableAutoStart((s:any)=>{

    },(err:any)=>{ } );
  }

```


### 卸载应用

设置device-owner不能直接卸载应用。因此需要先取消active-admin
```
adb shell dpm remove-active-admin {your-app-name}/com.huayu.cordova.plugin.android.kiosk.KioskModeDeviceAdminReceiver
``` 
