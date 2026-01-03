package project.side.domain.usecase.auth

import kotlinx.coroutines.flow.Flow
import project.side.domain.model.LogoutState
import project.side.domain.model.SocialAuthType
import project.side.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(authType: SocialAuthType): Flow<LogoutState> {
        return when (authType) {
            SocialAuthType.GOOGLE -> authRepository.googleLogout()
            SocialAuthType.NAVER -> authRepository.naverLogout()
            SocialAuthType.KAKAO -> authRepository.kakaoLogout()
        }
    }
}
