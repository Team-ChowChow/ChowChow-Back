-- ================================================================
-- [007] 캐릭터 관련 테이블
-- 포함 테이블: PetCharacters, CharacterGrowthLogs, CharacterAssets
-- 의존성: 001_users.sql (Users), 004_pets.sql (UserPets)
-- ================================================================

-- PetCharacters: 반려동물 기반 캐릭터 (반려동물당 1개)
CREATE TABLE "PetCharacters" (
    "characterId"       SERIAL    PRIMARY KEY,
    "petId"             INTEGER      UNIQUE NOT NULL REFERENCES "UserPets"("petId") ON DELETE CASCADE,
    "characterName"     VARCHAR(100) NULL,
    "characterImageUrl" TEXT         NOT NULL,
    "characterLevel"    INT          NOT NULL DEFAULT 1,
    "currentExp"        INT          NOT NULL DEFAULT 0,
    "characterStatus"   VARCHAR(10)  NOT NULL DEFAULT 'ACTIVE'
                            CHECK ("characterStatus" IN ('ACTIVE', 'HIDDEN')),
    "createdAt"         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    "updatedAt"         TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE  "PetCharacters"                    IS '반려동물 캐릭터. UNIQUE(petId) 반려동물당 캐릭터 1개';
COMMENT ON COLUMN "PetCharacters"."characterImageUrl" IS 'AI 생성 캐릭터 이미지. Supabase Storage URL (character-images 버킷)';
COMMENT ON COLUMN "PetCharacters"."currentExp"        IS '현재 누적 경험치. 레벨업 조건은 앱 로직에서 관리';

-- CharacterGrowthLogs: 캐릭터 경험치 획득 기록
-- 활동 종류별 경험치: RECIPE_USE=+10, COMMUNITY_POST=+15,
--                   COMMENT=+5, FEEDING=+20
CREATE TABLE "CharacterGrowthLogs" (
    "growthLogId"         SERIAL    PRIMARY KEY,
    "characterId"         INTEGER      NOT NULL REFERENCES "PetCharacters"("characterId") ON DELETE CASCADE,
    "userId"              INTEGER      NOT NULL REFERENCES "Users"("userId")              ON DELETE CASCADE,
    "activityType"        VARCHAR(30)  NOT NULL
                              CHECK ("activityType" IN ('RECIPE_USE', 'COMMUNITY_POST', 'COMMENT', 'FEEDING')),
    "expAmount"           INT          NOT NULL DEFAULT 0,
    "activityDescription" VARCHAR(200) NULL,
    "createdAt"           TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- 인덱스
CREATE INDEX idx_growth_logs_character_id ON "CharacterGrowthLogs"("characterId");

COMMENT ON TABLE  "CharacterGrowthLogs"                IS '활동 기반 경험치 기록. 게임화 요소 활용';
COMMENT ON COLUMN "CharacterGrowthLogs"."activityType" IS 'RECIPE_USE=식단추천이용 / COMMUNITY_POST=게시글작성 / COMMENT=댓글작성 / FEEDING=식단기록';
COMMENT ON COLUMN "CharacterGrowthLogs"."expAmount"    IS '획득 경험치량. 기준: RECIPE_USE=10, POST=15, COMMENT=5, FEEDING=20';

-- CharacterAssets: 캐릭터 스킨/아이템 관리
CREATE TABLE "CharacterAssets" (
    "assetId"       SERIAL    PRIMARY KEY,
    "characterId"   INTEGER      NOT NULL REFERENCES "PetCharacters"("characterId") ON DELETE CASCADE,
    "assetType"     VARCHAR(20)  NOT NULL CHECK ("assetType" IN ('SKIN', 'ITEM', 'FRAME', 'BACKGROUND')),
    "assetName"     VARCHAR(100) NOT NULL,
    "assetImageUrl" TEXT         NULL,
    "isOwned"       BOOLEAN      NOT NULL DEFAULT FALSE,
    "isEquipped"    BOOLEAN      NOT NULL DEFAULT FALSE,
    "createdAt"     TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- 인덱스
CREATE INDEX idx_character_assets_character_id ON "CharacterAssets"("characterId");

COMMENT ON TABLE "CharacterAssets" IS '캐릭터 에셋. 스킨/아이템 확장 가능';