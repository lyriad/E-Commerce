package com.lyriad.e_commerce.Utils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.lyriad.e_commerce.BuildConfig;
import com.lyriad.e_commerce.R;

public final class FragmentNavigationManager {

    private static AppCompatActivity mActivity;
    private FragmentManager mManager;
    private static FragmentNavigationManager mInstance;

    public static FragmentNavigationManager getInstance() {

        if (mInstance == null) {

            try {
                throw new Exception("Fragment Nav Manager hasn't been initialized");

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        mInstance.configure(mActivity);
        return mInstance;
    }

    public static void initialize(final AppCompatActivity activity) {

        if (mInstance == null) {

            mInstance = new FragmentNavigationManager();
        }
        mInstance.configure(activity);
    }

    private void configure(AppCompatActivity activity) {

        mActivity = activity;
        mManager = activity.getSupportFragmentManager();
    }

    public void showFragment(Fragment fragment, boolean allowStateLoss) {

        mManager.popBackStack(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        FragmentTransaction ft = mManager.beginTransaction()
        .replace(R.id.main_frame_container, fragment)
                .setCustomAnimations(R.anim.rtl, R.anim.ltr);

        if (allowStateLoss || !BuildConfig.DEBUG) {

            ft.commitNowAllowingStateLoss();

        } else {
            ft.commit();
        }

        mManager.executePendingTransactions();
    }
}
