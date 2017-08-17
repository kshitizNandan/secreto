package com.secreto.socialintegration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.secreto.common.Constants;


import org.json.JSONObject;

import java.util.Arrays;

public class FacebookHandler {
    private final Activity activity;
    private final CallbackManager callbackManager;

    public FacebookHandler(final Activity activity, final SocialLoginInterface socialLoginInterface) {
        this.activity = activity;
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    SocialLoginResponse socialLoginResponse = new SocialLoginResponse();
                                    if (object.has("email")) {
                                        socialLoginResponse.setSocialEmailId(object.getString("email"));
                                    }
                                    socialLoginResponse.setSocialUrl(object.getString("link"));
                                    String fbName = object.getString("name");
                                    if (!TextUtils.isEmpty(fbName)) {
                                        if (fbName.contains(" ")) {
                                            String[] separated = fbName.split(" ");
                                            socialLoginResponse.setFirstName(separated[0]);
                                            socialLoginResponse.setLastName(separated[1]);
                                        } else {
                                            socialLoginResponse.setFirstName(fbName);
                                            socialLoginResponse.setLastName("");
                                        }
                                    }
                                    JSONObject picture = object.getJSONObject("picture");
                                    JSONObject data = picture.getJSONObject("data");
                                    socialLoginResponse.setSocialImageUrl(data.getString("url"));
                                    socialLoginResponse.setSocialType(Constants.SocialType.FACEBOOK);
                                    socialLoginResponse.setSocialId(loginResult.getAccessToken().getUserId());
                                    socialLoginInterface.socialResponseSuccess(socialLoginResponse);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    socialLoginInterface.socialResponseFailure();
                                }
                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,link,email,picture");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        socialLoginInterface.socialResponseFailure();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        if (exception instanceof FacebookAuthorizationException) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                LoginManager.getInstance().logOut();
                            }
                        }
                        Toast.makeText(activity, exception.toString(), Toast.LENGTH_SHORT).show();
                        socialLoginInterface.socialResponseFailure();
                    }
                });
    }

    public void performLogin() {
        LoginManager.getInstance().logInWithReadPermissions(this.activity, Arrays.asList("public_profile", "email"));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
