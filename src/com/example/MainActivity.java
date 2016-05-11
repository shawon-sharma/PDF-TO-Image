package com.example;

import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;

import com.example.utils.ExifUtil;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;

import com.lowagie.text.pdf.PdfWriter;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.provider.MediaStore;
import android.util.Log;

import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private Button b, choose, camera, showPdf,send,rotate;
    ImageView imageView,newImage;
    Bitmap bitmap;
    private String imgPath;
    File destination;
    EditText eemail;
    String email;
    Bitmap bmpp;





    Bitmap imageScaled, imageOriginal;
    private int dialerHeight, dialerWidth;
    Matrix matrix;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camera = (Button) findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent, 2);
            }
        });

        newImage= (ImageView) findViewById(R.id.image);
        rotate= (Button) findViewById(R.id.rotate);
        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotateImage();
            }
        });


        choose = (Button) findViewById(R.id.choose);
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, 1);
            }
        });

        b = (Button) findViewById(R.id.button1);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                createPDF();
            }
        });
        eemail = (EditText) findViewById(R.id.emailTo);
        showPdf =(Button) findViewById(R.id.show);
        showPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/droidText/sample.pdf");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);

            }
        });
        send=(Button)findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eemail= (EditText) findViewById(R.id.emailTo);
                email=eemail.getText().toString();

                String filename="/droidText/sample.pdf";
                File filelocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), filename);
                Uri path = Uri.fromFile(filelocation);
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
// set the type to 'email'
                emailIntent .setType("vnd.android.cursor.dir/email");
                String to[] = {email};
                emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
// the attachment
                emailIntent .putExtra(Intent.EXTRA_STREAM, path);
// the mail subject
                emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Subject");

                startActivity(Intent.createChooser(emailIntent , "Send email..."));

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageView = (ImageView) findViewById(R.id.img);
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();


            Bitmap im = BitmapFactory.decodeFile(picturePath);

            Bitmap orientedBitmap= ExifUtil.rotateBitmap(picturePath,im);


            imageView.setImageBitmap(orientedBitmap);

            // rotateImage();

        } else if (requestCode == 2) {

            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection, null, null, null);
            int column_index_data = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToLast();

            String imagePath = cursor.getString(column_index_data);
            Bitmap im = BitmapFactory.decodeFile(imagePath);


            Bitmap orientedBitmap= ExifUtil.rotateBitmap(imagePath,im);
            imageView.setImageBitmap(orientedBitmap);
            // rotateImage();

        } else if (requestCode == 3) {
            newImage= (ImageView) findViewById(R.id.imageview);
           // imageView.setVisibility(View.GONE);


            //byte[] byteArray = data.getByteArrayExtra("picture");
            if (imageOriginal == null) {
                imageOriginal = data.getParcelableExtra("picture");
                imageView.setImageBitmap(imageOriginal);
            }
            if(imageOriginal!=null) {
                imageOriginal = data.getParcelableExtra("picture");
                imageView.setImageBitmap(imageOriginal);
                //newImage.setVisibility(View.VISIBLE);
            }
         /*   matrix = new Matrix();

         *//*   newImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {*//*
                    // method called more than once, but the values only need to be initialized one time
                    if (dialerHeight == 0 || dialerWidth == 0) {
                        dialerHeight = newImage.getHeight();
                        dialerWidth = newImage.getWidth();

                        // resize
                        Matrix resize = new Matrix();
                        //resize.postScale(500/ (float) imageOriginal.getWidth(),(float) Math.max(dialerWidth, dialerHeight) / (float) imageOriginal.getHeight());
                        resize.postScale((float) Math.min(dialerWidth, dialerHeight) / (float) imageOriginal.getWidth(), (float) Math.min(dialerWidth, dialerHeight) / (float) imageOriginal.getHeight());
                        //resize.postScale(300, 300);
                        imageScaled = Bitmap.createBitmap(imageOriginal, 0, 0, imageOriginal.getWidth(), imageOriginal.getHeight(), resize, false);
                        // translate to the image view's center
                        float translateX = dialerWidth / 2 - imageScaled.getWidth() / 2;
                        float translateY = dialerHeight / 2 - imageScaled.getHeight() / 2;
                        matrix.postTranslate(translateX, translateY);
                        newImage.setImageBitmap(imageScaled);
                        newImage.setImageMatrix(matrix);
                        newImage.setVisibility(View.VISIBLE);

                    }
                }*/
            //    });
            // showImage();
        }

    }


    public void showImage()
    {


    }

    public void rotateImage(){

             Toast.makeText(getApplicationContext(),"pachhe",Toast.LENGTH_LONG).show();
             BitmapDrawable image = (BitmapDrawable) imageView.getDrawable();
             bmpp = image.getBitmap();
             ByteArrayOutputStream stream = new ByteArrayOutputStream();
             Bitmap bb=Bitmap.createScaledBitmap(bmpp,120,120,false);
             bb.compress(Bitmap.CompressFormat.PNG, 100, stream);
             byte[] byteArray = stream.toByteArray();
             Intent intent = new Intent(MainActivity.this, RotateImage.class);
             intent.putExtra("picture", bb);
             startActivityForResult(intent,3);
    }

    public void createPDF() {
        Document doc = new Document();

        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/droidText";

            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            Log.d("PDFCreator", "PDF Path: " + path);

            File file = new File(dir, "sample.pdf");
            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter.getInstance(doc, fOut);

            //open the document
            doc.open();

            BitmapDrawable image = (BitmapDrawable) imageView.getDrawable();

            Bitmap bmp = image.getBitmap();
             bmpp = image.getBitmap();
            int width = image.getMinimumWidth();
            Log.e("actual width", String.valueOf(width));
            if (width > 500) {
                bitmap = getResizedBitmap(bmp, 500, 300);
            } else {
                bitmap = getResizedBitmap(bmp, width, 300);
            }

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            Image myImg = Image.getInstance(stream.toByteArray());
            myImg.setAlignment(Image.MIDDLE);

            //add image to document
            doc.add(myImg);

        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
        } finally {
            doc.close();
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();

        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        // bm.recycle();
        return resizedBitmap;
    }
}

