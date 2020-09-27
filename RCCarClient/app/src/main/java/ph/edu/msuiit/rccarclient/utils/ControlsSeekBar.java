package ph.edu.msuiit.rccarclient.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;

public class ControlsSeekBar extends SeekBar {
    protected int minValue = 0;
    protected int maxValue = 0;
    private OnSeekBarChangeListener onChangeListener;

    public ControlsSeekBar(Context context) {
        super(context);
        setUpInternalListener();
    }

    public ControlsSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setUpInternalListener();
    }

    public ControlsSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUpInternalListener();
    }

    public void setMaxValue(int max) {
        this.maxValue = max;
        super.setMax(maxValue - minValue);
    }

    public void setMinValue(int min) {
        this.minValue = min;
        super.setMax(maxValue - minValue);
    }

    public int getProgressValue() {
        return super.getProgress() + minValue;
    }

    public void setProgressValue(int value) {
        super.setProgress(value - minValue);
    }

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onChangeListener){
        this.onChangeListener = onChangeListener;
    }

    private void setUpInternalListener() {
        super.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (onChangeListener != null) {
                    onChangeListener.onProgressChanged(seekBar, minValue + i, b);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (onChangeListener != null)
                    onChangeListener.onStartTrackingTouch(seekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (onChangeListener != null)
                    onChangeListener.onStopTrackingTouch(seekBar);
            }
        });
    }

    public void enableTouchEvent() {
        setProgressValue(0);
        super.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }
    public void disableTouchEvent() {
        setProgressValue(0);
        super.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }
}
