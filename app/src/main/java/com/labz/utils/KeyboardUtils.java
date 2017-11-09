package com.labz.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import java.util.HashMap;

/**
 * Created by vostopolets on 27.01.17.
 */
public class KeyboardUtils implements ViewTreeObserver.OnGlobalLayoutListener {

    private SoftKeyboardToggleListener mCallback;
    private final View mRootView;
    private static final HashMap<SoftKeyboardToggleListener, KeyboardUtils> sListenerMap = new HashMap<>();


    private KeyboardUtils(@NonNull Activity act, @NonNull SoftKeyboardToggleListener listener) {
        mCallback = listener;
        mRootView = ((ViewGroup) act.findViewById(android.R.id.content)).getChildAt(0);
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        Rect r = new Rect();
        mRootView.getWindowVisibleDisplayFrame(r);
        int screenHeight = mRootView.getRootView().getHeight();
        int heightDiff = screenHeight - (r.bottom - r.top);
        boolean visible = heightDiff > screenHeight / 3;
        if (mCallback != null) {
            mCallback.onToggleSoftKeyboard(visible);
        }
    }


    public static void addKeyboardToggleListener(@NonNull Activity act, @NonNull SoftKeyboardToggleListener listener) {
        removeKeyboardToggleListener(listener);
        sListenerMap.put(listener, new KeyboardUtils(act, listener));
    }

    private static void removeKeyboardToggleListener(@NonNull SoftKeyboardToggleListener listener) {
        if (sListenerMap.containsKey(listener)) {
            KeyboardUtils k = sListenerMap.get(listener);
            k.removeListener();
            sListenerMap.remove(listener);
        }
    }

    public static void removeAllKeyboardToggleListeners() {
        for (SoftKeyboardToggleListener l : sListenerMap.keySet()) {
            sListenerMap.get(l).removeListener();
        }
        sListenerMap.clear();
    }

    private void removeListener() {
        mCallback = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        } else {
            mRootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    }


    public static void hideKeyboard(@NonNull Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        if (activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }

    }

    public static void showSoftKeyboard(View view) {
        if (view == null) return;

        Context context = view.getContext();
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }

    public static void toggleKeyboard(@NonNull Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        } else {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    public interface SoftKeyboardToggleListener {
        void onToggleSoftKeyboard(boolean isVisible);

    }

}