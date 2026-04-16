-- ================================================================
-- [004] UserPets / PetAllergies / PetDiseases RLS 정책
-- 의존성: 000_rls_helper.sql (get_current_user_id 함수)
-- ================================================================

ALTER TABLE "UserPets"     ENABLE ROW LEVEL SECURITY;
ALTER TABLE "PetAllergies" ENABLE ROW LEVEL SECURITY;
ALTER TABLE "PetDiseases"  ENABLE ROW LEVEL SECURITY;

-- ----------------------------------------------------------------
-- UserPets: 본인 반려동물만 CRUD
-- ----------------------------------------------------------------

CREATE POLICY "user_pets_select_own"
    ON "UserPets" FOR SELECT
    TO authenticated
    USING ("userId" = get_current_user_id());

CREATE POLICY "user_pets_insert_own"
    ON "UserPets" FOR INSERT
    TO authenticated
    WITH CHECK ("userId" = get_current_user_id());

CREATE POLICY "user_pets_update_own"
    ON "UserPets" FOR UPDATE
    TO authenticated
    USING ("userId" = get_current_user_id())
    WITH CHECK ("userId" = get_current_user_id());

CREATE POLICY "user_pets_delete_own"
    ON "UserPets" FOR DELETE
    TO authenticated
    USING ("userId" = get_current_user_id());

-- ----------------------------------------------------------------
-- PetAllergies: 본인 반려동물에 속한 알러지만 CRUD
-- ----------------------------------------------------------------

CREATE POLICY "pet_allergies_select_own"
    ON "PetAllergies" FOR SELECT
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM "UserPets" up
            WHERE up."petId" = "PetAllergies"."petId"
              AND up."userId" = get_current_user_id()
        )
    );

CREATE POLICY "pet_allergies_insert_own"
    ON "PetAllergies" FOR INSERT
    TO authenticated
    WITH CHECK (
        EXISTS (
            SELECT 1 FROM "UserPets" up
            WHERE up."petId" = "PetAllergies"."petId"
              AND up."userId" = get_current_user_id()
        )
    );

CREATE POLICY "pet_allergies_delete_own"
    ON "PetAllergies" FOR DELETE
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM "UserPets" up
            WHERE up."petId" = "PetAllergies"."petId"
              AND up."userId" = get_current_user_id()
        )
    );

-- ----------------------------------------------------------------
-- PetDiseases: 본인 반려동물에 속한 질환만 CRUD
-- ----------------------------------------------------------------

CREATE POLICY "pet_diseases_select_own"
    ON "PetDiseases" FOR SELECT
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM "UserPets" up
            WHERE up."petId" = "PetDiseases"."petId"
              AND up."userId" = get_current_user_id()
        )
    );

CREATE POLICY "pet_diseases_insert_own"
    ON "PetDiseases" FOR INSERT
    TO authenticated
    WITH CHECK (
        EXISTS (
            SELECT 1 FROM "UserPets" up
            WHERE up."petId" = "PetDiseases"."petId"
              AND up."userId" = get_current_user_id()
        )
    );

CREATE POLICY "pet_diseases_delete_own"
    ON "PetDiseases" FOR DELETE
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM "UserPets" up
            WHERE up."petId" = "PetDiseases"."petId"
              AND up."userId" = get_current_user_id()
        )
    );
