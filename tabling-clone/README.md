# 테이블링 클론 프로젝트

레스토랑 예약 시스템을 제공하는 테이블링 서비스의 클론 프로젝트입니다.

## 프로젝트 개요

이 프로젝트는 테이블링의 핵심 기능을 구현하여 레스토랑 예약 시스템을 학습하고, 하이브리드 모바일 앱 개발 경험을 쌓기 위한 개인 프로젝트입니다.

## 기술 스택

### Frontend (하이브리드 모바일)
- **프레임워크**: React Native (Expo)
- **언어**: TypeScript
- **상태 관리**: Zustand
- **네비게이션**: React Navigation
- **HTTP 클라이언트**: Axios
- **폼 관리**: React Hook Form + Zod
- **스타일링**: Styled Components Native / NativeWind

### Backend
- 사용자가 선택한 기술 스택
- RESTful API
- JWT 기반 인증

## 주요 기능

### Phase 1: 사용자 인증 및 로그인
- 회원가입 / 로그인
- 소셜 로그인 (선택)
- 프로필 관리
- 비밀번호 재설정

### Phase 2: 식당 정보
- 식당 목록 조회 (검색, 필터링, 정렬)
- 식당 상세 정보
- 지도 기반 검색
- 카테고리별 분류

### Phase 3: 예약 시스템 (핵심)
- 예약 생성 (날짜/시간/인원 선택)
- 예약 내역 관리
- 예약 수정/취소
- 예약 알림 (Push Notification)

### Phase 4: 찜하기/북마크
- 식당 찜하기/취소
- 찜 목록 관리

### Phase 5: 리뷰 시스템
- 리뷰 작성 (별점, 텍스트, 사진)
- 리뷰 조회 (필터링, 정렬)
- 리뷰 수정/삭제
- 리뷰 좋아요

## 문서

프로젝트 관련 상세 문서는 `docs/` 디렉토리에 있습니다:

- [요구사항 명세서](./docs/requirements.md) - 프로젝트의 전체 요구사항과 기능 명세
- [실행 계획](./docs/implementation-plan.md) - Phase별 개발 계획과 작업 내용
- [API 명세서](./docs/api-spec.md) - Backend API 엔드포인트 상세 명세

## 프로젝트 구조

```
tabling-clone/
├── frontend/           # React Native 앱
│   ├── src/
│   │   ├── components/   # 재사용 컴포넌트
│   │   ├── screens/      # 화면 컴포넌트
│   │   ├── navigation/   # 네비게이션 설정
│   │   ├── store/        # 상태 관리
│   │   ├── api/          # API 클라이언트
│   │   ├── hooks/        # Custom Hooks
│   │   ├── utils/        # 유틸리티
│   │   └── types/        # TypeScript 타입
│   └── ...
├── backend/            # Backend (사용자 담당)
└── docs/               # 프로젝트 문서
    ├── requirements.md
    ├── implementation-plan.md
    └── api-spec.md
```

## 개발 우선순위

1. Phase 3: 예약 시스템 (핵심 기능)
2. Phase 2: 식당 정보 (예약을 위한 기반)
3. Phase 1: 사용자 인증 (모든 기능의 전제)
4. Phase 4: 찜하기/북마크
5. Phase 5: 리뷰 시스템

**권장 개발 순서**: Phase 1 → Phase 2 → Phase 3 → Phase 4 → Phase 5

## 시작하기

### Frontend 설정

```bash
# 프로젝트 디렉토리로 이동
cd tabling-clone/frontend

# 패키지 설치
npm install
# 또는
yarn install

# 개발 서버 실행
npm start
# 또는
yarn start
```

### Backend 설정

Backend 설정은 사용자가 담당합니다.
API 명세서(`docs/api-spec.md`)를 참조하여 개발해주세요.

## 다음 단계

1. **Backend 개발자**:
   - Phase 1 API 개발 (인증 관련)
   - 데이터베이스 스키마 설계
   - API 문서 작성 완료

2. **Frontend 개발자**:
   - React Native 프로젝트 초기 설정
   - 디자인 시스템 구축
   - Phase 1 UI 개발 (Mock 데이터 사용)

## 기여 방법

이 프로젝트는 개인 학습용 프로젝트입니다.

## 라이선스

이 프로젝트는 학습 목적으로 만들어졌습니다.

## 연락처

프로젝트 관련 문의사항이 있으시면 이슈를 등록해주세요.
