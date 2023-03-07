
常用的有四种，分为需要权限和不需要权限

无需申请权限：

- getFilesDir()，保存在 data/user/package/files 下
  - 目录内文件只对本应用开放，除非 root
  - 卸载时，或者用户点击清除数据时，会清空该目录
- getCacheDir()，保存在 data/user/package/cache 目录下
  - 同样只对本应用开放
  - 用户清除缓存，或卸载时，清空目录

6.0 以上需要申请写权限：

- getExternalFilesDir(dir)，保存在 storage/emulated/Android/data/package/files/dir 下
  - 该目录还挂载在 sdcard 节点下，完整目录：sdcard/Android/data/package/files/dir
  - 必须传入文件名称
  - 卸载时不会清除
  - 其他应用可查看
- getExternalCacheDir()，保存在 storage/emulated/Android/data/package/cache 下

使用外置目录时，可以先 Environment#getExternalStorageState() 判断是否 sdcard 节点是否挂载，判断设备有没有外置存储选项

