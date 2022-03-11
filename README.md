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

> 1. 各路神仙的日志输出，内容的风格不统一，还有那种随心所欲的~
> 2. 个别那个谁谁谁，十分喜欢用`System.out.println()`，老八辈儿习惯了！但谁说这个不叫日志呢
> 3. 不管日志重要与否，可劲儿的往外打就完了！并没有意识要对日志内容进行级别区分，统一用info级别（或者可以把这种行为理解为，不知道该以什么级别输出合适？）
> 4. 超长日志的存在（这种逆天操作还挺常见的）。例如打印个图片base64字符串、json字符串什么的（你开发自己看两眼也就算了，都上生产了还不现出原形），这样日志我真不爱下眼瞧，更不用说那不怎么贵的磁盘空间了
> 5. MDC和Maker功能永远没有出头之日了，当然！这也无所谓了
> 6. 日志文件太TM大了，老是download也不是个事儿，毕竟有时候就为了看那么一两行关键内容，大几百兆的下载啊，真有功夫啊
> 7. 还有......就算是自己的代码，看日志的时候，也TM不一定知道打出来的些什么玩意儿~！当时打日志的前后关系是啥？所处环境条件是啥？啥都没有，还打个P
> 8. 有时候，在某些条件下不想让他打出来可咋整，用if判断？格局小了哈
> 9. 不用说在不同的项目下了，就连同一个项目中输出风格都不团结，就不用往多了说。别往外说是一个团队做出来的事儿哈，太low
> 10. 不撸了，肯定还有乱七八糟的其他事儿

上边说的这些个，要碰见个爱较真儿的，那肯定就开腔了：一直这么用的挺好的啊！也能解决问题啊！！不就是看日志么，能看懂不就行了么，再说只有开发测试时看，生产上要是不出问题，谁看？？

但叫我说，这么硬刚还真TMD没毛病！出门左转不送~ 不爱用拉JB倒

再但是，具备中华优良传统的团伙作案，应该考虑到...... 逼格问题！那么`cn.labzen:logger`就很有看头了（您拿这框架当装逼指南就成！）

## Ideas

基于上述痒点，解决思路大致如下：

> 1. Firstly，要解决各路神仙产出的内容风格迥异，这个灰常滴..........难，我只能说尽全力吧（之后我会尝试结合着 idea 的 plugin 来尽量解决）
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

目前已实现的功能包括

1. 基于 SLF4j 1.7.x + Logback 1.2.x 实现
2. 兼容kotlin的使用，`val logger = logger {}`
3. SLF4j Logger 接口的实现类`EnhancedLogger`增加了几个与异常相关的记录方法，并将异常参数（throwable）放至第一位
4. 增加`PipedLogger`实现类似 SLF4j 2.x 中的 [fluent logging API](https://www.slf4j.org/manual.html#fluent)
   的日志记录方式
5. 通过`EnhancedLogger`获取`PipedLogger`，并提供了如下方法：
    1. `decide(boolean)`: 可根据布尔值控制日志是否打印
    2. `tag(string..)`: 对日志标注（多个）标签
    3. `scene(enum)`: 标记当前日志的场景，场景为预定义的枚举类型，例如：SUCCESS, COMPLETE, IMPORTANT, TODO 等
    4. `logJson(), logXml()`: 打印JSON或XML（带有格式化），方便查看
    5. `log(), logArguments(), logCalculated(), logError()`: 普通日志（增强）打印、异常
6. 通过`PipedLogger`打印的日志，可使用 SLF4j 传统的`{}` 占位符，来打印参数值；另外扩展了多个占位符 "{@xxx}"，可增加更多的功能。在这里，每个占位符功能被称为`Tile`
    1. `{@number_}`: 对数字参数进行格式化，可接受整数、浮点数类型，下划线后接格式化pattern，pattern规则参考`DecimalFormat`类。例如：定义参数`int x = 1`，
       `logArguments(“number is {@number_0.0}”, x)`，日志输出`number is 1.0`
    2. `{@date_}`: 对日期时间参数进行格式化，可接受`LocalDate, LocalTime, LocalDateTime, Date`等类型，下划线后跟日期时间格式化字符串。例如：定义参数
       `LocalDate x = LocalDate.now()`，`logArguments(“现在是{@date_yyyy年MM月}”, x)`，日志输出`现在是2022年03月`
    3. `{@wrap_}`：对参数值进行字符串包裹，下划线后必须跟2个字符，分别为包裹的前缀与后缀字符。例如：定义参数`double x = 12.34`，
       `logArguments(“important: {@wrap_[]}”, x)`，日志输出`important: [12.34]`
    5. `{@whether_}`：判断输出，对boolean类型的参数进行预定义输出，下划线后跟两个字符串，使用英文逗号分隔，参数为true时，输出第一个字符串，否则输出另一个。例如：
       定义参数`boolean completed = true`，`logArguments(“job is done: {@whether_yes,no}”, completed)`，日志输出`job is done: yes`
    6. `{@width_}`：控制参数的显示宽度，下划线后最多可跟两个正整数宽度，用英文逗号分隔。第一个数字代表最小位宽，第二个数字代表最大位宽（可忽略）。
       例如：定义参数`String str = "content"`。`logArguments(“fixed width string: '{@width_8}'.”, str)`，
       日志输出`fixed width string: 'content '.`；`logArguments(“fixed width string: '{@width_0,4}'.”, str)`，
       日志输出`fixed width string: 'cont'.`；
7. `PipedLogger`使用占位符功能时，可出现多个`Tile`组合使用。例如：
   `logArguments("r u ready? : '{@whether_yes,no@wrap_()@width_5}'", false)`，日志输出`r u ready? : '(no) '`
   `logArguments("r u ready? : '{@whether_yes,no@wrap_()@width_5}'", true)`，日志输出`r u ready? : '(yes)'`

## TODO

在 `PipedLogger` 中实现如下功能：

1. `force(boolean)`: 是否强制打印日志，即忽略本条日志所指定的级别是否允许打印，例如日志为debug级别，但当前环境的打印级别为warn，当参数为true时，级别不变日志也能打印出来（带有特殊标识，加以区分）
2. `wait(func)`: 延迟打印，等待参数指定的匿名函数执行完成后，再打印日志
3. `counting()`: 对日志计数，使用后，本条日志的打印将会计数，可简化通过日志调试程序的场景
4. `phaseStart(), phasePause(), phaseEnd()`: 阶段性日志，可针对一次业务处理流程，分不同的阶段打印日志，并统计执行时长

针对日志使用中的一些规则，进行基于Java Code的灵活配置

考虑加入对自动化链路追踪的功能支撑

支持能以最低的代码配置，使用Redis或Kafka自动收集日志

开发IDEA的插件，支持JSR269，自动注入logger

## Installation

```xml

<dependency>
  <groupId>cn.labzen</groupId>
  <artifactId>logger</artifactId>
  <version>${latest_version}</version>
</dependency>
```

## Usage

### 传统获取Logger方式

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// 也可以使用Lombok的@Slf4j注解，效果是一样的
public class SimpleBizClass {

  private static Logger logger = LoggerFactory.getLogger(SimpleBizClass.class);

  public void someFunction() {
    // 日志输出内容为：logger的实现类为 : class cn.labzen.logger.core.EnhancedLogger
    logger.info("logger的实现类为 : {}", logger.getClass());
  }
}
```
不管使用Lombok还是使用LoggerFactory获取到的都是SLF4j的Logger接口。所以只能使用基本功能。

Logger接口的实现类是`EnhancedLogger`，所以可以强转类型，以使用更多的功能。
```java
EnhancedLogger enhancedLogger = (EnhancedLogger) logger;
// 使用PipedLogger的功能，根据不同的Level获取
PipedLogger pipedLogger = enhancedLogger.info();
```

### 通过Labzen Logger的方式

```java
import cn.labzen.logger.Loggers;
import cn.labzen.logger.core.EnhancedLogger;

public class SimpleBizClass {

  private static EnhancedLogger logger = Loggers.getLogger(SimpleBizClass.class);

  public void someFunction() {
    logger.info().logArguments("{@number_0.00}", 1.2);
  }
}
```

搭配IDEA的插件，也可以类似Lombok的使用方式

```java
import cn.labzen.logger.annotation.Logging;

@Logging
public class SimpleBizClass {

  public void someFunction() {
    logger.info().logArguments("{@number_0.00}", 1.2);
  }
}
```

## 备注

当前版本，基于 Slf4j 1.7.36 + Logback 1.2.10，借用 logback classic 的 ContextSelector 实现 LoggerFactory 实例的获取切入。

后续版本，将基于 Slf4j 2.0 + Logback 1.3.x / Log4j 1.2.x ；基于 ServiceLoader 的 SLF4JServiceProvider 类来实现LoggerFactory 实例的获取切入。
