package com.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

import static android.R.attr.scaleType;

/**
 * 说明：层叠窗口，简化操作。
 * 需要依赖Glide进行图片加载
 * Created by 季景胜 on 2018/1/26.
 */

public class StackImageView extends StackView {
    private int mViewWidth, mViewHeight;
    private int mImageBackgroundColor = Color.TRANSPARENT;//item的背景颜色(默认透明)
    private int mImagePadding = 10;//比控件小那么一点点
    //  private ImageView.ScaleType mImageScaleType = ImageView.ScaleType.CENTER_CROP;
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
        mImgList = list;
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
     * 设置2张图片的差值，单位dp
     */
    public void setImagePadding(@IntRange(from = 0) int padding) {
        this.mImagePadding = padding;
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
        if (mViewWidth != 0 && mImgList != null && mImgList.size() > 0) {
            if (getAdapter() == null) {
                mAdapter = new ImageAdapter(mImgList);
                this.setAdapter(mAdapter);
            } else {
                mAdapter.setNewData(mImgList);
            }
        }
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mViewWidth = getWidth();
        mViewHeight = getHeight();
        if (this.getAdapter() == null && mImgList != null && mImgList.size() > 0) {
            mAdapter = new ImageAdapter(mImgList);
            this.setAdapter(mAdapter);
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
        private List<String> mImages;

        private ImageAdapter(List<String> mImages) {
            this.mImages = mImages;
        }

        private void setNewData(List<String> mImages) {
            this.mImages = mImages;
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mImages.size();
        }

        @Override
        public Object getItem(int position) {
            return mImages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(getContext());
            imageView.setBackgroundColor(mImageBackgroundColor);
            imageView.setMinimumWidth(mViewWidth - ConvertUtils.dp2px(mImagePadding * getCount()));
            imageView.setMinimumHeight(mViewHeight - ConvertUtils.dp2px(mImagePadding * getCount()));
            Glide.with(getContext()).load(mImages.get(position)).apply(RequestOptions.bitmapTransform(mTransformation)).into(imageView);
            return imageView;
        }
    }


}
