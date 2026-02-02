package project.side.presentation.model

import project.side.domain.model.SocialAuthType

enum class AuthType {
    GOOGLE, NAVER, KAKAO;

    fun toDomainAuthType(): SocialAuthType {
        return when (this) {
            GOOGLE -> SocialAuthType.GOOGLE
            NAVER -> SocialAuthType.NAVER
            KAKAO -> SocialAuthType.KAKAO
        }
    }
}