package com.a.a;

public class f implements ae {
    public Object a(float f, Object obj, Object obj2) {
        int intValue = ((Integer) obj).intValue();
        int i = intValue >> 24;
        int i2 = (intValue >> 16) & 255;
        int i3 = (intValue >> 8) & 255;
        intValue &= 255;
        int intValue2 = ((Integer) obj2).intValue();
        return Integer.valueOf((intValue + ((int) (((float) ((intValue2 & 255) - intValue)) * f))) | ((((i + ((int) (((float) ((intValue2 >> 24) - i)) * f))) << 24) | ((i2 + ((int) (((float) (((intValue2 >> 16) & 255) - i2)) * f))) << 16)) | ((((int) (((float) (((intValue2 >> 8) & 255) - i3)) * f)) + i3) << 8)));
    }
}