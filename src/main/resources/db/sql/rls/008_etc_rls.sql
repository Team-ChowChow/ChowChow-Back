-- ================================================================
-- [008] 기타 테이블 RLS 정책
-- 포함 테이블: Notifications, SearchLogs, FeedingRecords
-- 의존성: 000_rls_helper.sql (get_current_user_id 함수)
--
-- [핵심 정책]
-- Notifications  : 조회/읽음처리는 본인만, 생성은 서버만
-- SearchLogs     : 본인 검색 기록만 CRUD
-- FeedingRecords : 본인 반려동물 급여 기록만 CRUD
-- ================================================================

-- ----------------------------------------------------------------
-- Notifications: 알림 생성은 서버만, 조회/읽음처리는 본인만
-- ----------------------------------------------------------------

ALTER TABLE "Notifications" ENABLE ROW LEVEL SECURITY;

-- SELECT: 본인 알림만 조회
CREATE POLICY "notifications_select_own"
    ON "Notifications" FOR SELECT
    TO authenticated
    USING ("userId" = get_current_user_id());

-- UPDATE: 본인 알림 읽음 처리(isRead)만 가능
CREATE POLICY "notifications_update_own"
    ON "Notifications" FOR UPDATE
    TO authenticated
    USING ("userId" = get_current_user_id())
    WITH CHECK ("userId" = get_current_user_id());

-- INSERT: Spring Boot 서버(service_role)에서만 처리 (알림 생성 권한 제한)

-- ----------------------------------------------------------------
-- SearchLogs: 본인 검색 기록만 CRUD
-- ----------------------------------------------------------------

ALTER TABLE "SearchLogs" ENABLE ROW LEVEL SECURITY;

CREATE POLICY "search_logs_select_own"
    ON "SearchLogs" FOR SELECT
    TO authenticated
    USING ("userId" = get_current_user_id());

CREATE POLICY "search_logs_insert_own"
    ON "SearchLogs" FOR INSERT
    TO authenticated
    WITH CHECK ("userId" = get_current_user_id());

CREATE POLICY "search_logs_delete_own"
    ON "SearchLogs" FOR DELETE
    TO authenticated
    USING ("userId" = get_current_user_id());

-- ----------------------------------------------------------------
-- FeedingRecords: 본인 반려동물 급여 기록만 CRUD
-- ----------------------------------------------------------------

ALTER TABLE "FeedingRecords" ENABLE ROW LEVEL SECURITY;

CREATE POLICY "feeding_records_select_own"
    ON "FeedingRecords" FOR SELECT
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM "UserPets" up
            WHERE up."petId" = "FeedingRecords"."petId"
              AND up."userId" = get_current_user_id()
        )
    );

CREATE POLICY "feeding_records_insert_own"
    ON "FeedingRecords" FOR INSERT
    TO authenticated
    WITH CHECK (
        EXISTS (
            SELECT 1 FROM "UserPets" up
            WHERE up."petId" = "FeedingRecords"."petId"
              AND up."userId" = get_current_user_id()
        )
    );

CREATE POLICY "feeding_records_update_own"
    ON "FeedingRecords" FOR UPDATE
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM "UserPets" up
            WHERE up."petId" = "FeedingRecords"."petId"
              AND up."userId" = get_current_user_id()
        )
    )
    WITH CHECK (
        EXISTS (
            SELECT 1 FROM "UserPets" up
            WHERE up."petId" = "FeedingRecords"."petId"
              AND up."userId" = get_current_user_id()
        )
    );

CREATE POLICY "feeding_records_delete_own"
    ON "FeedingRecords" FOR DELETE
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM "UserPets" up
            WHERE up."petId" = "FeedingRecords"."petId"
              AND up."userId" = get_current_user_id()
        )
    );
