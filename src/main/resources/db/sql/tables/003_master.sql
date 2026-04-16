-- ================================================================
-- [003] 마스터 데이터 테이블 (기준 데이터)
-- 포함 테이블: Breeds, Allergies, Diseases, Ingredients, AllergyIngredients
-- 의존성: 없음 (독립 테이블)
-- 설명: 앱 운영에 필요한 기준 데이터. 서비스 시작 전 초기 데이터 삽입 필요.
-- ================================================================

-- Breeds: 반려동물 품종 마스터
CREATE TABLE "Breeds" (
    "breedId"          SERIAL       PRIMARY KEY,
    "petType"          VARCHAR(10)     NOT NULL CHECK ("petType" IN ('DOG', 'CAT')),
    "breedName"        VARCHAR(100)    UNIQUE NOT NULL,
    "breedDescription" TEXT            NULL
);

COMMENT ON TABLE "Breeds" IS '반려동물 품종 기준 테이블. DOG/CAT 구분 필수';

-- Allergies: 알러지 마스터
CREATE TABLE "Allergies" (
    "allergyId"          SERIAL       PRIMARY KEY,
    "allergyName"        VARCHAR(100)    UNIQUE NOT NULL,
    "allergyDescription" TEXT            NULL
);

COMMENT ON TABLE "Allergies" IS '알러지 마스터. AllergyIngredients로 재료와 연결';

-- Diseases: 질환 마스터
CREATE TABLE "Diseases" (
    "diseaseId"          SERIAL       PRIMARY KEY,
    "diseaseName"        VARCHAR(100)    UNIQUE NOT NULL,
    "diseaseDescription" TEXT            NULL
);

COMMENT ON TABLE "Diseases" IS '질환 마스터. 질환별 식단 필터링 가능';

-- Ingredients: 레시피 재료 마스터
CREATE TABLE "Ingredients" (
    "ingredientId"          SERIAL       PRIMARY KEY,
    "ingredientName"        VARCHAR(100)    UNIQUE NOT NULL,
    "ingredientDescription" TEXT            NULL,
    "ingredientCategory"    VARCHAR(50)     NULL
                                CHECK ("ingredientCategory" IN ('육류', '채소', '곡물', '유제품', '과일', '기타')),
    "petType"               VARCHAR(10)     NULL CHECK ("petType" IN ('DOG', 'CAT', 'ALL')),
    "createdAt"             TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    "updatedAt"             TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE "Ingredients" IS '재료 마스터. AllergyIngredients로 알러지와 연결';

-- AllergyIngredients: 알러지-재료 N:M 매핑 (금지 식재료 필터링 핵심)
CREATE TABLE "AllergyIngredients" (
    "allergyIngredientId" SERIAL    PRIMARY KEY,
    "allergyId"           INTEGER      NOT NULL REFERENCES "Allergies"("allergyId")     ON DELETE CASCADE,
    "ingredientId"        INTEGER      NOT NULL REFERENCES "Ingredients"("ingredientId") ON DELETE CASCADE,
    "createdAt"           TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    UNIQUE ("allergyId", "ingredientId")
);

COMMENT ON TABLE "AllergyIngredients" IS '알러지-재료 매핑. 금지 식재료 자동 필터링 핵심';