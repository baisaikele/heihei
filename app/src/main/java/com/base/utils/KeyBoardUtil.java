package com.base.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class KeyBoardUtil {

    public static void showSoftKeyBoard(Activity activity, EditText etV) {
        etV.setFocusable(true);
        etV.setFocusableInTouchMode(true);
        etV.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(etV, 0);
    }

    public static void hideSoftKeyBoard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (activity.getCurrentFocus() == null || activity.getCurrentFocus().getWindowToken() == null)
                return;
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
