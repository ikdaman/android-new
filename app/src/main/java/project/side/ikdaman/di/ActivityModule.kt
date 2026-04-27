package project.side.ikdaman.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import project.side.data.auth.TokenCacheManager
import project.side.data.datasource.AuthDataSource
import project.side.data.datasource.AuthDataStoreSource
import project.side.data.datasource.SocialAuthDataSource
import project.side.data.repository.AuthRepositoryImpl
import project.side.domain.repository.AuthRepository
import project.side.domain.usecase.SignupUseCase
import project.side.domain.usecase.auth.GetProviderUseCase
import project.side.domain.usecase.auth.LoginUseCase
import project.side.domain.usecase.auth.LogoutUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ActivityModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        authDataSource: AuthDataSource,
        socialAuthDataSource: SocialAuthDataSource,
        authDataStoreSource: AuthDataStoreSource,
        tokenCacheManager: TokenCacheManager
    ): AuthRepository = AuthRepositoryImpl(authDataSource, socialAuthDataSource, authDataStoreSource, tokenCacheManager)

    @Provides
    @Singleton
    fun provideLoginUseCase(authRepository: AuthRepository) = LoginUseCase(authRepository)

    @Provides
    @Singleton
    fun provideLogoutUseCase(authRepository: AuthRepository) = LogoutUseCase(authRepository)

    @Provides
    @Singleton
    fun provideSignupUseCase(authRepository: AuthRepository) = SignupUseCase(authRepository)

    @Provides
    @Singleton
    fun provideGetProviderUseCase(authRepository: AuthRepository) = GetProviderUseCase(authRepository)
}
