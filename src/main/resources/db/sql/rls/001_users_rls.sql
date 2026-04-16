-- ================================================================
-- [001] Users / UserSettings RLS 정책
-- 의존성: 000_rls_helper.sql (get_current_user_id 함수)
-- ================================================================

ALTER TABLE "Users" ENABLE ROW LEVEL SECURITY;

-- SELECT: 로그인한 사용자라면 모든 유저 기본 정보 조회 가능
--         (커뮤니티에서 닉네임, 프로필 이미지 표시 필요)
CREATE POLICY "users_select"
    ON "Users" FOR SELECT
    TO authenticated
    USING (true);

-- UPDATE: 본인 정보만 수정 가능
CREATE POLICY "users_update_own"
    ON "Users" FOR UPDATE
    TO authenticated
    USING ("userId" = get_current_user_id())
    WITH CHECK ("userId" = get_current_user_id());

-- DELETE: 본인 계정만 삭제 가능
CREATE POLICY "users_delete_own"
    ON "Users" FOR DELETE
    TO authenticated
    USING ("userId" = get_current_user_id());

-- INSERT: 클라이언트 직접 생성 불가
--         회원가입 시 Spring Boot 서버(service_role)에서만 처리

-- ----------------------------------------------------------------

ALTER TABLE "UserSettings" ENABLE ROW LEVEL SECURITY;

CREATE POLICY "user_settings_select_own"
    ON "UserSettings" FOR SELECT
    TO authenticated
    USING ("userId" = get_current_user_id());

CREATE POLICY "user_settings_insert_own"
    ON "UserSettings" FOR INSERT
    TO authenticated
    WITH CHECK ("userId" = get_current_user_id());

CREATE POLICY "user_settings_update_own"
    ON "UserSettings" FOR UPDATE
    TO authenticated
    USING ("userId" = get_current_user_id())
    WITH CHECK ("userId" = get_current_user_id());

CREATE POLICY "user_settings_delete_own"
    ON "UserSettings" FOR DELETE
    TO authenticated
    USING ("userId" = get_current_user_id());
