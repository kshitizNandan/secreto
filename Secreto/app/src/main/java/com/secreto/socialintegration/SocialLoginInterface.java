package com.secreto.socialintegration;

public interface SocialLoginInterface {
    void socialResponseSuccess(SocialLoginResponse socialLoginResponse);
    void socialResponseFailure();
}
