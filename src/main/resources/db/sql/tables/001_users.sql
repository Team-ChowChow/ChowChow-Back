-- ================================================================
-- [001] 사용자 관련 테이블
-- 포함 테이블: Users, UserSettings
-- 의존성: auth.users (Supabase 내장 인증 테이블)
-- ================================================================

-- Users: 앱의 기본 사용자 정보 (모든 서비스의 중심 엔티티)
CREATE TABLE "Users" (
    "userId"          SERIAL       PRIMARY KEY,
    "authUuid"        UUID            UNIQUE NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    "userName"        VARCHAR(100)    NOT NULL,
    "userNickname"    VARCHAR(50)     UNIQUE NOT NULL,
    "userProfileImg"  TEXT            NULL,
    "userStatus"      VARCHAR(20)     NOT NULL DEFAULT 'PENDING'
                          CHECK ("userStatus" IN ('PENDING', 'ACTIVE', 'SUSPENDED', 'WITHDRAWN')),
    "userCreatedAt"   TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    "userUpdatedAt"   TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- 인덱스
CREATE INDEX idx_users_auth_uuid ON "Users"("authUuid");
CREATE INDEX idx_users_status    ON "Users"("userStatus");

COMMENT ON TABLE  "Users"                IS '앱의 기본 사용자 정보. 모든 테이블에서 참조되는 중심 엔티티';
COMMENT ON COLUMN "Users"."authUuid"     IS 'Supabase auth.users(id)와 1:1 연결. 로그인 후 UUID로 조회';
COMMENT ON COLUMN "Users"."userProfileImg" IS 'Supabase Storage URL';
COMMENT ON COLUMN "Users"."userStatus"  IS 'PENDING=가입대기 / ACTIVE=활성 / SUSPENDED=정지 / WITHDRAWN=탈퇴';

-- UserSettings: 사용자 앱 설정 및 동의 상태 관리 (Users 1:1)
CREATE TABLE "UserSettings" (
    "userId"                  INTEGER         PRIMARY KEY REFERENCES "Users"("userId") ON DELETE CASCADE,
    "isNotificationEnabled"   BOOLEAN         NOT NULL DEFAULT TRUE,
    "isDarkMode"              BOOLEAN         NOT NULL DEFAULT FALSE,
    "isSearchHistoryEnabled"  BOOLEAN         NOT NULL DEFAULT TRUE,
    "isPersonalInfoAgreed"    BOOLEAN         NOT NULL DEFAULT FALSE,
    "personalInfoAgreedAt"    TIMESTAMPTZ     NULL,
    "createdAt"               TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    "updatedAt"               TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE "UserSettings" IS '사용자당 설정 1개 보장. Users와 1:1 관계';