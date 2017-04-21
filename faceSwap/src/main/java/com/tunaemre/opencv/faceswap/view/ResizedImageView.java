package com.tunaemre.opencv.faceswap.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ResizedImageView extends ImageView
{
	private int widthRatio = -1, heightRatio = -1;

	public ResizedImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		Drawable drawable = getDrawable();

		if (drawable != null)
		{
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = (int) Math.ceil((float) width * (float) drawable.getIntrinsicHeight() / (float) drawable.getIntrinsicWidth());

			if ((width > 0 && height > 0 && widthRatio > 0 && heightRatio > 0) && ((float) width / (float) height) != ((float) widthRatio / (float) heightRatio))
				height = Math.round((float) width / ((float) widthRatio / (float) heightRatio));

			setMeasuredDimension(width, height);
		}
		else
		{
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	/**
	 * Set aspect ratio of image view by changing height value.
	 * 
	 * @param widthRatio
	 * @param heightRatio
	 */
	public void setAspectRatio(int widthRatio, int heightRatio)
	{
		if (widthRatio < 1 || heightRatio < 1) return;

		this.widthRatio = widthRatio;
		this.heightRatio = heightRatio;
	}

}