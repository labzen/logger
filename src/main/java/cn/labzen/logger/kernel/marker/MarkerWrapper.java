package cn.labzen.logger.kernel.marker;

import cn.labzen.logger.meta.LoggerConfiguration;
import cn.labzen.meta.Labzens;
import org.slf4j.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * Marker包装器，组合多个Marker到一个对象中。
 *
 * <p>设计目的：
 * <ul>
 *   <li>将ForcedMarker、SceneMarker、StatusMarker、TagMarker组合</li>
 *   <li>通过单一Marker传递多个标记信息</li>
 *   <li>统一处理和渲染</li>
 * </ul>
 *
 * <p>输出格式：
 * <pre>
 * &lt;SCENE&gt; STATUS [tag1] [tag2] -
 * </pre>
 *
 * @see ForcedMarker
 * @see SceneMarker
 * @see StatusMarker
 * @see TagMarker
 */
public class MarkerWrapper extends AbstractLimitedMarker {

  /** Marker后缀分隔符，延迟初始化 */
  private static String tailed;

  /** 强制打印标记 */
  private final ForcedMarker forced;

  /** 场景标记 */
  private final SceneMarker scene;

  /** 状态标记 */
  private final StatusMarker status;

  /** 标签标记 */
  private final TagMarker tag;

  /** 引用的其他Marker */
  private List<Marker> references;

  /**
   * 构造方法
   *
   * @param forced 强制标记（可为null）
   * @param scene  场景标记（可为null）
   * @param status 状态标记（可为null）
   * @param tag    标签标记（可为null）
   */
  public MarkerWrapper(ForcedMarker forced, SceneMarker scene, StatusMarker status, TagMarker tag) {
    this.forced = forced;
    this.scene = scene;
    this.status = status;
    this.tag = tag;
  }

  /**
   * 检查是否有引用的Marker
   *
   * @return true表示有引用
   */
  public boolean hasReferences() {
    return references != null && !references.isEmpty();
  }

  /**
   * 添加引用Marker
   *
   * @param marker 被引用的Marker
   */
  public void addReference(Marker marker) {
    if (marker == null) {
      return;
    }
    if (references == null) {
      references = new ArrayList<Marker>();
    }
    references.add(marker);
  }

  /**
   * 获取强制标记
   *
   * @return ForcedMarker或null
   */
  public ForcedMarker getForced() {
    return forced;
  }

  /**
   * 获取场景标记
   *
   * @return SceneMarker或null
   */
  public SceneMarker getScene() {
    return scene;
  }

  /**
   * 获取状态标记
   *
   * @return StatusMarker或null
   */
  public StatusMarker getStatus() {
    return status;
  }

  /**
   * 获取标签标记
   *
   * @return TagMarker或null
   */
  public TagMarker getTag() {
    return tag;
  }

  /**
   * 延迟获取分隔符配置
   *
   * @return 分隔符字符串
   */
  private String getTailed() {
    if (tailed == null) {
      tailed = Labzens.configurationWith(LoggerConfiguration.class).markerTailed();
    }
    return tailed;
  }

  /**
   * 返回格式化后的Marker文本
   *
   * <p>格式：{@code <SCENE> STATUS [tag1] [tag2] -}
   *
   * @return Marker字符串
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    if (scene != null) {
      sb.append(scene).append(" ");
    }
    if (status != null) {
      sb.append(status).append(" ");
    }
    if (tag != null) {
      sb.append(tag).append(" ");
    }

    String result = sb.toString();
    if (result.isBlank()) {
      return result;
    }
    return result + getTailed();
  }
}