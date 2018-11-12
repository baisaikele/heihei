package com.heihei.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

public class BaseDialog extends Dialog {

    public BaseDialog(Context context, int styleId) {
        super(context, styleId);
        setOwnerActivity((Activity) context);
    }

    public BaseDialog(Context context) {
        super(context);
        setOwnerActivity((Activity) context);
    }

    public static interface OnActionSheetSelected {

        void onClick(int whichButton);
    }
    
    public static interface BaseDialogOnclicklistener {

        public void onOkClick(Dialog dialog);

        public void onCancleClick(Dialog dialog);
    }
}
