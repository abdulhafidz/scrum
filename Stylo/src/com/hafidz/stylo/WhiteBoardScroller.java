/**
 * 
 */
package com.hafidz.stylo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.widget.ScrollView;

/**
 * @author hafidz
 * 
 */
public class WhiteBoardScroller extends ScrollView {

	public static final int LINE_IN_PROGRESS = 40;
	public static final int LINE_DONE = 70;

	private Context context;
	private Paint paint;

	public WhiteBoardScroller(Context context) {
		super(context);

		this.context = context;

		// draw whiteboard backgrounds
		paint = new Paint();
		paint.setStrokeWidth(3);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setAntiAlias(true);
		paint.setColor(Color.parseColor("#9FA8DA"));
		paint.setPathEffect(new DashPathEffect(new float[] { 15, 25 }, 0));

	}

	@Override
	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);

		int topStart = 5;
		// int bottomEnd = toPixelsHeight(98);
		int bottomEnd = 1990;

		int rightEnd = toPixelsWidth(98);

		// lines
		canvas.drawLine(toPixelsWidth(10), topStart, toPixelsWidth(10),
				bottomEnd, paint);
		canvas.drawLine(toPixelsWidth(LINE_IN_PROGRESS), topStart, toPixelsWidth(LINE_IN_PROGRESS),
				bottomEnd, paint);
		canvas.drawLine(toPixelsWidth(LINE_DONE), topStart, toPixelsWidth(LINE_DONE),
				bottomEnd, paint);

		canvas.drawLine(10, 50, rightEnd, 50, paint);

	}

	// int toPixels(float dp) {
	// return Util.toPixels(context, dp);
	// }

	int toPixelsWidth(int percentage) {
		return Util.toPixelsWidth(context, percentage);
	}

}
