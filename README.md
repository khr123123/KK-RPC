🚀 PRC - Private Remote Call Framework
轻量级、高性能、可扩展的 Java RPC 框架

## 🧠 什么是 PRC？

PRC (Private Remote Call) 是我自研的一个轻量级 RPC 框架，目标是提供一种简单易用、性能优异、具备良好扩展性的远程调用解决方案。PRC
适用于分布式系统中的服务间通信，帮助你专注于业务逻辑，而无需关心底层通信细节。

## 🔥 特性亮点

✅ 轻量高效：底层基于 Netty 实现，极致性能、低延迟通信

🛠️ 服务注册与发现：内置注册中心，支持自定义实现（Zookeeper、Nacos 可插拔）

🔄 自动代理生成：无需手动实现 Stub，接口即服务

🧩 可插拔协议与序列化：支持自定义协议（如 JSON、Protobuf、Kryo 等）

🧵 异步与同步调用支持：灵活满足不同场景需求

🔐 容错与限流：结合 Sentinel 轻松实现熔断与限流

📦 Spring Boot Starter：快速集成到你的项目中

## 🛠️ 快速开始

1. 引入依赖（Maven 示例）
   ```xml
   <dependency>
      <groupId>com.khr</groupId>
      <artifactId>prc-core</artifactId>
      <version>1.0.0</version>
   </dependency>
   ```
2. 定义服务接口
   ```java
   public interface HelloService {
   String sayHello(String name);
    ```
3. 实现服务端并注册
    ```java
   @PrcService
   public class HelloServiceImpl implements HelloService {
            public String sayHello(String name) {
                    return "Hello, " + name;
            }
   }
    ```
4. 客户端调用
   ```java
   @PrcReference
   private HelloService helloService;
   public void test() {
      System.out.println(helloService.sayHello("PRC"));
   }
   ```

## 🧪 性能测试（可选）

请求数 平均延迟 QPS
10000 1.2ms 8000+

实际性能依赖于网络和硬件环境。欢迎自行测试！

## 📦 模块说明

prc-core：核心通信与代理逻辑

prc-spring：Spring Boot 集成

prc-example：使用示例项目

🙋‍♂️ 为什么选择 PRC？
自研能力展示，从协议设计到注册中心全栈开发

比肩 Dubbo 的架构能力，适合学习、教学、定制

代码简洁、模块清晰，非常适合作为中小型项目 RPC 基础设施

📎 项目地址
GitHub：https://github.com/khr123123/KK-RPC
Issue 与 PR 欢迎随时提交！

## 🌱 Spring Boot Starter 快速接入 PRC

PRC 框架已提供官方 Starter，无需复杂配置，只需三步，即可完成服务注册与远程调用！

## ✅ 1. 添加依赖

 ```xml

<dependency>
    <groupId>com.khr</groupId>
    <artifactId>prc-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

## ✅ 2. 配置 application.yml

 ```yml
prc:
application:
  name: order-service
registry:
address: localhost:8848
type: nacos # 支持 nacos、zookeeper、custom
protocol:
  name: prc
port: 20880
 ```

✅ 支持的注册中心类型包括：nacos、zookeeper，也可扩展 custom
✅ protocol.name 可自定义，如需更换序列化协议或通信协议

## ✅ 3. 编写服务和调用代码

 ```java

@PrcService
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "Hello, " + name;
    }
}
 ```

服务消费方

 ```java

@SpringBootApplication
public class ClientApp {
    @PrcReference
    private HelloService helloService;

    @PostConstruct
    public void init() {
        System.out.println(helloService.sayHello("SpringBoot PRC"));
    }

    public static void main(String[] args) {
        SpringApplication.run(ClientApp.class, args);
    }

}
 ```

## 📌 注解说明
## 注解 作用
`@PrcService` 暴露服务供远程调用
`@PrcReference` 引用远程服务（自动注入代理）

💡 实现原理概览
使用 Spring Boot 自动配置机制 spring.factories 加载 PrcAutoConfiguration

自动扫描 @PrcService 并注册至本地服务发布容器

@PrcReference 使用 BeanPostProcessor 动态代理接口

注册中心通过 SPI 加载（支持扩展）

