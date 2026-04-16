# auth 모듈

회원가입, 로그인, JWT 발급, OAuth 소셜 로그인을 처리하는 모듈입니다.

## 폴더 구조

| 폴더 | 역할 |
|------|------|
| `controller/` | 인증 관련 API 엔드포인트 (회원가입, 로그인, 토큰 재발급) |
| `service/` | 인증 비즈니스 로직, JWT 생성/검증, OAuth 처리 |
| `dto/` | 로그인 요청/응답, 회원가입 요청/응답 DTO |
| `model/` | AuthAccounts, EmailVerifications 엔티티 |

## 지원 로그인 방식

- EMAIL (이메일/비밀번호)
- KAKAO (카카오 소셜)
- GOOGLE (구글 소셜)
- NAVER (네이버 소셜)

## 보안 규칙

- 비밀번호는 반드시 bcrypt 해시 저장
- JWT Secret은 환경변수로 분리
- 이메일 인증코드는 해시 저장
