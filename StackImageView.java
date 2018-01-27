package com.zn.yzw.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.StackView;

import com.blankj.utilcode.util.ConvertUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * 说明：层叠窗口，简化操作。
 * 需要依赖Glide进行图片加载
 * Created by 季景胜 on 2018/1/26.
 */

public class StackImageView extends StackView {
    private int mViewWidth, mViewHeight;
    private int mImageBackgroundColor = Color.TRANSPARENT;//item的背景颜色(默认透明)
    private float mImageScale = 0.7f;//第一张图占stackView的百分比，剩余宽度会被4等分进行差值展示
    private List<String> mImgList;
    private ImageAdapter mAdapter;
    private Transformation<Bitmap> mTransformation = new CenterCrop();
    private boolean canMove = true;//是否能够拖动操作
    private OnClickListener mOnClickListener;

    public StackImageView(Context context) {
        super(context);
    }

    public StackImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StackImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置图片参数
     *
     * @param str
     */
    public void init(String... str) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < str.length; i++) {
            list.add(str[i]);
        }
        init(list);
    }

    public void init(List<String> list) {
        mImgList = list == null ? new ArrayList<String>() : list;
        initAdapter();
    }

    /**
     * 设置glide加载图片的变换模式，默认为CenterCorp
     */
    public void setTransformation(Transformation<Bitmap> transformation) {
        this.mTransformation = transformation;
    }

    /**
     * 设置图片的背景色
     */
    public void setImageBackgroundColor(@ColorInt int color) {
        this.mImageBackgroundColor = color;
    }

    /**
     * 设置图片大小占StackView的百分比,默认0.9f
     */
    public void setImageScale(@FloatRange(from = 0.1f, to = 0.8f) float scale) {
        this.mImageScale = scale;
    }

    /**
     * 设置是否可以拖动操作，true时点击调用onItemClick，为false时调用onClick
     */
    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    /**
     * 增加onClick支持，与onItemClick不会同时调用
     */
    @Override
    public void setOnClickListener(OnClickListener l) {
        mOnClickListener = l;
    }

    private void initAdapter() {
        if (mViewWidth == 0 || mViewHeight == 0) {
            this.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    StackImageView.this.getViewTreeObserver().removeOnPreDrawListener(this);
                    mViewWidth = StackImageView.this.getMeasuredWidth();
                    mViewHeight = StackImageView.this.getMeasuredHeight();
                    if (getAdapter() != null) {
                        mAdapter.setNewData(mImgList);
                    } else {
                        mAdapter = new ImageAdapter(mImgList);
                        StackImageView.this.setAdapter(mAdapter);
                    }
                    return true;
                }
            });
        } else {
            if (getAdapter() != null) {
                mAdapter.setNewData(mImgList);
            }
        }
    }


    private float dx, dy;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (canMove) {
            return super.dispatchTouchEvent(ev);
        } else {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                dx = ev.getX();
                dy = ev.getY();
            } else if (ev.getAction() == MotionEvent.ACTION_UP) {
                if (ev.getX() >= 0 && ev.getX() <= mViewWidth && ev.getY() >= 0 && ev.getY() <= mViewHeight && Math.abs(ev.getX() - dx) < 30 && Math.abs(ev.getY() - dy) < 30) {
                    if (mOnClickListener != null) {
                        mOnClickListener.onClick(this);
                    }
                }
                dx = 0;
                dy = 0;
            }
            return true;
        }


    }

    //适配器
    private class ImageAdapter extends BaseAdapter {
        private List<String> mList;

        private ImageAdapter(List<String> mImages) {
            this.mList = mImages;
        }

        private void setNewData(List<String> mImages) {
            this.mList = mImages;
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewGroup.MarginLayoutParams p = new ViewGroup.MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            int margins = ConvertUtils.dp2px(3);
            p.setMargins(margins, margins, margins, margins);
            CardView cardView = new CardView(getContext());
            cardView.setLayoutParams(p);
            ImageView imageView = new ImageView(getContext());
            imageView.setBackgroundColor(mImageBackgroundColor);
            imageView.setMinimumWidth((int) (mViewWidth * mImageScale));
            imageView.setMinimumHeight((int) (mViewWidth * mImageScale));
            cardView.addView(imageView);
            Glide.with(getContext().getApplicationContext()).load(mList.get(position)).apply(RequestOptions.bitmapTransform(mTransformation)).into(imageView);

            return cardView;
        }
    }


}
