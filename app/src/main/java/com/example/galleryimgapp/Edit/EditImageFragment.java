package com.example.galleryimgapp.Edit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.example.galleryimgapp.R;

public class EditImageFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    SeekBar seekBarBrightness, seekBarContrast, seekBarSaturation;
    private EditImageFragmentListener listener;

    public void setListener(EditImageFragmentListener listener) {
        this.listener = listener;
    }

    public EditImageFragment() {
        //Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_image_fragment, container, false);

        seekBarBrightness = (SeekBar) view.findViewById(R.id.edit_image_fragment_sb_brightness);
        seekBarContrast = (SeekBar) view.findViewById(R.id.edit_image_fragment_sb_contrast);
        seekBarSaturation = (SeekBar) view.findViewById(R.id.edit_image_fragment_sb_saturation);

        // keeping brightness value b/w -100 / +100
        seekBarBrightness.setMax(200);
        seekBarBrightness.setProgress(100);

        // keeping contrast value b/w 1.0 - 3.0
        seekBarContrast.setMax(20);
        seekBarContrast.setProgress(0);

        // keeping saturation value b/w 0.0 - 3.0
        seekBarSaturation.setMax(30);
        seekBarSaturation.setProgress(10);

        seekBarBrightness.setOnSeekBarChangeListener(this);
        seekBarContrast.setOnSeekBarChangeListener(this);
        seekBarSaturation.setOnSeekBarChangeListener(this);

        return view;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (listener != null) {

            if (seekBar.getId() == R.id.edit_image_fragment_sb_brightness) {
                // brightness values are b/w -100 to +100
                listener.onBrightnessChanged(progress - 100);
            }

            if (seekBar.getId() == R.id.edit_image_fragment_sb_contrast) {
                // converting int value to float
                // contrast values are b/w 1.0f - 3.0f
                // progress = progress > 10 ? progress : 10;
                progress += 10;
                float floatVal = .10f * progress;
                listener.onContrastChanged(floatVal);
            }

            if (seekBar.getId() == R.id.edit_image_fragment_sb_saturation) {
                // converting int value to float
                // saturation values are b/w 0.0f - 3.0f
                float floatVal = .10f * progress;
                listener.onSaturationChanged(floatVal);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (listener != null)
            listener.onEditStarted();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (listener != null)
            listener.onEditCompleted();
    }

    public void resetControls() {
        seekBarBrightness.setProgress(100);
        seekBarContrast.setProgress(0);
        seekBarSaturation.setProgress(10);
    }

    public interface EditImageFragmentListener {
        void onBrightnessChanged(int brightness);

        void onSaturationChanged(float saturation);

        void onContrastChanged(float contrast);

        void onEditStarted();

        void onEditCompleted();
    }
}
