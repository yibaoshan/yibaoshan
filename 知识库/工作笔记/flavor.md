

```
android{
...
//定义风味维度
flavorDimensions "channel"

    //声明产品风味
    productFlavors {
       //声明风味
        huawei {
            //指定风味维度
            dimension "channel"
        }
        xiaomi {
            dimension "channel"
        }
        ...
    }

}
```

## 参考资料

- [Android Gradle flavor —— 打造不同风味的app](https://juejin.cn/post/7031399811173744648)
