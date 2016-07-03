'use strict';

function CopyrightBadge() {
    // 默认停靠位置和偏移量
    this.defaultAnchor = BMAP_ANCHOR_BOTTOM_RIGHT;
    this.defaultOffset = new BMap.Size(0, 0);
}

CopyrightBadge.prototype = new BMap.Control();
