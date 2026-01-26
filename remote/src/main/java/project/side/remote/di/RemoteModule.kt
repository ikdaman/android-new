package project.side.remote.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import project.side.data.datasource.AuthDataSource
import project.side.data.datasource.TestDataSource
import project.side.remote.BuildConfig
import project.side.remote.api.AuthService
import project.side.remote.api.TestApiService
import project.side.remote.api.UserService
import project.side.remote.auth.AuthInterceptor
import project.side.remote.auth.TokenAuthenticator
import project.side.remote.datasource.AuthDataSourceImpl
import project.side.remote.datasource.TestDataSourceImpl
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthRetrofit

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {
    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    @DefaultOkHttpClient
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        ).build()

    @Provides
    @Singleton
    @AuthOkHttpClient
    fun provideAuthOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .build()

    @Provides
    @Singleton
    @DefaultRetrofit
    fun provideRetrofit(moshi: Moshi, @DefaultOkHttpClient okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    @AuthRetrofit
    fun provideAuthRetrofit(moshi: Moshi, @AuthOkHttpClient okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun provideTestApiService(@DefaultRetrofit retrofit: Retrofit): TestApiService =
        retrofit.create(TestApiService::class.java)

    @Provides
    @Singleton
    fun provideTestDataSource(testApiService: TestApiService): TestDataSource =
        TestDataSourceImpl(testApiService)

    @Provides
    @Singleton
    fun provideAuthService(@DefaultRetrofit retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun provideAuthDataSource(authService: AuthService): AuthDataSource =
        AuthDataSourceImpl(authService)

    @Provides
    @Singleton
    fun provideRefreshService(@DefaultRetrofit retrofit: Retrofit): UserService =
        retrofit.create(UserService::class.java)
}