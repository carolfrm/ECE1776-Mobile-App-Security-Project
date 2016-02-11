package com.google.android.gms.games.achievement;

import com.google.android.gms.common.data.DataBuffer;
import com.google.android.gms.common.data.DataHolder;

public final class AchievementBuffer extends DataBuffer {
    public AchievementBuffer(DataHolder dataHolder) {
        super(dataHolder);
    }

    public Achievement get(int i) {
        return new a(this.zU, i);
    }
}