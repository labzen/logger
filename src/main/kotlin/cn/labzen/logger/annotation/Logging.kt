package cn.labzen.logger.annotation

import cn.labzen.logger.core.EnhancedLogger

/**
 * 提供基于 JSR269 实现自动插入日志Logger属性的能力，以此注解作为标记，类似Lombok中的@Slf4j
 *
 * 使用该注解的类，将会自动加入 [EnhancedLogger] 类型属性 logger
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Logging
