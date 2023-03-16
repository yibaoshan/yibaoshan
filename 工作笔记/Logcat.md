
## 过滤关键字

- Displayed：应用启动时长

## 日志过滤

Android Studio Dolphin（2021.3.1）版本及以后，Logcat 页面发生了变化

其中最重要的是，使用键值对搜索查询日志的方式变了，现在我们需要使用以下方式：

- tag：与日志条目的 tag 字段匹配。
- package：与日志记录应用的软件包名称匹配。
- process：与日志记录应用的进程名称匹配。
- message：与日志条目的消息部分匹配。
- level：与指定或更高严重级别的日志匹配，例如 DEBUG。
- age：如果条目时间戳是最近的，则匹配。值要指定为数字，后跟表示时间单位的字母：s 表示秒，m 表示分钟，h 表示小时，d 表示天。例如，age: 5m 只会过滤过去 5 分钟内记录的消息。

## 参考资料

- [使用 Logcat 查看日志 - Android Developer](https://developer.android.google.cn/studio/debug/logcat?hl=zh-cn#key-value-search)