package com.labz.base.fragment;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;


/**
 * Created by Stanislav on 11.05.17.
 */

public interface FragmentManagerTransaction {
    void replaceActivityFragment(@NonNull Fragment fragment, boolean addToBackStack, boolean anim);

    void replaceActivityFragment(@NonNull Fragment fragment, @IdRes int container, String tag, boolean addToBackStack, boolean anim);

    void replaceActivityFragment(@NonNull Fragment fragment, String tag, boolean addToBackStack, boolean anim);

    void replaceActivityFragmentUpDown(@NonNull Fragment fragment, boolean addToBackStack, boolean anim);

    void replaceActivityFragmentUpDown(@NonNull Fragment fragment, @IdRes int container, String tag, boolean addToBackStack, boolean anim);

    void changeToolbarTitle(CharSequence title);

    void changeToolbarTitle(@StringRes int res_id);

    void changeToolbarNavigationIcon(@DrawableRes int res_id);

    void showToast(@StringRes int res);

    void changeStatusBarColor(@ColorRes int color);

}