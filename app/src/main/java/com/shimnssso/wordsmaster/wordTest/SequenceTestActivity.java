package com.shimnssso.wordsmaster.wordTest;

import android.app.Activity;
import android.content.ClipData;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shimnssso.wordsmaster.R;

public class SequenceTestActivity extends Activity {
    private static final String TAG = "SequenceTest";

    private String mCurrentWord;

    LinearLayout mMixedWordLayout;
    LinearLayout mAnswerWordLayout;

    int mTempTextIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");

        setContentView(R.layout.sequence_test);
        mTempTextIndex = 0;

        mMixedWordLayout = (LinearLayout)findViewById(R.id.layout_mixed_words);
        mAnswerWordLayout = (LinearLayout)findViewById(R.id.layout_answer_words);
        mAnswerWordLayout.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                View currentView = (View) dragEvent.getLocalState();
                LinearLayout currentContainer = (LinearLayout)view;

                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // do nothing
                        Log.d(TAG, "ACTION_DRAG_STARTED");
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        //v.setBackgroundDrawable(enterShape);
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        //v.setBackgroundDrawable(normalShape);
                        break;
                    case DragEvent.ACTION_DROP:
                        // Dropped, reassign View to ViewGroup
                        ViewGroup owner = (ViewGroup)currentView.getParent();
                        owner.removeView(currentView);

                        if (currentContainer.getChildCount() > mTempTextIndex) {
                            currentContainer.addView(currentView, mTempTextIndex);
                        }
                        else {
                            currentContainer.addView(currentView);
                        }
                        for (int i=0; i<currentContainer.getChildCount();i++){
                            View tempView = currentContainer.getChildAt(i);
                            tempView.setPadding(10,10,10,10);
                        }
                        currentView.setVisibility(View.VISIBLE);
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        //v.setBackgroundDrawable(normalShape);
                        break;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        float x = dragEvent.getX();
                        float y = dragEvent.getY();
                        Log.d(TAG, "drag_location " + ", x:" + x + ", y:" + y);

                        View tempView = null;
                        int tempIndex = 0;
                        for (int i=0; i<currentContainer.getChildCount(); i++) {
                            tempView = currentContainer.getChildAt(i);
                            float tempX = tempView.getX();
                            float tempY = tempView.getY();
                            int tempWidth = tempView.getWidth();
                            int tempHeight = tempView.getHeight();
                            Log.d(TAG, "index " + i + ", x:" + tempX + ", y:" + tempY + ", w:" + tempWidth + ", h:" + tempHeight);

                            if ( x < tempX+(tempWidth/2) ) {
                                tempIndex = i;
                                break;
                            } else {
                                tempIndex = i+1;
                            }
                        }
                        Log.d(TAG, "index candidate " + tempIndex);
                        if (mTempTextIndex != tempIndex) {
                            int width = currentView.getWidth();
                            mTempTextIndex = tempIndex;

                            for (int i=0; i<currentContainer.getChildCount(); i++) {
                                tempView = currentContainer.getChildAt(i);
                                if (i == mTempTextIndex) {
                                    tempView.setPadding(10 + width, 10, 10, 10);
                                }
                                else {
                                    tempView.setPadding(10, 10, 10, 10);
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");

        mCurrentWord = "I'm a boy.";

        String[] subStrings = mCurrentWord.split(" ");

        for (String s: subStrings) {
            final TextView textView = new TextView(this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            textView.setBackgroundColor(Color.parseColor("#00FFFFFF"));
            textView.setPadding(10, 10, 10, 10);
            textView.setTextColor(Color.parseColor("#FF7200"));
            textView.setTextSize(40);
            textView.setText(s);
            mMixedWordLayout.addView(textView);

            textView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    textView.startDrag(data, shadowBuilder, view, 0);
                    textView.setVisibility(View.GONE);
                    return true;
                }
            });
        }
    }
}
