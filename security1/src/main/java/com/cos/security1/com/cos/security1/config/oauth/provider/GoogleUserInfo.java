package com.cos.security1.config.oauth.provider;

import java.util.Map;

public class GoogleUserInfo implements OAuth2UserInfo{

    // getAttributes : {id=7646002862170636, name=임솔, email=dlathf0202@naver.com} 이걸 받을 객체
    private Map<String,Object> attributes; // = oauth2User.getAttributes();
    public GoogleUserInfo(Map<String,Object> attributes) {
        this.attributes = attributes;
    }
    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }
}
