package com.workspike.pedotracker.pedotrackerv1.draw_traking_path_test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.workspike.pedotracker.pedotrackerv1.R;

public class DrawManualActivity extends AppCompatActivity {
    DrawManualView drawManualView;
    int width,height;
    ImageView imageView;
   //static Display display = ( DrawManualActivity.this).getWindowManager().getDefaultDisplay();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
      // imageView=(ImageView)findViewById(R.id.floormap1);





        ImageView myImageView = (ImageView) findViewById(R.id.imageView3);
        myImageView.setImageResource(R.drawable.floor1map);

        DrawManualView mDrawingView=new DrawManualView(this);
        setContentView(R.layout.activity_draw);
        LinearLayout mDrawingPad=(LinearLayout)findViewById(R.id.view_drawing_pad);
        mDrawingPad.addView(mDrawingView);




        //DrawImageView mapImageView=(DrawImageView) findViewById(R.id.widgetMap);

     //  drawView = new DrawView(this);
       // drawView.setBackgroundColor(Color.WHITE);
      //  setContentView(drawView);


    }

















}
