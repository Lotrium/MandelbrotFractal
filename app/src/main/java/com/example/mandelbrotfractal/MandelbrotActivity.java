package com.example.mandelbrotfractal;

import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MandelbrotActivity extends AppCompatActivity {

    private static final int MOVE_DELAY = 16; // ~60 FPS
    private Handler handler = new Handler();
    private boolean isMoving = false;
    private Runnable moveRunnable;
    private MandelbrotView mandelbrotView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mandelbrot);

        mandelbrotView = findViewById(R.id.mandelbrotView);

        View.OnTouchListener buttonTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    isMoving = true;
                    moveRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if (isMoving) {
                                int id = v.getId();
                                if (id == R.id.btnZoomIn) {
                                    mandelbrotView.zoomIn();
                                } else if (id == R.id.btnZoomOut) {
                                    mandelbrotView.zoomOut();
                                } else if (id == R.id.btnMoveUp) {
                                    mandelbrotView.moveUp();
                                } else if (id == R.id.btnMoveDown) {
                                    mandelbrotView.moveDown();
                                } else if (id == R.id.btnMoveLeft) {
                                    mandelbrotView.moveLeft();
                                } else if (id == R.id.btnMoveRight) {
                                    mandelbrotView.moveRight();
                                }
                                handler.postDelayed(this, MOVE_DELAY);
                            }
                        }
                    };
                    handler.post(moveRunnable);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    isMoving = false;
                    handler.removeCallbacks(moveRunnable);
                }
                return true;
            }
        };

        Button btnZoomIn = findViewById(R.id.btnZoomIn);
        Button btnZoomOut = findViewById(R.id.btnZoomOut);
        Button btnMoveUp = findViewById(R.id.btnMoveUp);
        Button btnMoveDown = findViewById(R.id.btnMoveDown);
        Button btnMoveLeft = findViewById(R.id.btnMoveLeft);
        Button btnMoveRight = findViewById(R.id.btnMoveRight);

        btnZoomIn.setOnTouchListener(buttonTouchListener);
        btnZoomOut.setOnTouchListener(buttonTouchListener);
        btnMoveUp.setOnTouchListener(buttonTouchListener);
        btnMoveDown.setOnTouchListener(buttonTouchListener);
        btnMoveLeft.setOnTouchListener(buttonTouchListener);
        btnMoveRight.setOnTouchListener(buttonTouchListener);
    }
}
