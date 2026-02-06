# 프로젝트 개선 구현 계획서

## 목차

1. [테스트 코드 작성](#1-테스트-코드-작성)
2. [에러 핸들링 개선](#2-에러-핸들링-개선)
3. [ProGuard/R8 릴리즈 빌드 최적화](#3-proguardr8-릴리즈-빌드-최적화)
4. [검색 결과 페이지네이션](#4-검색-결과-페이지네이션)

---

## 1. 테스트 코드 작성

### 1.1 현재 상태

- 모든 테스트 파일이 Android Studio 기본 템플릿 (`2 + 2 = 4`) 상태
- **실제 테스트 0개**
- 테스트 의존성 부족: `coroutines-test`, `mockk`, `turbine` 등 미설치
- `:domain`, `:data` 모듈에는 테스트 의존성 자체가 없음

### 1.2 사전 작업: 테스트 의존성 추가

#### `gradle/libs.versions.toml`에 추가할 의존성

```toml
[versions]
coroutines-test = "1.10.2"
mockk = "1.13.16"
turbine = "1.2.0"

[libraries]
kotlinx-coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "coroutines-test" }
mockk = { group = "io.mockk", name = "mockk", version.ref = "mockk" }
turbine = { group = "app.cash.turbine", name = "turbine", version.ref = "turbine" }
```

#### 모듈별 `build.gradle.kts`에 추가

| 모듈 | 추가할 의존성 |
|------|-------------|
| `:domain` | `junit`, `kotlinx-coroutines-test`, `mockk` |
| `:data` | `junit`, `kotlinx-coroutines-test`, `mockk` |
| `:presentation` | `kotlinx-coroutines-test`, `mockk`, `turbine` |

### 1.3 테스트 대상 및 우선순위

#### Phase 1: UseCase 단위 테스트 (`:domain`)

UseCase는 로직이 단순하므로 가장 먼저 테스트를 작성합니다.

| UseCase | 테스트 케이스 |
|---------|-------------|
| `SearchBookWithTitleUseCase` | 정상 검색 결과 반환, 빈 결과, Repository 예외 발생 |
| `SearchBookWithIsbnUseCase` | 정상 조회, 존재하지 않는 ISBN, Repository 예외 |
| `SaveManualBookInfoUseCase` | 정상 저장 (Loading→Success), 저장 실패 (Loading→Error) |
| `GetHistoryBooksUseCase` | 정상 조회, 빈 히스토리, 페이지네이션 파라미터 전달 확인 |
| `GetLoginStateUseCase` | 로그인 상태 true/false |
| `GetAuthEventUseCase` | LOGIN_REQUIRED 이벤트 수신 |

#### Phase 2: Repository 단위 테스트 (`:data`)

DataSource를 Mock하여 Repository 로직을 검증합니다.

| Repository | 테스트 케이스 |
|-----------|-------------|
| `AladinRepositoryImpl` | 정상 검색→Domain 매핑 확인, **예외 발생 시 빈 결과 반환 확인** (현재 버그 검증), ISBN 검색 매핑 |
| `BackendRepositoryImpl` | 저장 성공 (code 201)→`Success(true)`, 저장 실패→`Success(false)`, 예외→`Error` emit |
| `HistoryRepositoryImpl` | 정상 조회→Domain 매핑, 빈 결과, 예외→Error emit |
| `AuthEventRepositoryImpl` | DataAuthEvent→DomainAuthEvent 매핑 |

#### Phase 3: ViewModel 단위 테스트 (`:presentation`)

UseCase를 Mock하고 StateFlow/SharedFlow 방출을 Turbine으로 검증합니다.

| ViewModel | 테스트 케이스 |
|-----------|-------------|
| `SearchBookViewModel` | 검색 성공→Success 상태, 빈 결과→Error 상태, 예외→Error 상태, ISBN 검색, 책 선택/저장, 상태 초기화 |
| `ManualInputViewModel` | 저장 성공→`saveState=true`, 저장 실패→`saveState=false`, UI 파라미터→Domain 매핑 확인 |
| `HistoryViewModel` | 첫 페이지 조회, 추가 로드 (isLoadMore), 뷰 타입 토글, 에러 상태 |
| `LoginViewModel` | 각 소셜 로그인 성공/실패, 로그아웃 성공/실패 |
| `MainViewModel` | 로그인 상태 반영, Snackbar 이벤트 발행 |

### 1.4 테스트 파일 구조

```
domain/src/test/java/project/side/domain/usecase/
├── SearchBookWithTitleUseCaseTest.kt
├── SearchBookWithIsbnUseCaseTest.kt
├── SaveManualBookInfoUseCaseTest.kt
├── GetHistoryBooksUseCaseTest.kt
├── GetLoginStateUseCaseTest.kt
└── GetAuthEventUseCaseTest.kt

data/src/test/java/project/side/data/repository/
├── AladinRepositoryImplTest.kt
├── BackendRepositoryImplTest.kt
├── HistoryRepositoryImplTest.kt
└── AuthEventRepositoryImplTest.kt

presentation/src/test/java/project/side/presentation/viewmodel/
├── SearchBookViewModelTest.kt
├── ManualInputViewModelTest.kt
├── HistoryViewModelTest.kt
├── LoginViewModelTest.kt
└── MainViewModelTest.kt
```

### 1.5 작업 단계

| 단계 | 작업 | 예상 범위 |
|------|------|---------|
| 1 | `libs.versions.toml` + 각 모듈 `build.gradle.kts`에 테스트 의존성 추가 | 5개 파일 수정 |
| 2 | Phase 1: UseCase 테스트 작성 (6개 파일) | 6개 파일 생성 |
| 3 | Phase 2: Repository 테스트 작성 (4개 파일) | 4개 파일 생성 |
| 4 | Phase 3: ViewModel 테스트 작성 (5개 파일) | 5개 파일 생성 |
| 5 | 전체 테스트 실행 및 커버리지 확인 | - |

---

## 2. 에러 핸들링 개선

### 2.1 현재 상태 및 문제점

| # | 문제 | 위치 | 심각도 |
|---|------|------|--------|
| 1 | 예외를 삼키고 빈 결과 반환 | `AladinRepositoryImpl` | **Critical** |
| 2 | 네트워크 오류와 빈 결과를 구분 불가 | `SearchBookViewModel` | High |
| 3 | 에러 상태가 UI에 표시되지 않음 | `HistoryScreen` | High |
| 4 | 로그아웃 에러 무시 (`else -> {}`) | `LoginViewModel` | Medium |
| 5 | 에러를 로그만 찍고 UI에 반영 안 함 | `TestViewModel` | Medium |
| 6 | `saveState`가 `Boolean?` + `delay(200)` 패턴 (레이스 컨디션) | `ManualInputViewModel`, `SearchBookViewModel` | Medium |
| 7 | OkHttpClient에 타임아웃 미설정 | `RemoteModule` | Medium |
| 8 | 알라딘 API Retrofit에 OkHttpClient 미주입 | `RemoteModule` | Low |
| 9 | Result 래퍼가 3종류로 불일치 | `DataResource`, `DomainResult`, `DataApiResult` | Low |
| 10 | `println` 등 비일관적 로깅 | 여러 파일 | Low |

### 2.2 개선 계획

#### Phase 1: Repository 에러 전파 수정

**`AladinRepositoryImpl`** — 에러를 삼키는 대신 전파하도록 수정:

```kotlin
// Before (문제)
catch (e: Exception) {
    println("searchBookWithTitle: $e")
    return BookSearchResult()  // 에러가 사라짐
}

// After (개선)
// UseCase/ViewModel에서 처리할 수 있도록 예외를 그대로 throw하거나,
// DataResource/Result 패턴으로 감싸서 반환
```

**방안 A**: Repository가 `Flow<DataResource<BookSearchResult>>`를 반환하도록 변경 (BackendRepositoryImpl과 동일 패턴)
- 장점: 프로젝트 내 일관성
- 단점: UseCase, ViewModel 시그니처 변경 필요

**방안 B**: 예외를 그대로 throw하고 ViewModel의 catch에서 처리
- 장점: 변경 범위 최소화
- 단점: Repository마다 에러 처리 패턴 불일치

**추천: 방안 A** — 전체 일관성을 위해 Flow + DataResource 패턴 통일

#### Phase 2: ViewModel 에러 상태 개선

**`SearchBookViewModel`** — 에러 종류 구분:

```kotlin
// 네트워크 에러와 빈 결과를 구분하여 다른 메시지 표시
is DataResource.Error -> DomainResult.Error("네트워크 오류가 발생했습니다.")
is DataResource.Success -> {
    if (result.books.isEmpty()) DomainResult.Error("검색 결과가 없습니다.")
    else DomainResult.Success(result.books)
}
```

**`LoginViewModel`** — 로그아웃 에러 처리 추가:

```kotlin
// Before
else -> {}
// After
is LogoutState.Error -> { /* Snackbar 또는 UI 상태 업데이트 */ }
```

**`saveState` 패턴 개선** — `Boolean?` + `delay` 대신 `SharedFlow` 이벤트 사용:

```kotlin
// Before (레이스 컨디션 위험)
_saveState.value = true
delay(200)
_saveState.value = null

// After (안전한 일회성 이벤트)
private val _saveEvent = MutableSharedFlow<SaveEvent>()
val saveEvent = _saveEvent.asSharedFlow()

sealed class SaveEvent {
    data object Success : SaveEvent()
    data class Error(val message: String) : SaveEvent()
}
```

#### Phase 3: UI 에러 표시 추가

**`HistoryScreen`** — 에러 메시지 표시 UI 추가:

```kotlin
// errorMessage가 있을 때 에러 UI 표시
if (uiState.errorMessage != null) {
    // 재시도 버튼 포함한 에러 화면
}
```

**공통 에러 UI 컴포넌트 생성** (`ui/component/ErrorView.kt`):
- 에러 메시지 텍스트
- 재시도 버튼 (선택적)
- 네트워크 오류 / 서버 오류 / 일반 오류 구분 아이콘

#### Phase 4: 네트워크 설정 강화

**OkHttpClient 타임아웃 설정** (`RemoteModule`):

```kotlin
OkHttpClient.Builder()
    .connectTimeout(15, TimeUnit.SECONDS)
    .readTimeout(15, TimeUnit.SECONDS)
    .writeTimeout(15, TimeUnit.SECONDS)
```

**알라딘 API Retrofit에 OkHttpClient 주입** — 로깅 + 타임아웃 적용

### 2.3 작업 단계

| 단계 | 작업 | 수정 파일 |
|------|------|---------|
| 1 | `AladinRepositoryImpl` 에러 전파 수정 + UseCase/ViewModel 시그니처 조정 | 4~5개 파일 |
| 2 | `SearchBookViewModel` 에러 메시지 분기 | 1개 파일 |
| 3 | `LoginViewModel` 로그아웃 에러 처리 | 1개 파일 |
| 4 | `saveState` 패턴을 `SharedFlow` 이벤트로 변경 | 4개 파일 (VM 2 + Screen 2) |
| 5 | `HistoryScreen` 에러 UI 추가 | 1개 파일 |
| 6 | 공통 에러 UI 컴포넌트 (`ErrorView`) 생성 | 1개 파일 생성 |
| 7 | OkHttpClient 타임아웃 + 알라딘 Retrofit에 클라이언트 주입 | 1개 파일 |
| 8 | `println` → `Log` 통일, 로깅 태그 정리 | 3~4개 파일 |

---

## 3. ProGuard/R8 릴리즈 빌드 최적화

### 3.1 현재 상태

- 모든 모듈에서 `isMinifyEnabled = false`
- `app/proguard-rules.pro`에만 일부 규칙 존재 (Kakao SDK, Retrofit, OkHttp, Coroutines)
- 나머지 모듈의 `proguard-rules.pro`와 `consumer-rules.pro`는 비어 있음
- `isShrinkResources` 미설정

### 3.2 누락된 ProGuard 규칙 목록

| 라이브러리 | 상태 | 필요한 조치 |
|-----------|------|-----------|
| Retrofit 3.0.0 | 부분 존재 | 확인 및 보완 |
| Moshi 1.15.2 | **누락** | 모델 클래스 keep 규칙 추가 |
| Gson 2.13.2 | **누락** | `@SerializedName` keep 규칙 추가 |
| Naver SDK 5.10.0 | **누락** | SDK 공식 ProGuard 규칙 추가 |
| Google Credentials 1.5.0 | **누락** | keep 규칙 추가 |
| Google Identity 1.1.1 | **누락** | keep 규칙 추가 |
| ML Kit Barcode 17.2.0 | **누락** | 모델 클래스 keep 규칙 추가 |
| OkHttp 5.3.2 | 부분 존재 (dontwarn만) | 보완 |
| Coroutines 1.10.2 | 부분 존재 | 확인 |

### 3.3 보존해야 할 데이터 모델 클래스

JSON 역직렬화에 사용되므로 반드시 keep 규칙이 필요합니다:

**`:data` 모듈**:
- `project.side.data.model.BookSearchResponse`, `BookSearchItem`, `BookSubInfoResponse`
- `project.side.data.model.DataManualBookInfo` (`@SerializedName` 사용)
- `project.side.data.model.SaveResultEntity`
- `project.side.data.model.HistoryBookEntity`, `HistoryBookInfoEntity`
- `project.side.data.model.SocialLoginResult`, `LoginResult`, `NicknameEntity`

**`:remote` 모듈**:
- `project.side.remote.model.HistoryBookResponse`, `HistoryBook`, `HistoryBookInfo`, `HistoryInfo`
- `project.side.remote.model.SaveManualBookResponse`
- `project.side.remote.model.NicknameResponse`
- `project.side.remote.model.login.LoginResponse`

### 3.4 Moshi Codegen 마이그레이션 고려

현재 Moshi를 **Reflection 기반** (`KotlinJsonAdapterFactory`)으로 사용 중입니다.

| | Reflection (현재) | Codegen (추천) |
|---|---|---|
| 동작 방식 | 런타임 리플렉션 | 컴파일 타임 코드 생성 |
| 성능 | 느림 | 빠름 |
| ProGuard | 모델 클래스 전체 keep 필요 | `@JsonClass(generateAdapter = true)`만 추가 |
| 의존성 | `moshi-kotlin` | `moshi-kotlin-codegen` + KSP |

**추천**: Codegen으로 마이그레이션하면 ProGuard 규칙이 단순해지고 성능도 향상됩니다.

### 3.5 작업 단계

| 단계 | 작업 | 수정 파일 |
|------|------|---------|
| 1 | (선택) Moshi Reflection → Codegen 마이그레이션 | `libs.versions.toml`, 모델 클래스 전체 |
| 2 | `:remote` 모듈 `consumer-rules.pro`에 Naver SDK, Moshi, OkHttp 규칙 추가 | 1개 파일 |
| 3 | `:data` 모듈에 Gson `@SerializedName` keep 규칙 추가 (또는 Moshi `@Json`으로 통일) | 1~2개 파일 |
| 4 | `app/proguard-rules.pro` 보완: Google Credentials, ML Kit, 누락 항목 추가 | 1개 파일 |
| 5 | `app/build.gradle.kts`에서 `isMinifyEnabled = true`, `isShrinkResources = true` 활성화 | 1개 파일 |
| 6 | 릴리즈 빌드 후 테스트 — 크래시, JSON 파싱, 소셜 로그인 동작 검증 | - |
| 7 | 문제 발생 시 ProGuard 매핑 파일로 디버깅 및 규칙 보완 | - |

### 3.6 주의사항

- `@SerializedName` (Gson)과 `@Json` (Moshi) 혼용 문제: 현재 `DataManualBookInfo`에서 Gson의 `@SerializedName`을 쓰지만, Retrofit은 Moshi를 사용하므로 이 어노테이션이 **무시**됩니다. Moshi `@Json(name = ...)`으로 교체 필요.
- 라이브러리 모듈의 규칙은 `consumer-rules.pro`에 작성해야 앱 빌드 시 자동 적용됩니다.
- minify 활성화 후 반드시 릴리즈 APK로 전체 기능 테스트 필요.

---

## 4. 검색 결과 페이지네이션

### 4.1 현재 상태

- 알라딘 API에 `start` (페이지), `maxResults` (페이지당 50건) 파라미터 이미 정의됨
- 응답의 `totalResults` 필드가 Domain 모델 `BookSearchResult.totalBookCount`에 매핑되지만 **사용되지 않음**
- `SearchBookViewModel`에서 항상 페이지 `0`을 하드코딩 (알라딘 API는 **1-indexed**)
- UI가 `Column` + `forEach`를 사용 — `LazyColumn`이 아니어서 스크롤 감지 불가
- 기존 `HistoryViewModel`에 페이지네이션 패턴이 이미 구현되어 있어 참고 가능

### 4.2 버그 수정

`SearchBookWithTitleUseCase`의 기본값 `startPage = 0`은 알라딘 API의 1-indexed 페이지네이션과 불일치합니다. `startPage = 1`로 수정 필요.

### 4.3 구현 계획

#### 4.3.1 Presentation 계층 변경

**`SearchBookViewModel` 상태 모델 추가**:

```kotlin
data class SearchBookState(
    val query: String = "",
    val books: List<BookItem> = emptyList(),
    val currentPage: Int = 1,
    val totalBookCount: Int = 0,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = false,
    val errorMessage: String? = null
)
```

**`SearchBookViewModel` 메서드 추가**:

```kotlin
// 새 검색 (기존 결과 초기화)
fun searchBook(title: String)

// 다음 페이지 로드 (기존 결과에 추가)
fun loadNextPage()
```

`hasMore` 계산: `currentPage * 50 < totalBookCount`

#### 4.3.2 Domain 계층

- `SearchBookWithTitleUseCase`의 `startPage` 기본값을 `1`로 수정
- `BookSearchResult`는 이미 `totalBookCount`를 포함하므로 변경 불필요

#### 4.3.3 UI 계층 변경

**`SearchBookScreen` 수정사항**:

1. `Column` + `forEach` → `LazyColumn` + `items`로 변경
2. 스크롤 끝 감지 로직 추가:
   ```kotlin
   // LazyListState로 마지막 아이템 도달 감지
   val listState = rememberLazyListState()
   val shouldLoadMore = remember {
       derivedStateOf {
           val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
           lastVisibleItem?.index == listState.layoutInfo.totalItemsCount - 1
       }
   }
   LaunchedEffect(shouldLoadMore.value) {
       if (shouldLoadMore.value && hasMore && !isLoadingMore) {
           viewModel.loadNextPage()
       }
   }
   ```
3. 하단 로딩 인디케이터 추가 (추가 페이지 로드 중)
4. 전체 검색 결과 수 표시 (예: "총 120건")

#### 4.3.4 참고: HistoryViewModel 패턴

`HistoryViewModel`에 이미 유사한 패턴이 구현되어 있습니다:

```kotlin
// isLoadMore = true일 때 기존 목록에 추가
books = if (isLoadMore) _uiState.value.books + it.data.books else it.data.books
```

이 패턴을 `SearchBookViewModel`에도 동일하게 적용합니다.

### 4.4 작업 단계

| 단계 | 작업 | 수정 파일 |
|------|------|---------|
| 1 | `SearchBookWithTitleUseCase` startPage 기본값 0→1 수정 | 1개 파일 |
| 2 | `SearchBookState` 데이터 클래스 생성 | 1개 파일 생성 |
| 3 | `SearchBookViewModel` 페이지네이션 로직 구현 (`searchBook`, `loadNextPage`) | 1개 파일 |
| 4 | `SearchBookScreen` LazyColumn + 무한 스크롤 + 로딩 인디케이터 적용 | 1개 파일 |
| 5 | 전체 검색 결과 수 표시 UI 추가 | 위 파일에 포함 |
| 6 | 검색 → 스크롤 → 추가 로드 → 새 검색 시나리오 테스트 | - |

---

## 전체 작업 순서 (추천)

의존성과 리스크를 고려한 추천 순서입니다:

| 순서 | 작업 | 이유 |
|------|------|------|
| **1** | 에러 핸들링 개선 | 다른 작업의 기반이 되는 안정성 개선. 테스트 작성 전에 에러 흐름을 바로잡아야 올바른 테스트 케이스 설계 가능 |
| **2** | 검색 페이지네이션 | 에러 핸들링 개선과 함께 SearchBookViewModel 리팩토링 가능 |
| **3** | 테스트 코드 작성 | 1, 2번 작업으로 안정화된 코드에 대해 테스트 작성 |
| **4** | ProGuard/R8 최적화 | 기능 구현이 안정된 후 마지막에 적용. 릴리즈 빌드 검증 필요 |

### 브랜치 전략

```
master
├── feature/error-handling      ← 1번 작업
├── feature/search-pagination   ← 2번 작업
├── feature/test-code           ← 3번 작업
└── feature/proguard-setup      ← 4번 작업
```

각 작업은 독립 브랜치에서 진행하고, PR을 통해 master에 병합합니다.
