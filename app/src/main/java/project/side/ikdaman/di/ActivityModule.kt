package project.side.ikdaman.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import project.side.data.datasource.AuthDataSource
import project.side.data.datasource.AuthDataStoreSource
import project.side.data.datasource.SocialAuthDataSource
import project.side.data.repository.AuthRepositoryImpl
import project.side.domain.repository.AuthRepository
import project.side.domain.usecase.SignupUseCase
import project.side.domain.usecase.auth.GetProviderUseCase
import project.side.domain.usecase.auth.LoginUseCase
import project.side.domain.usecase.auth.LogoutUseCase

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {

    @Provides
    @ActivityScoped
    fun provideAuthRepository(
        authDataSource: AuthDataSource,
        socialAuthDataSource: SocialAuthDataSource,
        authDataStoreSource: AuthDataStoreSource
    ): AuthRepository = AuthRepositoryImpl(authDataSource, socialAuthDataSource, authDataStoreSource)

    @Provides
    @ActivityScoped
    fun provideLoginUseCase(authRepository: AuthRepository) = LoginUseCase(authRepository)

    @Provides
    @ActivityScoped
    fun provideLogoutUseCase(authRepository: AuthRepository) = LogoutUseCase(authRepository)

    @Provides
    @ActivityScoped
    fun provideSignupUseCase(authRepository: AuthRepository) = SignupUseCase(authRepository)

    @Provides
    @ActivityScoped
    fun provideGetProviderUseCase(authRepository: AuthRepository) = GetProviderUseCase(authRepository)
}
