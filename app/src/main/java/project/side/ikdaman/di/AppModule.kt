package project.side.ikdaman.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import project.side.data.datasource.AladinBookSearchSource
import project.side.data.datasource.AuthDataStoreSource
import project.side.data.datasource.HistoryDataSource
import project.side.data.datasource.MemberDataSource
import project.side.data.datasource.MyBookDataSource
import project.side.data.datasource.TestDataSource

import project.side.data.repository.AladinRepositoryImpl
import project.side.data.repository.HistoryRepositoryImpl
import project.side.data.repository.AuthEventRepositoryImpl
import project.side.data.repository.MemberRepositoryImpl
import project.side.data.repository.MyBookRepositoryImpl
import project.side.data.repository.TestRepositoryImpl
import project.side.data.repository.UserRepositoryImpl
import project.side.domain.repository.AladinRepository
import project.side.domain.repository.HistoryRepository
import project.side.domain.repository.AuthEventRepository
import project.side.domain.repository.MemberRepository
import project.side.domain.repository.MyBookRepository
import project.side.domain.repository.TestRepository
import project.side.domain.repository.UserRepository
import project.side.domain.usecase.GetHistoryBooksUseCase
import project.side.domain.usecase.GetAuthEventUseCase
import project.side.domain.usecase.GetLoginStateUseCase
import project.side.domain.usecase.TestUseCase
import project.side.domain.usecase.member.CheckNicknameUseCase
import project.side.domain.usecase.member.GetMyInfoUseCase
import project.side.domain.usecase.member.UpdateNicknameUseCase
import project.side.domain.usecase.member.WithdrawUseCase
import project.side.domain.usecase.mybook.DeleteMyBookUseCase
import project.side.domain.usecase.mybook.GetMyBookDetailUseCase
import project.side.domain.usecase.mybook.GetStoreBooksUseCase
import project.side.domain.usecase.mybook.SearchMyBooksUseCase
import project.side.domain.usecase.mybook.UpdateMyBookUseCase
import project.side.domain.usecase.mybook.UpdateReadingStatusUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideTestRepository(testDataSource: TestDataSource): TestRepository =
        TestRepositoryImpl(testDataSource)

    @Provides
    @Singleton
    fun provideAladinRepository(aladinBookSearchSource: AladinBookSearchSource): AladinRepository =
        AladinRepositoryImpl(aladinBookSearchSource)


    @Provides
    @Singleton
    fun provideTestUseCase(testRepository: TestRepository) = TestUseCase(testRepository)

    @Provides
    @Singleton
    fun provideSaveManualBookInfoUseCase(myBookRepository: MyBookRepository) =
        project.side.domain.usecase.SaveManualBookInfoUseCase(myBookRepository)

    @Provides
    @Singleton
    fun provideUserRepository(authDataStoreSource: AuthDataStoreSource): UserRepository =
        UserRepositoryImpl(authDataStoreSource)

    @Provides
    @Singleton
    fun provideGetLoginStateUseCase(userRepository: UserRepository) =
        GetLoginStateUseCase(userRepository)

    @Provides
    @Singleton
    fun provideAuthEventRepository(): AuthEventRepository = AuthEventRepositoryImpl()

    @Provides
    @Singleton
    fun provideAuthEventUseCase(authEventRepository: AuthEventRepository): GetAuthEventUseCase =
        GetAuthEventUseCase(authEventRepository)

    @Provides
    @Singleton
    fun provideHistoryBookRepository(historyDataSource: HistoryDataSource): HistoryRepository =
        HistoryRepositoryImpl(historyDataSource)

    @Provides
    @Singleton
    fun provideGetHistoryBooksUseCase(historyRepository: HistoryRepository) =
        GetHistoryBooksUseCase(historyRepository)

    // Member
    @Provides
    @Singleton
    fun provideMemberRepository(memberDataSource: MemberDataSource): MemberRepository =
        MemberRepositoryImpl(memberDataSource)

    @Provides
    @Singleton
    fun provideGetMyInfoUseCase(memberRepository: MemberRepository) =
        GetMyInfoUseCase(memberRepository)

    @Provides
    @Singleton
    fun provideUpdateNicknameUseCase(memberRepository: MemberRepository) =
        UpdateNicknameUseCase(memberRepository)

    @Provides
    @Singleton
    fun provideWithdrawUseCase(memberRepository: MemberRepository) =
        WithdrawUseCase(memberRepository)

    @Provides
    @Singleton
    fun provideCheckNicknameUseCase(memberRepository: MemberRepository) =
        CheckNicknameUseCase(memberRepository)

    // MyBook
    @Provides
    @Singleton
    fun provideMyBookRepository(myBookDataSource: MyBookDataSource): MyBookRepository =
        MyBookRepositoryImpl(myBookDataSource)

    @Provides
    @Singleton
    fun provideGetMyBookDetailUseCase(myBookRepository: MyBookRepository) =
        GetMyBookDetailUseCase(myBookRepository)

    @Provides
    @Singleton
    fun provideDeleteMyBookUseCase(myBookRepository: MyBookRepository) =
        DeleteMyBookUseCase(myBookRepository)

    @Provides
    @Singleton
    fun provideSearchMyBooksUseCase(myBookRepository: MyBookRepository) =
        SearchMyBooksUseCase(myBookRepository)

    @Provides
    @Singleton
    fun provideGetStoreBooksUseCase(myBookRepository: MyBookRepository) =
        GetStoreBooksUseCase(myBookRepository)

    @Provides
    @Singleton
    fun provideUpdateReadingStatusUseCase(myBookRepository: MyBookRepository) =
        UpdateReadingStatusUseCase(myBookRepository)

    @Provides
    @Singleton
    fun provideUpdateMyBookUseCase(myBookRepository: MyBookRepository) =
        UpdateMyBookUseCase(myBookRepository)

}
