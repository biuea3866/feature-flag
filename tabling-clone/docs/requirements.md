# 테이블링 클론 프로젝트 요구사항 명세서

## 1. 프로젝트 개요

### 1.1 프로젝트 목적
테이블링 서비스를 클론 코딩하여 레스토랑 예약 시스템의 전반적인 기능을 구현하고, 하이브리드 모바일 애플리케이션 개발 경험을 축적한다.

### 1.2 프로젝트 범위
- **플랫폼**: 하이브리드 모바일 앱 (iOS/Android)
- **역할 분담**:
  - Frontend: Claude (하이브리드 모바일 앱)
  - Backend: 사용자 담당

---

## 2. 기술 스택

### 2.1 Frontend (하이브리드 모바일)
- **프레임워크**: React Native 또는 Ionic + React
  - **권장**: React Native (네이티브 성능 우수, 커뮤니티 활성화)
  - **대안**: Ionic React (웹 기술 기반, PWA 지원)
- **언어**: TypeScript
- **상태 관리**: Zustand (가볍고 간단한 상태 관리)
- **네비게이션**: React Navigation (React Native) / React Router (Ionic)
- **스타일링**:
  - React Native: Styled Components Native 또는 NativeWind (Tailwind for RN)
  - Ionic: Tailwind CSS
- **HTTP 클라이언트**: Axios
- **폼 관리**: React Hook Form + Zod (타입 안전한 유효성 검사)
- **날짜/시간**: date-fns
- **지도**:
  - React Native Maps (Google Maps 연동)
  - 또는 Kakao Map API
- **이미지**: React Native Fast Image 또는 Expo Image

### 2.2 Backend (사용자 담당)
- 기술 스택은 사용자가 선택
- RESTful API 또는 GraphQL
- 인증: JWT 기반

### 2.3 추천 개발 환경
- **React Native 선택 시**: Expo (빠른 개발, 간편한 빌드)
- **패키지 매니저**: pnpm 또는 yarn
- **코드 품질**: ESLint + Prettier
- **버전 관리**: Git

---

## 3. 기능 요구사항

### Phase 1: 사용자 인증 및 로그인 ⭐ 우선순위 3
#### 3.1 회원가입
- 이메일/비밀번호 기반 회원가입
- 소셜 로그인 (Google, Kakao, Apple) - 선택사항
- 필수 정보:
  - 이메일
  - 비밀번호 (8자 이상, 영문/숫자/특수문자 조합)
  - 이름
  - 전화번호
- 이메일 인증 (선택사항)
- 약관 동의 (서비스 이용약관, 개인정보 처리방침)

#### 3.2 로그인
- 이메일/비밀번호 로그인
- 소셜 로그인
- 자동 로그인 (토큰 저장)
- 비밀번호 찾기/재설정

#### 3.3 프로필 관리
- 프로필 정보 조회
- 프로필 정보 수정 (이름, 전화번호, 프로필 사진)
- 비밀번호 변경
- 회원 탈퇴

---

### Phase 2: 식당 정보 ⭐ 우선순위 2
#### 3.4 식당 목록
- 식당 목록 조회 (페이지네이션 또는 무한 스크롤)
- 필터링:
  - 지역/위치 기반
  - 음식 카테고리 (한식, 중식, 일식, 양식, 카페 등)
  - 가격대
  - 평점
  - 예약 가능 시간
- 정렬:
  - 인기순
  - 평점순
  - 거리순
  - 최신순
- 검색 기능 (식당명, 지역, 메뉴)

#### 3.5 식당 상세 정보
- 기본 정보:
  - 식당명
  - 카테고리
  - 주소 및 지도
  - 영업시간
  - 전화번호
  - 가격대
  - 평균 평점
- 이미지 갤러리 (식당 사진, 메뉴 사진)
- 메뉴 정보
- 편의 시설 (주차, Wi-Fi, 단체석 등)
- 리뷰 미리보기 (최신 리뷰 3개)
- 예약 가능 시간 표시
- 찜하기 버튼

#### 3.6 지도 기반 검색
- 현재 위치 기반 식당 표시
- 지도에서 식당 마커 표시
- 마커 클릭 시 간단한 정보 표시
- 상세 정보로 이동

---

### Phase 3: 예약 시스템 ⭐ 우선순위 1 (최우선)
#### 3.7 예약하기
- 예약 정보 입력:
  - 날짜 선택 (캘린더)
  - 시간 선택 (식당의 예약 가능 시간 중 선택)
  - 인원 수 (성인/어린이 구분 - 선택사항)
  - 요청사항 (선택사항)
- 예약 가능 여부 실시간 확인
- 예약 확정
- 예약 알림 설정

#### 3.8 예약 내역 관리
- 예약 내역 조회:
  - 예정된 예약
  - 완료된 예약
  - 취소된 예약
- 예약 상세 정보 확인
- 예약 수정 (날짜, 시간, 인원)
- 예약 취소 (취소 정책에 따라)
- 예약 상태:
  - 예약 대기
  - 예약 확정
  - 방문 완료
  - 예약 취소
  - 노쇼

#### 3.9 예약 알림
- 예약 확정 알림 (Push Notification)
- 예약 하루 전 알림
- 예약 1시간 전 알림

---

### Phase 4: 찜하기/북마크 기능 ⭐ 우선순위 4
#### 3.10 찜하기
- 식당 찜하기/찜 취소 (토글)
- 찜한 식당 목록 조회
- 찜한 식당 수 표시
- 찜한 식당 정렬 (최근 추가순, 평점순)

#### 3.11 찜 목록 관리
- 찜 목록 그룹화 (선택사항, 예: "가고싶은 곳", "자주 가는 곳")
- 찜 목록에서 삭제

---

### Phase 5: 리뷰 시스템 ⭐ 우선순위 5
#### 3.12 리뷰 작성
- 리뷰 작성 권한: 방문 완료한 예약에 한해 작성 가능
- 리뷰 내용:
  - 평점 (1~5점, 0.5점 단위)
  - 방문 일자 (자동 입력)
  - 리뷰 텍스트 (최소 10자 이상)
  - 사진 업로드 (최대 5장)
  - 재방문 의사 (선택사항)
- 리뷰 수정/삭제

#### 3.13 리뷰 조회
- 식당별 리뷰 목록 (페이지네이션)
- 리뷰 정렬:
  - 최신순
  - 평점 높은순
  - 평점 낮은순
- 리뷰 필터:
  - 평점별
  - 사진 있는 리뷰만
- 리뷰 좋아요/도움됨 기능
- 사장님 댓글

#### 3.14 내 리뷰 관리
- 내가 작성한 리뷰 목록
- 리뷰 수정/삭제

---

## 4. 비기능 요구사항

### 4.1 성능
- 앱 초기 로딩 시간: 3초 이내
- 페이지 전환: 1초 이내
- 이미지 로딩: 레이지 로딩 적용
- API 응답 시간: 2초 이내

### 4.2 보안
- HTTPS 통신
- JWT 토큰 기반 인증
- 토큰 안전한 저장 (Keychain/Keystore)
- 민감한 정보 암호화
- API Rate Limiting

### 4.3 사용성
- 직관적인 UI/UX
- 일관된 디자인 시스템
- 에러 메시지 사용자 친화적 표현
- 로딩 상태 표시
- 오프라인 모드 지원 (캐싱)

### 4.4 접근성
- 적절한 터치 영역 크기 (최소 44x44pt)
- 명확한 라벨링
- 고대비 모드 지원 (선택사항)

### 4.5 호환성
- iOS 14 이상
- Android 8.0 (API 26) 이상
- 다양한 화면 크기 대응 (스마트폰, 태블릿)

---

## 5. 화면 구성

### 5.1 인증 관련
- 스플래시 화면
- 온보딩 화면 (첫 실행 시)
- 로그인 화면
- 회원가입 화면
- 비밀번호 찾기 화면

### 5.2 메인 화면
- 홈 (식당 목록)
- 검색
- 예약 내역
- 찜 목록
- 마이페이지

### 5.3 식당 관련
- 식당 목록 화면
- 식당 상세 화면
- 지도 화면
- 식당 검색 화면

### 5.4 예약 관련
- 예약하기 화면
- 예약 확인 화면
- 예약 내역 목록
- 예약 상세 화면

### 5.5 리뷰 관련
- 리뷰 목록 화면
- 리뷰 작성 화면
- 내 리뷰 목록

### 5.6 프로필 관련
- 마이페이지
- 프로필 수정
- 설정

---

## 6. API 요구사항

### 6.1 필요한 API 엔드포인트
Backend 개발자는 다음 API를 제공해야 합니다:

#### 인증
- `POST /api/auth/register` - 회원가입
- `POST /api/auth/login` - 로그인
- `POST /api/auth/logout` - 로그아웃
- `POST /api/auth/refresh` - 토큰 갱신
- `POST /api/auth/password/reset` - 비밀번호 재설정

#### 사용자
- `GET /api/users/me` - 내 정보 조회
- `PUT /api/users/me` - 내 정보 수정
- `DELETE /api/users/me` - 회원 탈퇴
- `POST /api/users/me/avatar` - 프로필 사진 업로드

#### 식당
- `GET /api/restaurants` - 식당 목록 조회 (필터, 검색, 페이지네이션)
- `GET /api/restaurants/:id` - 식당 상세 조회
- `GET /api/restaurants/nearby` - 주변 식당 조회 (위치 기반)

#### 예약
- `POST /api/reservations` - 예약 생성
- `GET /api/reservations` - 내 예약 목록
- `GET /api/reservations/:id` - 예약 상세 조회
- `PUT /api/reservations/:id` - 예약 수정
- `DELETE /api/reservations/:id` - 예약 취소
- `GET /api/restaurants/:id/availability` - 예약 가능 시간 조회

#### 찜하기
- `POST /api/favorites` - 찜하기
- `DELETE /api/favorites/:restaurantId` - 찜 취소
- `GET /api/favorites` - 찜 목록 조회

#### 리뷰
- `POST /api/reviews` - 리뷰 작성
- `GET /api/reviews` - 리뷰 목록 조회 (식당별, 사용자별)
- `GET /api/reviews/:id` - 리뷰 상세 조회
- `PUT /api/reviews/:id` - 리뷰 수정
- `DELETE /api/reviews/:id` - 리뷰 삭제
- `POST /api/reviews/:id/like` - 리뷰 좋아요
- `POST /api/reviews/:id/images` - 리뷰 이미지 업로드

### 6.2 API 응답 형식
```json
{
  "success": true,
  "data": {},
  "message": "Success",
  "error": null
}
```

### 6.3 에러 응답 형식
```json
{
  "success": false,
  "data": null,
  "message": "Error message",
  "error": {
    "code": "ERROR_CODE",
    "details": {}
  }
}
```

---

## 7. 데이터 모델 (Frontend 관점)

### 7.1 User
```typescript
interface User {
  id: string;
  email: string;
  name: string;
  phoneNumber: string;
  profileImage?: string;
  createdAt: string;
  updatedAt: string;
}
```

### 7.2 Restaurant
```typescript
interface Restaurant {
  id: string;
  name: string;
  category: string;
  description: string;
  address: string;
  location: {
    latitude: number;
    longitude: number;
  };
  phoneNumber: string;
  openingHours: {
    [key: string]: { open: string; close: string } | null;
  };
  priceRange: 1 | 2 | 3 | 4; // 1: 저렴, 4: 고가
  rating: number;
  reviewCount: number;
  images: string[];
  menus?: Menu[];
  facilities: string[]; // ['parking', 'wifi', 'group_seat']
  isFavorite: boolean;
}
```

### 7.3 Reservation
```typescript
interface Reservation {
  id: string;
  restaurantId: string;
  restaurant: Restaurant;
  userId: string;
  date: string; // ISO 8601
  time: string; // HH:mm
  partySize: number;
  status: 'pending' | 'confirmed' | 'completed' | 'cancelled' | 'no_show';
  specialRequests?: string;
  createdAt: string;
  updatedAt: string;
}
```

### 7.4 Review
```typescript
interface Review {
  id: string;
  restaurantId: string;
  userId: string;
  user: {
    name: string;
    profileImage?: string;
  };
  reservationId: string;
  rating: number; // 1~5, 0.5 단위
  content: string;
  images: string[];
  visitDate: string;
  wouldRecommend?: boolean;
  likeCount: number;
  isLiked: boolean;
  createdAt: string;
  updatedAt: string;
}
```

### 7.5 Menu
```typescript
interface Menu {
  id: string;
  name: string;
  description?: string;
  price: number;
  image?: string;
  isSignature: boolean;
}
```

---

## 8. 제약사항 및 고려사항

### 8.1 제약사항
- 예약은 최소 1시간 전까지만 가능
- 예약 취소는 최소 2시간 전까지만 가능
- 리뷰는 방문 완료 후 30일 이내 작성 가능
- 동일 식당에 대한 중복 예약 불가 (동일 날짜/시간)

### 8.2 고려사항
- 네트워크 오류 처리 및 재시도 로직
- 오프라인 모드에서 데이터 캐싱
- 이미지 최적화 (압축, 리사이징)
- 무한 스크롤 시 메모리 관리
- 푸시 알림 권한 요청 타이밍
- 위치 권한 요청 타이밍

---

## 9. 향후 확장 가능성

### 9.1 Phase 6 이후 기능 (선택사항)
- 대기 등록 시스템
- 쿠폰/할인 시스템
- 결제 시스템 연동
- 채팅 기능 (식당과 고객 간)
- 추천 시스템 (AI 기반)
- 공유 기능 (SNS 연동)
- 다국어 지원
- 다크 모드

---

## 10. 개발 우선순위

1. **Phase 3**: 예약 시스템 (핵심 기능)
2. **Phase 2**: 식당 정보 (예약을 위한 기반)
3. **Phase 1**: 사용자 인증 (모든 기능의 전제)
4. **Phase 4**: 찜하기/북마크
5. **Phase 5**: 리뷰 시스템

**권장 개발 순서**: Phase 1 → Phase 2 → Phase 3 → Phase 4 → Phase 5

하지만 병렬 개발을 위해:
- Backend: Phase 1, 2, 3의 API를 우선 개발
- Frontend: Phase 1 (UI/Mock 데이터) → Backend API 연동 순서로 진행

---

## 11. 협업 방식

### 11.1 API 문서화
- Swagger 또는 Postman을 통한 API 문서 제공
- API 변경 사항은 사전 공유

### 11.2 커뮤니케이션
- 주요 결정 사항 문서화
- API 스펙 변경 시 즉시 공유
- 이슈 발생 시 빠른 피드백

### 11.3 버전 관리
- Git을 통한 소스 관리
- Feature Branch 전략
- 의미 있는 커밋 메시지

---

## 12. 테스트 계획

### 12.1 Frontend 테스트
- Unit Test: 유틸리티 함수, 커스텀 훅
- Integration Test: API 연동 테스트
- E2E Test: 주요 사용자 플로우 (로그인 → 검색 → 예약)

### 12.2 수동 테스트
- 다양한 기기에서 테스트
- 네트워크 상태 테스트 (느린 네트워크, 오프라인)
- 에러 케이스 테스트

---

이 문서는 프로젝트 진행 중 지속적으로 업데이트됩니다.
