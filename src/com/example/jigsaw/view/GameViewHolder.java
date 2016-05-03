package com.example.jigsaw.view;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.example.jigsaw.R;
import com.example.jigsaw.bean.ImgPieces;
import com.example.jigsaw.utils.jigsawSplitUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * 
 * @author huangtengteng
 *自定义View用来作用游戏显示界面，放置多个ImageView来显示图片切割碎片
 */
public class GameViewHolder extends RelativeLayout implements OnClickListener {

	int width,height;//RelativeLayout宽高(游戏面板宽高）
	int img_width,img_height;//imgView宽高（每个图片碎片宽高）
	int padding;//内边距
	int margin;//每个碎片单元之间的缝隙（两个imgView间的缝隙）
	
	Bitmap bitmap;//待切割的原图片
	int count=3;//切割后的图片碎片数（为count*count)
	List<ImgPieces> imgPieceslist;//图片碎片列表
	
	ImageView[] imgArray;
	
	boolean once=false;//是否是第一次运行
	
	
	public GameViewHolder(Context context) {
		this(context,null);
		// TODO Auto-generated constructor stub
	}
	
	public GameViewHolder(Context context, AttributeSet attrs) {
		this(context, attrs,0); //不能用super
		// TODO Auto-generated constructor stub
	}
	
	public GameViewHolder(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();//没有执行？？？
	}
	
	
	/**
	 * 切割Bitmap，并实现乱序排列
	 * @param 
	 */
	void initBitmap()
	{
		if(bitmap == null){
			bitmap =BitmapFactory.decodeResource(getResources(), R.drawable.moon);
		}
		imgPieceslist=jigsawSplitUtils.split(bitmap, count);
		
		//实现乱序排列
		Collections.sort(imgPieceslist, new Comparator<ImgPieces>() {

			@Override
			public int compare(ImgPieces lhs, ImgPieces rhs) {
				// TODO Auto-generated method stub
				return Math.random()>0.5?1:-1;
			}
			
		});
	}
	
	/**
	 * 设置内外边距
	 * @param
	 */
	void init()
	{
		margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				3, getResources().getDisplayMetrics());
		padding = min(getPaddingLeft(), getPaddingRight(), getPaddingTop(),
				getPaddingBottom());
		
		Log.i("game", margin+"  margin");
		Log.i("game", padding + " padding");
	}
	
	int min(int ...params)
	{
		int min = params[0];
		for(int i=0;i<params.length;i++)
		{
			if(params[i]<min)
			{
				min=params[i];
			}
		}
		return min;
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		width=Math.min(getMeasuredWidth(), getMeasuredHeight());
		Log.i("game", "---onMeasure()called!!--当前width="+width);
		if(!once)//
		{
			initBitmap();//切片，乱序排列
			initImgItem();//设置Imgview属性
			once=true;
		}
		setMeasuredDimension(width, width);
	}
	
	void initImgItem()
	{
		imgArray = new ImageView[count*count];
		img_width= (width-padding*2-margin*(count-1))/count;//每个ImgView宽度（高度与宽度一致）
		
		//生成ImageVIew，设置rules
		for(int i=0;i<imgArray.length;i++)
		{
			ImageView imageView = new ImageView(getContext());
			imageView.setOnClickListener(this);
			
			imageView.setImageBitmap(imgPieceslist.get(i).getBitmap());//设置图片源
			imgArray[i]=imageView;
			
			imageView.setId(i+1);//id必须为正数！！否则会出问题！
			//记录是第几个bitmap在imgPiecelist中的位置和显示的是第几片碎片
			imageView.setTag(i+"_"+imgPieceslist.get(i).getIndex());
			
			RelativeLayout.LayoutParams lParams =new RelativeLayout.LayoutParams(img_width, img_width);
			
			//如果不是第一列，设置leftMargin和摆放规则Rule
			if(i%count!=0)
			{
				lParams.leftMargin=margin;
				lParams.addRule(RelativeLayout.RIGHT_OF,imgArray[i-1].getId());
			}
			//如果不是第一行，设置topMargin和摆放规则Rule
			if(i/count>0)
			{
				lParams.topMargin=margin;
				lParams.addRule(RelativeLayout.BELOW, imgArray[i-count].getId());
			}
			addView(imageView,lParams);
		}
	}

	ImageView firstImgClicked,secondImgClicked;//点击的第一个和第二个imgView
	boolean isAniming=false;//是否正在播放动画
	boolean isSuccess=false;//判断是否过关
	
	@Override
	public void onClick(View v) {
		// 实现图片交换（bitmap 和 tag)
		if(isAniming)
		{
			return;
		}
		if(firstImgClicked==null)
		{//点击的第一个Img
			firstImgClicked=(ImageView) v;
			firstImgClicked.setColorFilter(Color.parseColor("#55005500"));//设置过滤色，作为选中效果
			return;
		}else{
			//已经选中了第一个Img,当前选中的是第二个Img
			firstImgClicked.setColorFilter(null);//取消过滤色效果
			secondImgClicked=(ImageView)v;
		}
		
		if(firstImgClicked==secondImgClicked)
		{//如果两次选中的是同一个img
			firstImgClicked=null;
			secondImgClicked=null;
			return;
		}
		      
//		final Bitmap firstBitmap;
//		final Bitmap secondBitmap;
		final String firstTag=(String) firstImgClicked.getTag();
		final String secondTag=(String) secondImgClicked.getTag();
		//分割字符串，得到的第一个元素为imgView序号，第二个元素为碎片图片序号
		String firstSplit[] = firstTag.split("_");
		String secondSplit[] = secondTag.split("_");
		final Bitmap firstBitmap = imgPieceslist.get(Integer.parseInt(firstSplit[0])).getBitmap();
		final Bitmap secondBitmap = imgPieceslist.get(Integer.parseInt(secondSplit[0])).getBitmap();
		//设置动画层
		setAnimationLayer();
		
		ImageView first = new ImageView(getContext());
		first.setImageBitmap(firstBitmap);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(img_width, img_width);
		layoutParams.leftMargin=firstImgClicked.getLeft()-padding;
		layoutParams.topMargin=firstImgClicked.getTop()-padding;
		first.setLayoutParams(layoutParams);
		animationLayout.addView(first);
		//animationLayout.addView(first, layoutParams);
		
		ImageView second = new ImageView(getContext());
		second.setImageBitmap(secondBitmap);
		RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(img_width, img_width);
		layoutParams2.leftMargin=secondImgClicked.getLeft()-padding;
		layoutParams2.topMargin=secondImgClicked.getTop()-padding;
		animationLayout.addView(second, layoutParams2);
		
		TranslateAnimation animation1 = new TranslateAnimation(0, secondImgClicked.getLeft()-firstImgClicked.getLeft(),
				                         0, secondImgClicked.getTop()-firstImgClicked.getTop());
		animation1.setDuration(300);
		animation1.setFillAfter(true);
		
		TranslateAnimation animation2 = new TranslateAnimation(0, -secondImgClicked.getLeft()+firstImgClicked.getLeft(),
                0, -secondImgClicked.getTop()+firstImgClicked.getTop());
		animation2.setDuration(300);
		animation2.setFillAfter(true);
		
		first.startAnimation(animation1);
		second.startAnimation(animation2);
		
		animation1.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				firstImgClicked.setVisibility(View.INVISIBLE);
				secondImgClicked.setVisibility(View.INVISIBLE);
				
				isAniming=true;
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				animationLayout.removeAllViews();
				
				firstImgClicked.setImageBitmap(secondBitmap);
				secondImgClicked.setImageBitmap(firstBitmap);
				firstImgClicked.setTag(secondTag);
				secondImgClicked.setTag(firstTag);
				
				firstImgClicked.setVisibility(View.VISIBLE);
				secondImgClicked.setVisibility(View.VISIBLE);
				
				firstImgClicked=null;
				secondImgClicked=null;
				isAniming=false;
				
				//每次动画结束，判断是否过关
				if(true==isSuccess)
				{
					Toast.makeText(getContext(), "恭喜你，过关了!", Toast.LENGTH_LONG).show();	
					
				}
			}
		});
	}
	
	RelativeLayout animationLayout;
	/**
	 * 设置动画层
	 * @param
	 */
	void setAnimationLayer()
	{
		if(animationLayout==null)
		{
			animationLayout = new RelativeLayout(getContext());
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width-2*padding, width-2*padding);
			addView(animationLayout);
		}
	}
	
	/**
	 * 根据tag获取index
	 */
	int getIndexById(String tag)
	{
		String s[] = tag.split("_");
		return Integer.parseInt(s[1]);
	}
	
	/**
	 * 判断是否成功过关
	 */
	void IsSuccess(){
		for(int i=0;i< imgArray.length;i++)
		{
			if(i!=getIndexById((String)imgArray[i].getTag())){
				isSuccess=false;
				return;
			}
		}
		isSuccess=true;
	}
	
	interface OnSuccessListener{
		void show_success_dialog();//显示成功dialog
	}
	
}
