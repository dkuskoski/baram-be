package mk.com.barambe.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


public class PicassoImageGetter implements Html.ImageGetter {

    private TextView textView;

    public PicassoImageGetter(TextView target) {
        textView = target;
    }

    @Override
    public Drawable getDrawable(String source) {
        BitmapDrawablePlaceHolder drawable = new BitmapDrawablePlaceHolder(null, null, null);
        Picasso.get().load(source).into(drawable);
        return drawable;
    }

    private class BitmapDrawablePlaceHolder extends BitmapDrawable implements Target {

        protected Drawable drawable;

        BitmapDrawablePlaceHolder(Resources res, Bitmap bitmap, Drawable drawable) {
            super(res, bitmap);
            this.drawable = drawable;
        }

        @Override
        public void draw(final Canvas canvas) {
            if (drawable != null) {
                drawable.draw(canvas);
            }
        }

        public void setDrawable(final Drawable drawable) {
            this.drawable = drawable;
            float width = drawable.getIntrinsicWidth();
            float height = drawable.getIntrinsicHeight();


            if (textView.getMeasuredWidth() == 0) {
                textView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setDrawable(drawable);
                    }
                }, 200);
                return;
            }
            float ratio = textView.getMeasuredWidth() / width;
            width = textView.getMeasuredWidth();
            height = height * ratio;

            int margin = 10;

            drawable.setBounds(0, margin, (int) width, (int) height + margin * 2);
            setBounds(0, margin, (int) width, (int) height + margin * 2);
            if (textView != null) {
                textView.setText(textView.getText());
            }
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            setDrawable(new BitmapDrawable(textView.getResources(), bitmap));
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
//            setDrawable(new BitmapDrawable(textView.getResources(),
//                    BitmapFactory.decodeResource(textView.getResources(), R.mipmap.ic_launcher)));
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    }
}
