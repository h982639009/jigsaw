package com.example.jigsaw.utils;

import java.util.ArrayList;
import java.util.List;

import com.example.jigsaw.bean.ImgPieces;

import android.graphics.Bitmap;

//图片切割工具类
public class jigsawSplitUtils {
	/**
	 * 将图片切割成指定块数的正方形小碎片(count*count块）
	 * @param bitmap 待切割的图片
	 * @param count水平方向切割成的图片总块数
	 * @return
	 */
	public static List<ImgPieces> split(Bitmap bitmap,int count)
	{
		int width=bitmap.getWidth(),height=bitmap.getHeight();//得到待切割图片宽高
		int width_after = Math.min(width, height)/count;//切割后的碎片宽高
		
		int index;
		Bitmap pieces;
		List<ImgPieces> list = new ArrayList<ImgPieces>();
		ImgPieces imgPieces;
		
		for(int i=0;i<count;i++)
		{
			for(int j=0;j<count;j++)
			{
				index = i*count+j;
				pieces=Bitmap.createBitmap(bitmap, j*width_after, i*width_after, 
						width_after, width_after);
				imgPieces=new ImgPieces(pieces, index);
				list.add(imgPieces);
			}
			
		}
		return list;
	}

}
