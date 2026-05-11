-- Migration: 반려동물 독성 식재료 플래그 설정
-- 출처: ASPCA Animal Poison Control Center 기준
-- 실행: Supabase SQL Editor에서 직접 실행

-- ─────────────────────────────────────────────
-- 개·고양이 모두 위험
-- ─────────────────────────────────────────────
UPDATE "Ingredients"
SET "isToxicToDog" = TRUE,
    "isToxicToCat" = TRUE,
    "toxicityNote" = '적혈구 손상(하인즈소체 빈혈) 유발. 소량도 위험'
WHERE LOWER("ingredientName") LIKE '%onion%'
   OR LOWER("ingredientName") LIKE '%garlic%'
   OR LOWER("ingredientName") LIKE '%leek%'
   OR LOWER("ingredientName") LIKE '%chive%'
   OR LOWER("ingredientName") LIKE '%shallot%';

UPDATE "Ingredients"
SET "isToxicToDog" = TRUE,
    "isToxicToCat" = TRUE,
    "toxicityNote" = '테오브로민 함유. 심장 부정맥·경련·사망 유발'
WHERE LOWER("ingredientName") LIKE '%chocolate%'
   OR LOWER("ingredientName") LIKE '%cocoa%'
   OR LOWER("ingredientName") LIKE '%cacao%';

UPDATE "Ingredients"
SET "isToxicToDog" = TRUE,
    "isToxicToCat" = TRUE,
    "toxicityNote" = '급성 신부전 유발. 소량도 치명적'
WHERE LOWER("ingredientName") LIKE '%grape%'
   OR LOWER("ingredientName") LIKE '%raisin%'
   OR LOWER("ingredientName") LIKE '%currant%';

UPDATE "Ingredients"
SET "isToxicToDog" = TRUE,
    "isToxicToCat" = TRUE,
    "toxicityNote" = '자일리톨 함유. 저혈당·간부전 유발. 껌·무설탕 제품 주의'
WHERE LOWER("ingredientName") LIKE '%xylitol%';

UPDATE "Ingredients"
SET "isToxicToDog" = TRUE,
    "isToxicToCat" = TRUE,
    "toxicityNote" = '에탄올 중독. 소량으로도 간·신장 손상'
WHERE LOWER("ingredientName") LIKE '%alcohol%'
   OR LOWER("ingredientName") LIKE '%wine%'
   OR LOWER("ingredientName") LIKE '%beer%';

UPDATE "Ingredients"
SET "isToxicToDog" = TRUE,
    "isToxicToCat" = TRUE,
    "toxicityNote" = '카페인 함유. 심박수 증가·경련·사망 유발'
WHERE LOWER("ingredientName") LIKE '%coffee%'
   OR LOWER("ingredientName") LIKE '%caffeine%'
   OR LOWER("ingredientName") LIKE '%tea%';

UPDATE "Ingredients"
SET "isToxicToDog" = TRUE,
    "isToxicToCat" = TRUE,
    "toxicityNote" = '과당 과다. 비만·당뇨 유발 가능. 마카다미아는 신경계 독성'
WHERE LOWER("ingredientName") LIKE '%macadamia%';

-- ─────────────────────────────────────────────
-- 개에게만 위험
-- ─────────────────────────────────────────────
UPDATE "Ingredients"
SET "isToxicToDog" = TRUE,
    "toxicityNote" = '아보카딘 성분. 구토·설사·심근 손상 유발'
WHERE LOWER("ingredientName") LIKE '%avocado%';

-- ─────────────────────────────────────────────
-- 결과 확인
-- ─────────────────────────────────────────────
SELECT "ingredientName", "isToxicToDog", "isToxicToCat", "toxicityNote"
FROM "Ingredients"
WHERE "isToxicToDog" = TRUE OR "isToxicToCat" = TRUE
ORDER BY "ingredientName";
