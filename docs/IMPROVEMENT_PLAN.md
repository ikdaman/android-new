# 모아북(읽다만) 개선 계획서

> 작성일: 2026-04-12
> 최종 수정: 2026-04-14
> 기준 버전: 1.0.18

---

## 목차

1. [현재 상태 요약](#1-현재-상태-요약)
2. [발견된 문제점](#2-발견된-문제점)
   - [A. 아키텍처 / 코드 품질](#a-아키텍처--코드-품질)
   - [B. 테스트 부족](#b-테스트-부족)
   - [C. 에러 처리 / 안정성](#c-에러-처리--안정성)
   - [D. UI/UX](#d-uiux)
   - [E. 보안](#e-보안)
   - [F. 빌드 / 인프라](#f-빌드--인프라)
3. [수정 계획 (Phase별)](#3-수정-계획-phase별)
4. [각 항목 상세 가이드](#4-각-항목-상세-가이드)

---

## 1. 현재 상태 요약

| 항목 | 상태 |
|------|------|
| 아키텍처 | Clean Architecture + MVVM (7개 모듈) |
| UI | 100% Jetpack Compose |
| DI | Hilt 2.57.1 |
| 네트워크 | Retrofit 3.0 + Moshi |
| 인증 | 카카오/네이버/구글 소셜 로그인 |
| 저장소 | DataStore Preferences (로컬), 백엔드 API (리모트) |
| 테스트 | Domain UseCase 단위테스트 20개, UI 통합테스트 9개 |
| CI/CD | GitHub Actions + Fastlane (Play Store 내부 테스트 배포) |

전반적으로 모듈 분리, DI, 레이어 분리가 잘 되어있는 프로젝트.
아래는 발견된 부족한 점들과 개선 방향.

---

## 2. 발견된 문제점

### A. 아키텍처 / 코드 품질

| ID | 문제 | 심각도 | 위치 | 설명 |
|----|------|--------|------|------|
| A1 | ~~TokenAuthenticator에서 `runBlocking` 사용~~ | ~~**높음**~~ | `remote/.../TokenAuthenticator.kt` | ✅ **해결됨** — `AuthTokenProvider` in-memory 캐시 도입으로 토큰 읽기 시 `runBlocking` 제거. 토큰 저장/클리어 시에만 최소 사용 |
| A2 | ~~UseCase를 Activity에 직접 주입~~ | ~~중간~~ | `ui/.../MainActivity.kt` | ✅ **해결됨** — UseCase 직접 주입 제거. `AuthViewModel` 신규 생성, 각 ViewModel에 생성자 주입 |
| A3 | ~~SearchBookViewModel을 Activity에서 공유~~ | ~~중간~~ | `ui/.../MainActivity.kt` | ✅ **해결됨** — `MAIN_ROUTE` backStackEntry 기반 Navigation scoped ViewModel로 변경 |
| A4 | ~~Moshi와 Gson 동시 사용~~ | ~~낮음~~ | `gradle/libs.versions.toml` | ✅ **해결됨** — Gson 의존 완전 제거 (미사용 확인), Moshi 단일 사용 |
| A5 | ~~HomeScreen 중복 import~~ | ~~낮음~~ | `ui/.../HomeScreen.kt` | ✅ **해결됨** — 중복 import 정리 완료 |

### B. 테스트 부족

| ID | 문제 | 심각도 | 설명 |
|----|------|--------|------|
| B1 | Data/Remote 레이어 테스트 전무 | **높음** | Repository 구현체, DataSource에 대한 테스트가 없어 API 응답 매핑, 에러 처리 검증 불가 |
| B2 | Presentation(ViewModel) 테스트 전무 | **높음** | ViewModel의 상태 관리, 이벤트 발행 로직이 검증되지 않음 |
| B3 | Repository 구현체 테스트 없음 | 중간 | Data 레이어의 매핑 로직, 에러 변환 로직 미검증 |

### C. 에러 처리 / 안정성

| ID | 문제 | 심각도 | 설명 |
|----|------|--------|------|
| C1 | ~~네트워크 에러 처리 불일관~~ | ~~중간~~ | ✅ **해결됨** — `DomainResult` 제거, 모든 ViewModel에서 `DataResource` 패턴 통일 |
| C2 | 오프라인 대응 없음 | 중간 | Room DB 같은 로컬 캐시 없이 모든 데이터를 서버에서만 가져옴. 네트워크 없으면 빈 화면 |
| C3 | Retry/재시도 메커니즘 부재 | 낮음 | 네트워크 실패 시 자동 재시도 없음 (HistoryScreen만 수동 "다시 시도" 버튼 있음) |

### D. UI/UX

| ID | 문제 | 심각도 | 설명 |
|----|------|--------|------|
| D1 | 화면 전환 애니메이션 전부 None | 낮음 | `MainActivity.kt:93-94` — 모든 전환이 `EnterTransition.None`. 사용자 경험이 딱딱함 |
| D2 | 접근성(Accessibility) 미흡 | 중간 | `contentDescription`이 null이거나 한국어 설명이 부족한 Image/Icon 다수 |
| D3 | 다크 모드 미지원 | 낮음 | `IkdamanTheme`에 Light 테마만 정의 |

### E. 보안

| ID | 문제 | 심각도 | 설명 |
|----|------|--------|------|
| E1 | ~~소셜 토큰이 Navigation 파라미터로 노출~~ | ~~중간~~ | ✅ **해결됨** — `SignupDataHolder` 싱글톤으로 변경. Navigation 경로에 토큰 미포함 |
| E2 | ~~ProGuard 규칙 불완전~~ | ~~낮음~~ | ✅ **해결됨** — `remote/proguard-rules.pro`에 DTO keep rule 추가 |

### F. 빌드 / 인프라

| ID | 문제 | 심각도 | 설명 |
|----|------|--------|------|
| F1 | Navigation Compose 버전 오래됨 | 낮음 | 2.8.5 사용 중. Type-safe Navigation API 미적용 |
| F2 | Coil 2.x 사용 | 낮음 | Coil 3.x가 나와있고 Compose 지원이 개선됨 |
| F3 | CI에서 테스트 실행 없음 | 중간 | `deploy-android.yml`에 빌드만 있고 `./gradlew test` 단계 없음. 테스트 실패가 배포를 막지 못함 |

---

## 3. 수정 계획 (Phase별)

### Phase 1 — 즉시 수정 (안정성/보안) ✅ 완료 (2026-04-14)

| 순서 | 항목 | 작업 내용 | 상태 |
|------|------|----------|------|
| 1 | A1 | `TokenAuthenticator`에서 `runBlocking` 제거. `AuthTokenProvider` in-memory 캐시 기반 동기 접근으로 리팩토링. 토큰 저장/클리어 시에만 `runBlocking` 최소 사용 | ✅ 완료 |
| 2 | E1 | 소셜 토큰을 Navigation 파라미터 대신 `SignupDataHolder` 싱글톤으로 전달 | ✅ 완료 |
| 3 | E2 | Remote DTO 클래스에 ProGuard keep rule 추가 (`remote/proguard-rules.pro`) | ✅ 완료 |
| 4 | A5 | HomeScreen.kt 중복 import 정리 | ✅ 완료 |

### Phase 2 — 아키텍처 개선 ✅ 완료 (2026-04-14)

| 순서 | 항목 | 작업 내용 | 상태 |
|------|------|----------|------|
| 5 | A2 | Activity에서 UseCase 직접 주입 제거. `AuthViewModel` 신규 생성, LoginViewModel/SignupViewModel/SettingViewModel에 UseCase 생성자 주입 | ✅ 완료 |
| 6 | A3 | SearchBookViewModel을 Navigation Graph scoped VM으로 변경 (`MAIN_ROUTE` backStackEntry 기반) | ✅ 완료 |
| 7 | C1 | 에러 처리 패턴 통일 — `DomainResult` 제거, 모든 ViewModel에서 `DataResource` 기반 표준 패턴 적용 | ✅ 완료 |
| 8 | A4 | Gson 의존 제거 (미사용 확인), Moshi 단일 사용으로 통일 | ✅ 완료 |

### Phase 3 — 테스트 강화 ✅ 완료 (2026-04-14)

| 순서 | 항목 | 작업 내용 | 상태 |
|------|------|----------|------|
| 9 | B2 | ViewModel 테스트 업데이트: 생성자 주입 반영, `AuthViewModelTest` 신규 추가, `HistoryViewModelTest` 수정 | ✅ 완료 |
| 10 | B1/B3 | Repository 테스트 수정: `MyBookRepositoryImplTest` 누락 파라미터 수정 | ✅ 완료 |
| 11 | F3 | CI 파이프라인에 `./gradlew test` 단계 추가 (`deploy-android.yml`) | ✅ 완료 |

### Phase 4 — UX 개선 [예상: 3~4주]

| 순서 | 항목 | 작업 내용 | 담당 |
|------|------|----------|------|
| 12 | C2 | Room DB 도입. 서점 목록, 히스토리 오프라인 캐시 | |
| 13 | D1 | 화면 전환 애니메이션 추가 (slideIn/slideOut 또는 fade) | |
| 14 | D2 | 접근성 개선: contentDescription 추가, 터치 영역 최소 48dp 보장 | |
| 15 | D3 | 다크 모드 지원: darkColorScheme 정의, Theme 분기 | |

---

## 4. 각 항목 상세 가이드

### A1. TokenAuthenticator runBlocking 제거 ✅ 완료

**수정 내용:**
- `AuthTokenProvider`에 in-memory 캐시(`@Volatile` 필드) 도입
- `getToken()`, `getRefreshTokenSync()`, `getAuthorizationSync()` 동기 메서드 제공
- `TokenCacheManager` 인터페이스로 추상화
- 토큰 읽기 시 `runBlocking` 완전 제거. 저장/클리어 시에만 최소 사용
- 관련 테스트 추가 (`AuthTokenProviderTest`)

### A2. Activity에서 UseCase 직접 주입 제거 ✅ 완료

**수정 내용:**
- `MainActivity`에서 6개 `@Inject` UseCase 필드 제거 (loginUseCase, logoutUseCase, getProviderUseCase, signupUseCase, getAuthEventUseCase, getLoginStateUseCase)
- `AuthViewModel` 신규 생성 — `GetAuthEventUseCase`, `GetLoginStateUseCase` 처리
- `LoginViewModel` — `LoginUseCase` 생성자 주입, logout 관련 메서드 제거
- `SignupViewModel` — `SignupUseCase` 생성자 주입
- `SettingViewModel` — `LogoutUseCase`, `GetProviderUseCase` 생성자 주입
- Screen composable에서 UseCase 파라미터 전부 제거

### A3. SearchBookViewModel 공유 방식 변경 ✅ 완료

**수정 내용:**
- `MainActivity`에서 Activity 스코프의 `hiltViewModel()` 제거
- `MAIN_ROUTE` backStackEntry 기반 scoped ViewModel 패턴 적용:
```kotlin
val searchBookViewModel: SearchBookViewModel = hiltViewModel(
    remember(it) { navController.getBackStackEntry(MAIN_ROUTE) }
)
```
- `BarcodeScreen`, `AddBookScreen`, `SearchBookScreen`에서 동일 패턴으로 VM 공유

### C1. 에러 처리 패턴 통일 ✅ 완료

**수정 내용:**
- `DomainResult` sealed class 삭제
- `SearchBookViewModel.bookDetail`을 `DataResource<BookItem>?`으로 변경
- `BarcodeScreen`, `SearchBookScreen`에서 `DomainResult` → `DataResource` 전환
- 모든 ViewModel이 `DataResource` 패턴만 사용

### E1. 소셜 토큰 Navigation 파라미터 제거 ✅ 완료

**수정 내용:**
- `SignupDataHolder` 싱글톤(`@Singleton`) 도입
- 로그인 화면에서 `signupDataHolder.set(socialToken, provider, providerId)` 호출
- Navigation 경로에서 토큰 파라미터 제거 → `navController.navigate("Signup")`
- Signup 화면에서 `signupDataHolder.consume()`으로 데이터 소비 (일회성)
- 관련 테스트 추가 (`SignupDataHolderTest`)

### B2. ViewModel 테스트 작성 가이드

**테스트 대상 우선순위:**

1. `MainViewModel` — 서점 목록 로딩, 정렬, 삭제, 독서 시작
2. `SearchBookViewModel` — 검색, 페이지네이션, ISBN 검색, 책 저장
3. `HistoryViewModel` — 히스토리 로딩, 뷰 타입 전환, 정렬
4. `LoginViewModel` — 로그인 상태 전환
5. `BookInfoViewModel` — 책 상세 로딩, 수정, 삭제

**테스트 구조:**
```kotlin
@Test
fun `서점 목록 로딩 성공 시 storeBooks가 업데이트된다`() = runTest {
    // Given
    coEvery { getStoreBooksUseCase(any(), any(), any()) } returns flowOf(
        DataResource.Success(mockStoreBook)
    )
    // When
    viewModel.refreshStoreBooks()
    // Then
    viewModel.storeBooks.test {
        val items = awaitItem()
        assertEquals(expectedBooks, items)
    }
}
```

### F3. CI 테스트 단계 추가

**추가할 GitHub Actions step:**
```yaml
- name: Run Unit Tests
  run: ./gradlew test --no-daemon

- name: Upload Test Results
  if: always()
  uses: actions/upload-artifact@v4
  with:
    name: test-results
    path: '**/build/reports/tests/'
```

---

## 변경 이력

| 날짜 | 버전 | 변경 내용 |
|------|------|----------|
| 2026-04-12 | 1.0 | 최초 작성 |
| 2026-04-14 | 1.1 | Phase 1 완료 반영 (A1, E1, E2, A5 해결 상태 업데이트) |
| 2026-04-14 | 2.0 | Phase 2~3 완료 (A2, A3, A4, C1 해결, AuthViewModel 추가, DomainResult 제거, Gson 제거, 테스트 업데이트) |
