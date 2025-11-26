# 테이블링 클론 API 명세서

## 목차
1. [개요](#1-개요)
2. [인증 (Authentication)](#2-인증-authentication)
3. [사용자 (Users)](#3-사용자-users)
4. [식당 (Restaurants)](#4-식당-restaurants)
5. [예약 (Reservations)](#5-예약-reservations)
6. [찜하기 (Favorites)](#6-찜하기-favorites)
7. [리뷰 (Reviews)](#7-리뷰-reviews)
8. [공통 응답 형식](#8-공통-응답-형식)
9. [에러 코드](#9-에러-코드)

---

## 1. 개요

### 1.1 Base URL
```
개발: http://localhost:3000/api
프로덕션: https://api.tabling-clone.com/api
```

### 1.2 인증 방식
- JWT (JSON Web Token) 기반 인증
- Header: `Authorization: Bearer {token}`

### 1.3 공통 헤더
```
Content-Type: application/json
Authorization: Bearer {jwt_token}
```

### 1.4 날짜/시간 형식
- ISO 8601 형식 사용
- 예: `2024-01-15T14:30:00Z`

---

## 2. 인증 (Authentication)

### 2.1 회원가입
```
POST /auth/register
```

**Request Body**
```json
{
  "email": "user@example.com",
  "password": "Password123!",
  "name": "홍길동",
  "phoneNumber": "010-1234-5678"
}
```

**Response (201 Created)**
```json
{
  "success": true,
  "data": {
    "user": {
      "id": "user_123",
      "email": "user@example.com",
      "name": "홍길동",
      "phoneNumber": "010-1234-5678",
      "profileImage": null,
      "createdAt": "2024-01-15T10:00:00Z"
    },
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  "message": "회원가입이 완료되었습니다."
}
```

**Validation Rules**
- email: 유효한 이메일 형식
- password: 최소 8자, 영문/숫자/특수문자 조합
- name: 2-50자
- phoneNumber: 010-0000-0000 형식 또는 01000000000

---

### 2.2 로그인
```
POST /auth/login
```

**Request Body**
```json
{
  "email": "user@example.com",
  "password": "Password123!"
}
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "user": {
      "id": "user_123",
      "email": "user@example.com",
      "name": "홍길동",
      "phoneNumber": "010-1234-5678",
      "profileImage": "https://cdn.example.com/profiles/user_123.jpg"
    },
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  "message": "로그인 성공"
}
```

---

### 2.3 토큰 갱신
```
POST /auth/refresh
```

**Request Body**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  "message": "토큰 갱신 성공"
}
```

---

### 2.4 로그아웃
```
POST /auth/logout
```
**인증 필요**: Yes

**Request Body**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response (200 OK)**
```json
{
  "success": true,
  "message": "로그아웃 성공"
}
```

---

### 2.5 비밀번호 재설정 요청
```
POST /auth/password/reset-request
```

**Request Body**
```json
{
  "email": "user@example.com"
}
```

**Response (200 OK)**
```json
{
  "success": true,
  "message": "비밀번호 재설정 이메일이 발송되었습니다."
}
```

---

### 2.6 비밀번호 재설정
```
POST /auth/password/reset
```

**Request Body**
```json
{
  "token": "reset_token_from_email",
  "newPassword": "NewPassword123!"
}
```

**Response (200 OK)**
```json
{
  "success": true,
  "message": "비밀번호가 성공적으로 변경되었습니다."
}
```

---

## 3. 사용자 (Users)

### 3.1 내 정보 조회
```
GET /users/me
```
**인증 필요**: Yes

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "id": "user_123",
    "email": "user@example.com",
    "name": "홍길동",
    "phoneNumber": "010-1234-5678",
    "profileImage": "https://cdn.example.com/profiles/user_123.jpg",
    "createdAt": "2024-01-01T10:00:00Z",
    "updatedAt": "2024-01-15T10:00:00Z"
  }
}
```

---

### 3.2 내 정보 수정
```
PUT /users/me
```
**인증 필요**: Yes

**Request Body**
```json
{
  "name": "홍길동",
  "phoneNumber": "010-9999-8888"
}
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "id": "user_123",
    "email": "user@example.com",
    "name": "홍길동",
    "phoneNumber": "010-9999-8888",
    "profileImage": "https://cdn.example.com/profiles/user_123.jpg",
    "updatedAt": "2024-01-15T11:00:00Z"
  },
  "message": "프로필이 업데이트되었습니다."
}
```

---

### 3.3 프로필 사진 업로드
```
POST /users/me/avatar
```
**인증 필요**: Yes
**Content-Type**: `multipart/form-data`

**Request Body**
```
avatar: (binary file)
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "profileImage": "https://cdn.example.com/profiles/user_123.jpg"
  },
  "message": "프로필 사진이 업데이트되었습니다."
}
```

---

### 3.4 비밀번호 변경
```
PUT /users/me/password
```
**인증 필요**: Yes

**Request Body**
```json
{
  "currentPassword": "OldPassword123!",
  "newPassword": "NewPassword123!"
}
```

**Response (200 OK)**
```json
{
  "success": true,
  "message": "비밀번호가 변경되었습니다."
}
```

---

### 3.5 회원 탈퇴
```
DELETE /users/me
```
**인증 필요**: Yes

**Request Body**
```json
{
  "password": "Password123!"
}
```

**Response (200 OK)**
```json
{
  "success": true,
  "message": "회원 탈퇴가 완료되었습니다."
}
```

---

## 4. 식당 (Restaurants)

### 4.1 식당 목록 조회
```
GET /restaurants
```

**Query Parameters**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| page | number | No | 페이지 번호 (기본: 1) |
| limit | number | No | 페이지당 항목 수 (기본: 20, 최대: 50) |
| category | string | No | 카테고리 (한식, 중식, 일식, 양식, 카페 등) |
| search | string | No | 검색어 (식당명, 지역, 메뉴) |
| minPrice | number | No | 최소 가격대 (1-4) |
| maxPrice | number | No | 최대 가격대 (1-4) |
| minRating | number | No | 최소 평점 (1-5) |
| sort | string | No | 정렬 (popular, rating, distance, newest) |
| lat | number | No | 위도 (거리순 정렬 시 필요) |
| lng | number | No | 경도 (거리순 정렬 시 필요) |

**Example Request**
```
GET /restaurants?page=1&limit=20&category=한식&sort=rating&minRating=4
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "restaurants": [
      {
        "id": "rest_123",
        "name": "맛있는 한식당",
        "category": "한식",
        "description": "정통 한식을 제공하는 식당입니다.",
        "address": "서울시 강남구 테헤란로 123",
        "location": {
          "latitude": 37.5012345,
          "longitude": 127.0398765
        },
        "phoneNumber": "02-1234-5678",
        "priceRange": 3,
        "rating": 4.5,
        "reviewCount": 128,
        "thumbnailImage": "https://cdn.example.com/restaurants/rest_123_thumb.jpg",
        "isFavorite": false,
        "distance": 1.2
      }
    ],
    "pagination": {
      "currentPage": 1,
      "totalPages": 10,
      "totalCount": 200,
      "hasNext": true,
      "hasPrev": false
    }
  }
}
```

---

### 4.2 식당 상세 조회
```
GET /restaurants/:id
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "id": "rest_123",
    "name": "맛있는 한식당",
    "category": "한식",
    "description": "정통 한식을 제공하는 식당입니다. 30년 전통의 가정식 한식당으로...",
    "address": "서울시 강남구 테헤란로 123",
    "location": {
      "latitude": 37.5012345,
      "longitude": 127.0398765
    },
    "phoneNumber": "02-1234-5678",
    "openingHours": {
      "monday": { "open": "11:00", "close": "22:00" },
      "tuesday": { "open": "11:00", "close": "22:00" },
      "wednesday": { "open": "11:00", "close": "22:00" },
      "thursday": { "open": "11:00", "close": "22:00" },
      "friday": { "open": "11:00", "close": "23:00" },
      "saturday": { "open": "11:00", "close": "23:00" },
      "sunday": null
    },
    "priceRange": 3,
    "rating": 4.5,
    "reviewCount": 128,
    "images": [
      "https://cdn.example.com/restaurants/rest_123_1.jpg",
      "https://cdn.example.com/restaurants/rest_123_2.jpg"
    ],
    "menus": [
      {
        "id": "menu_1",
        "name": "김치찌개",
        "description": "돼지고기와 김치가 들어간 찌개",
        "price": 9000,
        "image": "https://cdn.example.com/menus/menu_1.jpg",
        "isSignature": true
      }
    ],
    "facilities": ["parking", "wifi", "group_seat", "pet_friendly"],
    "isFavorite": true,
    "recentReviews": [
      {
        "id": "review_1",
        "user": {
          "name": "김철수",
          "profileImage": "https://cdn.example.com/profiles/user_456.jpg"
        },
        "rating": 5,
        "content": "정말 맛있었습니다!",
        "images": ["https://cdn.example.com/reviews/review_1_1.jpg"],
        "createdAt": "2024-01-14T18:00:00Z"
      }
    ],
    "createdAt": "2023-06-01T10:00:00Z",
    "updatedAt": "2024-01-15T10:00:00Z"
  }
}
```

---

### 4.3 주변 식당 조회
```
GET /restaurants/nearby
```

**Query Parameters**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| lat | number | Yes | 위도 |
| lng | number | Yes | 경도 |
| radius | number | No | 반경 (km, 기본: 5, 최대: 20) |
| category | string | No | 카테고리 필터 |
| limit | number | No | 최대 결과 수 (기본: 20) |

**Example Request**
```
GET /restaurants/nearby?lat=37.5012345&lng=127.0398765&radius=3&limit=10
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "restaurants": [
      {
        "id": "rest_123",
        "name": "맛있는 한식당",
        "category": "한식",
        "address": "서울시 강남구 테헤란로 123",
        "location": {
          "latitude": 37.5012345,
          "longitude": 127.0398765
        },
        "priceRange": 3,
        "rating": 4.5,
        "reviewCount": 128,
        "thumbnailImage": "https://cdn.example.com/restaurants/rest_123_thumb.jpg",
        "distance": 0.5,
        "isFavorite": false
      }
    ],
    "count": 10
  }
}
```

---

### 4.4 카테고리 목록 조회
```
GET /categories
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": [
    { "id": "cat_1", "name": "한식", "icon": "🍚" },
    { "id": "cat_2", "name": "중식", "icon": "🥢" },
    { "id": "cat_3", "name": "일식", "icon": "🍱" },
    { "id": "cat_4", "name": "양식", "icon": "🍝" },
    { "id": "cat_5", "name": "카페", "icon": "☕" }
  ]
}
```

---

## 5. 예약 (Reservations)

### 5.1 예약 가능 시간 조회
```
GET /restaurants/:id/availability
```

**Query Parameters**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| date | string | Yes | 날짜 (YYYY-MM-DD) |
| partySize | number | No | 인원 수 (기본: 2) |

**Example Request**
```
GET /restaurants/rest_123/availability?date=2024-01-20&partySize=4
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "date": "2024-01-20",
    "availableSlots": [
      {
        "time": "12:00",
        "available": true,
        "remainingSeats": 5
      },
      {
        "time": "12:30",
        "available": true,
        "remainingSeats": 3
      },
      {
        "time": "13:00",
        "available": false,
        "remainingSeats": 0
      },
      {
        "time": "18:00",
        "available": true,
        "remainingSeats": 8
      }
    ]
  }
}
```

---

### 5.2 예약 생성
```
POST /reservations
```
**인증 필요**: Yes

**Request Body**
```json
{
  "restaurantId": "rest_123",
  "date": "2024-01-20",
  "time": "18:00",
  "partySize": 4,
  "specialRequests": "창가 자리 부탁드립니다."
}
```

**Response (201 Created)**
```json
{
  "success": true,
  "data": {
    "id": "resv_123",
    "restaurantId": "rest_123",
    "restaurant": {
      "name": "맛있는 한식당",
      "address": "서울시 강남구 테헤란로 123",
      "phoneNumber": "02-1234-5678",
      "thumbnailImage": "https://cdn.example.com/restaurants/rest_123_thumb.jpg"
    },
    "userId": "user_123",
    "date": "2024-01-20",
    "time": "18:00",
    "partySize": 4,
    "status": "pending",
    "specialRequests": "창가 자리 부탁드립니다.",
    "createdAt": "2024-01-15T14:00:00Z"
  },
  "message": "예약이 요청되었습니다. 식당 확인 후 확정됩니다."
}
```

---

### 5.3 예약 목록 조회
```
GET /reservations
```
**인증 필요**: Yes

**Query Parameters**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| status | string | No | 상태 필터 (pending, confirmed, completed, cancelled) |
| page | number | No | 페이지 번호 (기본: 1) |
| limit | number | No | 페이지당 항목 수 (기본: 20) |

**Example Request**
```
GET /reservations?status=confirmed&page=1&limit=10
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "reservations": [
      {
        "id": "resv_123",
        "restaurantId": "rest_123",
        "restaurant": {
          "name": "맛있는 한식당",
          "address": "서울시 강남구 테헤란로 123",
          "phoneNumber": "02-1234-5678",
          "thumbnailImage": "https://cdn.example.com/restaurants/rest_123_thumb.jpg"
        },
        "date": "2024-01-20",
        "time": "18:00",
        "partySize": 4,
        "status": "confirmed",
        "specialRequests": "창가 자리 부탁드립니다.",
        "createdAt": "2024-01-15T14:00:00Z",
        "updatedAt": "2024-01-15T15:00:00Z"
      }
    ],
    "pagination": {
      "currentPage": 1,
      "totalPages": 3,
      "totalCount": 25,
      "hasNext": true,
      "hasPrev": false
    }
  }
}
```

---

### 5.4 예약 상세 조회
```
GET /reservations/:id
```
**인증 필요**: Yes

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "id": "resv_123",
    "restaurantId": "rest_123",
    "restaurant": {
      "id": "rest_123",
      "name": "맛있는 한식당",
      "address": "서울시 강남구 테헤란로 123",
      "location": {
        "latitude": 37.5012345,
        "longitude": 127.0398765
      },
      "phoneNumber": "02-1234-5678",
      "thumbnailImage": "https://cdn.example.com/restaurants/rest_123_thumb.jpg"
    },
    "userId": "user_123",
    "date": "2024-01-20",
    "time": "18:00",
    "partySize": 4,
    "status": "confirmed",
    "specialRequests": "창가 자리 부탁드립니다.",
    "canCancel": true,
    "canModify": true,
    "canReview": false,
    "createdAt": "2024-01-15T14:00:00Z",
    "updatedAt": "2024-01-15T15:00:00Z"
  }
}
```

---

### 5.5 예약 수정
```
PUT /reservations/:id
```
**인증 필요**: Yes

**Request Body**
```json
{
  "date": "2024-01-21",
  "time": "19:00",
  "partySize": 5,
  "specialRequests": "조용한 자리 부탁드립니다."
}
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "id": "resv_123",
    "restaurantId": "rest_123",
    "date": "2024-01-21",
    "time": "19:00",
    "partySize": 5,
    "status": "pending",
    "specialRequests": "조용한 자리 부탁드립니다.",
    "updatedAt": "2024-01-15T16:00:00Z"
  },
  "message": "예약이 수정되었습니다. 식당 재확인이 필요합니다."
}
```

---

### 5.6 예약 취소
```
DELETE /reservations/:id
```
**인증 필요**: Yes

**Request Body** (선택사항)
```json
{
  "reason": "개인 사정으로 취소합니다."
}
```

**Response (200 OK)**
```json
{
  "success": true,
  "message": "예약이 취소되었습니다."
}
```

---

## 6. 찜하기 (Favorites)

### 6.1 찜하기
```
POST /favorites
```
**인증 필요**: Yes

**Request Body**
```json
{
  "restaurantId": "rest_123"
}
```

**Response (201 Created)**
```json
{
  "success": true,
  "data": {
    "id": "fav_123",
    "restaurantId": "rest_123",
    "userId": "user_123",
    "createdAt": "2024-01-15T14:00:00Z"
  },
  "message": "찜 목록에 추가되었습니다."
}
```

---

### 6.2 찜 취소
```
DELETE /favorites/:restaurantId
```
**인증 필요**: Yes

**Response (200 OK)**
```json
{
  "success": true,
  "message": "찜 목록에서 제거되었습니다."
}
```

---

### 6.3 찜 목록 조회
```
GET /favorites
```
**인증 필요**: Yes

**Query Parameters**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| page | number | No | 페이지 번호 (기본: 1) |
| limit | number | No | 페이지당 항목 수 (기본: 20) |
| sort | string | No | 정렬 (recent, rating) |

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "favorites": [
      {
        "id": "fav_123",
        "restaurant": {
          "id": "rest_123",
          "name": "맛있는 한식당",
          "category": "한식",
          "address": "서울시 강남구 테헤란로 123",
          "priceRange": 3,
          "rating": 4.5,
          "reviewCount": 128,
          "thumbnailImage": "https://cdn.example.com/restaurants/rest_123_thumb.jpg"
        },
        "createdAt": "2024-01-15T14:00:00Z"
      }
    ],
    "pagination": {
      "currentPage": 1,
      "totalPages": 2,
      "totalCount": 15,
      "hasNext": true,
      "hasPrev": false
    }
  }
}
```

---

### 6.4 찜 여부 확인
```
GET /restaurants/:id/is-favorite
```
**인증 필요**: Yes

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "isFavorite": true
  }
}
```

---

## 7. 리뷰 (Reviews)

### 7.1 리뷰 작성
```
POST /reviews
```
**인증 필요**: Yes

**Request Body**
```json
{
  "restaurantId": "rest_123",
  "reservationId": "resv_123",
  "rating": 4.5,
  "content": "음식이 정말 맛있었습니다. 분위기도 좋고 서비스도 훌륭했어요!",
  "wouldRecommend": true,
  "visitDate": "2024-01-20"
}
```

**Response (201 Created)**
```json
{
  "success": true,
  "data": {
    "id": "review_123",
    "restaurantId": "rest_123",
    "userId": "user_123",
    "user": {
      "name": "홍길동",
      "profileImage": "https://cdn.example.com/profiles/user_123.jpg"
    },
    "reservationId": "resv_123",
    "rating": 4.5,
    "content": "음식이 정말 맛있었습니다. 분위기도 좋고 서비스도 훌륭했어요!",
    "images": [],
    "visitDate": "2024-01-20",
    "wouldRecommend": true,
    "likeCount": 0,
    "isLiked": false,
    "createdAt": "2024-01-21T10:00:00Z"
  },
  "message": "리뷰가 작성되었습니다."
}
```

---

### 7.2 리뷰 이미지 업로드
```
POST /reviews/:id/images
```
**인증 필요**: Yes
**Content-Type**: `multipart/form-data`

**Request Body**
```
images: (binary files, 최대 5개)
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "images": [
      "https://cdn.example.com/reviews/review_123_1.jpg",
      "https://cdn.example.com/reviews/review_123_2.jpg"
    ]
  },
  "message": "이미지가 업로드되었습니다."
}
```

---

### 7.3 리뷰 목록 조회
```
GET /reviews
```

**Query Parameters**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| restaurantId | string | No | 식당 ID (특정 식당의 리뷰) |
| userId | string | No | 사용자 ID (특정 사용자의 리뷰) |
| page | number | No | 페이지 번호 (기본: 1) |
| limit | number | No | 페이지당 항목 수 (기본: 20) |
| sort | string | No | 정렬 (recent, rating_high, rating_low) |
| rating | number | No | 평점 필터 (1-5) |
| withPhotos | boolean | No | 사진 있는 리뷰만 (true/false) |

**Example Request**
```
GET /reviews?restaurantId=rest_123&sort=recent&withPhotos=true&page=1&limit=10
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "reviews": [
      {
        "id": "review_123",
        "restaurantId": "rest_123",
        "userId": "user_123",
        "user": {
          "name": "홍길동",
          "profileImage": "https://cdn.example.com/profiles/user_123.jpg"
        },
        "rating": 4.5,
        "content": "음식이 정말 맛있었습니다!",
        "images": [
          "https://cdn.example.com/reviews/review_123_1.jpg"
        ],
        "visitDate": "2024-01-20",
        "wouldRecommend": true,
        "likeCount": 15,
        "isLiked": false,
        "createdAt": "2024-01-21T10:00:00Z",
        "updatedAt": "2024-01-21T10:00:00Z"
      }
    ],
    "pagination": {
      "currentPage": 1,
      "totalPages": 5,
      "totalCount": 128,
      "hasNext": true,
      "hasPrev": false
    },
    "statistics": {
      "averageRating": 4.3,
      "totalCount": 128,
      "ratingDistribution": {
        "5": 60,
        "4": 40,
        "3": 20,
        "2": 5,
        "1": 3
      }
    }
  }
}
```

---

### 7.4 리뷰 상세 조회
```
GET /reviews/:id
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "id": "review_123",
    "restaurantId": "rest_123",
    "restaurant": {
      "id": "rest_123",
      "name": "맛있는 한식당",
      "thumbnailImage": "https://cdn.example.com/restaurants/rest_123_thumb.jpg"
    },
    "userId": "user_123",
    "user": {
      "name": "홍길동",
      "profileImage": "https://cdn.example.com/profiles/user_123.jpg"
    },
    "reservationId": "resv_123",
    "rating": 4.5,
    "content": "음식이 정말 맛있었습니다!",
    "images": [
      "https://cdn.example.com/reviews/review_123_1.jpg"
    ],
    "visitDate": "2024-01-20",
    "wouldRecommend": true,
    "likeCount": 15,
    "isLiked": false,
    "createdAt": "2024-01-21T10:00:00Z",
    "updatedAt": "2024-01-21T10:00:00Z"
  }
}
```

---

### 7.5 리뷰 수정
```
PUT /reviews/:id
```
**인증 필요**: Yes (본인 리뷰만)

**Request Body**
```json
{
  "rating": 5,
  "content": "다시 방문했는데 역시 맛있네요!",
  "wouldRecommend": true
}
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "id": "review_123",
    "rating": 5,
    "content": "다시 방문했는데 역시 맛있네요!",
    "wouldRecommend": true,
    "updatedAt": "2024-01-22T10:00:00Z"
  },
  "message": "리뷰가 수정되었습니다."
}
```

---

### 7.6 리뷰 삭제
```
DELETE /reviews/:id
```
**인증 필요**: Yes (본인 리뷰만)

**Response (200 OK)**
```json
{
  "success": true,
  "message": "리뷰가 삭제되었습니다."
}
```

---

### 7.7 리뷰 좋아요
```
POST /reviews/:id/like
```
**인증 필요**: Yes

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "isLiked": true,
    "likeCount": 16
  },
  "message": "리뷰에 좋아요를 눌렀습니다."
}
```

---

### 7.8 리뷰 좋아요 취소
```
DELETE /reviews/:id/like
```
**인증 필요**: Yes

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "isLiked": false,
    "likeCount": 15
  },
  "message": "좋아요가 취소되었습니다."
}
```

---

### 7.9 내 리뷰 목록 조회
```
GET /users/me/reviews
```
**인증 필요**: Yes

**Query Parameters**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| page | number | No | 페이지 번호 (기본: 1) |
| limit | number | No | 페이지당 항목 수 (기본: 20) |

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "reviews": [
      {
        "id": "review_123",
        "restaurant": {
          "id": "rest_123",
          "name": "맛있는 한식당",
          "thumbnailImage": "https://cdn.example.com/restaurants/rest_123_thumb.jpg"
        },
        "rating": 4.5,
        "content": "음식이 정말 맛있었습니다!",
        "images": ["https://cdn.example.com/reviews/review_123_1.jpg"],
        "visitDate": "2024-01-20",
        "likeCount": 15,
        "createdAt": "2024-01-21T10:00:00Z"
      }
    ],
    "pagination": {
      "currentPage": 1,
      "totalPages": 2,
      "totalCount": 5,
      "hasNext": true,
      "hasPrev": false
    }
  }
}
```

---

## 8. 공통 응답 형식

### 8.1 성공 응답
```json
{
  "success": true,
  "data": {},
  "message": "Success message"
}
```

### 8.2 에러 응답
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

## 9. 에러 코드

### 9.1 인증 관련 (AUTH)
| Code | HTTP Status | Description |
|------|-------------|-------------|
| AUTH_001 | 401 | 인증 토큰이 없습니다 |
| AUTH_002 | 401 | 유효하지 않은 토큰입니다 |
| AUTH_003 | 401 | 토큰이 만료되었습니다 |
| AUTH_004 | 400 | 이메일 또는 비밀번호가 올바르지 않습니다 |
| AUTH_005 | 409 | 이미 존재하는 이메일입니다 |
| AUTH_006 | 403 | 권한이 없습니다 |

### 9.2 유효성 검사 (VALIDATION)
| Code | HTTP Status | Description |
|------|-------------|-------------|
| VAL_001 | 400 | 필수 필드가 누락되었습니다 |
| VAL_002 | 400 | 유효하지 않은 형식입니다 |
| VAL_003 | 400 | 값의 범위를 벗어났습니다 |

### 9.3 리소스 (RESOURCE)
| Code | HTTP Status | Description |
|------|-------------|-------------|
| RES_001 | 404 | 리소스를 찾을 수 없습니다 |
| RES_002 | 409 | 이미 존재하는 리소스입니다 |

### 9.4 예약 관련 (RESERVATION)
| Code | HTTP Status | Description |
|------|-------------|-------------|
| RESV_001 | 400 | 예약 가능한 시간이 아닙니다 |
| RESV_002 | 400 | 예약 최소 시간을 만족하지 않습니다 (1시간 전) |
| RESV_003 | 400 | 취소 가능 시간이 지났습니다 (2시간 전) |
| RESV_004 | 409 | 이미 예약이 존재합니다 |
| RESV_005 | 400 | 예약을 수정할 수 없습니다 |

### 9.5 리뷰 관련 (REVIEW)
| Code | HTTP Status | Description |
|------|-------------|-------------|
| REV_001 | 403 | 방문 완료한 예약만 리뷰를 작성할 수 있습니다 |
| REV_002 | 409 | 이미 리뷰를 작성했습니다 |
| REV_003 | 400 | 리뷰 작성 기간이 지났습니다 (30일) |
| REV_004 | 400 | 이미지는 최대 5개까지 업로드 가능합니다 |

### 9.6 서버 에러 (SERVER)
| Code | HTTP Status | Description |
|------|-------------|-------------|
| SRV_001 | 500 | 내부 서버 오류가 발생했습니다 |
| SRV_002 | 503 | 서비스를 일시적으로 사용할 수 없습니다 |

---

## 10. 페이지네이션

모든 목록 API는 다음과 같은 페이지네이션 형식을 따릅니다:

**Request Parameters**
```
page: 페이지 번호 (기본: 1)
limit: 페이지당 항목 수 (기본: 20, 최대: 50)
```

**Response**
```json
{
  "pagination": {
    "currentPage": 1,
    "totalPages": 10,
    "totalCount": 200,
    "hasNext": true,
    "hasPrev": false
  }
}
```

---

## 11. Rate Limiting

API 요청에는 다음과 같은 제한이 있습니다:
- 인증된 사용자: 100 requests/minute
- 미인증 사용자: 20 requests/minute

Rate Limit 초과 시:
```json
{
  "success": false,
  "message": "요청 제한을 초과했습니다. 잠시 후 다시 시도해주세요.",
  "error": {
    "code": "RATE_LIMIT_EXCEEDED",
    "retryAfter": 60
  }
}
```

---

## 12. 파일 업로드 제한

- 프로필 이미지: 최대 5MB, jpg/png
- 리뷰 이미지: 최대 10MB/개, 최대 5개, jpg/png

---

## 변경 이력
- 2024-01-15: 초안 작성
