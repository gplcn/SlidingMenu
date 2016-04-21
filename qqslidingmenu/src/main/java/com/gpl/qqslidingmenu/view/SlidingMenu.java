package com.gpl.qqslidingmenu.view;

import com.gpl.qqslidingmenu.ColorUtil;
import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class SlidingMenu extends FrameLayout {

    private View menuView;
    private View mainView;
    private int mainViewWidth;
    private int mainViewHeight;
    private int menuViewHeight;
    private int menuViewWidth;
    private int dragRange;
    private float fraction;
    private FloatEvaluator floatEvaluator;
    private ViewDragHelper mViewDragHelper;
    private DragState dragState = DragState.CLOSE;

    public enum DragState {
        CLOSE, OPEN;
    }

    public SlidingMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public SlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingMenu(Context context) {
        this(context, null, 0);
    }

    /**
     * 初始化
     */
    private void init() {
        floatEvaluator = new FloatEvaluator();
        mViewDragHelper = ViewDragHelper.create(this, 1f,cb);
    }

    public DragState getDragState() {
        return dragState;
    }

    /**
     * 在xml文件解析完成后调用
     */
    @Override
    protected void onFinishInflate() {
        if (getChildCount() != 2) {
            throw new IllegalArgumentException(
                    "slidingMenu must have and only support 2 children!");
        }
        menuView = (View) getChildAt(0);
        mainView = (View) getChildAt(1);
        super.onFinishInflate();
    }

    /**
     * 在该方法中可以准确获取父窗体宽高
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        dragRange = (int) (getMeasuredWidth() * 0.6f);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        menuViewWidth = menuView.getMeasuredWidth();
        menuViewHeight = menuView.getMeasuredHeight();
        menuView.layout(-menuViewWidth, 0, 0, menuViewHeight);
        mainViewWidth = mainView.getMeasuredWidth();
        mainViewHeight = mainView.getMeasuredHeight();
        mainView.layout(0, 0, mainViewWidth, mainViewHeight);
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * 自处理触摸事件
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = mViewDragHelper.shouldInterceptTouchEvent(ev);
        return result;
    }

    /**
     * 让mViewDragHelper处理触摸事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }


    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(SlidingMenu.this);
        }
    }


    private Callback cb = new Callback() {

        /** 捕获所有子View的触摸事件 */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (mainView.getLeft() > dragRange / 2) {
                open();
            } else {
                close();
            }

            if (xvel > 200) {
                open();
            } else if (xvel < -200) {
                close();
            }
        }
        /** 关闭侧拉菜单 */
        private void close() {
            mViewDragHelper.smoothSlideViewTo(mainView, 0, 0);

            ViewCompat.postInvalidateOnAnimation(SlidingMenu.this);
        }
        /** 打开侧拉菜单 */
        private void open() {
            mViewDragHelper.smoothSlideViewTo(mainView, dragRange, 0);
            ViewCompat.postInvalidateOnAnimation(SlidingMenu.this);
        }

        @Override
        public int getViewHorizontalDragRange(View child) {

            return dragRange;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == mainView) {
                //限制左边
                if (left < 0) {
                    left = 0;
                }
                //限制右边
                else if (left > dragRange) {
                    left = dragRange;
                }
            }
            return left;
        }
        @Override
        public void onViewPositionChanged(View changedView, int left, int top,
                                          int dx, int dy) {
            if (changedView == menuView) {
//                menuView.layout(0, 0, menuView.getMeasuredWidth(),
//                        menuView.getMeasuredHeight());
                int newLeft = mainView.getLeft() + dx;
                if (newLeft < 0) {
                    newLeft = 0;

                } else if (newLeft > dragRange) {
                    newLeft = dragRange;
                }
                //让mianview做跟随移动
                mainView.layout(newLeft, 0, newLeft + mainViewWidth,
                        mainViewHeight);
            }
            //执行伴随动画
            fraction = mainView.getLeft() * 1f / dragRange;
            executeAnimation(fraction);

            //根据fraction的值和dragState回调监听器方法
            if (fraction == 1 && dragState != DragState.OPEN) {
                dragState = DragState.OPEN;
                listener.onOpen();
            } else if (fraction == 0 && dragState != DragState.CLOSE) {
                dragState = DragState.CLOSE;
                listener.onClose();
            }
            listener.onDrag(fraction);
        }

        private void executeAnimation(float fraction) {
            // 主界面缩放
            ViewHelper.setScaleX(mainView,
                    floatEvaluator.evaluate(fraction, 1, 0.8f));
            ViewHelper.setScaleY(mainView,
                    floatEvaluator.evaluate(fraction, 1, 0.8f));
            // 菜单界面缩放
            ViewHelper.setScaleX(menuView,
                    floatEvaluator.evaluate(fraction, 0.8f, 1));
            ViewHelper.setScaleY(menuView,
                    floatEvaluator.evaluate(fraction, 0.8f, 1));
            // 平移
            ViewHelper.setTranslationX(menuView,
                    floatEvaluator.evaluate(fraction, -menuViewWidth / 2, 0));
            // 透明度
            ViewHelper.setAlpha(menuView,
                    floatEvaluator.evaluate(fraction, 0, 1));

            //遮罩
            getBackground().setColorFilter((Integer) ColorUtil.evaluateColor(fraction, Color.DKGRAY, Color.TRANSPARENT), Mode.SRC_OVER);
        }

    };

    private OnDragStateChangeListener listener;

    public void setOnDragStateChangeListener(OnDragStateChangeListener listener) {
        this.listener = listener;
    }

    public interface OnDragStateChangeListener {
        void onOpen();

        void onClose();

        void onDrag(float fraction);
    }
}
