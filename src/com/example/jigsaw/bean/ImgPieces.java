package com.example.jigsaw.bean;

import android.graphics.Bitmap;

//封装了切割后的图片碎片和其序号index
public class ImgPieces {
	
	int index;
	Bitmap bitmap;
	
	public ImgPieces(Bitmap bitmap,int index){
		this.bitmap=bitmap;
		this.index=index;
	}
	
	public void setIndex(int index)
	{
		this.index=index;
	}
	
	public void setBitmap(Bitmap bitmap)
	{
		this.bitmap=bitmap;
	}
	
	public int getIndex()
	{
		return index;
	}
	
	public Bitmap getBitmap()
	{
		return bitmap;
	}

}
