package project.side.domain.usecase.auth

import kotlinx.coroutines.flow.Flow
import project.side.domain.model.LoginState
import project.side.domain.model.SocialAuthType
import project.side.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(authType: SocialAuthType): Flow<LoginState> {
        return when (authType) {
            SocialAuthType.GOOGLE -> authRepository.googleLogin()
            SocialAuthType.NAVER -> authRepository.naverLogin()
            SocialAuthType.KAKAO -> authRepository.kakaoLogin()
        }
    }
}
