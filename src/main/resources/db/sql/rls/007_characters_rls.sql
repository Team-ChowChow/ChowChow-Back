-- ================================================================
-- [007] 캐릭터 관련 RLS 정책
-- 포함 테이블: PetCharacters, CharacterGrowthLogs, CharacterAssets
-- 의존성: 000_rls_helper.sql (get_current_user_id 함수)
--
-- [핵심 정책]
-- PetCharacters  : UserPets → PetCharacters 소유 확인
-- CharacterAssets: UserPets → PetCharacters → CharacterAssets 체인 확인
-- CharacterGrowthLogs: 경험치 조작 방지를 위해 INSERT는 서버만 허용
-- ================================================================

-- ----------------------------------------------------------------
-- PetCharacters: 본인 반려동물 캐릭터만 CRUD
-- ----------------------------------------------------------------

ALTER TABLE "PetCharacters" ENABLE ROW LEVEL SECURITY;

CREATE POLICY "pet_characters_select_own"
    ON "PetCharacters" FOR SELECT
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM "UserPets" up
            WHERE up."petId" = "PetCharacters"."petId"
              AND up."userId" = get_current_user_id()
        )
    );

CREATE POLICY "pet_characters_insert_own"
    ON "PetCharacters" FOR INSERT
    TO authenticated
    WITH CHECK (
        EXISTS (
            SELECT 1 FROM "UserPets" up
            WHERE up."petId" = "PetCharacters"."petId"
              AND up."userId" = get_current_user_id()
        )
    );

CREATE POLICY "pet_characters_update_own"
    ON "PetCharacters" FOR UPDATE
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM "UserPets" up
            WHERE up."petId" = "PetCharacters"."petId"
              AND up."userId" = get_current_user_id()
        )
    )
    WITH CHECK (
        EXISTS (
            SELECT 1 FROM "UserPets" up
            WHERE up."petId" = "PetCharacters"."petId"
              AND up."userId" = get_current_user_id()
        )
    );

-- ----------------------------------------------------------------
-- CharacterGrowthLogs: 조회만 본인, 경험치 INSERT는 서버만
-- ----------------------------------------------------------------

ALTER TABLE "CharacterGrowthLogs" ENABLE ROW LEVEL SECURITY;

-- SELECT: 본인 성장 기록만 조회
CREATE POLICY "character_growth_logs_select_own"
    ON "CharacterGrowthLogs" FOR SELECT
    TO authenticated
    USING ("userId" = get_current_user_id());

-- INSERT: Spring Boot 서버(service_role)에서만 처리
--         클라이언트 직접 경험치 추가 불가 (조작 방지)

-- ----------------------------------------------------------------
-- CharacterAssets: UserPets → PetCharacters 체인으로 소유 확인
-- ----------------------------------------------------------------

ALTER TABLE "CharacterAssets" ENABLE ROW LEVEL SECURITY;

CREATE POLICY "character_assets_select_own"
    ON "CharacterAssets" FOR SELECT
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM "PetCharacters" pc
            JOIN "UserPets" up ON up."petId" = pc."petId"
            WHERE pc."characterId" = "CharacterAssets"."characterId"
              AND up."userId" = get_current_user_id()
        )
    );

-- UPDATE: 스킨/아이템 장착 여부(isEquipped) 변경만 허용
CREATE POLICY "character_assets_update_own"
    ON "CharacterAssets" FOR UPDATE
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM "PetCharacters" pc
            JOIN "UserPets" up ON up."petId" = pc."petId"
            WHERE pc."characterId" = "CharacterAssets"."characterId"
              AND up."userId" = get_current_user_id()
        )
    )
    WITH CHECK (
        EXISTS (
            SELECT 1 FROM "PetCharacters" pc
            JOIN "UserPets" up ON up."petId" = pc."petId"
            WHERE pc."characterId" = "CharacterAssets"."characterId"
              AND up."userId" = get_current_user_id()
        )
    );

-- INSERT: Spring Boot 서버(service_role)에서만 처리 (아이템 지급)
