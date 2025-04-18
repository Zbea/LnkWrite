package com.bll.lnkwrite.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.bll.lnkwrite.R;

import java.util.Objects;


/**
 * 取名 super Toast 的意思
 * 同一时间只能显示一个toast
 * 支持在任意线程调用
 * 可以取消toast
 */
public class SToast {
    private static Context ctx;
    private static Toast toast;
    private static Handler handler;

    public static void initToast(Context context) {
        ctx = context;
        handler = new Handler(ctx.getMainLooper());
    }

    public static void showText(@StringRes int res) {
        showText(1,ctx.getString(res));
    }

    public static void showText( final CharSequence str) {
        if (Thread.currentThread().getId() != 1) {
            // 在子线程
            handler.post(() -> finalShow(1,str));
        } else {
            finalShow(1,str);
        }
    }

    public static void showText(int screen,@StringRes int res) {
        showText(screen,ctx.getString(res));
    }

    public static void showText(int screen, final CharSequence str) {
        if (Thread.currentThread().getId() != 1) {
            // 在子线程
            handler.post(() -> finalShow(screen,str));
        } else {
            finalShow(screen,str);
        }
    }

    private static void finalShow(int screen, CharSequence str) {
        if (toast == null) {
            toast = Toast.makeText(ctx, str, Toast.LENGTH_SHORT);
        }
        if (Build.VERSION.SDK_INT < 30) {
            TextView text = Objects.requireNonNull(toast.getView()).findViewById(android.R.id.message);
            text.setWidth(400);
            text.setGravity(Gravity.CENTER);
            text.setTextColor(Color.WHITE);
            text.setTextSize(20);
            text.setPadding(30, 20, 30, 20);
            toast.getView().setBackground(ctx.getDrawable(R.drawable.bg_black_solid_10dp_corner));
        }
        if (screen==1){
            toast.setGravity(Gravity.BOTTOM|Gravity.START, 500, 200);
        }
        else {
            toast.setGravity(Gravity.BOTTOM|Gravity.END, 500, 200);
        }
        toast.setText(str);
        toast.show();
    }

    public static void showTextLong(@StringRes int res) {
        showTextLong(1,ctx.getString(res));
    }

    public static void showTextLong( final CharSequence str) {
        if (Thread.currentThread().getId() != 1) {
            // 在子线程
            handler.post(() -> finalShowLong(1,str));
        } else {
            finalShowLong(1,str);
        }
    }

    public static void showTextLong(int screen,@StringRes int res) {
        showTextLong(screen,ctx.getString(res));
    }

    public static void showTextLong(int screen, final CharSequence str) {
        if (Thread.currentThread().getId() != 1) {
            // 在子线程
            handler.post(() -> finalShowLong(screen,str));
        } else {
            finalShowLong(screen,str);
        }
    }

    private static void finalShowLong(int screen, CharSequence str) {
        if (toast == null) {
            toast = Toast.makeText(ctx, str, Toast.LENGTH_LONG);
        }
        if (Build.VERSION.SDK_INT < 30) {
            TextView text = Objects.requireNonNull(toast.getView()).findViewById(android.R.id.message);
            text.setWidth(400);
            text.setGravity(Gravity.CENTER);
            text.setTextColor(Color.WHITE);
            text.setTextSize(20);
            text.setPadding(30, 20, 30, 20);
            toast.getView().setBackground(ctx.getDrawable(R.drawable.bg_black_solid_10dp_corner));
        }
        if (screen==1){
            toast.setGravity(Gravity.BOTTOM|Gravity.START, 500, 200);
        }
        else {
            toast.setGravity(Gravity.BOTTOM|Gravity.END, 500, 200);
        }
        toast.setText(str);
        toast.show();
    }


    /**
     * 取消显示
     * 建议放在 baseActivity中做统一处理
     */
    public static void cancel() {
        if (toast != null) {
            toast.cancel();
        }
    }
}
