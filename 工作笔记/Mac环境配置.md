
配置 Java 、Android、ADB 等环境

编辑 profile：

```
sudo vi ~/.bash_profile
```

按下键盘 i ，配置 profile：

```agsl

# android
export ANDROID_HOME=/Users/xxx/Library/Android/sdk
export PATH=${PATH}:${ANDROID_HOME}/tools
export PATH=${PATH}:${ANDROID_HOME}/platform-tools

# java
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_201.jdk/Contents/Home
export CLASSPAHT=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
export PATH=$JAVA_HOME/bin:$PATH:
```

编辑完成后，按 esc 退出插入模式，再输入 :wq 退出保存即可，立即生效：

```
source ~/.bash_profile
```

生效后验证：

```
echo $JAVA_HOME
```

## 参考资料

- [adb command not found. Mac OS](https://punjabicoder.medium.com/adb-command-not-found-mac-os-69b3f2bb3859)