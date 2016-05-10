package com.example;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class RotateImage extends Activity {

Button save;
    ImageView image;
    Button btnRotate;
    TextView coordinate;
    Bitmap bMap;
    int x = 5;
    float a, b;
    Matrix matrix;
    private int dialerHeight, dialerWidth;
    private double startAngle;
    private static Bitmap imageOriginal, imageScaled, lastone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotate_image);
        image = (ImageView) findViewById(R.id.image);

        save= (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Bitmap bb=Bitmap.createScaledBitmap(lastone,120,120,false);
                bb.compress(Bitmap.CompressFormat.PNG, 100, stream);

                Intent intent = new Intent();
                intent.putExtra("picture", bb);

                setResult(3,intent);
                image.setImageDrawable(null);
                finish();
            }
        });
        Intent intent = getIntent();
        Bitmap bitmap = (Bitmap) intent.getParcelableExtra("picture");
     /*  = extras.get.getByteArray("picture");*/
        if (imageOriginal == null) {
            imageOriginal =bitmap;
        }
        matrix = new Matrix();
        image.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // method called more than once, but the values only need to be initialized one time
                if (dialerHeight == 0 || dialerWidth == 0) {
                    dialerHeight = image.getHeight();
                    dialerWidth = image.getWidth();

                    // resize
                    Matrix resize = new Matrix();
                    //resize.postScale(500/ (float) imageOriginal.getWidth(),(float) Math.max(dialerWidth, dialerHeight) / (float) imageOriginal.getHeight());
                    resize.postScale((float) Math.min(dialerWidth, dialerHeight) / (float) imageOriginal.getWidth(), (float) Math.min(dialerWidth, dialerHeight) / (float) imageOriginal.getHeight());
                    imageScaled = Bitmap.createBitmap(imageOriginal, 0, 0, imageOriginal.getWidth(), imageOriginal.getHeight(), resize, false);
                    // translate to the image view's center
                    float translateX = dialerWidth / 2 - imageScaled.getWidth() / 2;
                    float translateY = dialerHeight / 2 - imageScaled.getHeight() / 2;
                    matrix.postTranslate(translateX, translateY);
                    image.setImageBitmap(imageScaled);
                    image.setImageMatrix(matrix);

                }
            }
        });
        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        Log.e("DOWN", "DOWN");
                        startAngle = getAngle(event.getX(), event.getY());
                        a = event.getX();
                        b = event.getY();

                        Toast.makeText(getApplicationContext(), "down", Toast.LENGTH_LONG).show();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        Log.e("MOVE", "MOVE");
                        double currentAngle = getAngle(event.getX(), event.getY());
                        rotateDialer((float) (startAngle - currentAngle));
                        startAngle = currentAngle;
                        Toast.makeText(getApplicationContext(), "move", Toast.LENGTH_LONG).show();
                        break;

                    case MotionEvent.ACTION_UP:
                        Log.e("UP", "UP");
                        Toast.makeText(getApplicationContext(), "up", Toast.LENGTH_LONG).show();
                        break;
                }
                return true;
            }
        });
    }
    public void rotate(float x_axis, float y_axis) {
        image.buildDrawingCache();
        bMap = image.getDrawingCache();

        coordinate.setText("x+ y " + x_axis + "  " + a);
        if (a > x_axis)

            matrix.preRotate(-1);
        else if (a == x_axis) {
        } else
            matrix.preRotate(1);
        Bitmap bMapRotate = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(), bMap.getHeight(), matrix, true);
        //put rotated image in ImageView.
        image.setImageBitmap(bMapRotate);


    }

    public void drag(float x_axis, float y_axis) {
        float dx = x_axis - a;
        float dy = y_axis - b;
        matrix.postTranslate(dx, dy);
        image.setImageMatrix(matrix);
    }

    void rotateDialer(float degrees) {
        //matrix.postRotate(degrees);
        //image.setImageBitmap(Bitmap.createBitmap(imageScaled, 0, 0, imageScaled.getWidth(), imageScaled.getHeight(), matrix, true));
        matrix.postRotate(degrees, dialerWidth / 2, dialerHeight / 2);
        Matrix ll=matrix;
        image.setImageMatrix(matrix);
        lastone = Bitmap.createBitmap(imageScaled, 0, 0, imageScaled.getWidth(), imageScaled.getHeight(),ll, true);
        Canvas canvas = new Canvas(lastone);
        canvas.drawColor(0xfff);
    }

    private double getAngle(double xTouch, double yTouch) {
        double x = xTouch - (image.getWidth() / 2d);
        double y = image.getHeight() - yTouch - (image.getHeight() / 2d);

        switch (getQuadrant(x, y)) {
            case 1:
                return Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            case 2:
                return 180 - Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            case 3:
                return 180 + (-1 * Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
            case 4:
                return 360 + Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            default:
                return 0;
        }
    }
    private static int getQuadrant(double x, double y) {
        if (x >= 0) {
            return y >= 0 ? 1 : 4;
        } else {
            return y >= 0 ? 2 : 3;
        }
    }
}
