package cn.labzen.logger.kernel.tile.handle;

import cn.labzen.logger.kernel.tile.HeadTile;

/**
 * 占位符包装类，记录占位符的位置信息和对应的Tile链。
 *
 * <p>用于在解析阶段存储占位符的元数据
 *
 * @param position    占位符序号，从0开始
 * @param startIndex  左括号在模板中的起始位置
 * @param endIndex    右括号在模板中的结束位置
 * @param internalText 占位符内部内容（不含大括号）
 * @param firstTile   链首Tile，处理参数提取和转换
 */
public record PlaceholderWrapper(int position, int startIndex, int endIndex, String internalText,
                                 HeadTile<?> firstTile) {

}