/*
* Copyright (C) 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.conecuh.bluetoothjoystick;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.View;

import com.conecuh.bluetoothjoystick.common.logger.Log;
import com.conecuh.bluetoothjoystick.common.logger.LogFragment;

public class BasicGestureDetectFragment extends Fragment{
    SerialTouchProcessor touchProcessor = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View gestureView = getActivity().findViewById(R.id.fullscreen_content);
        gestureView.setClickable(true);
        gestureView.setFocusable(true);

        // BEGIN_INCLUDE(init_detector)

        // First create the GestureListener that will include all our callbacks.
        // Then create the GestureDetector, which takes that listener as an argument.
        GestureDetector.SimpleOnGestureListener gestureListener = new GestureListener();
        final GestureDetector gd = new GestureDetector(getActivity(), gestureListener);

        /* For the view where gestures will occur, create an onTouchListener that sends
         * all motion events to the gesture detector.  When the gesture detector
         * actually detects an event, it will use the callbacks you created in the
         * SimpleOnGestureListener to alert your application.
        */
        touchProcessor = new SerialTouchProcessor();
        gestureView.setOnTouchListener(touchProcessor);
        Log.i("TAG", "SerialTouchProcessor Created");
//        gestureView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                gd.onTouchEvent(motionEvent);
//                return false;
//            }
//        });
        // END_INCLUDE(init_detector)
    }

    public void onPause() {
        super.onPause();
        touchProcessor.onPause();
    }

    public void onResume() {
        super.onResume();
        touchProcessor.onResume();
    }

    public void clearLog() {
        LogFragment logFragment =  ((LogFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.fullscreen_content));
        logFragment.getLogView().setText("");
    }
}
