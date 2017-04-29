package com.moggot.vkontaktephotoviewer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

/**
 * Created by toor on 25.04.17.
 */

public class LoginActivity extends FragmentActivity {

    private boolean isResumed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        VKSdk.wakeUpSession(this, new VKCallback<VKSdk.LoginState>() {
            @Override
            public void onResult(VKSdk.LoginState res) {
                if (isResumed) {
                    switch (res) {
                        case LoggedOut:
                            showLogin();
                            break;
                        case LoggedIn:
                            showPreview();
                            break;
                        case Pending:
                            break;
                        case Unknown:
                            break;
                    }
                }
            }

            @Override
            public void onError(VKError error) {

            }
        });
    }

    private void showLogin() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(Consts.LOGIN_FRAGMENT_TAG);
        if (fragment == null)
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, LoginFragment.newInstance(), Consts.LOGIN_FRAGMENT_TAG)
                    .commitAllowingStateLoss();
    }

    private void showPreview() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(Consts.PREVIEW_FRAGMENT_TAG);
        if (fragment == null)
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, PreviewFragment.newInstance(), Consts.PREVIEW_FRAGMENT_TAG)
                    .commitAllowingStateLoss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResumed = true;
        if (VKSdk.isLoggedIn()) {
            showPreview();
        } else {
            showLogin();
        }
    }

    @Override
    protected void onPause() {
        isResumed = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        VKCallback<VKAccessToken> callback = new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                // User passed Authorization
                showLogin();
            }

            @Override
            public void onError(VKError error) {
                // User didn't pass Authorization
            }
        };

        if (!VKSdk.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

//    private void startTestActivity() {
//        startActivity(new Intent(this, PreviewActivity.class));
//    }

//    public static class LoginFragment extends android.support.v4.app.Fragment {
//        public LoginFragment() {
//            super();
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//            View v = inflater.inflate(R.layout.fragment_login, container, false);
//            v.findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    VKSdk.login(getActivity(), sMyScope);
//                }
//            });
//            return v;
//        }
//
//    }

//    public static class LoginFragment extends android.support.v4.app.Fragment {
//        public LoginFragment() {
//            super();
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//            View v = inflater.inflate(R.layout.fragment_login, container, false);
//            v.findViewById(R.id.continue_button).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    ((LoginActivity) getActivity()).startTestActivity();
//                }
//            });
//
//            v.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    VKSdk.logout();
//                    if (!VKSdk.isLoggedIn()) {
//                        ((LoginActivity) getActivity()).showLogin();
//                    }
//                }
//            });
//            return v;
//        }
//    }
}