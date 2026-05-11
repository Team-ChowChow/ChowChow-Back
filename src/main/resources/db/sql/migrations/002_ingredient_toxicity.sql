-- Migration: Ingredients 테이블에 독성 플래그 컬럼 추가
ALTER TABLE "Ingredients"
    ADD COLUMN IF NOT EXISTS "isToxicToDog" BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS "isToxicToCat" BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS "toxicityNote"  TEXT NULL,
    ADD COLUMN IF NOT EXISTS "ingredientNameKo" VARCHAR(200) NULL,
    ADD COLUMN IF NOT EXISTS "spoonacularId"    INTEGER NULL,
    ADD COLUMN IF NOT EXISTS "caloriesPer100g"  DECIMAL(8,2) NULL,
    ADD COLUMN IF NOT EXISTS "proteinG"         DECIMAL(8,2) NULL,
    ADD COLUMN IF NOT EXISTS "fatG"             DECIMAL(8,2) NULL,
    ADD COLUMN IF NOT EXISTS "carbohydrateG"    DECIMAL(8,2) NULL,
    ADD COLUMN IF NOT EXISTS "fiberG"           DECIMAL(8,2) NULL;

COMMENT ON COLUMN "Ingredients"."isToxicToDog" IS '개에게 독성 여부 (true=위험)';
COMMENT ON COLUMN "Ingredients"."isToxicToCat" IS '고양이에게 독성 여부 (true=위험)';
COMMENT ON COLUMN "Ingredients"."toxicityNote" IS '독성 이유 및 증상 설명';
