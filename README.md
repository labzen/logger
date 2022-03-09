![LOGO](http://r7jiu5wkl.hd-bkt.clouddn.com/images/2022/02/19/16-34-57-167.png)

# Labzen Logger

![Labzen Logger](https://img.shields.io/badge/Labzen-Logger-green)
![Maven Central](https://img.shields.io/maven-central/v/cn.labzen/logger)
![GitHub](https://img.shields.io/github/license/labzen/logger)

![GitHub last commit](https://img.shields.io/github/last-commit/labzen/logger)
![GitHub commit activity](https://img.shields.io/github/commit-activity/m/labzen/logger)

Labzen Logger 基于SLF4j接口与Logback实现，做的日志增强组件（超强，超强，超超强！）

## Why?

在团队协作开发的过程中，就目前从业人员修炼段位（觉悟！主要是觉悟！）参差不齐的大环境下

程序猿们不论使用什么样的日志框架（哦，对！还有就是你们的口味真的难以统一），都不可避免的存在下边几个问题：

> 1. 各路神仙的日志输出内容风格不统一
> 2. 个别谁谁谁，十分喜欢用`System.out.println()`，习惯了！但谁说这个不叫日志呢
> 3. 不管日志重要与否，可劲儿的往外打，还没有意识对内容区分级别，一般都统一用info（或者把这种行为理解为，其不知道该以什么级别输出合适？）
> 4. 超长日志的存在（这就逆天了），例如打印个图片base64字符串、json字符串什么的（你开发自己看两眼也就算了，都上生产了还不现出原形），这样日志我真不爱下眼瞧，更不用说那不怎么贵的磁盘空间了
> 5. MDC和Maker功能永远没有出头之日了，当然！这也无所谓了
> 6. 日志文件太TM大了，老是download也不是个事儿，毕竟有时候就为了看那么一两行关键内容，大几百兆的下载啊，真有功夫啊
> 7. 还有......谁知道都打出来些什么玩意儿？日志的前后关系是啥？所处环境条件是啥？啥都没有，还说个P
> 8. 有时候，在某些条件下不想让他打出来可咋整，用if判断？格局小了哈
> 9. 不用说不同的项目，同一个项目输出风格都不团结，就不用往多了说。别往外说是一个团队做出来的事儿哈，太low
> 10. 不撸了，肯定还有乱七八糟的其他事儿

上边说的这些个，要碰见个爱较真儿的，那肯定就开腔了，一直这么用的挺好的啊！也能解决问题啊！！不就是看日志么，能看懂不就行了么，再说只有开发测试时看，生产上要是不出问题，谁看？？

但叫我说，这么硬刚还真TMD没毛病！退下了便是~ 不爱用拉JB倒

再但是，具备中华优良传统的团伙作案，应该考虑到...... 逼格问题！那么`cn.labzen:logger`就很有看头了（您拿这框架当装逼指南都成！）

## Ideas

基于此，解决思路大致如下：

> 1. First of all，要解决各路神仙产出的内容风格迥异，这个灰常滴..........难，我只能说尽全力吧（之后我会结合着 idea 的 plugin 来尽量解决）
> 2. `System.out.println()`这个事儿，谁爱忍谁忍，反正老子忍不了，碰到直接枪毙（也交起idea plugin了）
> 3. 日志的级别、重要程度啥的，有思路解决（不过还是要靠广大人民群众提高觉悟），而且日志贼拉好看（有点儿吹过头了）
> 4. 过长的日志，打印出来真的有意义么？看的完么？提供个机制，给你缩略个摘要看下得了
> 5. MDC还是不要随便用了吧，但是！底层我能用起来啊，同志们就甭操心了
> 6. 增加日志收集机制，以后别down文件下来了，low啊，回头得空我得整个平台......再说哈
> 7. 跟第4条差不多的事儿，如果这个框架用好了，前后关系啥的，清晰的很
> 8. 提供条件打印，告别自己if..else，虽然好像是一回事儿，额....
> 9. 日志的格式，你说了不算，嗯！你说了不算
> 10. 有了乱七八糟的再说
> 11. 思路有点儿跳跃模糊

## Features

目前可预见的功能包括

1. 基于 SLF4j, Logback 实现增强 2兼容kotlin的使用
2. 日志记录函数中，将异常参数（throwable）提升至第一位，这个要改改习惯了哦
3. 日志记录函数的参数，支持lambda函数
4. 提供Pipeline方式记录日志（类似SLF4j 2.x版本的使用）
5. Pipeline中，提供

- 根据参数选择性打印 - decide()
- 强制打印，忽略日志界别 - force() （暂不实现）
- 延迟打印 - wait() （暂不实现）
- 日志标签 - tag()
- 场景标识 - scene()
- 日志计数 - counting() （暂不实现）
- 日志阶段性打印 - phaseStart(), phasePause(), phaseEnd()（暂不实现）
- 打印JSON，XML - logJson(), logXml()
- 打印异常 - logError()
- 打印普通日志（增强） - log(), logCalculated(), logArguments()

7. 通过Pipeline打印的日志，日志格式更加丰富，并扩展placeholder - "{}"，可增加更多的功能，每个功能成为Tile

- tile对数字参数进行格式化 - {@number_}，例如参数 int x = 1, {@number_0.0}, 输出"1.0"
- tile对日期参数进行格式化 - {@date_}，例如参数 LocalDate x = LocalDate.now(), {@date_yyyy-MM}, 输出"2021-03"
- tile对参数进行字符包裹 - {@wrap_}，例如参数 String x = "123", {@wrap\_<>}, 输出"<123>"
- tile对参数进行是非判断输出 - {@whether_,}，例如参数 boolean x = true, {@whether_yes,no}, 输出"yes"
- tile控制参数的字符显示宽度 - {@width_}，例如参数 String x = "123", {@width_5}, 输出"123  "

8. Pipeline功能使用placeholder时，可出现多个tile组合使用，例如 boolean x = flase, {@whether_yes,no@wrap_()@width_5}, 输出"(no) "
9. ** 使用SPI对日志增强进行配置
10. ** 支持自动化链路追踪支撑
11. ** 支持使用redis或kafka，做日志收集支撑

## Installation

```xml

<dependency>
  <groupId>cn.labzen</groupId>
  <artifactId>logger</artifactId>
  <version>${latest_version}</version>
</dependency>
```

## Usage

先欠着吧哈，用起来很简单，自己试试

## Roadmap

再写吧，下班了

## 备注

当前版本，基于 Slf4j 1.7.36 + Logback 1.2.10，借用 logback classic 的 ContextSelector 实现 LoggerFactory 实例的获取切入。

后续版本，将基于 Slf4j 2.0 + Logback 1.3.x / Log4j 1.2.x ；基于 ServiceLoader 的 SLF4JServiceProvider 类来实现LoggerFactory 实例的获取切入。
