package android.support.v4.c;

// compiled from: ProGuard
class c {
    static final int[] a;
    static final long[] b;
    static final Object[] c;

    static {
        a = new int[0];
        b = new long[0];
        c = new Object[0];
    }

    public static int a(int i) {
        return c(i * 4) / 4;
    }

    static int a(int[] iArr, int i, int i2) {
        int i3 = 0;
        int i4 = i - 1;
        while (i3 <= i4) {
            int i5 = (i3 + i4) >>> 1;
            int i6 = iArr[i5];
            if (i6 < i2) {
                i3 = i5 + 1;
            } else if (i6 <= i2) {
                return i5;
            } else {
                i4 = i5 - 1;
            }
        }
        return i3 ^ -1;
    }

    static int a(long[] jArr, int i, long j) {
        int i2 = 0;
        int i3 = i - 1;
        while (i2 <= i3) {
            int i4 = (i2 + i3) >>> 1;
            long j2 = jArr[i4];
            if (j2 < j) {
                i2 = i4 + 1;
            } else if (j2 <= j) {
                return i4;
            } else {
                i3 = i4 - 1;
            }
        }
        return i2 ^ -1;
    }

    public static boolean a(Object obj, Object obj2) {
        return obj == obj2 || (obj != null && obj.equals(obj2));
    }

    public static int b(int i) {
        return c(i * 8) / 8;
    }

    public static int c_(int i) {
        int i2 = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_DEFAULT;
        while (i2 < 32) {
            if (i <= 1 << i2 - 12) {
                return 1 << i2 - 12;
            }
            i2++;
        }
        return i;
    }
}