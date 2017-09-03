package com.workspike.pedotracker.pedotrackerv1;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class DrawActivity extends AppCompatActivity {
    DrawView drawView;
    int width,height;
    ImageView imageView;
   //static Display display = ( DrawActivity.this).getWindowManager().getDefaultDisplay();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
      // imageView=(ImageView)findViewById(R.id.floormap1);





        ImageView myImageView = (ImageView) findViewById(R.id.imageView3);
        myImageView.setImageResource(R.drawable.floor1map);

        DrawView mDrawingView=new DrawView(this);
        setContentView(R.layout.activity_draw);
        LinearLayout mDrawingPad=(LinearLayout)findViewById(R.id.view_drawing_pad);
        mDrawingPad.addView(mDrawingView);




        //DrawImageView mapImageView=(DrawImageView) findViewById(R.id.widgetMap);

     //  drawView = new DrawView(this);
       // drawView.setBackgroundColor(Color.WHITE);
      //  setContentView(drawView);


    }

















}
