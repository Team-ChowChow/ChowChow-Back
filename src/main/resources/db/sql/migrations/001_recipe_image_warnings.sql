-- Migration: Recipes 테이블에 imageUrl, warnings 컬럼 추가
ALTER TABLE "Recipes"
    ADD COLUMN IF NOT EXISTS "imageUrl" TEXT NULL,
    ADD COLUMN IF NOT EXISTS "warnings" TEXT NULL;

COMMENT ON COLUMN "Recipes"."imageUrl" IS 'AI 생성 레시피 이미지 URL (DALL-E)';
COMMENT ON COLUMN "Recipes"."warnings" IS 'AI 생성 주의사항 목록 (JSON 배열 문자열)';
