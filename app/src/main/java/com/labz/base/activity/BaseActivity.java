package com.labz.base.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.labz.R;
import com.labz.base.fragment.FragmentManagerTransaction;
import com.labz.preferences.PreferencesHelper;
import com.labz.utils.KeyboardUtils;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class BaseActivity extends AppCompatActivity implements FragmentManagerTransaction {

    private static final String TAG = BaseActivity.class.getSimpleName();
    //    private final static int toolbarId = R.id.toolbar;
    protected final static int fragmentContainerId = R.id.container;

    private Toolbar mToolbar;
    private AppCompatTextView toolbarTitle;

    @DrawableRes
    private int toolbarFragmentNavigationIcon;

    @DrawableRes
    protected int getToolbarNavigationIcon() {
        return 0;
    }

    @MenuRes
    protected int getMenuLayoutId() {
        return 0;
    }

    @ColorRes
    protected int getStatusBarColor() {
        return 0;
    }

    public void setStatusBarColor(@ColorRes int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, color));
        }
    }

    public void setStatusBarColorValue(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap((base)));
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        setTheme(defineAppTheme());
        super.setContentView(layoutResID);
    }

    @StyleRes
    private int defineAppTheme() {
        PreferencesHelper preferences = PreferencesHelper.getInstance(this);
        switch (preferences.getThemeType()) {
            case "Dark theme":
                return R.style.AppThemeDefault;
            case "Light theme":
                return R.style.AppThemeBlue;
            default:
                return R.style.AppThemeDefault;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleExtras(savedInstanceState != null ? savedInstanceState : getIntent().getExtras());
        if (getStatusBarColor() != 0)
            setStatusBarColor(getStatusBarColor());
        initToolbar();
    }

    protected void handleExtras(@Nullable Bundle extras) {

    }

    public Toolbar getToolbar() {
        return mToolbar;
    }


    @Override
    public void changeToolbarTitle(CharSequence title) {
        if (toolbarTitle != null) {
            toolbarTitle.setText(title);
        }
    }

    @Override
    public void changeToolbarTitle(@StringRes int res_id) {
        if (toolbarTitle != null) {
            toolbarTitle.setText(res_id);
        }
    }

    @Override
    public void changeToolbarNavigationIcon(@DrawableRes int res_id) {
        toolbarFragmentNavigationIcon = res_id;
        initToolbar();
    }

    @Override
    public void replaceActivityFragment(@NonNull Fragment fragment, boolean addToBackStack, boolean anim) {
        if (findViewById(fragmentContainerId) != null) {
            replaceFragment(fragment, fragmentContainerId, addToBackStack, anim);
        }
    }

    @Override
    public void replaceActivityFragment(@NonNull Fragment fragment, @IdRes int container, String tag, boolean addToBackStack, boolean anim) {
        replaceFragment(fragment, container, tag, addToBackStack, anim);
    }

    @Override
    public void replaceActivityFragment(@NonNull Fragment fragment, String tag, boolean addToBackStack, boolean anim) {
        if (findViewById(fragmentContainerId) != null) {
            replaceFragment(fragment, fragmentContainerId, tag, addToBackStack, anim);
        }
    }

    @Override
    public void replaceActivityFragmentUpDown(@NonNull Fragment fragment, boolean addToBackStack, boolean anim) {
        if (findViewById(fragmentContainerId) != null) {
            replaceFragmentUpDown(fragment, fragmentContainerId, fragment.getClass().getSimpleName(), addToBackStack, anim);
        }
    }

    @Override
    public void replaceActivityFragmentUpDown(@NonNull Fragment fragment, @IdRes int container, String tag, boolean addToBackStack, boolean anim) {
        replaceFragmentUpDown(fragment, fragmentContainerId, tag, addToBackStack, anim);
    }


    public void replaceFragment(@NonNull Fragment fragment, @IdRes int container, boolean addToBackStack, boolean anim) {
        replaceFragment(fragment, container, fragment.getClass().getSimpleName(), addToBackStack, anim);
    }

    @Override
    public void showToast(@StringRes int res) {
        try {
            Toast toast = Toast.makeText(this, res, Toast.LENGTH_LONG);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            if (v != null) v.setGravity(Gravity.CENTER);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void changeStatusBarColor(@ColorRes int color) {
        if (color != 0)
            setStatusBarColor(color);
    }

    public void changeStatusBarColorValue(int color) {
        if (color != 0)
            setStatusBarColorValue(color);
    }

    public void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);
//        if (mToolbar != null && getSupportActionBar() != null) {
//            getSupportActionBar().setTitle(null);
//            toolbarTitle = (AppCompatTextView) mToolbar.findViewById(R.id.toolbarTitle);
//        }
        if (mToolbar != null && getSupportActionBar() != null) {
            int navigationIcon = toolbarFragmentNavigationIcon != 0 ?
                    toolbarFragmentNavigationIcon : getToolbarNavigationIcon();
            if (navigationIcon != 0) {
                mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "onClick: Sup");
                        onBackPressed();
                    }
                });
                mToolbar.setNavigationIcon(navigationIcon);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int menuLayoutId = getMenuLayoutId();
        if (menuLayoutId > 0) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(menuLayoutId, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void showAlert(String alertTitle, String alertMessage, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(this)
                .setTitle(alertTitle)
                .setMessage(alertMessage)
                .setPositiveButton("Done", listener)
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void replaceFragment(@NonNull Fragment fragment, @IdRes int container, String tag, boolean addToBackStack, boolean anim) {
        if (container == 0) {
            throw new Resources.NotFoundException("Fragment id container not found!");
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        if (anim)
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        if (addToBackStack)
            transaction.addToBackStack(tag);

        transaction.replace(container, fragment, tag);
        transaction.commitAllowingStateLoss();

        KeyboardUtils.hideKeyboard(BaseActivity.this);
    }

    private void replaceFragmentUpDown(@NonNull Fragment fragment, @IdRes int container, String tag, boolean addToBackStack, boolean anim) {
        if (container == 0) {
            throw new Resources.NotFoundException("Fragment id container not found!");
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        if (anim)
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down);
        if (addToBackStack)
            transaction.addToBackStack(tag);

        transaction.replace(container, fragment, tag);
        transaction.commitAllowingStateLoss();

        KeyboardUtils.hideKeyboard(BaseActivity.this);
    }

}