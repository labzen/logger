package cn.labzen.logger.kernel.marker;

import org.slf4j.Marker;

import java.util.Iterator;

/**
 * 有限Marker抽象基类，实现SLF4J的Marker接口。
 *
 * <p>设计目的：
 * <ul>
 *   <li>简化Marker接口实现，不需要的方法抛出异常</li>
 *   <li>适用于不需要Marker层级结构的简单场景</li>
 * </ul>
 *
 * <p>本项目中的Marker主要用于日志标记，不涉及父子Marker关系
 *
 * @see Marker
 * @see ForcedMarker
 * @see SceneMarker
 * @see StatusMarker
 * @see TagMarker
 * @see MarkerWrapper
 */
public abstract class AbstractLimitedMarker implements Marker {

  /**
   * 不支持的方法：获取Marker名称
   *
   * @throws IllegalStateException 总是抛出
   */
  @Override
  public String getName() {
    throw new IllegalStateException("unnecessary method");
  }

  /**
   * 不支持的方法：添加子Marker
   *
   * @throws IllegalStateException 总是抛出
   */
  @Override
  public void add(Marker reference) {
    throw new IllegalStateException("unnecessary method");
  }

  /**
   * 不支持的方法：移除子Marker
   *
   * @throws IllegalStateException 总是抛出
   */
  @Override
  public boolean remove(Marker reference) {
    throw new IllegalStateException("unnecessary method");
  }

  /**
   * 总是返回false，表示没有子Marker
   *
   * @return false
   */
  @Override
  public boolean hasChildren() {
    return false;
  }

  /**
   * 总是返回false，表示没有引用其他Marker
   *
   * @return false
   */
  @Override
  public boolean hasReferences() {
    return false;
  }

  /**
   * 不支持的方法：获取迭代器
   *
   * @throws IllegalStateException 总是抛出
   */
  @Override
  public Iterator<Marker> iterator() {
    throw new IllegalStateException("unnecessary method");
  }

  /**
   * 不支持的方法：检查是否包含指定Marker
   *
   * @throws IllegalStateException 总是抛出
   */
  @Override
  public boolean contains(Marker other) {
    throw new IllegalStateException("unnecessary method");
  }

  /**
   * 不支持的方法：检查是否包含指定名称的Marker
   *
   * @throws IllegalStateException 总是抛出
   */
  @Override
  public boolean contains(String name) {
    throw new IllegalStateException("unnecessary method");
  }
}