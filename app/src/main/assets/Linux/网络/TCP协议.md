
## 三握四挥

### 三步握手

```
客户端：SYN = 1 ; seq = x
服务端：SYN = 1 ; ACK = 1 ; seq = y ; ack = x + 1
客户端：ACK = 1 ; seq = x + 1 ; ack = y + 1
```

三步握手，是为了让客户端与服务端双方，相互确认本次连接的唯一性，依赖于双方发送的ISN

避免了因为网络阻塞、网络抖动而建立起的不正常连接

并且，还可以验证两端硬件是否正常工作

### 四步挥手

```
客户端：FIN = 1 ; seq = x
服务端：ACK = 1 ; seq = y ; ack = x + 1
服务端：FIN = 1 ; ACK = 1 ; seq = z ; ack = x + 1
客户端：ACK = 1 ; seq = x + 1 ; ack = z + 1
```

## 参考资料

- [聊聊 TCP 长连接和心跳那些事](https://www.cnkirito.moe/tcp-talk/)