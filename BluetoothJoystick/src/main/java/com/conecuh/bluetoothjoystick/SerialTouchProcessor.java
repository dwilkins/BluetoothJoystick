package com.conecuh.bluetoothjoystick;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.conecuh.bluetoothjoystick.common.logger.Log;


/**
 * Created by dwilkins on 12/3/13.
 */
public class SerialTouchProcessor implements View.OnTouchListener {
    MotionEvent.PointerCoords origin_coordinates = new MotionEvent.PointerCoords();
    MotionEvent.PointerCoords target_coordinates = new MotionEvent.PointerCoords();

    private ConnectedThread mConnectedThread;

    public SerialTouchProcessor() {
        mConnectedThread = new ConnectedThread();
        mConnectedThread.start();

    }
    public void onPause(){
        mConnectedThread.onPause();
    }

    public void onResume(){
        mConnectedThread.onResume();
    }

    public boolean onTouch(View v, MotionEvent motionEvent) {

        int motionType = motionEvent.getAction();
        switch(motionType) {
            case MotionEvent.ACTION_CANCEL:
                onActionCancel(motionEvent,(SurfaceView) v);
                break;
            case MotionEvent.ACTION_DOWN:
                onActionDown(motionEvent,(SurfaceView) v);
                break;
            case MotionEvent.ACTION_MOVE:
                onActionMove(motionEvent,(SurfaceView) v);
                break;
            case MotionEvent.ACTION_UP:
                onActionUp(motionEvent,(SurfaceView) v);
                break;

        }
        return true;
    }

    private boolean onActionCancel(MotionEvent e,SurfaceView v) {
        Log.i("TAG", "ACTION_CANCEL");
        return true;
    }
    private boolean onActionUp(MotionEvent e,SurfaceView v) {
        Log.i("TAG","ACTION_UP");
        Paint p = new Paint();
        SurfaceHolder sh = v.getHolder();
        Canvas c = sh.lockCanvas();
        c.drawColor(Color.LTGRAY);
        sh.unlockCanvasAndPost(c);

        return true;
    }
    private boolean onActionMove(MotionEvent e,SurfaceView v) {
        Paint origin_paint = new Paint();
        Paint target_paint = new Paint();
        SurfaceHolder sh = v.getHolder();
        Canvas c = sh.lockCanvas();
        MotionEvent.PointerCoords coordinates= new MotionEvent.PointerCoords();
        final int pointerCount = e.getPointerCount();
        origin_paint.setColor(Color.RED);
        target_paint.setColor(Color.WHITE);
        for (int ptr = 0; ptr < pointerCount; ptr++) {
            e.getPointerCoords(ptr,coordinates);
            break;
        }
        target_coordinates = coordinates;

        c.drawColor(Color.GREEN);
        c.drawCircle(origin_coordinates.x, origin_coordinates.y, 100, origin_paint);
        c.drawCircle(target_coordinates.x, target_coordinates.y, 50, target_paint);
        sh.unlockCanvasAndPost(c);
        return true;
    }
    private boolean onActionDown(MotionEvent e,SurfaceView v) {
        Paint origin_paint = new Paint();
        Paint target_paint = new Paint();
        SurfaceHolder sh = v.getHolder();
        Canvas c = sh.lockCanvas();
        MotionEvent.PointerCoords coordinates= new MotionEvent.PointerCoords();
        final int pointerCount = e.getPointerCount();
        origin_paint.setColor(Color.RED);
        target_paint.setColor(Color.WHITE);
        for (int ptr = 0; ptr < pointerCount; ptr++) {
            e.getPointerCoords(ptr,coordinates);
            break;
        }
        origin_coordinates = coordinates;
        target_coordinates = coordinates;

        c.drawColor(Color.GREEN);
        c.drawCircle(origin_coordinates.x, origin_coordinates.y, 100, origin_paint);
        c.drawCircle(target_coordinates.x, target_coordinates.y, 50, target_paint);

        sh.unlockCanvasAndPost(c);
        return true;
    }


}
