# StackImageView
对原生的StackView进行简单封装，使之可以`加载网络图片`，并暴露几个常用接口
***
主要方法如下：
 * **public void init(List<String> list) {}**
 * **public void init(String... str) {}**
<br>初始化adapter的方法，必须调用。传入图片地址
 
 * **public void setOnClickListener(OnClickListener l){ }**
 <br>原有无法设置onClick，现在可以了

 *  **public void setCanMove(boolean canMove){ }**
 <br>设置是否可以进行原生的拖动操作；注意：true时回调onItemClick，false时回调onClick，不会同时调用；默认 true
 
 * **public void setImagePadding(@IntRange(from = 0) int padding) { }**
 <br>设置2张图片之间的间隔差值，单位dp，与原生逻辑有点区别；默认 10dp
 
 * **public void setImageBackgroundColor(@ColorInt int color) { }**
 <br>设置图片的背景颜色；默认 Color.TRANSPARENT
 
 * **public void setTransformation(Transformation<Bitmap> transformation) { }**
 <br>由于图片加载使用Glide4.0库，所以图片的展示效果可以用transformation控制，所以传入吧；默认 CenterCorp
 
 
 

