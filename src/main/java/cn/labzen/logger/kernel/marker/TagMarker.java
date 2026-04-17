package cn.labzen.logger.kernel.marker;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签Marker，用于为日志添加多个标签。
 *
 * <p>用途：
 * <ul>
 *   <li>日志分类和筛选</li>
 *   <li>多维度标记日志</li>
 *   <li>配合日志分析工具使用</li>
 * </ul>
 *
 * <p>输出格式：{@code [tag1] [tag2] [tag3]}
 *
 * @see AbstractLimitedMarker
 */
public class TagMarker extends AbstractLimitedMarker {

  /** 标签列表 */
  private final List<String> tags;

  /**
   * 构造方法
   *
   * @param tags 标签列表
   */
  public TagMarker(List<String> tags) {
    this.tags = tags;
  }

  /**
   * 返回格式化后的标签文本
   *
   * <p>格式：{@code [tag1] [tag2] [tag3]}
   *
   * @return 标签字符串，空列表返回空字符串
   */
  @Override
  public String toString() {
    if (tags == null) {
      return "";
    }

    return tags.stream().map(t -> "[" + t + "]").collect(Collectors.joining(" "));
  }
}