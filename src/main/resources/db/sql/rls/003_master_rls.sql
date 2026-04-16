-- ================================================================
-- [003] 마스터 데이터 RLS 정책
-- 포함 테이블: Breeds, Allergies, Diseases, Ingredients, AllergyIngredients
-- 의존성: 없음 (Helper 함수 불필요)
--
-- [정책 방향]
-- 전체 공개 읽기 전용 (비로그인 포함)
-- INSERT/UPDATE/DELETE: Spring Boot 서버(service_role) 관리자만 처리
-- ================================================================

ALTER TABLE "Breeds"             ENABLE ROW LEVEL SECURITY;
ALTER TABLE "Allergies"          ENABLE ROW LEVEL SECURITY;
ALTER TABLE "Diseases"           ENABLE ROW LEVEL SECURITY;
ALTER TABLE "Ingredients"        ENABLE ROW LEVEL SECURITY;
ALTER TABLE "AllergyIngredients" ENABLE ROW LEVEL SECURITY;

CREATE POLICY "breeds_public_read"
    ON "Breeds" FOR SELECT
    USING (true);

CREATE POLICY "allergies_public_read"
    ON "Allergies" FOR SELECT
    USING (true);

CREATE POLICY "diseases_public_read"
    ON "Diseases" FOR SELECT
    USING (true);

CREATE POLICY "ingredients_public_read"
    ON "Ingredients" FOR SELECT
    USING (true);

CREATE POLICY "allergy_ingredients_public_read"
    ON "AllergyIngredients" FOR SELECT
    USING (true);
