
Android 10 以及更低的版本，必须通过 USB 连接后，才可实现同一 WiFi 下无线调试。

1. 手机和电脑需连接在同一 WiFi 下； 
2. 手机开启开发者选项和 USB 调试模式，并通过 USB 连接电脑（即adb devices -l可以查看到手机）； 
3. 设置手机的监听 adb tcpip 5555; 
4. 拔掉 USB 线，找到手机的 IP 地址; 
5. 通过 IP 连接到手机adb connect ip（端口默认：5555）; 
6. adb devices -l命令查看。