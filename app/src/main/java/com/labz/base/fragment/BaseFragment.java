package com.labz.base.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;

import com.labz.utils.KeyboardUtils;

/**
 * Created by Stanislav on 10.01.17.
 */

public class BaseFragment extends Fragment {

    private FragmentManagerTransaction fragmentManagerTransaction;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            fragmentManagerTransaction = (FragmentManagerTransaction) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.getClass().getSimpleName() + " must implement " + FragmentManagerTransaction.class);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setToolbarNavigationIcon();
        setRetainInstance(true);
        handleArguments(savedInstanceState != null ? savedInstanceState : getArguments());
    }


    protected void handleArguments(@Nullable Bundle args) {
    }

    @ColorRes
    protected int getStatusBarColor() {
        return 0;
    }

    @StringRes
    protected int getScreenTitle() {
        return 0;
    }

    @DrawableRes
    protected int getToolbarNavigationIcon() {
        return 0;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        setScreenTitle();
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void changeStatusBarColor(@ColorRes int colour) {
        if (getStatusBarColor() != 0 && fragmentManagerTransaction != null) {
            fragmentManagerTransaction.changeStatusBarColor(colour);
        }
    }

    private void setToolbarNavigationIcon() {
        if (getToolbarNavigationIcon() != 0 && fragmentManagerTransaction != null) {
            fragmentManagerTransaction.changeToolbarNavigationIcon(getToolbarNavigationIcon());
        }
    }

    private void setScreenTitle() {
        if (getScreenTitle() != 0 && fragmentManagerTransaction != null) {
            fragmentManagerTransaction.changeToolbarTitle(getScreenTitle());
        }
    }

    protected void setScreenTitle(CharSequence title) {
        if (getScreenTitle() != 0 && fragmentManagerTransaction != null) {
            fragmentManagerTransaction.changeToolbarTitle(title);
        }
    }

    protected void replaceNestedFragment(@NonNull Fragment fragment, @IdRes int fragmentContainerId) {
        if (fragmentContainerId == 0) {
            throw new Resources.NotFoundException("Fragment id container not found!");
        }
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(fragmentContainerId, fragment, fragment.getClass().getSimpleName());
        transaction.commitAllowingStateLoss();
    }

    protected void replaceActivityFragment(@NonNull Fragment fragment, boolean addToBackStack, boolean anim) {
        if (fragmentManagerTransaction != null) {
            fragmentManagerTransaction.replaceActivityFragment(fragment, addToBackStack, anim);
        }
    }

    protected void replaceActivityFragmentUpDown(@NonNull Fragment fragment, boolean addToBackStack, boolean anim) {
        if (fragmentManagerTransaction != null) {
            fragmentManagerTransaction.replaceActivityFragmentUpDown(fragment, addToBackStack, anim);
        }
    }

    protected void replaceActivityFragment(@NonNull Fragment fragment, String tag, boolean addToBackStack, boolean anim) {
        if (fragmentManagerTransaction != null) {
            fragmentManagerTransaction.replaceActivityFragment(fragment, tag, addToBackStack, anim);
        }
    }

    protected void popBackStackFragment() {
        KeyboardUtils.hideKeyboard(getActivity());
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    protected void popBackStackToFragment(@NonNull String tag) {
        KeyboardUtils.hideKeyboard(getActivity());
        getActivity().getSupportFragmentManager().popBackStackImmediate(tag, 0);
    }

    protected void popBackStackToFragmentInclude(@NonNull String tag) {
        KeyboardUtils.hideKeyboard(getActivity());
        getActivity().getSupportFragmentManager().popBackStackImmediate(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    protected void popAllBackStackFragment() {
        KeyboardUtils.hideKeyboard(getActivity());
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStackImmediate();
        }
    }

    @Override
    public void onDetach() {
        if (fragmentManagerTransaction != null) {
            if (getToolbarNavigationIcon() != 0) {
                fragmentManagerTransaction.changeToolbarNavigationIcon(0);
            }
            fragmentManagerTransaction = null;
        }

        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}