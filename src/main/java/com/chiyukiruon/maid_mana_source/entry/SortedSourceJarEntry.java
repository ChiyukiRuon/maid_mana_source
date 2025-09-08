package com.chiyukiruon.maid_mana_source.entry;

import net.minecraft.core.BlockPos;

public class SortedSourceJarEntry {
    private final BlockPos pos;
    private boolean enabled; // 是否允许访问

    public SortedSourceJarEntry(BlockPos pos, boolean enabled) {
        this.pos = pos;
        this.enabled = enabled;
    }

    public BlockPos getPos() {
        return pos;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
