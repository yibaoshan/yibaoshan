## Overview
1. 广播的基本使用
2. Android广播原理浅析

## 一、基本使用

### 1、静态广播

#### 接收者：
1. 继承BroadcastReceiver类，重写onReceive方法
2. AndroidManifest.xml中使用<receiver>标签注册，action-name为广播接受标识

#### 发送者：
1. context.sendBroadcast(intent(action))

### 2、动态广播

#### 接收者：
1. 继承BroadcastReceiver类，重写onReceive方法
2. context.registerReceiver(receiver,intentFilter(action))

#### 发送者：
1. context.sendBroadcast(intent(action))

#### 注意点：
1. 记得调用unregisterReceiver()解除注册，否则会造成内存泄漏

### 3、有序广播

参考静态广播和动态广播的使用流程，唯一有差别的地方在于：
#### 接收者：
1. 静态广播：xml中intent-filter设置priority的值
2. 动态广播：intentFilter设置priority的值
注意：priority值越大优先级越高

#### 发送者：
1. context.sendOrderedBroadcast(intent,receiverPermission)

#### 注意：
1. receiverPermission和Activity的permission一样，在跨进程中，拥有相同权限AMS才会处理，防止其他APP攻击。同一进程无视
2. 拦截广播：abortBroadcast()

### 4、本地广播
参考静态广播和动态广播的使用流程，唯一有差别的地方在于：
1. 不支持发送有序广播

### 5、粘性广播(已废弃)

## 二、Android广播原理浅析

### 1、广播的注册
#### 1.1 静态广播的注册
PMS解析manifest文件

#### 1.2 动态广播的注册
AMS

### 2、广播的发送
1. Activity把广播发送到AMS中
2. AMS首先检测广播是否合法，然后根据IntentFilter规则，把所有符合条件的广播接收器整理成一个队列
3. 依次遍历队列中的广播接收器，判断是否拥有权限
4. 把广播发送到广播接收器所在进程，回调广播的onReceive方法

## 常见问题

### Q:广播的功能并不复杂，为什么被称为四大组件之一？
