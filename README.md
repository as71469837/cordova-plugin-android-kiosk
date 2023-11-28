# Cordova Plugin Android Kisok Mode

此插件，基于DPC(DevicePolicyManager),在使用中，需要DeviceOwner权限。适用于专用设备。
详细可见：https://developer.android.google.cn/work/dpc/dedicated-devices/lock-task-mode?hl=zh-cn

### 注意点

此插件，需要DeviceOwner权限。


### 设置DeviceOwner

```
adb shell dpm set-device-owner {your-app-name}/.com.huayu.cordova.plugin.android.kiosk.KioskModeDeviceAdminReceiver
``` 

如果设置DeviceOwner报错，可以通过
```
adb shell pm list users
```
查看用户数。有多个用户时，请删除非机主用户。

如果删除非机主用户后，设置DeviceOwner依旧报错。最简单的方式就是恢复出厂设置。



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