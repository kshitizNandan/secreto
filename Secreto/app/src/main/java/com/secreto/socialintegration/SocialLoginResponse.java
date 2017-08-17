package com.secreto.socialintegration;

import java.io.Serializable;

public class SocialLoginResponse implements Serializable{
    private String socialEmailId;
    private String firstName;
    private String lastName;
    private String socialImageUrl;
    private String socialType;
    private String socialId;
    private String socialUrl;

    public String getSocialEmailId() {
        return socialEmailId;
    }

    public void setSocialEmailId(String socialEmailId) {
        this.socialEmailId = socialEmailId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSocialImageUrl() {
        return socialImageUrl;
    }

    public void setSocialImageUrl(String socialImageUrl) {
        this.socialImageUrl = socialImageUrl;
    }

    public String getSocialType() {
        return socialType;
    }

    public void setSocialType(String socialType) {
        this.socialType = socialType;
    }

    public String getSocialId() {
        return socialId;
    }

    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }

    public String getSocialUrl() {
        return socialUrl;
    }

    public void setSocialUrl(String socialUrl) {
        this.socialUrl = socialUrl;
    }

    @Override
    public String toString() {
        return "SocialLoginResponse{" +
                "socialEmailId='" + socialEmailId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", socialImageUrl='" + socialImageUrl + '\'' +
                ", socialType='" + socialType + '\'' +
                ", socialId='" + socialId + '\'' +
                ", socialUrl='" + socialUrl + '\'' +
                '}';
    }
}
