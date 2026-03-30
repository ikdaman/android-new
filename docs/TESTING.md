# 테스트 가이드

## 개요

총 **368+개 단위 테스트** + **9개 UI 테스트**로 구성.
domain/data/presentation 계층은 MockK 기반 단위 테스트, UI 계층은 Compose UI 테스트(androidTest)를 사용.

## 테스트 실행

```bash
# 단위 테스트 (domain + data + presentation)
./gradlew :domain:test :data:test :presentation:test

# UI 테스트 (에뮬레이터/실기기 필요)
./gradlew :ui:connectedDebugAndroidTest
```

## 계층별 테스트 현황

| 계층 | 테스트 파일 | 테스트 수 | 도구 |
|------|-----------|----------|------|
| domain | 20개 | ~74개 | JUnit + MockK + Turbine |
| data | 7개 | ~68개 | JUnit + MockK + Turbine |
| presentation | 8개 | ~226개 | JUnit + MockK + Turbine |
| ui (androidTest) | 4개 | 9개 | Compose UI Test |

---

## Domain Layer 테스트

UseCase의 입출력과 Repository 위임을 검증.

### 인증

| 파일 | 대상 | 테스트 항목 |
|------|------|------------|
| `LoginUseCaseTest` | LoginUseCase | Google/Naver/Kakao 로그인 성공/실패, 회원가입 필요 상태, 소셜 타입별 메서드 호출 |
| `LogoutUseCaseTest` | LogoutUseCase | Google/Naver/Kakao 로그아웃 성공/실패, 소셜 타입별 메서드 호출 |
| `GetProviderUseCaseTest` | GetProviderUseCase | 제공자 정보 반환, null 처리 |
| `SignupUseCaseTest` | SignupUseCase | 회원가입 성공/실패, 파라미터 전달 |
| `GetLoginStateUseCaseTest` | GetLoginStateUseCase | 로그인/비로그인 상태, 상태 변화 추적 |
| `GetAuthEventUseCaseTest` | GetAuthEventUseCase | 인증 이벤트 반환, 다중 이벤트 |

### 책 검색

| 파일 | 대상 | 테스트 항목 |
|------|------|------------|
| `SearchBookWithTitleUseCaseTest` | SearchBookWithTitleUseCase | 제목 검색 결과, 기본 페이지, 빈 결과 |
| `SearchBookWithIsbnUseCaseTest` | SearchBookWithIsbnUseCase | ISBN 검색 성공, 미찾음, 파라미터 전달 |
| `SaveManualBookInfoUseCaseTest` | SaveManualBookInfoUseCase | 수동 입력 저장 성공/실패, 최소 정보 저장 |

### 내 책 관리

| 파일 | 대상 | 테스트 항목 |
|------|------|------------|
| `GetStoreBooksUseCaseTest` | GetStoreBooksUseCase | 보관함 조회 성공/실패, 키워드 필터 |
| `GetMyBookDetailUseCaseTest` | GetMyBookDetailUseCase | 상세 조회 성공/실패, mybookId 전달 |
| `DeleteMyBookUseCaseTest` | DeleteMyBookUseCase | 삭제 성공/실패, mybookId 전달 |
| `SearchMyBooksUseCaseTest` | SearchMyBooksUseCase | 검색 성공/실패, 기본 파라미터 |
| `UpdateReadingStatusUseCaseTest` | UpdateReadingStatusUseCase | 읽기 상태 변경 성공/실패, 파라미터 전달 |
| `UpdateMyBookUseCaseTest` | UpdateMyBookUseCase | 책 정보 수정 성공/실패, null 파라미터, 상태/정보 전달 |
| `GetHistoryBooksUseCaseTest` | GetHistoryBooksUseCase | 히스토리 조회 성공/실패, 파라미터 전달 |

### 멤버

| 파일 | 대상 | 테스트 항목 |
|------|------|------------|
| `GetMyInfoUseCaseTest` | GetMyInfoUseCase | 회원 정보 조회 성공/실패 |
| `CheckNicknameUseCaseTest` | CheckNicknameUseCase | 닉네임 사용 가능/중복/오류 |
| `UpdateNicknameUseCaseTest` | UpdateNicknameUseCase | 닉네임 수정 성공/실패 |
| `WithdrawUseCaseTest` | WithdrawUseCase | 회원 탈퇴 성공/실패 |

---

## Data Layer 테스트

Repository 구현체의 DataSource 호출, 데이터 매핑, 에러 처리를 검증.

| 파일 | 대상 | 테스트 항목 |
|------|------|------------|
| `AuthRepositoryImplTest` | AuthRepositoryImpl | Google/Naver/Kakao 로그인 전체 플로우 (소셜 인증→백엔드→토큰 저장), 404 회원가입 필요, 로그아웃, 회원가입, 제공자 조회 |
| `MyBookRepositoryImplTest` | MyBookRepositoryImpl | 책 상세 조회, 삭제, 검색, 보관함 목록, 읽기 상태 변경, 책 정보 수정 (status/bookInfo 포함), 빈 결과 처리 |
| `AladinRepositoryImplTest` | AladinRepositoryImpl | 제목/ISBN 검색 결과 매핑, 페이지 번호 처리, 네트워크 타임아웃 |
| `MemberRepositoryImplTest` | MemberRepositoryImpl | 회원 정보 조회, 닉네임 수정, 회원 탈퇴, 닉네임 중복 확인 |
| `HistoryRepositoryImplTest` | HistoryRepositoryImpl | 히스토리 조회, 빈 목록 처리, 기본 오류 메시지 |
| `AuthEventRepositoryImplTest` | AuthEventRepositoryImpl | DataAuthEvent → DomainAuthEvent 매핑, 다중 이벤트 |
| `UserRepositoryImplTest` | UserRepositoryImpl | 로그인 상태 조회, 다중 값 발행 |

---

## Presentation Layer 테스트

ViewModel의 상태 관리, UseCase 호출, UI 이벤트 발행을 검증.

| 파일 | 대상 | 테스트 항목 |
|------|------|------------|
| `LoginViewModelTest` | LoginViewModel | Google/Naver/Kakao 로그인/로그아웃 성공/실패, 회원가입 필요 상태, 초기 상태 |
| `SearchBookViewModelTest` | SearchBookViewModel | 책 검색 성공/실패/빈결과, 다음 페이지 로드, ISBN 검색, 책 저장 성공/실패, 네트워크 오류 |
| `MainViewModelTest` | MainViewModel | 토큰 검증, 닉네임 설정, 보관함 로드, 읽기 시작, 책 삭제, 스낵바 이벤트 |
| `SignupViewModelTest` | SignupViewModel | 닉네임 확인 가능/중복, 회원가입 전체 플로우, 실패 처리 |
| `BookInfoViewModelTest` | BookInfoViewModel | 상세 조회 성공/실패, 책 정보 수정, 삭제, 잘못된 mybookId |
| `SettingViewModelTest` | SettingViewModel | 소셜별 로그아웃, 닉네임 유효성 검사 (한글/영숫자/길이/특수문자), 닉네임 편집/취소/수정 |
| `HistoryViewModelTest` | HistoryViewModel | 히스토리 로드, 더보기, 마지막 페이지, 보기 유형 전환 |
| `ManualInputViewModelTest` | ManualInputViewModel | 수동 입력 저장 성공/실패, UI 입력→UseCase 변환, 날짜 포맷, 페이지 수 변환 |
| `MyBookSearchViewModelTest` | MyBookSearchViewModel | 검색 성공/빈쿼리, 더보기, 마지막 페이지, 새 검색 초기화 |

---

## UI Layer 테스트 (androidTest)

Compose UI 테스트로 화면 렌더링과 사용자 인터랙션을 검증. 에뮬레이터 또는 실기기 필요.

| 파일 | 대상 | 테스트 항목 |
|------|------|------------|
| `SplashScreenTest` | SplashScreen | 항상 홈으로 네비게이션 |
| `LoginScreenTest` | LoginScreen | 소셜 로그인 버튼 표시, 뒤로가기 버튼 조건부 표시, "로그인 후 저장이 가능합니다" 안내 메시지 |
| `AddBookScreenTest` | AddBookScreen | 책 정보 표시, 비로그인 저장 → onLoginRequired 호출, 로그인 저장 → BottomSheet 표시 |
| `ManualBookInputScreenTest` | ManualBookInputScreen | 입력 필드 표시, 비로그인 저장 → onLoginRequired 호출 |

---

## 테스트 패턴

### 사용 도구
- **JUnit 4**: 테스트 프레임워크
- **MockK**: Kotlin 모킹 라이브러리
- **Turbine**: Flow 테스트 라이브러리
- **Compose UI Test**: Jetpack Compose 화면 테스트
- **kotlinx-coroutines-test**: 코루틴 테스트 지원

### 공통 패턴
```kotlin
// Domain/Data: Flow 기반 테스트
@Test
fun `useCase returns success flow`() = runTest {
    coEvery { repository.method() } returns flowOf(DataResource.Success(data))
    useCase().test {
        // Turbine으로 Flow 검증
    }
}

// Presentation: ViewModel 상태 검증
@Test
fun `action updates state correctly`() = runTest {
    viewModel.action()
    assertEquals(expected, viewModel.state.value)
}

// UI: Compose 화면 검증
@Test
fun screen_displaysElement() {
    composeTestRule.setContent { Screen() }
    composeTestRule.onNodeWithText("텍스트").assertIsDisplayed()
}
```

## 테스트 커버리지 미포함 영역

- **UI 네비게이션 통합 테스트**: 화면 간 이동 (NavHost 통합)
- **local 모듈**: DataStore, Room 등 로컬 저장소
- **remote 모듈**: Retrofit API 서비스, 인터셉터
- **app 모듈**: Application, DI 모듈
