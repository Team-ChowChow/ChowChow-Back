-- ================================================================
-- [009] RLS Helper 함수
-- 의존성: 001_users.sql (Users 테이블)
-- 실행 순서: 반드시 모든 _rls.sql 파일보다 먼저 실행
--
-- [설명]
-- auth.uid() → Users.userId 변환 함수
-- 모든 RLS 정책 파일에서 공통으로 사용하는 핵심 함수
--
-- SECURITY DEFINER : 함수 소유자 권한으로 실행
--                    (RLS를 우회하여 userId 안전하게 조회)
-- STABLE           : 같은 트랜잭션 내 동일 결과 보장 (성능 최적화)
-- ================================================================

CREATE OR REPLACE FUNCTION get_current_user_id()
RETURNS INTEGER
LANGUAGE SQL
SECURITY DEFINER
STABLE
AS $$
    SELECT "userId"
    FROM "Users"
    WHERE "authUuid" = auth.uid()
    LIMIT 1;
$$;
