package android.example.myfoodrecords;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyEditText extends androidx.appcompat.widget.AppCompatEditText {
    public MyEditText(@NonNull Context context) {
        super(context);
        setupUI();
    }

    public MyEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupUI();
    }

    public MyEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupUI();
    }

    private void setupUI() {
        int index = getText() == null ? 0 : getText().length();
        setSelection(index);
    }
}