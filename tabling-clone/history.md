# 테이블링 클론 프로젝트 개발 히스토리

> 이 문서는 테이블링 클론 프로젝트의 모든 개발 과정을 시간순으로 기록합니다.

---

## 2024-11-26

### 프로젝트 초기 설정

#### 1. 프로젝트 기획 (18:30 - 18:40)

**작업 내용**
- 테이블링 클론 프로젝트 아이디어 확정
- 하이브리드 모바일 앱으로 개발 방향 결정
- Frontend/Backend 역할 분담
  - Frontend: Claude 담당 (React Native)
  - Backend: 사용자 담당

**주요 결정사항**
- 플랫폼: 하이브리드 모바일 (iOS/Android)
- 개발 순서: 예약 시스템 → 식당 정보 → 사용자 인증 → 찜하기 → 리뷰
- 5개 Phase로 구성된 점진적 개발

---

#### 2. 요구사항 명세서 작성 (18:35)

**생성 파일**: `docs/requirements.md`

**작성 내용**
- 프로젝트 개요 및 목적
- 기술 스택 상세 정의
  - Frontend: React Native (Expo), TypeScript, Zustand
  - Backend: RESTful API, JWT 인증
- 5개 Phase별 기능 요구사항
  - Phase 1: 사용자 인증 및 로그인
  - Phase 2: 식당 정보
  - Phase 3: 예약 시스템 (핵심)
  - Phase 4: 찜하기/북마크
  - Phase 5: 리뷰 시스템
- 화면 구성 (30+ 화면)
- 데이터 모델 정의 (User, Restaurant, Reservation, Review, Menu)
- 비기능 요구사항 (성능, 보안, 사용성, 접근성, 호환성)
- 제약사항 및 고려사항
- 향후 확장 가능성

**주요 결정사항**
- iOS 14+, Android 8.0+ 지원
- 예약은 최소 1시간 전까지, 취소는 2시간 전까지
- 리뷰는 방문 완료 후 30일 이내 작성 가능
- 이미지 최적화 및 레이지 로딩 적용

---

#### 3. 실행 계획 문서 작성 (18:36)

**생성 파일**: `docs/implementation-plan.md`

**작성 내용**
- Phase 0: 프로젝트 초기 설정
  - React Native (Expo) 프로젝트 생성
  - 디렉토리 구조 정의
  - 필수 패키지 설치 목록
  - ESLint/Prettier 설정
- Phase 1-5: 각 단계별 상세 작업 계획
  - Backend API 개발 항목
  - Frontend 화면 구현 항목
  - 상태 관리 구조 설계
  - 컴포넌트 설계
  - 테스트 계획
- 공통 작업 (모든 Phase)
  - UI/UX (로딩, 에러 처리, 빈 상태)
  - 성능 최적화
  - 오프라인 지원
  - 접근성
  - 보안
- 테스트 전략 (Unit, Integration, E2E)
- 배포 준비 및 모니터링
- 개발 일정 및 체크리스트

**주요 구조 결정**
```
frontend/src/
├── components/      # 재사용 컴포넌트
├── screens/         # 화면 컴포넌트
├── navigation/      # 네비게이션 설정
├── store/           # Zustand 상태 관리
├── api/             # API 클라이언트
├── hooks/           # Custom Hooks
├── utils/           # 유틸리티 함수
├── types/           # TypeScript 타입
└── assets/          # 이미지, 폰트
```

---

#### 4. API 명세서 작성 (18:38)

**생성 파일**: `docs/api-spec.md`

**작성 내용**
- Base URL 및 인증 방식 정의
- 50+ API 엔드포인트 상세 명세
  - **인증 API** (6개): 회원가입, 로그인, 토큰 갱신, 로그아웃, 비밀번호 재설정
  - **사용자 API** (5개): 프로필 조회/수정, 프로필 사진 업로드, 비밀번호 변경, 회원 탈퇴
  - **식당 API** (4개): 목록 조회, 상세 조회, 주변 식당, 카테고리
  - **예약 API** (6개): 예약 가능 시간, 예약 생성/조회/수정/취소
  - **찜하기 API** (4개): 찜하기/취소, 목록 조회, 찜 여부 확인
  - **리뷰 API** (9개): 작성/조회/수정/삭제, 이미지 업로드, 좋아요
- Request/Response 예시 (JSON)
- 에러 코드 체계 정의
  - AUTH: 인증 관련 (6개)
  - VALIDATION: 유효성 검사 (3개)
  - RESOURCE: 리소스 (2개)
  - RESERVATION: 예약 관련 (5개)
  - REVIEW: 리뷰 관련 (4개)
  - SERVER: 서버 에러 (2개)
- 페이지네이션 형식 통일
- Rate Limiting 정책 (인증: 100/min, 미인증: 20/min)
- 파일 업로드 제한 (프로필: 5MB, 리뷰: 10MB/개)

**API 응답 형식 표준화**
```json
// 성공
{
  "success": true,
  "data": {},
  "message": "Success message"
}

// 에러
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

#### 5. 프로젝트 README 작성 (18:39)

**생성 파일**: `README.md`

**작성 내용**
- 프로젝트 개요 및 목적
- 기술 스택 요약
- 주요 기능 5개 Phase
- 문서 링크 (requirements.md, implementation-plan.md, api-spec.md)
- 프로젝트 디렉토리 구조
- 개발 우선순위
- 시작하기 가이드
- 다음 단계 안내

---

### 프로젝트 상태

**생성된 파일 구조**
```
tabling-clone/
├── README.md                        # 프로젝트 개요
├── history.md                       # 개발 히스토리 (현재 파일)
└── docs/
    ├── requirements.md              # 요구사항 명세서 (12.6KB)
    ├── implementation-plan.md       # 실행 계획 (15.2KB)
    └── api-spec.md                  # API 명세서 (27.4KB)
```

**문서 통계**
- 총 문서 수: 4개 (+ history.md)
- 총 문서 크기: ~58KB
- API 엔드포인트: 50+개
- 정의된 기능: 30+개
- 화면 수: 30+개

---

### 다음 단계 (Phase 0)

#### Backend 개발자 할 일
- [ ] Backend 기술 스택 선택
- [ ] 프로젝트 초기 설정
- [ ] 데이터베이스 스키마 설계
- [ ] Phase 1 API 개발 시작 (인증)

#### Frontend 개발자 할 일
- [ ] React Native (Expo) 프로젝트 생성
- [ ] 필수 패키지 설치
- [ ] 디렉토리 구조 생성
- [ ] 디자인 시스템 구축
- [ ] Phase 1 UI 개발 시작 (Mock 데이터)

---

### 메모 및 참고사항

**기술 스택 선택 이유**
- **React Native**: 네이티브 성능 우수, 커뮤니티 활성화, 크로스 플랫폼 지원
- **Expo**: 빠른 개발, 간편한 빌드, 풍부한 내장 API
- **TypeScript**: 타입 안전성, 개발 생산성 향상
- **Zustand**: 가볍고 간단한 상태 관리, Redux보다 보일러플레이트 적음
- **React Hook Form + Zod**: 성능 좋은 폼 관리 + 타입 안전한 유효성 검사

**개발 우선순위를 예약 시스템 우선으로 한 이유**
- 테이블링의 핵심 기능은 예약
- 하지만 실제 개발 순서는 Phase 1 → 2 → 3으로 진행
- 예약 시스템이 가장 중요하므로 최우선 순위로 표시

**협업 방식**
- API 명세서를 기반으로 Frontend/Backend 병렬 개발 가능
- Frontend는 Mock 데이터로 UI 먼저 개발
- Backend API 완성 시 연동

---

## 변경 이력

| 날짜 | 버전 | 변경 내용 | 작성자 |
|------|------|-----------|--------|
| 2024-11-26 | 0.1.0 | 프로젝트 초기 설정 및 문서 작성 | Claude |

---

## 참고 링크

### 프로젝트 문서
- [요구사항 명세서](./docs/requirements.md)
- [실행 계획](./docs/implementation-plan.md)
- [API 명세서](./docs/api-spec.md)

### 기술 스택 문서
- [React Native 공식 문서](https://reactnative.dev/)
- [Expo 공식 문서](https://docs.expo.dev/)
- [Zustand 공식 문서](https://github.com/pmndrs/zustand)
- [React Navigation 공식 문서](https://reactnavigation.org/)

---

_이 문서는 프로젝트 진행에 따라 지속적으로 업데이트됩니다._
