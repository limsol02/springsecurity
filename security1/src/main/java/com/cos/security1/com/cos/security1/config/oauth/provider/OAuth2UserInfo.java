package com.cos.security1.config.oauth.provider;

public interface OAuth2UserInfo {
    // 페이스북이랑 구글 키 가 다르기 때문에
    // getAttributes : {id=7646002862170636, name=임솔, email=dlathf0202@naver.com}
    // 이거 처리할 필드들
    String getProviderId();
    String getProvider();
    String getEmail();
    String getName();

}
