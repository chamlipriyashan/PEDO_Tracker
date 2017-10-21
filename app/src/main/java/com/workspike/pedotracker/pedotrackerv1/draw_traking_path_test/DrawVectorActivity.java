package com.workspike.pedotracker.pedotrackerv1.draw_traking_path_test;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.workspike.pedotracker.pedotrackerv1.R;

public class DrawVectorActivity extends AppCompatActivity {

    DrawVectorView drawView;
    Canvas canvasmain = new Canvas();
//CanvasView mCanvasView= new CanvasView(this,null);
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_draw_vector);

        DrawVectorView temperatureView =
                (DrawVectorView) findViewById(R.id.custom_drawable_view2);
        this.setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
         drawView= new DrawVectorView(this,0,0,0,750);

       // drawView.setBackgroundColor(Color.WHITE);
        setContentView(drawView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.vector_draw_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_refresh2:

                Toast.makeText(this, "Refresh selected", Toast.LENGTH_SHORT)
                        .show();
                break;
            // action with ID action_settings was selected
            case R.id.action_settings2:

                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT)
                        .show();
                break;
            default:
                break;
        }

        return true;
    }




}



class DrawVectorView extends View {
    Bitmap board;
    int i=0;
    Paint paint = new Paint();
    public Paint mPaint;
    public static Canvas mCanvas;
    static int startX, startY, endX, endY;
    private PointF startPoint, endPoint;

    private void init() {
        mPaint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(10);
        board= BitmapFactory.decodeResource(getResources(),R.drawable.floor1map);
    }

    public DrawVectorView(Context context,int startX, int startY, int endX, int endY) {
        super(context);
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        init();
    }

    public DrawVectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawVectorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }




@Override
    public void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        canvas.drawBitmap(board, 0, 0, paint);//This worked
       // canvas.drawLine(0, 0, 500, 500, paint);
        canvas.drawLine(500, 0, 0, 500, paint);
//       canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, paint);
       canvas.drawLine(startX, startY, endX, endY,paint);
    }


}