
retrofit 通过 @Params / @GET / POST 注解，共同完成了 okhttp 中，对 request 封装

注解 + 动态代理的方式，使得我们只需要关注业务逻辑，请求哪个接口，无需关注，

如果再加上 kotlin 协程 suspend ，在使用上将会更加便利

- 构建 Retrofit：在 application 初始化 retrofit 实例，根据 debug 模式传入 baseUrl，创建拦截器，gson解析器给 okhttp client
- 创建 Service 接口：定义业务 interface ，指定请求路径，和返回 Response
- 生成 Call：业务层 GlobalRequest 定义全局的 create 方法 ，持有 retrofit ，根据传入的 Service 接口生成创建 Call 对象
- 执行请求：利用 suspend 定义全局的 execute 方法，接受上一步的 Call 对象，发起请求，并返回结果

## Retrofit#create()

这是使用 retrofit 发起请求的第一步动作，create 的参数是 service interface，它的主要工作

- 校验 class 是否是 interface，不合法则抛出异常
- 利用 Java Proxy#newProxyInstance 生成动态代理对象
- 在动态代理对象的 invoke 方法中，通过运行时注解，拿到方法指定的请求路径，参数等，封装成 call 对象，逻辑在 loadServiceMethod() 方法中
- 最终执行 call 的 execute() 方法时，会构建成 okhttp 需要的 request 对象，发起请求




