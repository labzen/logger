package cn.labzen.logger.kernel.marker;

/**
 * 强制打印Marker，用于忽略日志级别限制强制输出日志。
 *
 * <p>用途：
 * <ul>
 *   <li>配合Fluent API的{@code force()}方法使用</li>
 *   <li>即使日志级别被禁用也会输出日志</li>
 *   <li>Logback的TurboFilter通过检测此Marker决定是否放行</li>
 * </ul>
 *
 * @see MarkerWrapper
 * @see cn.labzen.logger.logback.filter.ForcedFilter
 */
public class ForcedMarker extends AbstractLimitedMarker {

}