package com.example.administrator.goldaappnew.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.example.administrator.goldaappnew.R;

@SuppressLint("DrawAllocation")
public class DashLineView extends View {

	private Paint mPaint;
	private Path mPath;

	@SuppressWarnings("deprecation")
	public DashLineView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(getResources().getColor(R.color.orange));
		// 需要加上这句，否则画不出东西
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(3);
		mPaint.setPathEffect(new DashPathEffect(new float[] { 15, 5 }, 0));

		mPath = new Path();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int centerY = getHeight() / 2;
		mPath.reset();
		mPath.moveTo(0, centerY);
		mPath.lineTo(getWidth(), centerY);
		canvas.drawPath(mPath, mPaint);
	}
}