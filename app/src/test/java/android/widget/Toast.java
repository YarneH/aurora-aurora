package android.widget;

import android.content.Context;

public class Toast {

    private Context mContext;
    private static CharSequence sText;
    private static int sLength;

    public Toast(Context context) {
        mContext = context;
    }

    public static Toast makeText(Context context, CharSequence text, int length) {
        sText = text;
        sLength = length;
        return new Toast(context);
    }

    public void show() {
        System.out.println("Display Toast: \"" + sText + "\" with length " + sLength + ".");
    }
}
