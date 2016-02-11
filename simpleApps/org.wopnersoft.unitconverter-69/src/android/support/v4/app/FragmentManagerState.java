package android.support.v4.app;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

// compiled from: ProGuard
final class FragmentManagerState implements Parcelable {
    public static final Creator CREATOR;
    FragmentState[] mActive;
    int[] mAdded;
    BackStackState[] mBackStack;

    static {
        CREATOR = new Creator() {
            public FragmentManagerState createFromParcel(Parcel parcel) {
                return new FragmentManagerState(parcel);
            }

            public FragmentManagerState[] newArray(int i) {
                return new FragmentManagerState[i];
            }
        };
    }

    public FragmentManagerState(Parcel parcel) {
        this.mActive = (FragmentState[]) parcel.createTypedArray(FragmentState.CREATOR);
        this.mAdded = parcel.createIntArray();
        this.mBackStack = (BackStackState[]) parcel.createTypedArray(BackStackState.CREATOR);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedArray(this.mActive, i);
        parcel.writeIntArray(this.mAdded);
        parcel.writeTypedArray(this.mBackStack, i);
    }
}