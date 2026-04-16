# ChowChow-Back
컴과 프로젝트 백엔드

---

# pet-diet

AI 기반 반려동물 맞춤 식단 추천 및 커뮤니티 시스템입니다.

## 프로젝트 개요

OpenAI API와 Supabase를 활용하여 반려동물의 종, 품종, 나이, 체중, 알레르기, 질병 정보를 기반으로
맞춤형 식단을 추천하고 커뮤니티를 통해 정보를 공유하는 모바일 서비스입니다.

## 핵심 기능

| 기능 | 설명 |
|------|------|
| 식단 추천 | OpenAI + Spoonacular API 기반 맞춤 레시피 생성 |
| 커뮤니티 | 게시글, 댓글, 좋아요, 카테고리 분류 |
| 캐릭터 생성 | 반려동물 이미지 AI 캐릭터화 |
| 캐릭터 육성 | 활동 기반 경험치 및 레벨업 시스템 |

## 기술 스택

- **Backend**: Java 17, Spring Boot 4.x
- **Database**: Supabase (PostgreSQL)
- **Storage**: Supabase Storage
- **AI**: OpenAI API, Spoonacular API
- **Auth**: JWT, OAuth2 (카카오, 구글, 네이버)
- **Build**: Gradle

## 시작하기

### 1. 환경변수 설정

```bash
cp .env.example .env
# .env 파일을 열어 실제 값 입력
```

### 2. application.yml 설정

```bash
cp application-example.yml src/main/resources/application.yml
```

### 3. DB 초기화 (Supabase SQL Editor에서 실행)

```
# 1. 테이블 생성
src/main/resources/db/sql/tables/ 파일을 번호 순서대로 실행

# 2. RLS 정책 적용
src/main/resources/db/sql/rls/ 파일을 번호 순서대로 실행

# 3. 마스터 데이터 적재
src/main/resources/db/csv/ CSV 파일을 테이블 임포트로 적재
```

### 4. 서버 실행

```bash
./gradlew bootRun
```

## 프로젝트 구조

```
src/main/java/com/petdiet/
├── ai/          # AI 식단 추천 및 이미지 생성
├── auth/        # 회원가입, 로그인, JWT, OAuth
├── character/   # 반려동물 캐릭터 생성·육성
├── client/      # 외부 서비스 클라이언트 (Supabase)
├── common/      # 공통 응답·예외 처리
├── community/   # 커뮤니티 게시글·댓글
├── config/      # Spring 설정 클래스
├── notification/# 알림 시스템
├── pet/         # 반려동물 등록·관리
├── recipe/      # 레시피 조회·검색·리뷰
├── user/        # 사용자 프로필·설정
└── util/        # 공통 유틸리티
```

## 문서

- [프로젝트 제안서](docs/Project_Proposal.pdf)
- [ERD 설계](docs/ERD_Draft.pdf)
- [화면별 기능 정의서](docs/Screen_Function_definition.pdf)

## 개발 우선순위

1. 인증 시스템 (auth)
2. Supabase DB 연결 (client/supabase)
3. 반려동물 등록 (pet)
4. AI 식단 추천 (ai/diet)
5. 커뮤니티 (community)
6. 캐릭터 시스템 (character)
