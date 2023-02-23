
## 底层网络支持

```
class RealConnection {
    // 所有 http / https 连接都被包装成 call 走该方法发起请求
    void connectSocket(timeout, readTimeout, call, eventListener){
        Platform.get().connectSocket(...);
    }
}
```

okhttp 的 Platform#get() 表示支持的平台，基本都是利用 java.net.socket 发起的请求，v3.9 版本支持的有：

- findPlatform()方法首先判断是不是 Android 平台，首选 AndroidPlatform
- 其次是：Jdk9Platform、JdkWithJettyBootPlatform、Platform

至于 java.net.socket ，交由 Android 的 dalvik / art 虚拟机，或者 Java 的 Hotspot 虚拟机实现

## 四层网络封装

- 应用层（消息）：基于 TCP （HTTP / HTTPS / FTP）、基于 UDP （NTP），
- 传输层（消息 + tcp 头）：TCP 、UDP
- 网络层（消息 + tcp 头 + ip）：IP
- 网络接口层消息 + tcp 头 + ip + mac：MAC

主要关注 TCP ：面向连接，“三次握手”、双向通信

- client 调用 connect() 函数触发三次握手，在 java 中 connect 动作被包含到 socket 的构造函数中，new 对象就握手

## 三次握手

### 说人话简单版：

- 客户端：喂喂喂，听到的我说话吗？
- 服务端：可以听到，你听得到吗？
- 客户端：听到了，那我开始说正事了

### 几个传输字段：

- 序号(sequence number)：seq 序号
- 确认号（acknowledgement number）：ack 序号
- 标志位（Flags）：共6个，即URG、ACK、PSH、RST、SYN、FIN 等
  - ACK：确认序号有效。（为了与确认号ack区分开，我们用大写表示）
  - PSH：接收方应该尽快将这个报文交给应用层。
  - RST：重置连接。
  - SYN：发起一个新连接
  - FIN：释放一个连接

其中，seq 序号和 ack 序号，用于确认数据是否准确，是否能正常通信

标志位主要用于确认/更改本次的连接状态

### 加上字段版：

- **客户端**：喂喂喂，听到的我说话吗？（seq = x，SYN = 1）
  - seq 序号为：x
  - ack 确认序号：无
  - flag 只有一个：
    - SYN = 1，表示发起新连接
- **服务端**：可以听到，你听得到吗？（seq = y ; ack = x + 1 ; SYN = 1 ; ACK = 1 ）
  - seq 序号为：y，服务端重新定义了自己的新序号
  - ack 确认序号：x + 1
    - *注1：这里的 x 是客户端发起请求时定义的客户端的 seq 序号*
    - *注2：客户端根据该 ack 是否在自己发送的基础上加1，来校验是否合法 / 有效*
  - flag 有两个：
    - SYN = 1，表示这是服务端发起的新连接
    - ACK = 1，表示客户端的序号有效，校验通过
- 客户端：听到了，那我开始说正事了（seq = x + 1 ; ack = y + 1 ; ACK = 1）
  - seq 序号为：x + 1，在首次的基础上 +1，表示这是客户端第二次发起握手了
  - ack 确认序号：y + 1
    - *注1：y 是服务端定义的自身 seq 序号*
    - *注2：服务端根据该 ack 是否在自己发送的基础上加1，来校验本次通信是否合法 / 有效*
  - flag 也只有一个：
    - ACK = 1，表示服务端发送的序号有效，我校验过了

## 四次挥手

具体含义参考握手环节

```
客户端：FIN = 1 ; seq = x
服务端：ACK = 1 ; seq = y ; ack = x + 1
服务端：FIN = 1 ; ACK = 1 ; seq = z ; ack = x + 1
客户端：ACK = 1 ; seq = x + 1 ; ack = z + 1
```

以上用客户端，服务端表示其实不合适，tcp 中任何一方都可以主动发起断开请求，大致过程是

- 发起方：flag fin = 1，表示要断开了，这里的 seq 和后面的都是用来校验，先忽略
- 接收方：先回复一个 ack 表示收到了，然后开始准备清理工作
- 接收方：清理工作完成，flag fin = 1，表示可以断开了
- 发起方：回复 ACK 表示上一步有效
  - 发送完成后，发起方关闭连接
  - 接收方收到后，也关闭连接，不再回复

## 参考资料

- [TCP的三次握手各字段(ack,seq,ACK,SYN)是什么意思？](https://blog.csdn.net/weixin_48684274/article/details/108263608)