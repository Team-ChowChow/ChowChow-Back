-- ================================================================
-- [008] 기타 테이블 (알림, 검색 로그, 급여 기록)
-- 포함 테이블: Notifications, SearchLogs, FeedingRecords
-- 의존성: 001_users.sql (Users), 004_pets.sql (UserPets),
--         005_recipes.sql (Recipes)
-- ================================================================

-- Notifications: 사용자 알림
CREATE TABLE "Notifications" (
    "notificationId"      SERIAL    PRIMARY KEY,
    "userId"              INTEGER      NOT NULL REFERENCES "Users"("userId") ON DELETE CASCADE,
    "notificationType"    VARCHAR(20)  NOT NULL CHECK ("notificationType" IN ('COMMENT', 'LIKE', 'RECIPE', 'SYSTEM')),
    "notificationTitle"   VARCHAR(200) NOT NULL,
    "notificationContent" TEXT         NULL,
    "targetType"          VARCHAR(20)  NULL CHECK ("targetType" IN ('POST', 'RECIPE', 'COMMENT')),
    "targetId"            INTEGER      NOT NULL,
    "isRead"              BOOLEAN      NOT NULL DEFAULT FALSE,
    "readAt"              TIMESTAMPTZ  NULL,
    "createdAt"           TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- 인덱스
CREATE INDEX idx_notifications_user_id ON "Notifications"("userId");
CREATE INDEX idx_notifications_is_read ON "Notifications"("isRead");

COMMENT ON TABLE  "Notifications"             IS '사용자 알림. 읽음 여부 관리 필요';
COMMENT ON COLUMN "Notifications"."targetType" IS 'POST / RECIPE / COMMENT 중 하나. targetId와 함께 대상 특정';
COMMENT ON COLUMN "Notifications"."targetId"   IS 'targetType에 따라 postId / recipeId / commentId. 다형성 참조로 FK 미설정';

-- SearchLogs: 검색 활동 로그 (추천/인기 검색어 기반 데이터)
CREATE TABLE "SearchLogs" (
    "searchLogId"   SERIAL    PRIMARY KEY,
    "userId"        INTEGER      NOT NULL REFERENCES "Users"("userId") ON DELETE SET NULL,
    "searchKeyword" VARCHAR(200) NOT NULL,
    "searchType"    VARCHAR(20)  NOT NULL CHECK ("searchType" IN ('KEYWORD', 'DISEASE', 'PURPOSE', 'FILTER')),
    "petType"       VARCHAR(10)  NULL CHECK ("petType" IN ('DOG', 'CAT')),
    "searchFilters" TEXT         NULL,
    "resultCount"   INT          NULL,
    "searchedAt"    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- 인덱스
CREATE INDEX idx_search_logs_user_id  ON "SearchLogs"("userId");
CREATE INDEX idx_search_logs_keyword  ON "SearchLogs"("searchKeyword");

COMMENT ON TABLE  "SearchLogs"             IS '검색 활동 데이터. 인기 검색어, 맞춤 추천 활용';
COMMENT ON COLUMN "SearchLogs"."userId"    IS 'NULL 허용: 비로그인 사용자 검색 기록 포함';
COMMENT ON COLUMN "SearchLogs"."searchFilters" IS 'JSON 문자열로 필터 조건 저장 (예: {"petType":"DOG","disease":"신장병"})';

-- FeedingRecords: 반려동물 급여 기록
CREATE TABLE "FeedingRecords" (
    "feedingRecordId" SERIAL    PRIMARY KEY,
    "petId"           INTEGER      NOT NULL REFERENCES "UserPets"("petId")  ON DELETE CASCADE,
    "recipeId"        INTEGER      NOT NULL     REFERENCES "Recipes"("recipeId") ON DELETE SET NULL,
    "feedingAmount"   VARCHAR(100) NULL,
    "feedingNote"     TEXT         NULL,
    "fedAt"           TIMESTAMPTZ  NOT NULL,
    "createdAt"       TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- 인덱스
CREATE INDEX idx_feeding_records_pet_id    ON "FeedingRecords"("petId");
CREATE INDEX idx_feeding_records_recipe_id ON "FeedingRecords"("recipeId");
CREATE INDEX idx_feeding_records_fed_at    ON "FeedingRecords"("fedAt" DESC);

COMMENT ON TABLE  "FeedingRecords"           IS '급여 기록. recipeId NULL 허용 (레시피 없이 직접 입력한 급여도 기록 가능)';
COMMENT ON COLUMN "FeedingRecords"."recipeId" IS 'NULL=직접 입력 급여 / 값 있음=레시피 기반 급여';