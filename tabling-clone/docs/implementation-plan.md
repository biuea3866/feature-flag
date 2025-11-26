# 테이블링 클론 프로젝트 실행 계획

## 1. 프로젝트 타임라인 개요

### Phase별 개발 순서
```
Phase 1: 사용자 인증 (기반)
    ↓
Phase 2: 식당 정보 (검색 및 조회)
    ↓
Phase 3: 예약 시스템 (핵심 기능)
    ↓
Phase 4: 찜하기/북마크
    ↓
Phase 5: 리뷰 시스템
```

---

## 2. Phase 0: 프로젝트 초기 설정

### 2.1 Frontend 환경 설정
#### 작업 내용
- [ ] React Native 프로젝트 생성 (Expo 사용)
- [ ] TypeScript 설정
- [ ] 디렉토리 구조 생성
- [ ] 필수 패키지 설치
- [ ] ESLint/Prettier 설정
- [ ] Git 설정 (.gitignore, README)

#### 디렉토리 구조
```
tabling-clone/
├── frontend/
│   ├── src/
│   │   ├── components/      # 재사용 가능한 컴포넌트
│   │   │   ├── common/      # 공통 컴포넌트 (Button, Input 등)
│   │   │   ├── layout/      # 레이아웃 컴포넌트
│   │   │   └── features/    # 기능별 컴포넌트
│   │   ├── screens/         # 화면 컴포넌트
│   │   │   ├── auth/
│   │   │   ├── restaurant/
│   │   │   ├── reservation/
│   │   │   ├── review/
│   │   │   └── profile/
│   │   ├── navigation/      # 네비게이션 설정
│   │   ├── store/           # 상태 관리 (Zustand)
│   │   ├── api/             # API 클라이언트
│   │   ├── hooks/           # Custom Hooks
│   │   ├── utils/           # 유틸리티 함수
│   │   ├── types/           # TypeScript 타입 정의
│   │   ├── constants/       # 상수
│   │   ├── styles/          # 스타일 관련
│   │   └── assets/          # 이미지, 폰트 등
│   ├── App.tsx
│   ├── package.json
│   └── tsconfig.json
├── backend/                 # 사용자 담당
└── docs/
    ├── requirements.md
    ├── implementation-plan.md
    └── api-spec.md
```

#### 설치할 주요 패키지
```json
{
  "dependencies": {
    "react-native": "latest",
    "expo": "latest",
    "typescript": "latest",
    "@react-navigation/native": "^6.x",
    "@react-navigation/bottom-tabs": "^6.x",
    "@react-navigation/native-stack": "^6.x",
    "zustand": "^4.x",
    "axios": "^1.x",
    "react-hook-form": "^7.x",
    "zod": "^3.x",
    "@hookform/resolvers": "^3.x",
    "date-fns": "^2.x",
    "react-native-maps": "latest",
    "expo-image-picker": "latest",
    "expo-notifications": "latest",
    "@react-native-async-storage/async-storage": "latest"
  },
  "devDependencies": {
    "@types/react": "latest",
    "@types/react-native": "latest",
    "eslint": "latest",
    "prettier": "latest"
  }
}
```

### 2.2 Backend 환경 설정 (사용자 담당)
- Backend 기술 스택 선택
- 프로젝트 초기 설정
- 데이터베이스 스키마 설계
- API 문서 작성 (Swagger/Postman)

### 2.3 디자인 시스템 구축
- [ ] 컬러 팔레트 정의
- [ ] 타이포그래피 정의
- [ ] 공통 컴포넌트 구현 (Button, Input, Card 등)
- [ ] 스페이싱 시스템
- [ ] 아이콘 세트 준비

---

## 3. Phase 1: 사용자 인증 및 로그인

### 3.1 Backend API 개발 (사용자 담당)
#### 작업 내용
- [ ] 회원가입 API
- [ ] 로그인 API
- [ ] JWT 토큰 발급/갱신
- [ ] 사용자 정보 조회/수정 API
- [ ] 비밀번호 재설정 API

#### API 엔드포인트
```
POST   /api/auth/register
POST   /api/auth/login
POST   /api/auth/logout
POST   /api/auth/refresh
POST   /api/auth/password/reset
GET    /api/users/me
PUT    /api/users/me
DELETE /api/users/me
```

### 3.2 Frontend 개발
#### 3.2.1 화면 구현
- [ ] 스플래시 스크린
- [ ] 로그인 화면
- [ ] 회원가입 화면
- [ ] 비밀번호 찾기 화면

#### 3.2.2 상태 관리
```typescript
// store/authStore.ts
interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;

  login: (email: string, password: string) => Promise<void>;
  register: (data: RegisterData) => Promise<void>;
  logout: () => void;
  refreshToken: () => Promise<void>;
}
```

#### 3.2.3 주요 기능
- [ ] 폼 유효성 검사 (Zod)
- [ ] 토큰 저장 (AsyncStorage + Secure Storage)
- [ ] 자동 로그인
- [ ] 토큰 자동 갱신
- [ ] 인증 필요 화면 접근 제어

#### 3.2.4 테스트
- [ ] 회원가입 플로우 테스트
- [ ] 로그인/로그아웃 테스트
- [ ] 토큰 갱신 테스트
- [ ] 유효성 검사 테스트

---

## 4. Phase 2: 식당 정보

### 4.1 Backend API 개발 (사용자 담당)
#### 작업 내용
- [ ] 식당 목록 조회 API (필터링, 검색, 페이지네이션)
- [ ] 식당 상세 조회 API
- [ ] 카테고리 목록 API
- [ ] 주변 식당 조회 API (위치 기반)

#### API 엔드포인트
```
GET /api/restaurants?page=1&limit=20&category=&search=&sort=
GET /api/restaurants/:id
GET /api/restaurants/nearby?lat=&lng=&radius=
GET /api/categories
```

### 4.2 Frontend 개발
#### 4.2.1 화면 구현
- [ ] 홈 화면 (식당 목록)
- [ ] 식당 상세 화면
- [ ] 검색 화면
- [ ] 지도 화면
- [ ] 필터 화면

#### 4.2.2 상태 관리
```typescript
// store/restaurantStore.ts
interface RestaurantState {
  restaurants: Restaurant[];
  selectedRestaurant: Restaurant | null;
  filters: FilterOptions;
  isLoading: boolean;
  hasMore: boolean;

  fetchRestaurants: (page: number) => Promise<void>;
  fetchRestaurantDetail: (id: string) => Promise<void>;
  searchRestaurants: (query: string) => Promise<void>;
  setFilters: (filters: FilterOptions) => void;
  clearFilters: () => void;
}
```

#### 4.2.3 주요 기능
- [ ] 식당 목록 무한 스크롤
- [ ] 검색 기능 (디바운싱)
- [ ] 필터링 (카테고리, 가격대, 평점)
- [ ] 정렬 (인기순, 평점순, 거리순)
- [ ] 이미지 레이지 로딩
- [ ] 지도에 식당 마커 표시
- [ ] 현재 위치 기반 검색
- [ ] 캐싱 전략 (React Query 또는 자체 구현)

#### 4.2.4 컴포넌트
- [ ] RestaurantCard (목록용)
- [ ] RestaurantDetail
- [ ] SearchBar
- [ ] FilterSheet
- [ ] MapView with Markers
- [ ] ImageGallery

#### 4.2.5 테스트
- [ ] 목록 조회 및 페이지네이션
- [ ] 검색 기능
- [ ] 필터링 및 정렬
- [ ] 상세 정보 조회

---

## 5. Phase 3: 예약 시스템 ⭐ 핵심 기능

### 5.1 Backend API 개발 (사용자 담당)
#### 작업 내용
- [ ] 예약 가능 시간 조회 API
- [ ] 예약 생성 API
- [ ] 예약 목록 조회 API
- [ ] 예약 상세 조회 API
- [ ] 예약 수정 API
- [ ] 예약 취소 API
- [ ] 예약 알림 전송 (Push Notification)

#### API 엔드포인트
```
GET    /api/restaurants/:id/availability?date=YYYY-MM-DD
POST   /api/reservations
GET    /api/reservations
GET    /api/reservations/:id
PUT    /api/reservations/:id
DELETE /api/reservations/:id
```

### 5.2 Frontend 개발
#### 5.2.1 화면 구현
- [ ] 예약하기 화면 (날짜/시간/인원 선택)
- [ ] 예약 확인 화면
- [ ] 예약 내역 목록 화면
- [ ] 예약 상세 화면

#### 5.2.2 상태 관리
```typescript
// store/reservationStore.ts
interface ReservationState {
  reservations: Reservation[];
  selectedReservation: Reservation | null;
  availableSlots: TimeSlot[];
  isLoading: boolean;

  fetchAvailableSlots: (restaurantId: string, date: string) => Promise<void>;
  createReservation: (data: ReservationData) => Promise<void>;
  fetchReservations: (status?: string) => Promise<void>;
  updateReservation: (id: string, data: Partial<ReservationData>) => Promise<void>;
  cancelReservation: (id: string) => Promise<void>;
}
```

#### 5.2.3 주요 기능
- [ ] 캘린더 (날짜 선택)
- [ ] 시간 슬롯 선택 (예약 가능 시간만 표시)
- [ ] 인원 선택
- [ ] 예약 가능 여부 실시간 확인
- [ ] 예약 확정
- [ ] 예약 내역 필터링 (예정/완료/취소)
- [ ] 예약 수정/취소
- [ ] Push Notification 설정
- [ ] 예약 알림 (하루 전, 1시간 전)

#### 5.2.4 컴포넌트
- [ ] DatePicker/Calendar
- [ ] TimeSlotSelector
- [ ] PartySizeSelector
- [ ] ReservationCard
- [ ] ReservationDetail
- [ ] CancellationConfirmDialog

#### 5.2.5 알림 기능
- [ ] Push Notification 권한 요청
- [ ] 알림 설정 저장
- [ ] 예약 확정 알림
- [ ] 예약 리마인더 알림
- [ ] 알림 클릭 시 예약 상세로 이동

#### 5.2.6 테스트
- [ ] 예약 가능 시간 조회
- [ ] 예약 생성 플로우
- [ ] 예약 수정/취소
- [ ] 알림 기능
- [ ] 동시 예약 방지

---

## 6. Phase 4: 찜하기/북마크 기능

### 6.1 Backend API 개발 (사용자 담당)
#### 작업 내용
- [ ] 찜하기/취소 API
- [ ] 찜 목록 조회 API
- [ ] 찜 여부 확인 API

#### API 엔드포인트
```
POST   /api/favorites
DELETE /api/favorites/:restaurantId
GET    /api/favorites
GET    /api/restaurants/:id/is-favorite
```

### 6.2 Frontend 개발
#### 6.2.1 화면 구현
- [ ] 찜 목록 화면
- [ ] 찜 버튼 (식당 목록/상세)

#### 6.2.2 상태 관리
```typescript
// store/favoriteStore.ts
interface FavoriteState {
  favorites: Restaurant[];
  isLoading: boolean;

  fetchFavorites: () => Promise<void>;
  toggleFavorite: (restaurantId: string) => Promise<void>;
  isFavorite: (restaurantId: string) => boolean;
}
```

#### 6.2.3 주요 기능
- [ ] 찜하기/취소 토글 (Optimistic Update)
- [ ] 찜 목록 조회
- [ ] 찜 상태 표시
- [ ] 찜 목록 정렬 (최근 추가순, 평점순)

#### 6.2.4 컴포넌트
- [ ] FavoriteButton (하트 아이콘)
- [ ] FavoriteList

#### 6.2.5 테스트
- [ ] 찜하기/취소 기능
- [ ] 찜 목록 조회
- [ ] Optimistic Update

---

## 7. Phase 5: 리뷰 시스템

### 7.1 Backend API 개발 (사용자 담당)
#### 작업 내용
- [ ] 리뷰 작성 API (이미지 업로드 포함)
- [ ] 리뷰 목록 조회 API
- [ ] 리뷰 수정/삭제 API
- [ ] 리뷰 좋아요 API
- [ ] 내 리뷰 목록 API

#### API 엔드포인트
```
POST   /api/reviews
GET    /api/reviews?restaurantId=&userId=&page=&sort=
GET    /api/reviews/:id
PUT    /api/reviews/:id
DELETE /api/reviews/:id
POST   /api/reviews/:id/like
DELETE /api/reviews/:id/like
POST   /api/reviews/:id/images
```

### 7.2 Frontend 개발
#### 7.2.1 화면 구현
- [ ] 리뷰 목록 화면 (식당 상세 내)
- [ ] 리뷰 작성 화면
- [ ] 리뷰 상세 화면
- [ ] 내 리뷰 목록 화면

#### 7.2.2 상태 관리
```typescript
// store/reviewStore.ts
interface ReviewState {
  reviews: Review[];
  myReviews: Review[];
  isLoading: boolean;

  fetchReviews: (restaurantId: string, options?: FetchOptions) => Promise<void>;
  fetchMyReviews: () => Promise<void>;
  createReview: (data: ReviewData) => Promise<void>;
  updateReview: (id: string, data: Partial<ReviewData>) => Promise<void>;
  deleteReview: (id: string) => Promise<void>;
  toggleLike: (reviewId: string) => Promise<void>;
}
```

#### 7.2.3 주요 기능
- [ ] 별점 선택 (0.5점 단위)
- [ ] 리뷰 텍스트 입력 (최소 10자)
- [ ] 이미지 업로드 (최대 5장)
  - 이미지 선택 (갤러리/카메라)
  - 이미지 미리보기
  - 이미지 압축
- [ ] 리뷰 수정/삭제
- [ ] 리뷰 정렬 (최신순, 평점순)
- [ ] 리뷰 필터 (평점별, 사진 있는 리뷰)
- [ ] 리뷰 좋아요
- [ ] 리뷰 페이지네이션

#### 7.2.4 컴포넌트
- [ ] StarRating (별점 입력/표시)
- [ ] ReviewCard
- [ ] ReviewForm
- [ ] ImagePicker
- [ ] ImageGallery
- [ ] ReviewFilter

#### 7.2.5 테스트
- [ ] 리뷰 작성 플로우
- [ ] 이미지 업로드
- [ ] 리뷰 수정/삭제
- [ ] 좋아요 기능
- [ ] 필터링 및 정렬

---

## 8. 공통 작업 (모든 Phase에서)

### 8.1 UI/UX
- [ ] 로딩 상태 표시 (스피너, 스켈레톤)
- [ ] 에러 처리 및 사용자 친화적 메시지
- [ ] 빈 상태 처리 (Empty State)
- [ ] 토스트/스낵바 알림
- [ ] 다이얼로그/모달

### 8.2 성능 최적화
- [ ] 이미지 최적화 (압축, 리사이징)
- [ ] 레이지 로딩
- [ ] 메모이제이션 (useMemo, useCallback)
- [ ] 리스트 가상화 (FlatList 최적화)
- [ ] API 응답 캐싱

### 8.3 오프라인 지원
- [ ] 네트워크 상태 감지
- [ ] 오프라인 시 캐시 데이터 표시
- [ ] 온라인 복구 시 데이터 동기화

### 8.4 접근성
- [ ] 적절한 터치 영역 크기
- [ ] 명확한 라벨링
- [ ] 키보드 네비게이션

### 8.5 보안
- [ ] HTTPS 통신
- [ ] 토큰 안전한 저장
- [ ] API 키 환경 변수 관리
- [ ] Input Sanitization

---

## 9. 테스트 전략

### 9.1 Unit Test
- [ ] 유틸리티 함수
- [ ] Custom Hooks
- [ ] 상태 관리 로직

### 9.2 Integration Test
- [ ] API 연동 테스트
- [ ] 인증 플로우

### 9.3 E2E Test
- [ ] 주요 사용자 시나리오
  - 회원가입 → 로그인
  - 식당 검색 → 상세 → 예약
  - 찜하기 → 찜 목록
  - 예약 완료 → 리뷰 작성

### 9.4 수동 테스트
- [ ] 다양한 기기 (iPhone, Android)
- [ ] 다양한 화면 크기
- [ ] 느린 네트워크 환경
- [ ] 오프라인 모드

---

## 10. 배포 준비

### 10.1 빌드 설정
- [ ] 환경 변수 설정 (개발/프로덕션)
- [ ] 앱 아이콘 및 스플래시 스크린
- [ ] 앱 메타데이터 (이름, 버전, 설명)

### 10.2 스토어 등록 준비
- [ ] iOS: App Store Connect 설정
- [ ] Android: Google Play Console 설정
- [ ] 앱 스크린샷 및 설명
- [ ] 개인정보 처리방침

### 10.3 모니터링
- [ ] 에러 트래킹 (Sentry)
- [ ] 분석 도구 (Firebase Analytics)
- [ ] 성능 모니터링

---

## 11. 개발 일정 (예상)

### Phase별 작업 예상
- **Phase 0**: 프로젝트 초기 설정
- **Phase 1**: 사용자 인증 (Backend + Frontend)
- **Phase 2**: 식당 정보 (Backend + Frontend)
- **Phase 3**: 예약 시스템 (Backend + Frontend)
- **Phase 4**: 찜하기/북마크 (Backend + Frontend)
- **Phase 5**: 리뷰 시스템 (Backend + Frontend)

### 각 Phase별 병렬 작업 가능
- Backend API 개발과 Frontend UI 개발은 Mock 데이터를 활용하여 병렬 진행
- Backend API 완성 시 Frontend 연동

---

## 12. 다음 단계

### 즉시 시작할 작업
1. **Backend 개발자**:
   - Phase 1 API 개발 시작 (회원가입, 로그인)
   - 데이터베이스 스키마 설계
   - API 문서 작성

2. **Frontend 개발자 (Claude)**:
   - React Native 프로젝트 초기 설정
   - 디자인 시스템 구축
   - Phase 1 UI 개발 (Mock 데이터)

### 협업을 위한 준비
- [ ] API 명세서 작성 및 공유
- [ ] Mock API 서버 준비 (선택사항)
- [ ] Git Repository 설정
- [ ] 커뮤니케이션 채널 확정

---

## 13. 체크리스트

### Phase 0 준비 완료
- [x] 요구사항 명세서 작성
- [x] 실행 계획 수립
- [ ] API 명세서 작성
- [ ] 기술 스택 최종 확정
- [ ] 개발 환경 설정

### 개발 준비
- [ ] Backend 저장소 준비
- [ ] Frontend 저장소 준비
- [ ] 개발 서버 환경
- [ ] 데이터베이스 준비

---

이 문서는 개발 진행에 따라 지속적으로 업데이트됩니다.
각 Phase 시작 전에 해당 섹션을 다시 검토하고 세부 계획을 조정합니다.
