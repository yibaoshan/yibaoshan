## Overview
1. ContentProvider介绍
2. Uri介绍

## 一、ContentProvider介绍

在Android中常见的存储方式有：
- I/O文件
- SharePreferences
- SQLite
- 网络存储
- 还有一个就是ContentProvider了

### 1、ContentProvider创建方式

通常情况下，当前APP有大量数据需要提供给外部应用使用时，会使用的到ContentProvider
创建的方式也很简单，继承ContentProvider父类，实现query/delete/insert/update等方法就行了
通常内部也是由SQLite来实现，不过你也可以自己解析URI，自行实现内部方法

在系统中，相册、联系人等都是通过ContentProvider分享

### 2、ContentProvider使用方式

调用context的getContentResolver()方法可以获得ContentResolver实例对象，拥有该实例就可以调用query/delete/insert/update操作数据了

### 二、URI介绍

> 是一种格式：content://authority/data_path/id


