package com.secreto.socialintegration;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.secreto.common.Constants;
import com.secreto.common.MyApplication;
import com.secreto.utils.Logger;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;

import retrofit2.Call;
import retrofit2.Response;

public class TwitterHandler {
    private static final String TAG = TwitterHandler.class.getSimpleName();
    private final Activity activity;
    private final TwitterAuthClient mTwitterAuthClient;
    private final SocialLoginInterface socialLoginInterface;

    public TwitterHandler(final Activity activity, final SocialLoginInterface socialLoginInterface) {
        this.activity = activity;
        this.socialLoginInterface = socialLoginInterface;
        mTwitterAuthClient = new TwitterAuthClient();
    }

    public void performLogin() {
        mTwitterAuthClient.authorize(this.activity, new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Logger.d(TAG, "authorize success : " + result.data);
                TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
                Call<User> call = twitterApiClient.getAccountService().verifyCredentials(true, false, true);
                call.enqueue(new retrofit2.Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        User user = response.body();
                        final SocialLoginResponse socialLoginResponse = new SocialLoginResponse();
                        // Adding empty string so that if preference value has some value it gets removed for twitter
                        socialLoginResponse.setSocialId(user.idStr);
                        socialLoginResponse.setSocialType(Constants.SocialType.TWITTER);
                        socialLoginResponse.setSocialImageUrl(user.profileImageUrl);
                        socialLoginResponse.setSocialEmailId(user.email);
                        socialLoginResponse.setSocialUrl("https://twitter.com/" + user.screenName);
                        String twName = user.name;
                        if (!TextUtils.isEmpty(twName)) {
                            if (twName.contains(" ")) {
                                String[] separated = twName.split(" ");
                                socialLoginResponse.setFirstName(separated[0]);
                                socialLoginResponse.setLastName(separated[1]);
                            } else {
                                socialLoginResponse.setFirstName(twName);
                                socialLoginResponse.setFirstName("");
                            }
                        }
                        socialLoginInterface.socialResponseSuccess(socialLoginResponse);
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(MyApplication.getInstance(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        socialLoginInterface.socialResponseFailure();
                    }
                });
            }

            @Override
            public void failure(TwitterException e) {
                Logger.e(TAG, "authorize failure : " + e);
                Toast.makeText(MyApplication.getInstance(), e.getMessage(), Toast.LENGTH_SHORT).show();
                socialLoginInterface.socialResponseFailure();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
    }
}
