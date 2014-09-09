package de.kaufda.android.views;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

/**
 * Created by manuel.delolmo on 08/09/14.
 */
public class VerticalCropImageView extends ImageView implements ImageLoader.ImageListener {

	public static final String URL_LANDSCAPE_SUFFIX = "#ls";
	public static final String URL_PORTRAIT_SUFFIX = "#pt";
	private int mTargetWidth;
	private int mTargetHeight;
	ImageLoader.ImageCache mCache;

	public VerticalCropImageView(Context context) {
		super(context);
	}

	public VerticalCropImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public VerticalCropImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void init(ImageLoader.ImageCache cache, int w, int h) {
		mCache = cache;
		mTargetWidth = w;
		mTargetHeight = h;
	}

	@Override
	public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
		Bitmap bitmap = response.getBitmap();
		if (!isImmediate) {
			float scaleX = (float) mTargetWidth / (float) bitmap.getWidth();
			Bitmap cropTopBitmap = Bitmap.createScaledBitmap(bitmap,
					(int) (bitmap.getWidth() * scaleX),
					(int) (bitmap.getHeight() * scaleX),
					false);
			cropTopBitmap = Bitmap.createBitmap(cropTopBitmap, 0, 0, cropTopBitmap.getWidth(),
					Math.min(mTargetHeight, cropTopBitmap.getHeight()), null, false);
			setImageBitmap(cropTopBitmap);
			String isLandscapeSuffix = getResources().getConfiguration().orientation ==
					Configuration.ORIENTATION_LANDSCAPE ? URL_LANDSCAPE_SUFFIX :
					URL_PORTRAIT_SUFFIX;
			mCache.putBitmap(response.getRequestUrl() + isLandscapeSuffix,
					cropTopBitmap);
		} else {
			String croppedUrl;
			if (getResources().getConfiguration().orientation ==
					Configuration.ORIENTATION_LANDSCAPE) {
				croppedUrl = response.getRequestUrl() + URL_LANDSCAPE_SUFFIX;
			} else {
				croppedUrl = response.getRequestUrl() + URL_PORTRAIT_SUFFIX;
			}
			Bitmap croppedBitmap = mCache.getBitmap(croppedUrl);
			if (croppedBitmap == null && bitmap != null && mTargetWidth > 0) {
				float scaleX = (float) mTargetWidth / (float) bitmap.getWidth();
				croppedBitmap = Bitmap.createScaledBitmap(bitmap,
						(int) (bitmap.getWidth() * scaleX),
						(int) (bitmap.getHeight() * scaleX),
						false);
				croppedBitmap = Bitmap.createBitmap(croppedBitmap, 0, 0, croppedBitmap.getWidth(),
						Math.min(mTargetHeight, croppedBitmap.getHeight()), null, false);
				mCache.putBitmap(croppedUrl, croppedBitmap);
			}
			setImageBitmap(croppedBitmap);
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		setVisibility(View.GONE);
	}
}
