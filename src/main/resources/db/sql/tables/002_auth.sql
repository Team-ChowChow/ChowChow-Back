-- ================================================================
-- [002] 인증 관련 테이블
-- 포함 테이블: AuthAccounts, EmailVerifications
-- 의존성: 001_users.sql (Users)
-- ================================================================

-- AuthAccounts: 사용자의 로그인 방식(이메일/소셜) 관리
CREATE TABLE "AuthAccounts" (
    "authId"           SERIAL       PRIMARY KEY,
    "userId"           INTEGER         NOT NULL REFERENCES "Users"("userId") ON DELETE CASCADE,
    "authProvider"     VARCHAR(20)     NOT NULL
                           CHECK ("authProvider" IN ('EMAIL', 'KAKAO', 'GOOGLE', 'NAVER')),
    "authEmail"        VARCHAR(255)    UNIQUE NULL,
    "authPassword"     TEXT            NULL,
    "providerUserId"   VARCHAR(255)    NULL,
    "authStatus"       VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE'
                           CHECK ("authStatus" IN ('ACTIVE', 'UNLINKED')),
    "authLoginAt"      TIMESTAMPTZ     NULL,
    "authCreatedAt"    TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    "authUpdatedAt"    TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- 인덱스
CREATE INDEX idx_auth_accounts_user_id  ON "AuthAccounts"("userId");
CREATE INDEX idx_auth_accounts_provider ON "AuthAccounts"("authProvider");

COMMENT ON TABLE  "AuthAccounts"                  IS '사용자 로그인 방식 관리. 한 Users에 여러 AuthAccounts 연결 가능';
COMMENT ON COLUMN "AuthAccounts"."authPassword"   IS 'bcrypt 해시값. 이메일 로그인 시에만 사용';
COMMENT ON COLUMN "AuthAccounts"."providerUserId" IS '소셜 로그인 제공자 고유 ID';

-- EmailVerifications: 이메일 인증 처리 테이블
CREATE TABLE "EmailVerifications" (
    "verificationId"       SERIAL       PRIMARY KEY,
    "authId"               INTEGER         NOT NULL REFERENCES "AuthAccounts"("authId") ON DELETE CASCADE,
    "verificationEmail"    VARCHAR(255)    NOT NULL,
    "verificationCodeHash" TEXT            NOT NULL,
    "verificationType"     VARCHAR(30)     NOT NULL
                               CHECK ("verificationType" IN ('SIGNUP', 'RESET_PASSWORD', 'CHANGE_EMAIL')),
    "verificationStatus"   VARCHAR(20)     NOT NULL DEFAULT 'PENDING'
                               CHECK ("verificationStatus" IN ('PENDING', 'VERIFIED', 'EXPIRED', 'FAILED')),
    "expiredAt"            TIMESTAMPTZ     NOT NULL,
    "verifiedAt"           TIMESTAMPTZ     NULL,
    "requestedAt"          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    "createdAt"            TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE  "EmailVerifications"                        IS '이메일 인증 처리. 인증코드는 해시로 저장';
COMMENT ON COLUMN "EmailVerifications"."verificationCodeHash" IS 'bcrypt 해시값. 원본 코드는 저장하지 않음';
COMMENT ON COLUMN "EmailVerifications"."expiredAt"            IS '인증 만료 시각. 보통 발급 후 5~10분';