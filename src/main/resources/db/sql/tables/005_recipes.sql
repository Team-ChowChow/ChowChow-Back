-- ================================================================
-- [005] 레시피 관련 테이블
-- 포함 테이블: Menus, Recipes, RecipeIngredients, RecipeSteps,
--             RecipeBookmarks, RecipeReviews, RecipeTags,
--             RecipeTagMap, RecipeNutritionSummaries
-- 의존성: 001_users.sql (Users), 003_master.sql (Ingredients),
--         004_pets.sql (UserPets)
-- ================================================================

-- Menus: 레시피의 상위 카테고리
CREATE TABLE "Menus" (
    "menuId"          SERIAL       PRIMARY KEY,
    "menuName"        VARCHAR(200)    UNIQUE NOT NULL,
    "menuDescription" TEXT            NULL,
    "petType"         VARCHAR(10)     NOT NULL CHECK ("petType" IN ('DOG', 'CAT')),
    "menuCategory"    VARCHAR(30)     NULL CHECK ("menuCategory" IN ('일반식', '간식', '특수식')),
    "menuStatus"      VARCHAR(10)     NOT NULL DEFAULT 'ACTIVE'
                          CHECK ("menuStatus" IN ('ACTIVE', 'HIDDEN')),
    "createdAt"       TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    "updatedAt"       TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE "Menus" IS '레시피 상위 카테고리. 한 메뉴에 여러 레시피 존재';

-- Recipes: AI 또는 사용자가 생성한 반려동물 식단 레시피 (핵심 테이블)
CREATE TABLE "Recipes" (
    "recipeId"          SERIAL       PRIMARY KEY,
    "menuId"            INTEGER         NOT NULL REFERENCES "Menus"("menuId")       ON DELETE RESTRICT,
    "userId"            INTEGER         NOT NULL     REFERENCES "Users"("userId")       ON DELETE SET NULL,
    "petId"             INTEGER         NOT NULL     REFERENCES "UserPets"("petId")     ON DELETE SET NULL,
    "recipeTitle"       VARCHAR(300)    NOT NULL,
    "recipeDescription" TEXT            NULL,
    "recipePurpose"     VARCHAR(100)    NULL,
    "feedingAmount"     VARCHAR(100)    NULL,
    "isAiGenerated"     BOOLEAN         NOT NULL DEFAULT FALSE,
    "isPublic"          BOOLEAN         NOT NULL DEFAULT TRUE,
    "recipeStatus"      VARCHAR(10)     NOT NULL DEFAULT 'ACTIVE'
                            CHECK ("recipeStatus" IN ('ACTIVE', 'HIDDEN', 'DELETED')),
    "createdAt"         TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    "updatedAt"         TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE  "Recipes"              IS '반려동물 맞춤 레시피. AI 생성 여부 중요. 추천 시스템 핵심 데이터';
COMMENT ON COLUMN "Recipes"."isAiGenerated" IS 'TRUE=AI 생성 / FALSE=사용자 작성';

-- 인덱스
CREATE INDEX idx_recipes_menu_id      ON "Recipes"("menuId");
CREATE INDEX idx_recipes_user_id      ON "Recipes"("userId");
CREATE INDEX idx_recipes_status       ON "Recipes"("recipeStatus");
CREATE INDEX idx_recipes_is_public    ON "Recipes"("isPublic");
CREATE INDEX idx_recipes_ai_generated ON "Recipes"("isAiGenerated");

-- RecipeIngredients: 레시피-재료 N:M 매핑 (사용량 포함)
CREATE TABLE "RecipeIngredients" (
    "recipeIngredientId" SERIAL       PRIMARY KEY,
    "recipeId"           INTEGER         NOT NULL REFERENCES "Recipes"("recipeId")         ON DELETE CASCADE,
    "ingredientId"       INTEGER         NOT NULL REFERENCES "Ingredients"("ingredientId") ON DELETE RESTRICT,
    "ingredientAmount"   DECIMAL(10, 2)  NULL,
    "ingredientUnit"     VARCHAR(20)     NULL,
    "ingredientNote"     VARCHAR(200)    NULL,
    "createdAt"          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE "RecipeIngredients" IS '레시피-재료 N:M 매핑. 사용량 포함';

-- RecipeSteps: 레시피 조리 단계
CREATE TABLE "RecipeSteps" (
    "recipeStepId"    SERIAL    PRIMARY KEY,
    "recipeId"        INTEGER      NOT NULL REFERENCES "Recipes"("recipeId") ON DELETE CASCADE,
    "stepNumber"      INT          NOT NULL,
    "stepDescription" TEXT         NOT NULL,
    "stepImage"       TEXT         NULL,
    "createdAt"       TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE "RecipeSteps" IS '조리 단계. stepNumber로 순서 관리';

-- RecipeBookmarks: 사용자 레시피 즐겨찾기
CREATE TABLE "RecipeBookmarks" (
    "bookmarkId" SERIAL    PRIMARY KEY,
    "userId"     INTEGER      NOT NULL REFERENCES "Users"("userId")     ON DELETE CASCADE,
    "recipeId"   INTEGER      NOT NULL REFERENCES "Recipes"("recipeId") ON DELETE CASCADE,
    "createdAt"  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    UNIQUE ("userId", "recipeId")
);

COMMENT ON TABLE "RecipeBookmarks" IS '사용자 즐겨찾기. UNIQUE(userId, recipeId) 중복 방지';

-- 인덱스
CREATE INDEX idx_recipe_bookmarks_user_id ON "RecipeBookmarks"("userId");

-- RecipeReviews: 레시피 리뷰 및 평점
CREATE TABLE "RecipeReviews" (
    "reviewId"      SERIAL       PRIMARY KEY,
    "recipeId"      INTEGER         NOT NULL REFERENCES "Recipes"("recipeId") ON DELETE CASCADE,
    "userId"        INTEGER         NOT NULL REFERENCES "Users"("userId")     ON DELETE CASCADE,
    "rating"        FLOAT           NULL CHECK ("rating" >= 1 AND "rating" <= 5),
    "reviewContent" TEXT            NULL,
    "createdAt"     TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    "updatedAt"     TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    UNIQUE ("recipeId", "userId")
);

COMMENT ON TABLE "RecipeReviews" IS '레시피 리뷰. UNIQUE(recipeId, userId) 1인 1리뷰 보장';

-- RecipeTags: 레시피 태그 마스터
CREATE TABLE "RecipeTags" (
    "recipeTagId"    SERIAL       PRIMARY KEY,
    "tagName"        VARCHAR(100)    UNIQUE NOT NULL,
    "tagType"        VARCHAR(20)     NULL CHECK ("tagType" IN ('PURPOSE', 'DISEASE', 'INGREDIENT', 'STYLE')),
    "tagDescription" TEXT            NULL,
    "createdAt"      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE "RecipeTags" IS '레시피 분류 및 검색을 위한 태그 마스터';

-- RecipeTagMap: 레시피-태그 N:M 매핑
CREATE TABLE "RecipeTagMap" (
    "recipeTagMapId" SERIAL    PRIMARY KEY,
    "recipeId"       INTEGER      NOT NULL REFERENCES "Recipes"("recipeId")     ON DELETE CASCADE,
    "recipeTagId"    INTEGER      NOT NULL REFERENCES "RecipeTags"("recipeTagId") ON DELETE CASCADE,
    "createdAt"      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    UNIQUE ("recipeId", "recipeTagId")
);

COMMENT ON TABLE "RecipeTagMap" IS '레시피-태그 N:M 매핑. 추천 알고리즘 기반 데이터';

-- RecipeNutritionSummaries: 레시피 영양 정보 (레시피당 1개)
CREATE TABLE "RecipeNutritionSummaries" (
    "nutritionSummaryId" SERIAL       PRIMARY KEY,
    "recipeId"           INTEGER         UNIQUE NOT NULL REFERENCES "Recipes"("recipeId") ON DELETE CASCADE,
    "totalWeight"        DECIMAL(8, 2)   NULL,
    "totalCalories"      DECIMAL(10, 2)  NULL,
    "proteinG"           DECIMAL(7, 2)   NULL,
    "fatG"               DECIMAL(7, 2)   NULL,
    "carbohydrateG"      DECIMAL(7, 2)   NULL,
    "sodiumMg"           DECIMAL(8, 2)   NULL,
    "nutritionComment"   TEXT            NULL,
    "createdAt"          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    "updatedAt"          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE "RecipeNutritionSummaries" IS '레시피 영양 정보. UNIQUE(recipeId) 레시피당 1개. 칼로리/영양소 단일 출처';
