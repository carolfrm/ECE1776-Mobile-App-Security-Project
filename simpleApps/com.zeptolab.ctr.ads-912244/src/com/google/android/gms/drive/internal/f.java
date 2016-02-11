package com.google.android.gms.drive.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.common.internal.safeparcel.b;
import com.zeptolab.ctr.scorer.GoogleScorer;

public class f implements Creator {
    static void a(CreateContentsRequest createContentsRequest, Parcel parcel, int i) {
        int p = b.p(parcel);
        b.c(parcel, 1, createContentsRequest.wj);
        b.D(parcel, p);
    }

    public CreateContentsRequest G(Parcel parcel) {
        int o = a.o(parcel);
        int i = 0;
        while (parcel.dataPosition() < o) {
            int n = a.n(parcel);
            switch (a.S(n)) {
                case GoogleScorer.CLIENT_GAMES:
                    i = a.g(parcel, n);
                    break;
                default:
                    a.b(parcel, n);
                    break;
            }
        }
        if (parcel.dataPosition() == o) {
            return new CreateContentsRequest(i);
        }
        throw new a.a("Overread allowed size end=" + o, parcel);
    }

    public CreateContentsRequest[] al(int i) {
        return new CreateContentsRequest[i];
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return G(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return al(i);
    }
}