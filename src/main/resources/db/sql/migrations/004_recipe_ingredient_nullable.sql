-- Migration: RecipeIngredients.ingredientId를 nullable로 변경
-- 이유: AI 생성 레시피에서 Spoonacular 매핑 실패 시 ingredientNote에만 이름을 저장하는 케이스 허용
ALTER TABLE "RecipeIngredients"
    ALTER COLUMN "ingredientId" DROP NOT NULL;

COMMENT ON COLUMN "RecipeIngredients"."ingredientId" IS 'Ingredients FK. AI 레시피에서 매핑 실패 시 NULL, ingredientNote에 원본 이름 저장';
