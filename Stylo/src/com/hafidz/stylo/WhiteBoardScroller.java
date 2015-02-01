/**
 * 
 */
package com.hafidz.stylo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.ScrollView;

/**
 * @author hafidz
 * 
 */
public class WhiteBoardScroller extends ScrollView {
	private Context context;
	private Paint paint;

	public WhiteBoardScroller(Context context) {
		super(context);

		this.context = context;

		// draw whiteboard backgrounds
		paint = new Paint();
		paint.setStrokeWidth(5);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setAntiAlias(true);
		paint.setColor(Color.DKGRAY);

	}

	@Override
	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);

		// // lines
		// canvas.drawLine(toPixels(75), toPixels(10), toPixels(75),
		// toPixels(588), paint);
		// canvas.drawLine(toPixels(375), toPixels(10), toPixels(375),
		// toPixels(588), paint);
		// canvas.drawLine(toPixels(675), toPixels(10), toPixels(675),
		// toPixels(588), paint);
		// canvas.drawLine(toPixels(10), toPixels(40), toPixels(940),
		// toPixels(40), paint);

		int topStart = 5;
		// int bottomEnd = toPixelsHeight(98);
		int bottomEnd = 1990;

		int rightEnd = toPixelsWidth(98);

		// lines
		canvas.drawLine(toPixelsWidth(10), topStart, toPixelsWidth(10),
				bottomEnd, paint);
		canvas.drawLine(toPixelsWidth(40), topStart, toPixelsWidth(40),
				bottomEnd, paint);
		canvas.drawLine(toPixelsWidth(70), topStart, toPixelsWidth(70),
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
