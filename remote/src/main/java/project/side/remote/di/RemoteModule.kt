package project.side.remote.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import project.side.data.datasource.AladinBookSearchSource
import project.side.data.datasource.AuthDataSource
import project.side.data.datasource.HistoryDataSource
import project.side.data.datasource.TestDataSource
import project.side.remote.BuildConfig
import project.side.remote.api.AladinBookService
import project.side.remote.api.AuthService
import project.side.remote.api.BackendApiService
import project.side.remote.api.HistoryService
import project.side.remote.api.TestApiService
import project.side.remote.api.UserService
import project.side.remote.auth.AuthInterceptor
import project.side.remote.auth.TokenAuthenticator
import project.side.remote.datasource.AladinBookSearchSourceImpl
import project.side.remote.datasource.AuthDataSourceImpl
import project.side.remote.datasource.HistoryDataSourceImpl
import project.side.remote.datasource.TestDataSourceImpl
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
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
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
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
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
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
    fun provideBackendApiService(@AuthRetrofit retrofit: Retrofit): BackendApiService =
        retrofit.create(BackendApiService::class.java)


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
    fun provideUserService(@DefaultRetrofit retrofit: Retrofit): UserService =
        retrofit.create(UserService::class.java)

    @Provides
    @Singleton
    fun provideHistoryService(@AuthRetrofit retrofit: Retrofit): HistoryService =
        retrofit.create(HistoryService::class.java)

    @Provides
    @Singleton
    fun provideHistoryDataSource(historyService: HistoryService): HistoryDataSource =
        HistoryDataSourceImpl(historyService)

    @Provides
    @Singleton
    fun provideAladinBookService(moshi: Moshi, @DefaultOkHttpClient okHttpClient: OkHttpClient): AladinBookService =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://www.aladin.co.kr/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(AladinBookService::class.java)

    @Provides
    @Singleton
    fun provideAladinBookSearchSource(service: AladinBookService) : AladinBookSearchSource =
        AladinBookSearchSourceImpl(service)

    @Provides
    @Singleton
    fun provideBackendDataSource(backendApiService: BackendApiService): project.side.data.datasource.BackendDataSource =
        project.side.remote.datasource.BackendDataSourceImpl(backendApiService)
}

