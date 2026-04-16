# client/supabase 모듈

Supabase의 Auth, Database, Storage 기능과 연동하는 클라이언트 모듈입니다.

## 폴더 구조

| 폴더 | 역할 |
|------|------|
| `auth/` | Supabase Auth 연동 (소셜 로그인, 세션 관리) |
| `database/` | Supabase PostgreSQL 연동 클라이언트 |
| `storage/` | Supabase Storage 연동 클라이언트 |

## 환경변수 (필수)

```
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_ANON_KEY=your-anon-key
SUPABASE_SERVICE_ROLE_KEY=your-service-role-key
```

## 주의사항

- `SUPABASE_SERVICE_ROLE_KEY`는 서버 사이드 전용으로만 사용
- 클라이언트에 절대 노출 금지
