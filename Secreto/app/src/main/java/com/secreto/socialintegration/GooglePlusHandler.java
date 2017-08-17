package com.secreto.socialintegration;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.secreto.common.Constants;


public class GooglePlusHandler implements GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 1200;
    private final FragmentActivity activity;
    private final SocialLoginInterface socialLoginInterface;
    private final GoogleApiClient mGoogleApiClient;

    public GooglePlusHandler(final FragmentActivity activity, final SocialLoginInterface socialLoginInterface) {
        this.activity = activity;
        this.socialLoginInterface = socialLoginInterface;

        // basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .enableAutoManage(activity, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        socialLoginInterface.socialResponseFailure();
    }

    public void performLogin() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                SocialLoginResponse socialLoginResponse = new SocialLoginResponse();
                // Get account information
                String fullName = acct.getDisplayName();
                if (!TextUtils.isEmpty(fullName)) {
                    if (fullName.contains(" ")) {
                        String[] separated = fullName.split(" ");
                        socialLoginResponse.setFirstName(separated[0]);
                        socialLoginResponse.setLastName(separated[1]);
                    } else {
                        socialLoginResponse.setFirstName(fullName);
                        socialLoginResponse.setLastName("");
                    }
                }
                socialLoginResponse.setSocialUrl("https://plus.google.com/" + acct.getId());
                socialLoginResponse.setSocialEmailId(acct.getEmail());
                socialLoginResponse.setSocialId(acct.getId());
                socialLoginResponse.setSocialType(Constants.SocialType.GOOGLE);
                if (acct.getPhotoUrl() != null) {
                    socialLoginResponse.setSocialImageUrl(acct.getPhotoUrl().toString());
                }
                socialLoginInterface.socialResponseSuccess(socialLoginResponse);
            } else {
                socialLoginInterface.socialResponseFailure();
            }
        }
    }
}
