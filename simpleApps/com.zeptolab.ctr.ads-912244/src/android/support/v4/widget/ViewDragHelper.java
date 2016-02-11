package android.support.v4.widget;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import com.google.android.gms.location.LocationStatusCodes;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.zeptolab.ctr.billing.google.utils.IabHelper;
import java.util.Arrays;

public class ViewDragHelper {
    private static final int BASE_SETTLE_DURATION = 256;
    public static final int DIRECTION_ALL = 3;
    public static final int DIRECTION_HORIZONTAL = 1;
    public static final int DIRECTION_VERTICAL = 2;
    public static final int EDGE_ALL = 15;
    public static final int EDGE_BOTTOM = 8;
    public static final int EDGE_LEFT = 1;
    public static final int EDGE_RIGHT = 2;
    private static final int EDGE_SIZE = 20;
    public static final int EDGE_TOP = 4;
    public static final int INVALID_POINTER = -1;
    private static final int MAX_SETTLE_DURATION = 600;
    public static final int STATE_DRAGGING = 1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_SETTLING = 2;
    private static final String TAG = "ViewDragHelper";
    private static final Interpolator sInterpolator;
    private int mActivePointerId;
    private final Callback mCallback;
    private View mCapturedView;
    private int mDragState;
    private int[] mEdgeDragsInProgress;
    private int[] mEdgeDragsLocked;
    private int mEdgeSize;
    private int[] mInitialEdgesTouched;
    private float[] mInitialMotionX;
    private float[] mInitialMotionY;
    private float[] mLastMotionX;
    private float[] mLastMotionY;
    private float mMaxVelocity;
    private float mMinVelocity;
    private final ViewGroup mParentView;
    private int mPointersDown;
    private boolean mReleaseInProgress;
    private ScrollerCompat mScroller;
    private final Runnable mSetIdleRunnable;
    private int mTouchSlop;
    private int mTrackingEdges;
    private VelocityTracker mVelocityTracker;

    public static abstract class Callback {
        public int clampViewPositionHorizontal(View view, int i, int i2) {
            return STATE_IDLE;
        }

        public int clampViewPositionVertical(View view, int i, int i2) {
            return STATE_IDLE;
        }

        public int getOrderedChildIndex(int i) {
            return i;
        }

        public int getViewHorizontalDragRange(View view) {
            return STATE_IDLE;
        }

        public int getViewVerticalDragRange(View view) {
            return STATE_IDLE;
        }

        public void onEdgeDragStarted(int i, int i2) {
        }

        public boolean onEdgeLock(int i) {
            return false;
        }

        public void onEdgeTouched(int i, int i2) {
        }

        public void onViewCaptured(View view, int i) {
        }

        public void onViewDragStateChanged(int i) {
        }

        public void onViewPositionChanged(View view, int i, int i2, int i3, int i4) {
        }

        public void onViewReleased(View view, float f, float f2) {
        }

        public abstract boolean tryCaptureView(View view, int i);
    }

    static {
        sInterpolator = new Interpolator() {
            public float getInterpolation(float f) {
                float f2 = f - 1.0f;
                return f2 * (((f2 * f2) * f2) * f2) + 1.0f;
            }
        };
    }

    private ViewDragHelper(Context context, ViewGroup viewGroup, Callback callback) {
        this.mActivePointerId = -1;
        this.mSetIdleRunnable = new Runnable() {
            public void run() {
                ViewDragHelper.this.setDragState(STATE_IDLE);
            }
        };
        if (viewGroup == null) {
            throw new IllegalArgumentException("Parent view may not be null");
        } else if (callback == null) {
            throw new IllegalArgumentException("Callback may not be null");
        } else {
            this.mParentView = viewGroup;
            this.mCallback = callback;
            ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
            this.mEdgeSize = (int) (context.getResources().getDisplayMetrics().density * 20.0f + 0.5f);
            this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
            this.mMaxVelocity = (float) viewConfiguration.getScaledMaximumFlingVelocity();
            this.mMinVelocity = (float) viewConfiguration.getScaledMinimumFlingVelocity();
            this.mScroller = ScrollerCompat.create(context, sInterpolator);
        }
    }

    private boolean checkNewEdgeDrag(float f, float f2, int i, int i2) {
        float abs = Math.abs(f);
        float abs2 = Math.abs(f2);
        if ((this.mInitialEdgesTouched[i] & i2) != i2 || (this.mTrackingEdges & i2) == 0 || (this.mEdgeDragsLocked[i] & i2) == i2 || (this.mEdgeDragsInProgress[i] & i2) == i2) {
            return false;
        }
        if (abs <= ((float) this.mTouchSlop) && abs2 <= ((float) this.mTouchSlop)) {
            return false;
        }
        if (abs < abs2 * 0.5f && this.mCallback.onEdgeLock(i2)) {
            int[] iArr = this.mEdgeDragsLocked;
            iArr[i] = iArr[i] | i2;
            return false;
        } else if ((this.mEdgeDragsInProgress[i] & i2) != 0 || abs <= ((float) this.mTouchSlop)) {
            return false;
        } else {
            return true;
        }
    }

    private boolean checkTouchSlop(View view, float f, float f2) {
        if (view == null) {
            return false;
        }
        int i = this.mCallback.getViewHorizontalDragRange(view) > 0;
        int i2 = this.mCallback.getViewVerticalDragRange(view) > 0;
        if (i == 0 || i2 == 0) {
            if (i != 0) {
                return Math.abs(f) > ((float) this.mTouchSlop);
            } else if (i2 == 0) {
                return false;
            } else {
                return Math.abs(f2) > ((float) this.mTouchSlop);
            }
        } else if (f * f + f2 * f2 <= ((float) (this.mTouchSlop * this.mTouchSlop))) {
            return false;
        } else {
            return true;
        }
    }

    private float clampMag(float f, float f2, float f3) {
        float abs = Math.abs(f);
        if (abs < f2) {
            return 0.0f;
        }
        if (abs <= f3) {
            return f;
        }
        return f <= 0.0f ? -f3 : f3;
    }

    private int clampMag(int i, int i2, int i3) {
        int abs = Math.abs(i);
        if (abs < i2) {
            return STATE_IDLE;
        }
        if (abs <= i3) {
            return i;
        }
        return i <= 0 ? -i3 : i3;
    }

    private void clearMotionHistory() {
        if (this.mInitialMotionX != null) {
            Arrays.fill(this.mInitialMotionX, BitmapDescriptorFactory.HUE_RED);
            Arrays.fill(this.mInitialMotionY, BitmapDescriptorFactory.HUE_RED);
            Arrays.fill(this.mLastMotionX, BitmapDescriptorFactory.HUE_RED);
            Arrays.fill(this.mLastMotionY, BitmapDescriptorFactory.HUE_RED);
            Arrays.fill(this.mInitialEdgesTouched, STATE_IDLE);
            Arrays.fill(this.mEdgeDragsInProgress, STATE_IDLE);
            Arrays.fill(this.mEdgeDragsLocked, STATE_IDLE);
            this.mPointersDown = 0;
        }
    }

    private void clearMotionHistory(int i) {
        if (this.mInitialMotionX != null) {
            this.mInitialMotionX[i] = 0.0f;
            this.mInitialMotionY[i] = 0.0f;
            this.mLastMotionX[i] = 0.0f;
            this.mLastMotionY[i] = 0.0f;
            this.mInitialEdgesTouched[i] = 0;
            this.mEdgeDragsInProgress[i] = 0;
            this.mEdgeDragsLocked[i] = 0;
            this.mPointersDown &= (1 << i) ^ -1;
        }
    }

    private int computeAxisDuration(int i, int i2, int i3) {
        if (i == 0) {
            return STATE_IDLE;
        }
        int width = this.mParentView.getWidth();
        int i4 = width / 2;
        float distanceInfluenceForSnapDuration = distanceInfluenceForSnapDuration(Math.min(1.0f, ((float) Math.abs(i)) / ((float) width))) * ((float) i4) + ((float) i4);
        i4 = Math.abs(i2);
        return Math.min(i4 > 0 ? Math.round(Math.abs(distanceInfluenceForSnapDuration / ((float) i4)) * 1000.0f) * 4 : (int) (((((float) Math.abs(i)) / ((float) i3)) + 1.0f) * 256.0f), MAX_SETTLE_DURATION);
    }

    private int computeSettleDuration(View view, int i, int i2, int i3, int i4) {
        int clampMag = clampMag(i3, (int) this.mMinVelocity, (int) this.mMaxVelocity);
        int clampMag2 = clampMag(i4, (int) this.mMinVelocity, (int) this.mMaxVelocity);
        int abs = Math.abs(i);
        int abs2 = Math.abs(i2);
        int abs3 = Math.abs(clampMag);
        int abs4 = Math.abs(clampMag2);
        int i5 = abs3 + abs4;
        int i6 = abs + abs2;
        return (int) ((clampMag2 != 0 ? ((float) abs4) / ((float) i5) : ((float) abs2) / ((float) i6)) * ((float) computeAxisDuration(i2, clampMag2, this.mCallback.getViewVerticalDragRange(view))) + (clampMag != 0 ? ((float) abs3) / ((float) i5) : ((float) abs) / ((float) i6)) * ((float) computeAxisDuration(i, clampMag, this.mCallback.getViewHorizontalDragRange(view))));
    }

    public static ViewDragHelper create(ViewGroup viewGroup, float f, Callback callback) {
        ViewDragHelper create = create(viewGroup, callback);
        create.mTouchSlop = (int) (((float) create.mTouchSlop) * (1.0f / f));
        return create;
    }

    public static ViewDragHelper create(ViewGroup viewGroup, Callback callback) {
        return new ViewDragHelper(viewGroup.getContext(), viewGroup, callback);
    }

    private void dispatchViewReleased(float f, float f2) {
        this.mReleaseInProgress = true;
        this.mCallback.onViewReleased(this.mCapturedView, f, f2);
        this.mReleaseInProgress = false;
        if (this.mDragState == 1) {
            setDragState(STATE_IDLE);
        }
    }

    private float distanceInfluenceForSnapDuration(float f) {
        return (float) Math.sin((double) ((float) (((double) (f - 0.5f)) * 0.4712389167638204d)));
    }

    private void dragTo(int i, int i2, int i3, int i4) {
        int clampViewPositionHorizontal;
        int clampViewPositionVertical;
        int left = this.mCapturedView.getLeft();
        int top = this.mCapturedView.getTop();
        if (i3 != 0) {
            clampViewPositionHorizontal = this.mCallback.clampViewPositionHorizontal(this.mCapturedView, i, i3);
            this.mCapturedView.offsetLeftAndRight(clampViewPositionHorizontal - left);
        } else {
            clampViewPositionHorizontal = i;
        }
        if (i4 != 0) {
            clampViewPositionVertical = this.mCallback.clampViewPositionVertical(this.mCapturedView, i2, i4);
            this.mCapturedView.offsetTopAndBottom(clampViewPositionVertical - top);
        } else {
            clampViewPositionVertical = i2;
        }
        if (i3 != 0 || i4 != 0) {
            this.mCallback.onViewPositionChanged(this.mCapturedView, clampViewPositionHorizontal, clampViewPositionVertical, clampViewPositionHorizontal - left, clampViewPositionVertical - top);
        }
    }

    private void ensureMotionHistorySizeForId(int i) {
        if (this.mInitialMotionX == null || this.mInitialMotionX.length <= i) {
            Object obj = new Object[(i + 1)];
            Object obj2 = new Object[(i + 1)];
            Object obj3 = new Object[(i + 1)];
            Object obj4 = new Object[(i + 1)];
            Object obj5 = new Object[(i + 1)];
            Object obj6 = new Object[(i + 1)];
            Object obj7 = new Object[(i + 1)];
            if (this.mInitialMotionX != null) {
                System.arraycopy(this.mInitialMotionX, STATE_IDLE, obj, STATE_IDLE, this.mInitialMotionX.length);
                System.arraycopy(this.mInitialMotionY, STATE_IDLE, obj2, STATE_IDLE, this.mInitialMotionY.length);
                System.arraycopy(this.mLastMotionX, STATE_IDLE, obj3, STATE_IDLE, this.mLastMotionX.length);
                System.arraycopy(this.mLastMotionY, STATE_IDLE, obj4, STATE_IDLE, this.mLastMotionY.length);
                System.arraycopy(this.mInitialEdgesTouched, STATE_IDLE, obj5, STATE_IDLE, this.mInitialEdgesTouched.length);
                System.arraycopy(this.mEdgeDragsInProgress, STATE_IDLE, obj6, STATE_IDLE, this.mEdgeDragsInProgress.length);
                System.arraycopy(this.mEdgeDragsLocked, STATE_IDLE, obj7, STATE_IDLE, this.mEdgeDragsLocked.length);
            }
            this.mInitialMotionX = obj;
            this.mInitialMotionY = obj2;
            this.mLastMotionX = obj3;
            this.mLastMotionY = obj4;
            this.mInitialEdgesTouched = obj5;
            this.mEdgeDragsInProgress = obj6;
            this.mEdgeDragsLocked = obj7;
        }
    }

    private boolean forceSettleCapturedViewAt(int i, int i2, int i3, int i4) {
        int left = this.mCapturedView.getLeft();
        int top = this.mCapturedView.getTop();
        int i5 = i - left;
        int i6 = i2 - top;
        if (i5 == 0 && i6 == 0) {
            this.mScroller.abortAnimation();
            setDragState(STATE_IDLE);
            return false;
        } else {
            this.mScroller.startScroll(left, top, i5, i6, computeSettleDuration(this.mCapturedView, i5, i6, i3, i4));
            setDragState(STATE_SETTLING);
            return true;
        }
    }

    private int getEdgesTouched(int i, int i2) {
        int i3 = STATE_IDLE;
        if (i < this.mParentView.getLeft() + this.mEdgeSize) {
            i3 = STATE_DRAGGING;
        }
        if (i2 < this.mParentView.getTop() + this.mEdgeSize) {
            i3 |= 4;
        }
        if (i > this.mParentView.getRight() - this.mEdgeSize) {
            i3 |= 2;
        }
        return i2 > this.mParentView.getBottom() - this.mEdgeSize ? i3 | 8 : i3;
    }

    private void releaseViewForPointerUp() {
        this.mVelocityTracker.computeCurrentVelocity(LocationStatusCodes.GEOFENCE_NOT_AVAILABLE, this.mMaxVelocity);
        dispatchViewReleased(clampMag(VelocityTrackerCompat.getXVelocity(this.mVelocityTracker, this.mActivePointerId), this.mMinVelocity, this.mMaxVelocity), clampMag(VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, this.mActivePointerId), this.mMinVelocity, this.mMaxVelocity));
    }

    private void reportNewEdgeDrags(float f, float f2, int i) {
        int i2 = STATE_DRAGGING;
        if (!checkNewEdgeDrag(f, f2, i, STATE_DRAGGING)) {
            i2 = 0;
        }
        if (checkNewEdgeDrag(f2, f, i, EDGE_TOP)) {
            i2 |= 4;
        }
        if (checkNewEdgeDrag(f, f2, i, STATE_SETTLING)) {
            i2 |= 2;
        }
        if (checkNewEdgeDrag(f2, f, i, EDGE_BOTTOM)) {
            i2 |= 8;
        }
        if (i2 != 0) {
            int[] iArr = this.mEdgeDragsInProgress;
            iArr[i] = iArr[i] | i2;
            this.mCallback.onEdgeDragStarted(i2, i);
        }
    }

    private void saveInitialMotion(float f, float f2, int i) {
        ensureMotionHistorySizeForId(i);
        float[] fArr = this.mInitialMotionX;
        this.mLastMotionX[i] = f;
        fArr[i] = f;
        fArr = this.mInitialMotionY;
        this.mLastMotionY[i] = f2;
        fArr[i] = f2;
        this.mInitialEdgesTouched[i] = getEdgesTouched((int) f, (int) f2);
        this.mPointersDown |= 1 << i;
    }

    private void saveLastMotion(MotionEvent motionEvent) {
        int pointerCount = MotionEventCompat.getPointerCount(motionEvent);
        int i = STATE_IDLE;
        while (i < pointerCount) {
            int pointerId = MotionEventCompat.getPointerId(motionEvent, i);
            float x = MotionEventCompat.getX(motionEvent, i);
            float y = MotionEventCompat.getY(motionEvent, i);
            this.mLastMotionX[pointerId] = x;
            this.mLastMotionY[pointerId] = y;
            i++;
        }
    }

    public void abort() {
        cancel();
        if (this.mDragState == 2) {
            int currX = this.mScroller.getCurrX();
            int currY = this.mScroller.getCurrY();
            this.mScroller.abortAnimation();
            int currX2 = this.mScroller.getCurrX();
            int currY2 = this.mScroller.getCurrY();
            this.mCallback.onViewPositionChanged(this.mCapturedView, currX2, currY2, currX2 - currX, currY2 - currY);
        }
        setDragState(STATE_IDLE);
    }

    protected boolean canScroll(View view, boolean z, int i, int i2, int i3, int i4) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int scrollX = view.getScrollX();
            int scrollY = view.getScrollY();
            int i5 = viewGroup.getChildCount() - 1;
            while (i5 >= 0) {
                View childAt = viewGroup.getChildAt(i5);
                if (i3 + scrollX >= childAt.getLeft() && i3 + scrollX < childAt.getRight() && i4 + scrollY >= childAt.getTop() && i4 + scrollY < childAt.getBottom()) {
                    if (canScroll(childAt, true, i, i2, i3 + scrollX - childAt.getLeft(), i4 + scrollY - childAt.getTop())) {
                        return true;
                    }
                }
                i5--;
            }
        }
        return z && (ViewCompat.canScrollHorizontally(view, -i) || ViewCompat.canScrollVertically(view, -i2));
    }

    public void cancel() {
        this.mActivePointerId = -1;
        clearMotionHistory();
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    public void captureChildView(View view, int i) {
        if (view.getParent() != this.mParentView) {
            throw new IllegalArgumentException("captureChildView: parameter must be a descendant of the ViewDragHelper's tracked parent view (" + this.mParentView + ")");
        }
        this.mCapturedView = view;
        this.mActivePointerId = i;
        this.mCallback.onViewCaptured(view, i);
        setDragState(STATE_DRAGGING);
    }

    public boolean checkTouchSlop(int i) {
        int length = this.mInitialMotionX.length;
        int i2 = 0;
        while (i2 < length) {
            if (checkTouchSlop(i, i2)) {
                return true;
            }
            i2++;
        }
        return false;
    }

    public boolean checkTouchSlop(int i, int i2) {
        if (!isPointerDown(i2)) {
            return false;
        }
        int i3 = (i & 1) == 1;
        int i4 = (i & 2) == 2;
        float f = this.mLastMotionX[i2] - this.mInitialMotionX[i2];
        float f2 = this.mLastMotionY[i2] - this.mInitialMotionY[i2];
        if (i3 == 0 || i4 == 0) {
            if (i3 != 0) {
                return Math.abs(f) > ((float) this.mTouchSlop);
            } else if (i4 == 0) {
                return false;
            } else {
                return Math.abs(f2) > ((float) this.mTouchSlop);
            }
        } else if (f * f + f2 * f2 <= ((float) (this.mTouchSlop * this.mTouchSlop))) {
            return false;
        } else {
            return true;
        }
    }

    public boolean continueSettling(boolean z) {
        if (this.mDragState == 2) {
            boolean isFinished;
            boolean computeScrollOffset = this.mScroller.computeScrollOffset();
            int currX = this.mScroller.getCurrX();
            int currY = this.mScroller.getCurrY();
            int left = currX - this.mCapturedView.getLeft();
            int top = currY - this.mCapturedView.getTop();
            if (left != 0) {
                this.mCapturedView.offsetLeftAndRight(left);
            }
            if (top != 0) {
                this.mCapturedView.offsetTopAndBottom(top);
            }
            if (!(left == 0 && top == 0)) {
                this.mCallback.onViewPositionChanged(this.mCapturedView, currX, currY, left, top);
            }
            if (computeScrollOffset && currX == this.mScroller.getFinalX() && currY == this.mScroller.getFinalY()) {
                this.mScroller.abortAnimation();
                isFinished = this.mScroller.isFinished();
            } else {
                isFinished = computeScrollOffset;
            }
            if (!isFinished) {
                if (z) {
                    this.mParentView.post(this.mSetIdleRunnable);
                } else {
                    setDragState(STATE_IDLE);
                }
            }
        }
        return this.mDragState == 2;
    }

    public View findTopChildUnder(int i, int i2) {
        int i3 = this.mParentView.getChildCount() - 1;
        while (i3 >= 0) {
            View childAt = this.mParentView.getChildAt(this.mCallback.getOrderedChildIndex(i3));
            if (i >= childAt.getLeft() && i < childAt.getRight() && i2 >= childAt.getTop() && i2 < childAt.getBottom()) {
                return childAt;
            }
            i3--;
        }
        return null;
    }

    public void flingCapturedView(int i, int i2, int i3, int i4) {
        if (this.mReleaseInProgress) {
            this.mScroller.fling(this.mCapturedView.getLeft(), this.mCapturedView.getTop(), (int) VelocityTrackerCompat.getXVelocity(this.mVelocityTracker, this.mActivePointerId), (int) VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, this.mActivePointerId), i, i3, i2, i4);
            setDragState(STATE_SETTLING);
        } else {
            throw new IllegalStateException("Cannot flingCapturedView outside of a call to Callback#onViewReleased");
        }
    }

    public int getActivePointerId() {
        return this.mActivePointerId;
    }

    public View getCapturedView() {
        return this.mCapturedView;
    }

    public int getEdgeSize() {
        return this.mEdgeSize;
    }

    public float getMinVelocity() {
        return this.mMinVelocity;
    }

    public int getTouchSlop() {
        return this.mTouchSlop;
    }

    public int getViewDragState() {
        return this.mDragState;
    }

    public boolean isCapturedViewUnder(int i, int i2) {
        return isViewUnder(this.mCapturedView, i, i2);
    }

    public boolean isEdgeTouched(int i) {
        int length = this.mInitialEdgesTouched.length;
        int i2 = 0;
        while (i2 < length) {
            if (isEdgeTouched(i, i2)) {
                return true;
            }
            i2++;
        }
        return false;
    }

    public boolean isEdgeTouched(int i, int i2) {
        return isPointerDown(i2) && (this.mInitialEdgesTouched[i2] & i) != 0;
    }

    public boolean isPointerDown(int i) {
        return (this.mPointersDown & (1 << i)) != 0;
    }

    public boolean isViewUnder(View view, int i, int i2) {
        return view != null && i >= view.getLeft() && i < view.getRight() && i2 >= view.getTop() && i2 < view.getBottom();
    }

    public void processTouchEvent(MotionEvent motionEvent) {
        int i = STATE_IDLE;
        int actionMasked = MotionEventCompat.getActionMasked(motionEvent);
        int actionIndex = MotionEventCompat.getActionIndex(motionEvent);
        if (actionMasked == 0) {
            cancel();
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
        float x;
        float y;
        View findTopChildUnder;
        int i2;
        switch (actionMasked) {
            case STATE_IDLE:
                x = motionEvent.getX();
                y = motionEvent.getY();
                i = MotionEventCompat.getPointerId(motionEvent, STATE_IDLE);
                findTopChildUnder = findTopChildUnder((int) x, (int) y);
                saveInitialMotion(x, y, i);
                tryCaptureViewForDrag(findTopChildUnder, i);
                i2 = this.mInitialEdgesTouched[i];
                if ((this.mTrackingEdges & i2) != 0) {
                    this.mCallback.onEdgeTouched(i2 & this.mTrackingEdges, i);
                }
            case STATE_DRAGGING:
                if (this.mDragState == 1) {
                    releaseViewForPointerUp();
                }
                cancel();
            case STATE_SETTLING:
                if (this.mDragState == 1) {
                    i = MotionEventCompat.findPointerIndex(motionEvent, this.mActivePointerId);
                    x = MotionEventCompat.getX(motionEvent, i);
                    i2 = (int) (x - this.mLastMotionX[this.mActivePointerId]);
                    i = (int) (MotionEventCompat.getY(motionEvent, i) - this.mLastMotionY[this.mActivePointerId]);
                    dragTo(this.mCapturedView.getLeft() + i2, this.mCapturedView.getTop() + i, i2, i);
                    saveLastMotion(motionEvent);
                } else {
                    i2 = MotionEventCompat.getPointerCount(motionEvent);
                    while (i < i2) {
                        actionMasked = MotionEventCompat.getPointerId(motionEvent, i);
                        float x2 = MotionEventCompat.getX(motionEvent, i);
                        float y2 = MotionEventCompat.getY(motionEvent, i);
                        float f = x2 - this.mInitialMotionX[actionMasked];
                        float f2 = y2 - this.mInitialMotionY[actionMasked];
                        reportNewEdgeDrags(f, f2, actionMasked);
                        if (this.mDragState != 1) {
                            findTopChildUnder = findTopChildUnder((int) x2, (int) y2);
                            if (!checkTouchSlop(findTopChildUnder, f, f2) || !tryCaptureViewForDrag(findTopChildUnder, actionMasked)) {
                                i++;
                            }
                        }
                        saveLastMotion(motionEvent);
                    }
                    saveLastMotion(motionEvent);
                }
            case DIRECTION_ALL:
                if (this.mDragState == 1) {
                    dispatchViewReleased(BitmapDescriptorFactory.HUE_RED, BitmapDescriptorFactory.HUE_RED);
                }
                cancel();
            case IabHelper.BILLING_RESPONSE_RESULT_DEVELOPER_ERROR:
                i = MotionEventCompat.getPointerId(motionEvent, actionIndex);
                x = MotionEventCompat.getX(motionEvent, actionIndex);
                y = MotionEventCompat.getY(motionEvent, actionIndex);
                saveInitialMotion(x, y, i);
                if (this.mDragState == 0) {
                    tryCaptureViewForDrag(findTopChildUnder((int) x, (int) y), i);
                    i2 = this.mInitialEdgesTouched[i];
                    if ((this.mTrackingEdges & i2) != 0) {
                        this.mCallback.onEdgeTouched(i2 & this.mTrackingEdges, i);
                    }
                } else if (isCapturedViewUnder((int) x, (int) y)) {
                    tryCaptureViewForDrag(this.mCapturedView, i);
                }
            case IabHelper.BILLING_RESPONSE_RESULT_ERROR:
                actionMasked = MotionEventCompat.getPointerId(motionEvent, actionIndex);
                if (this.mDragState == 1 && actionMasked == this.mActivePointerId) {
                    actionIndex = MotionEventCompat.getPointerCount(motionEvent);
                    while (i < actionIndex) {
                        int pointerId = MotionEventCompat.getPointerId(motionEvent, i);
                        if (pointerId != this.mActivePointerId) {
                            if (findTopChildUnder((int) MotionEventCompat.getX(motionEvent, i), (int) MotionEventCompat.getY(motionEvent, i)) == this.mCapturedView && tryCaptureViewForDrag(this.mCapturedView, pointerId)) {
                                i = this.mActivePointerId;
                                if (i == -1) {
                                    releaseViewForPointerUp();
                                }
                            }
                        }
                        i++;
                    }
                    i = -1;
                    if (i == -1) {
                        releaseViewForPointerUp();
                    }
                }
                clearMotionHistory(actionMasked);
            default:
                break;
        }
    }

    void setDragState(int i) {
        if (this.mDragState != i) {
            this.mDragState = i;
            this.mCallback.onViewDragStateChanged(i);
            if (i == 0) {
                this.mCapturedView = null;
            }
        }
    }

    public void setEdgeTrackingEnabled(int i) {
        this.mTrackingEdges = i;
    }

    public void setMinVelocity(float f) {
        this.mMinVelocity = f;
    }

    public boolean settleCapturedViewAt(int i, int i2) {
        if (this.mReleaseInProgress) {
            return forceSettleCapturedViewAt(i, i2, (int) VelocityTrackerCompat.getXVelocity(this.mVelocityTracker, this.mActivePointerId), (int) VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, this.mActivePointerId));
        }
        throw new IllegalStateException("Cannot settleCapturedViewAt outside of a call to Callback#onViewReleased");
    }

    public boolean shouldInterceptTouchEvent(MotionEvent motionEvent) {
        int actionMasked = MotionEventCompat.getActionMasked(motionEvent);
        int actionIndex = MotionEventCompat.getActionIndex(motionEvent);
        if (actionMasked == 0) {
            cancel();
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
        float y;
        int pointerId;
        switch (actionMasked) {
            case STATE_IDLE:
                float x = motionEvent.getX();
                y = motionEvent.getY();
                pointerId = MotionEventCompat.getPointerId(motionEvent, STATE_IDLE);
                saveInitialMotion(x, y, pointerId);
                View findTopChildUnder = findTopChildUnder((int) x, (int) y);
                if (findTopChildUnder == this.mCapturedView && this.mDragState == 2) {
                    tryCaptureViewForDrag(findTopChildUnder, pointerId);
                }
                actionMasked = this.mInitialEdgesTouched[pointerId];
                if ((this.mTrackingEdges & actionMasked) != 0) {
                    this.mCallback.onEdgeTouched(actionMasked & this.mTrackingEdges, pointerId);
                }
                break;
            case STATE_DRAGGING:
            case DIRECTION_ALL:
                cancel();
                break;
            case STATE_SETTLING:
                actionIndex = MotionEventCompat.getPointerCount(motionEvent);
                actionMasked = 0;
                while (actionMasked < actionIndex) {
                    pointerId = MotionEventCompat.getPointerId(motionEvent, actionMasked);
                    float x2 = MotionEventCompat.getX(motionEvent, actionMasked);
                    float y2 = MotionEventCompat.getY(motionEvent, actionMasked);
                    float f = x2 - this.mInitialMotionX[pointerId];
                    float f2 = y2 - this.mInitialMotionY[pointerId];
                    reportNewEdgeDrags(f, f2, pointerId);
                    if (this.mDragState != 1) {
                        View findTopChildUnder2 = findTopChildUnder((int) x2, (int) y2);
                        if (findTopChildUnder2 == null || !checkTouchSlop(findTopChildUnder2, f, f2) || !tryCaptureViewForDrag(findTopChildUnder2, pointerId)) {
                            actionMasked++;
                        }
                    }
                    saveLastMotion(motionEvent);
                }
                saveLastMotion(motionEvent);
                break;
            case IabHelper.BILLING_RESPONSE_RESULT_DEVELOPER_ERROR:
                actionMasked = MotionEventCompat.getPointerId(motionEvent, actionIndex);
                float x3 = MotionEventCompat.getX(motionEvent, actionIndex);
                y = MotionEventCompat.getY(motionEvent, actionIndex);
                saveInitialMotion(x3, y, actionMasked);
                if (this.mDragState == 0) {
                    actionIndex = this.mInitialEdgesTouched[actionMasked];
                    if ((this.mTrackingEdges & actionIndex) != 0) {
                        this.mCallback.onEdgeTouched(actionIndex & this.mTrackingEdges, actionMasked);
                    }
                } else if (this.mDragState == 2) {
                    View findTopChildUnder3 = findTopChildUnder((int) x3, (int) y);
                    if (findTopChildUnder3 == this.mCapturedView) {
                        tryCaptureViewForDrag(findTopChildUnder3, actionMasked);
                    }
                }
                break;
            case IabHelper.BILLING_RESPONSE_RESULT_ERROR:
                clearMotionHistory(MotionEventCompat.getPointerId(motionEvent, actionIndex));
                break;
        }
        return this.mDragState == 1;
    }

    public boolean smoothSlideViewTo(View view, int i, int i2) {
        this.mCapturedView = view;
        this.mActivePointerId = -1;
        return forceSettleCapturedViewAt(i, i2, STATE_IDLE, STATE_IDLE);
    }

    boolean tryCaptureViewForDrag(View view, int i) {
        if (view == this.mCapturedView && this.mActivePointerId == i) {
            return true;
        }
        if (view == null || !this.mCallback.tryCaptureView(view, i)) {
            return false;
        }
        this.mActivePointerId = i;
        captureChildView(view, i);
        return true;
    }
}