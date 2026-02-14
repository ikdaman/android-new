# "읽다만" (ikdaman) Android 프로젝트 종합 분석

## 목차

1. [프로젝트 개요](#1-프로젝트-개요)
2. [모듈 구성](#2-모듈-구성)
3. [빌드 설정](#3-빌드-설정)
4. [아키텍처](#4-아키텍처)
5. [의존성 주입 (DI)](#5-의존성-주입-di)
6. [기술 스택 및 라이브러리](#6-기술-스택-및-라이브러리)
7. [네비게이션](#7-네비게이션)
8. [주요 기능](#8-주요-기능)
9. [데이터 흐름](#9-데이터-흐름)
10. [인증 체계](#10-인증-체계)
11. [모듈 의존성 그래프](#11-모듈-의존성-그래프)
12. [API 엔드포인트](#12-api-엔드포인트)

---

## 1. 프로젝트 개요

**"읽다만"** 은 "읽다 만" 즉, 다 읽지 않은 책을 관리하는 독서 관리 앱입니다.

소셜 로그인(Google, Naver, Kakao)을 통해 인증하고, 알라딘 API 연동 책 검색, 바코드 스캔, 직접 입력 등 다양한 방식으로 책을 등록하며, 독서 히스토리를 관리할 수 있습니다.

---

## 2. 모듈 구성

프로젝트는 **7개의 Gradle 모듈**로 구성되어 있습니다.

| 모듈 | 유형 | 역할 |
|------|------|------|
| `:app` | `com.android.application` | 앱 진입점. Application 클래스, Hilt DI 모듈(AppModule, ActivityModule) 정의. 모든 모듈을 통합 |
| `:ui` | `com.android.library` | Jetpack Compose UI 계층. Screen, Component, Theme, Navigation, MainActivity 포함 |
| `:presentation` | `com.android.library` | ViewModel, UI State 모델, SnackbarManager 등 표현 계층 로직 |
| `:domain` | `java-library` (순수 Kotlin) | 비즈니스 로직 계층. UseCase, Repository 인터페이스, Domain 모델. Android 의존성 없음 |
| `:data` | `java-library` (순수 Kotlin) | 데이터 계층. Repository 구현체, DataSource 인터페이스, Data 모델, Domain-Data 매핑 |
| `:remote` | `com.android.library` | 원격 데이터 소스. Retrofit API Service, 소셜 로그인, Auth Interceptor/Authenticator, DI 모듈 |
| `:local` | `com.android.library` | 로컬 데이터 소스. DataStore를 이용한 토큰/인증 정보 영구 저장 |

---

## 3. 빌드 설정

### 공통 설정

| 항목 | 값 |
|------|----|
| compileSdk | 36 |
| minSdk | 29 (Android 10) |
| targetSdk | 36 |
| Java | 11 |
| Kotlin JVM Target | JVM 11 |
| Kotlin 버전 | 2.2.21 |
| AGP 버전 | 8.13.0 |
| KSP 버전 | 2.2.21-2.0.4 |

### 주요 플러그인

- `com.android.application` / `com.android.library`
- `org.jetbrains.kotlin.android` / `org.jetbrains.kotlin.jvm`
- `org.jetbrains.kotlin.plugin.compose`
- `com.google.dagger.hilt.android`
- `com.google.devtools.ksp`

### 서명 및 환경 설정

- `key.properties` 파일에서 키스토어 정보 및 API 키 로드
- 관리되는 키: `KAKAO_APP_KEY`, `NAVER_CLIENT_ID/SECRET`, `BASE_URL`, `GOOGLE_CLIENT_ID`, `TTB_KEY` (알라딘 API)
- ProGuard minify: 모든 모듈에서 비활성 상태

---

## 4. 아키텍처

### Clean Architecture + MVVM

프로젝트는 **Clean Architecture**의 계층 분리를 따르며, 표현 계층에서 **MVVM** 패턴을 사용합니다.

```
┌─────────────────────────────────────────────────────────┐
│  :app  (조립 계층 — DI 모듈, Application)                │
├─────────────────────────────────────────────────────────┤
│  :ui  (Jetpack Compose Screen, Navigation, Theme)       │
│           ↓ depends on                                   │
│  :presentation  (ViewModel, UI State)                    │
│           ↓ depends on                                   │
│  :domain  (UseCase, Repository Interface, Domain Model)  │
│           ↑ implements                                   │
│  :data  (Repository Impl, DataSource Interface)          │
│           ↑ implements                                   │
│  :remote  (Retrofit, Social Auth)  :local  (DataStore)   │
└─────────────────────────────────────────────────────────┘
```

### 의존성 역전 원칙 (DIP) 준수

- Repository 인터페이스 → `:domain`에 정의
- Repository 구현체 → `:data`에 위치
- DataSource 인터페이스 → `:data`에 정의
- DataSource 구현체 → `:remote`와 `:local`에 위치

`:domain` 모듈은 어떤 모듈에도 의존하지 않는 순수 Kotlin 모듈입니다.

---

## 5. 의존성 주입 (DI)

### 프레임워크: Dagger Hilt (v2.57.1)

### DI 모듈 구조

| 모듈 | 위치 | 스코프 | 역할 |
|------|------|--------|------|
| `AppModule` | `:app` | `SingletonComponent` | Repository 바인딩, UseCase 프로비전 |
| `ActivityModule` | `:app` | `ActivityComponent` | AuthRepository, LoginUseCase, LogoutUseCase (소셜 로그인에 Context 필요) |
| `RemoteModule` | `:remote` | `SingletonComponent` | OkHttpClient 2종, Retrofit 2종, API Service, 원격 DataSource |
| `SocialAuthModule` | `:remote` | `ActivityComponent` | SocialAuthDataSource (Activity Context 필요) |
| `LocalModule` | `:local` | `SingletonComponent` | DataStore, AuthDataStoreSource |

### Qualifier 어노테이션

| 어노테이션 | 용도 |
|-----------|------|
| `@DefaultOkHttpClient` | 인증 불필요 API용 클라이언트 |
| `@AuthOkHttpClient` | 인증 토큰 자동 첨부 클라이언트 |
| `@DefaultRetrofit` | 인증 불필요 API용 Retrofit |
| `@AuthRetrofit` | 인증 필요 API용 Retrofit |

### ViewModel 주입

모든 ViewModel은 `@HiltViewModel` + `@Inject constructor`를 사용하고, UI에서 `hiltViewModel()`로 주입합니다.

---

## 6. 기술 스택 및 라이브러리

### UI

| 라이브러리 | 버전 | 용도 |
|-----------|------|------|
| Jetpack Compose BOM | 2025.12.01 | Compose UI 프레임워크 |
| Material3 | BOM 관리 | UI 컴포넌트 |
| Material Icons Extended | 1.6.6 | 아이콘 |
| Compose Navigation | 2.8.5 | 화면 네비게이션 |
| Activity Compose | 1.12.1 | Compose Activity 통합 |
| Core Splashscreen | 1.2.0 | 스플래시 화면 |
| Coil Compose | 2.7.0 | 이미지 로딩 (책 표지) |
| CameraX | 1.5.3 | 바코드 스캔용 카메라 |
| ML Kit Barcode | 17.2.0 | ISBN 바코드 인식 |

### 네트워킹

| 라이브러리 | 버전 | 용도 |
|-----------|------|------|
| Retrofit | 3.0.0 | HTTP 클라이언트 |
| OkHttp Logging Interceptor | 5.3.2 | HTTP 로깅 |
| Moshi + Moshi Kotlin | 1.15.2 | JSON 직렬화/역직렬화 |
| Gson | 2.13.2 | `@SerializedName` 어노테이션용 |

### DI

| 라이브러리 | 버전 | 용도 |
|-----------|------|------|
| Dagger Hilt | 2.57.1 | 의존성 주입 |
| Hilt Navigation Compose | 1.2.0 | Compose에서 hiltViewModel() |
| javax.inject | 1 | @Inject 어노테이션 (순수 Kotlin 모듈용) |

### 소셜 로그인

| 라이브러리 | 버전 | 용도 |
|-----------|------|------|
| Kakao SDK v2-user | 2.23.1 | 카카오 로그인 |
| Naver OAuth | 5.10.0 | 네이버 로그인 |
| Credentials / Play-Services-Auth | 1.5.0 | Google 로그인 |
| GoogleID | 1.1.1 | Google ID 토큰 |

### 로컬 저장소

| 라이브러리 | 버전 | 용도 |
|-----------|------|------|
| DataStore Preferences | 1.1.4 | 토큰/인증 정보 저장 |

### Architecture / Lifecycle

| 라이브러리 | 버전 | 용도 |
|-----------|------|------|
| Lifecycle Runtime KTX | 2.10.0 | 생명주기 관리 |
| Lifecycle ViewModel KTX | 2.10.0 | ViewModel |
| Kotlinx Coroutines Core | 1.10.2 | 비동기 처리 |

### 로깅

| 라이브러리 | 버전 | 용도 |
|-----------|------|------|
| Timber | 5.0.1 | 구조화된 로깅 |

### 테스트

| 라이브러리 | 버전 | 용도 |
|-----------|------|------|
| JUnit | 4.13.2 | 단위 테스트 |
| MockK | 1.13.17 | Kotlin 모킹 프레임워크 |
| Kotlinx Coroutines Test | 1.10.2 | 코루틴 테스트 유틸리티 |
| AndroidX JUnit | 1.3.0 | 계측 테스트 |
| Espresso Core | 3.7.0 | UI 테스트 |
| Compose UI Test JUnit4 | BOM 관리 | Compose 테스트 |

---

## 7. 네비게이션

### Jetpack Compose Navigation (v2.8.5)

이중 NavHost 구조를 사용합니다.

### 라우트 정의

| 라우트 | 상수명 | 소속 NavHost |
|--------|--------|-------------|
| `"Main"` | `MAIN_ROUTE` | App-Level |
| `"Login"` | `LOGIN_ROUTE` | App-Level |
| `"AddBook"` | `ADD_BOOK_ROUTE` | App-Level |
| `"ManualBookInput"` | `MANUAL_BOOK_INPUT_ROUTE` | App-Level |
| `"Barcode"` | `BARCODE_ROUTE` | App-Level |
| `"Home"` | `HOME_ROUTE` | Main-Level |
| `"SearchBook"` | `SEARCH_BOOK_ROUTE` | Main-Level |
| `"History"` | `HISTORY_ROUTE` | Main-Level |
| `"Setting"` | `SETTING_ROUTE` | Main-Level |
| `"SearchMyBook"` | `SEARCH_MY_BOOK_ROUTE` | Main-Level |
| `"BookInfo"` | `BOOK_INFO_ROUTE` | Main-Level |

### 네비게이션 구조

```
App NavHost (MainActivity)
├── Main (MainScreen — BottomNavBar)
│   ├── Home (내 서점)         ← 하단 네비게이션
│   ├── SearchBook (책 추가)   ← 하단 네비게이션
│   ├── History (히스토리)     ← 하단 네비게이션 (로그인 필요)
│   ├── SearchMyBook (내 책 검색)
│   ├── Setting (설정)
│   └── BookInfo (책 상세)
├── Barcode (바코드 스캔)
├── Login (소셜 로그인)
├── AddBook (책 추가/상세)
└── ManualBookInput (직접 입력)
```

### 하단 네비게이션 바

- **3개 탭**: "내 서점" (Home), "책 추가" (SearchBook), "히스토리" (History)
- History 탭은 로그인 필요 (`navigateIfLoggedIn`)
- Setting, BookInfo 화면에서는 하단 바 숨김

### 인증 기반 네비게이션

- `navigateIfLoggedIn()`: 로그인 상태 확인 후 미로그인 시 Login 화면으로 리다이렉트
- 서버 401 + 토큰 갱신 실패 시 `AuthEvent`를 통해 Login 화면으로 강제 이동

---

## 8. 주요 기능

### 8-1. 소셜 로그인/로그아웃

- **3가지 소셜 로그인**: Google, Naver, Kakao
- 소셜 인증 → 백엔드 `/auth/login` API로 accessToken/provider/providerId 전송
- 서버에서 Authorization(JWT) + Refresh Token 발급 → DataStore에 저장
- 로그아웃: 백엔드 `/auth/logout` → 소셜 로그아웃 → DataStore 클리어

### 8-2. 책 검색 (알라딘 API)

- **제목 검색**: 알라딘 `ItemSearch.aspx` API
- **ISBN 검색**: 알라딘 `ItemLookUp.aspx` API
- 검색 결과: 제목, 작가, 표지, ISBN, 설명, 출간일, 페이지 수

### 8-3. 바코드 스캔

- CameraX + ML Kit Barcode Scanning
- ISBN 바코드 인식 → 알라딘 API에서 자동 조회
- 스캔 성공 시 AddBook 화면으로 자동 이동

### 8-4. 책 등록

- **검색 후 저장**: 알라딘 검색 결과에서 선택한 책 저장
- **직접 입력 저장**: 제목(필수), 작가(필수), 출판사, 출간일, ISBN, 페이지 수 직접 입력
- **등록 옵션 (BookRegisterBottomSheet)**:
  - "내 서점" 탭: 읽고 싶은 이유 입력 (최대 500자)
  - "히스토리" 탭: 독서 시작일/종료일 선택
- 백엔드 `POST /mybooks` API로 저장
- 저장 성공/실패 시 Snackbar 알림

### 8-5. 독서 히스토리

- 백엔드 `GET /mybooks/history` API (페이지네이션, 정렬)
- **2가지 뷰 타입**: 리스트 뷰 (시작일/종료일/제목 테이블), 데이터셋 뷰 (3열 그리드 표지)
- 뷰 타입 토글 기능

### 8-6. 내 책 검색

- 홈 화면(내 서점)의 검색 아이콘 클릭 시 내 책 검색 화면으로 이동
- 백엔드 `GET /mybooks?query=` API (페이지네이션)
- 검색 결과에서 readingStatus 기반으로 `[내 서점]`/`[히스토리]` 태그 표시
- 무한 스크롤 페이지네이션 (페이지 크기 10)
- 책 클릭 시 BookInfoScreen으로 이동

### 8-7. 책 상세 / 설정

- BookInfoScreen: 나의 책 상세 정보 조회, 삭제, 독서 상태 변경, 알라딘에서 보기
- SettingScreen: 닉네임 변경, 로그아웃, 회원 탈퇴

---

## 9. 데이터 흐름

### 일반적 데이터 흐름 (책 저장 예시)

```
[UI Layer]                     [Presentation]               [Domain]                     [Data]                    [Remote]
ManualBookInputScreen  --->  ManualInputViewModel  --->  SaveManualBookInfoUseCase  --->  BackendRepositoryImpl  --->  BackendDataSourceImpl
     (Compose)                .saveManualBookInfoFromUi()   .invoke(ManualBookInfo)       .saveManualBookInfo()       .saveManualBookInfo()
        |                          |                             |                             |                         |
        |  collectAsState()        |  viewModelScope.launch      |  Flow<DataResource>          |  flow { emit(loading)   |  backendApiService
        |  ← saveState             |  ← collect result           |                             |    → call datasource    |    .saveManualBookInfo()
        |                          |                             |                             |    → emit(success/err)  |    POST /mybooks
        |  Snackbar 표시            |                             |                             |  }.catch {}             |    Retrofit → Server
```

### 데이터 모델 매핑 체인

```
Domain Model              Data Model                 Remote Response
──────────────           ─────────────             ─────────────────
ManualBookInfo     ←→   DataManualBookInfo     →   (Request Body → Backend)
BookSearchResult   ←    BookSearchResponse     ←   Aladin API Response
HistoryBook        ←    HistoryBookEntity      ←   HistoryBookResponse
SaveResult         ←    SaveResultEntity       ←   SaveManualBookResponse
LoginState         ←    SocialLoginResult      ←   Social SDK + AuthService
```

### 상태 관리 패턴

| 패턴 | 용도 |
|------|------|
| `StateFlow` + `MutableStateFlow` | 모든 ViewModel에서 UI 상태 노출 |
| `SharedFlow` | 일회성 이벤트 (Snackbar, Auth 이벤트) |
| `DataResource` sealed class | Loading / Success / Error 3상태 |
| `DomainResult` sealed class | Init / Loading / Success / Error 4상태 |

---

## 10. 인증 체계

### 토큰 저장

DataStore Preferences (`auth_pref`)에 다음 키를 저장합니다:
- `provider` — 소셜 로그인 제공자
- `Authorization` — JWT 액세스 토큰
- `refresh-token` — 리프레시 토큰
- `nickname` — 사용자 닉네임

### 로그인 플로우

```
1. 사용자가 소셜 로그인 버튼 클릭
2. SocialAuthDataSource → Google/Naver/Kakao SDK 호출
3. 소셜 accessToken + provider + providerId 획득
4. AuthDataSource.login() → POST /auth/login (social-token 헤더)
5. 서버 응답 헤더에서 Authorization + refresh-token 추출
6. AuthDataStoreSource.saveAuthInfo() → DataStore에 저장
7. LoginState.Success 발행 → UI에서 Home으로 이동
```

### 토큰 관리 아키텍처

**2종류의 OkHttpClient**로 인증 필요/불필요 API를 분리합니다:

| 클라이언트 | 용도 | 예시 |
|-----------|------|------|
| `@DefaultOkHttpClient` | 인증 불필요 API | 로그인, 닉네임 확인, 알라딘 검색 |
| `@AuthOkHttpClient` | 인증 필요 API | 책 저장, 히스토리 조회 |

`@AuthOkHttpClient`에는 다음이 포함됩니다:
- **AuthInterceptor**: 매 요청에 `Bearer <token>` 헤더 자동 첨부
- **TokenAuthenticator**: 401 응답 시 자동 토큰 갱신

### AuthTokenProvider (캐싱 전략)

- `@Volatile` + double-checked locking으로 스레드 안전한 토큰 캐싱
- `getToken()`: 캐시 반환, 없으면 DataStore에서 동기 로드
- `updateToken()`: 로그인/갱신 성공 후 캐시 업데이트
- `clearToken()`: 로그아웃 시 캐시 클리어

### 토큰 갱신 (TokenAuthenticator)

```
1. 401 응답 수신
2. DataStore에서 refreshToken + authorization 로드
3. POST /auth/reissue (Authorization 헤더 + refresh-token 헤더)
4. 성공 시: 새 토큰 저장 + 캐시 갱신 + 원래 요청 재시도
5. 실패 시: DataStore 클리어 + AuthEvent.LOGIN_REQUIRED 발행 → 로그인 화면 강제 이동
```

### Auth Event 시스템

- `AuthEvent` → `MutableSharedFlow<DataAuthEvent>` (앱 전역 이벤트 버스)
- `AuthEventRepositoryImpl` → DataAuthEvent를 DomainAuthEvent로 매핑
- `GetAuthEventUseCase` → `Flow<DomainAuthEvent>` 반환
- `MainActivity`에서 `LaunchedEffect`로 구독, `LOGIN_REQUIRED` 수신 시 Login 화면으로 이동

---

## 11. 모듈 의존성 그래프

```
                          ┌──────┐
                          │ :app │
                          └──┬───┘
               ┌─────────────┼──────────────────────────┐
               │             │                          │
               v             v                          v
           ┌──────┐    ┌────────────┐            ┌──────────┐
           │ :ui  │    │:presentation│            │  :data   │
           └──┬───┘    └─────┬──────┘            └────┬─────┘
               │             │                        │
               │             v                        │
               │        ┌─────────┐                   │
               └──────→ │ :domain │ ←─────────────────┘
                        └─────────┘
                             ^
                             │
               ┌─────────────┼──────────────┐
               │                            │
           ┌───┴────┐                 ┌─────┴───┐
           │:remote │                 │ :local  │
           └───┬────┘                 └────┬────┘
               │                           │
               └──────────→ :data ←────────┘
```

### 구체적 모듈별 의존성

| 모듈 | 의존 대상 |
|------|-----------|
| `:app` | `:ui`, `:presentation`, `:domain`, `:data`, `:remote`, `:local` |
| `:ui` | `:presentation`, `:domain` |
| `:presentation` | `:domain` |
| `:domain` | 없음 (`javax.inject`, `kotlinx-coroutines-core`만 사용) |
| `:data` | `:domain` |
| `:remote` | `:data` |
| `:local` | `:data` |

---

## 12. API 엔드포인트

> 각 API의 상세 문서는 `docs/api/` 디렉토리를 참조하세요.

### 자체 백엔드 API

| 메서드 | 경로 | 인증 | 용도 | 상세 문서 |
|--------|------|------|------|-----------|
| `POST` | `/auth/login` | social-token 헤더 | 소셜 로그인 | [auth.md](api/auth.md) |
| `POST` | `/auth/signup` | social-token 헤더 | 회원가입 | [auth.md](api/auth.md) |
| `DELETE` | `/auth/logout` | Bearer JWT | 로그아웃 | [auth.md](api/auth.md) |
| `POST` | `/auth/reissue` | Authorization + refresh-token | 토큰 갱신 | [auth.md](api/auth.md) |
| `GET` | `/members/me` | Bearer JWT | 내 정보 조회 | [member.md](api/member.md) |
| `PATCH` | `/members/me` | Bearer JWT | 닉네임 변경 | [member.md](api/member.md) |
| `DELETE` | `/members/me` | Bearer JWT | 회원 탈퇴 | [member.md](api/member.md) |
| `GET` | `/members/check?nickname=` | 불필요 | 닉네임 중복 확인 | [member.md](api/member.md) |
| `POST` | `/mybooks` | Bearer JWT | 책 저장 | [mybook.md](api/mybook.md) |
| `GET` | `/mybooks?query=` | Bearer JWT | 내 책 검색 | [mybook.md](api/mybook.md) |
| `GET` | `/mybooks/store` | Bearer JWT | 내 서점 목록 조회 | [mybook.md](api/mybook.md) |
| `GET` | `/mybooks/{mybookId}` | Bearer JWT | 책 상세 조회 | [mybook.md](api/mybook.md) |
| `DELETE` | `/mybooks/{mybookId}` | Bearer JWT | 책 삭제 | [mybook.md](api/mybook.md) |
| `PATCH` | `/mybooks/{mybookId}` | Bearer JWT | 책 정보 수정 | [mybook.md](api/mybook.md) |
| `PATCH` | `/mybooks/{mybookId}/reading-status` | Bearer JWT | 독서 상태 변경 | [mybook.md](api/mybook.md) |
| `GET` | `/mybooks/history` | Bearer JWT | 독서 히스토리 조회 | [history.md](api/history.md) |

### 외부 API

| 서비스 | Base URL | 용도 | 상세 문서 |
|--------|----------|------|-----------|
| 알라딘 | `https://www.aladin.co.kr/` | 책 검색 (제목/ISBN) | [aladin.md](api/aladin.md) |
