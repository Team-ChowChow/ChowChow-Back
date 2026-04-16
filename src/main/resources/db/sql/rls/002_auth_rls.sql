-- ================================================================
-- [002] AuthAccounts / EmailVerifications RLS 정책
-- 의존성: 000_rls_helper.sql (get_current_user_id 함수)
-- ================================================================

ALTER TABLE "AuthAccounts" ENABLE ROW LEVEL SECURITY;

-- SELECT: 본인 로그인 방식만 조회 가능
CREATE POLICY "auth_accounts_select_own"
    ON "AuthAccounts" FOR SELECT
    TO authenticated
    USING ("userId" = get_current_user_id());

-- INSERT/UPDATE/DELETE: Spring Boot 서버(service_role)에서만 처리

-- ----------------------------------------------------------------

ALTER TABLE "EmailVerifications" ENABLE ROW LEVEL SECURITY;

-- SELECT: 본인 AuthAccount에 연결된 인증 정보만 조회
CREATE POLICY "email_verifications_select_own"
    ON "EmailVerifications" FOR SELECT
    TO authenticated
    USING (
        EXISTS (
            SELECT 1
            FROM "AuthAccounts" aa
            WHERE aa."authId" = "EmailVerifications"."authId"
              AND aa."userId" = get_current_user_id()
        )
    );

-- INSERT/UPDATE: 인증코드 생성/검증은 Spring Boot 서버(service_role)에서만 처리
