
- kotlin 协程 + retrofit 完成网络请求

## 使用示例

- 在 application 初始化 retrofit 实例，根据 debug 传入 baseUrl
- 业务层 GlobalRequest 定义  ，持有 retrofit

```
OkHttpClient.Builder builder = new OkHttpClient.Builder();
retrofit = new Retrofit.Builder().addConverterFactory(xx).baseUrl(xx).client(builder).build();

@GET("xxx")
fun query(): Call<CommonResponse<xxx>>
```
