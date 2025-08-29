package cn.labzen.logger.kernel.tile.handle;

import cn.labzen.logger.kernel.tile.HeadTile;

public record PlaceholderWrapper(int position, int startIndex, int endIndex, String internalText,
                                 HeadTile<?> firstTile) {

}
