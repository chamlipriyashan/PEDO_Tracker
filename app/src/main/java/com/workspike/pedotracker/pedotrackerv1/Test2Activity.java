package com.workspike.pedotracker.pedotrackerv1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.Random;

public class Test2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.canvas);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.FILL_PARENT);
        HeartGraphView v = new HeartGraphView(this);
        v.setLayoutParams(lp);
        relativeLayout.addView(v);
    }


    public class HeartGraphView extends View {
        public HeartGraphView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }

        int[] dataX = new int[30];
        int[] dataY = new int[30];

        public int generatRandomPositiveNegitiveValue(int max, int min) {
            Random r = new Random();
            int ii = r.nextInt(max - min + 1) + min;
            return (ii - 140);
        }


        public int getlocation(int max, int min) {
            Random r = new Random();
            int ii = r.nextInt(max - min + 1) + min;
            return (ii - 140);
        }

        @Override
        public void onDraw(Canvas canvas) {
            int w;
            int h;
            h = 280;
            w = 600;

            //Generate random
            int min = 0;
            int max = 280;

            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStrokeWidth(6);
            dataX[0] = 0;
            for (int i = 0; i < w / 20 - 1; i++) {
                dataX[i + 1] = (i + 1) * w / 20;
                //dataX[i]=getlocation(10,100);
                dataY[w / 20 - 1] = generatRandomPositiveNegitiveValue(max, min);
                dataY[i] = dataY[i + 1];
            }

            for (int i = 0; i < w / 20 - 1; i++) {
                // apply some transformation on data in order to map it correctly
                // in the coordinates of the canvas
                canvas.drawLine(dataX[i], h / 2 - dataY[i], dataX[i + 1], h / 2 - dataY[i + 1], paint);
                canvas.drawColor(Color.WHITE, PorterDuff.Mode.MULTIPLY);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                invalidate();
            }

        }
    }
}