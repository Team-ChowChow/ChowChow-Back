-- ================================================================
-- [005] 레시피 관련 RLS 정책
-- 포함 테이블: Menus, Recipes, RecipeIngredients, RecipeSteps,
--             RecipeBookmarks, RecipeReviews, RecipeTags,
--             RecipeTagMap, RecipeNutritionSummaries
-- 의존성: 000_rls_helper.sql (get_current_user_id 함수)
--
-- [핵심 정책]
-- isPublic = TRUE  → 로그인 사용자 전체 조회 가능
-- isPublic = FALSE → 본인만 조회 가능
-- ================================================================

-- ----------------------------------------------------------------
-- Menus: 공개 읽기 전용
-- ----------------------------------------------------------------

ALTER TABLE "Menus" ENABLE ROW LEVEL SECURITY;

CREATE POLICY "menus_public_read"
    ON "Menus" FOR SELECT
    USING (true);

-- ----------------------------------------------------------------
-- Recipes: 공개 여부에 따라 조회 권한 분리
-- ----------------------------------------------------------------

ALTER TABLE "Recipes" ENABLE ROW LEVEL SECURITY;

CREATE POLICY "recipes_select"
    ON "Recipes" FOR SELECT
    TO authenticated
    USING (
        "isPublic" = TRUE
        OR "userId" = get_current_user_id()
    );

CREATE POLICY "recipes_insert_own"
    ON "Recipes" FOR INSERT
    TO authenticated
    WITH CHECK ("userId" = get_current_user_id());

CREATE POLICY "recipes_update_own"
    ON "Recipes" FOR UPDATE
    TO authenticated
    USING ("userId" = get_current_user_id())
    WITH CHECK ("userId" = get_current_user_id());

CREATE POLICY "recipes_delete_own"
    ON "Recipes" FOR DELETE
    TO authenticated
    USING ("userId" = get_current_user_id());

-- ----------------------------------------------------------------
-- RecipeIngredients: 레시피 공개 여부에 따라 권한 결정
-- ----------------------------------------------------------------

ALTER TABLE "RecipeIngredients" ENABLE ROW LEVEL SECURITY;

CREATE POLICY "recipe_ingredients_select"
    ON "RecipeIngredients" FOR SELECT
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM "Recipes" r
            WHERE r."recipeId" = "RecipeIngredients"."recipeId"
              AND (r."isPublic" = TRUE OR r."userId" = get_current_user_id())
        )
    );

CREATE POLICY "recipe_ingredients_insert_own"
    ON "RecipeIngredients" FOR INSERT
    TO authenticated
    WITH CHECK (
        EXISTS (
            SELECT 1 FROM "Recipes" r
            WHERE r."recipeId" = "RecipeIngredients"."recipeId"
              AND r."userId" = get_current_user_id()
        )
    );

CREATE POLICY "recipe_ingredients_update_own"
    ON "RecipeIngredients" FOR UPDATE
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM "Recipes" r
            WHERE r."recipeId" = "RecipeIngredients"."recipeId"
              AND r."userId" = get_current_user_id()
        )
    );

CREATE POLICY "recipe_ingredients_delete_own"
    ON "RecipeIngredients" FOR DELETE
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM "Recipes" r
            WHERE r."recipeId" = "RecipeIngredients"."recipeId"
              AND r."userId" = get_current_user_id()
        )
    );

-- ----------------------------------------------------------------
-- RecipeSteps: RecipeIngredients와 동일한 패턴
-- ----------------------------------------------------------------

ALTER TABLE "RecipeSteps" ENABLE ROW LEVEL SECURITY;

CREATE POLICY "recipe_steps_select"
    ON "RecipeSteps" FOR SELECT
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM "Recipes" r
            WHERE r."recipeId" = "RecipeSteps"."recipeId"
              AND (r."isPublic" = TRUE OR r."userId" = get_current_user_id())
        )
    );

CREATE POLICY "recipe_steps_insert_own"
    ON "RecipeSteps" FOR INSERT
    TO authenticated
    WITH CHECK (
        EXISTS (
            SELECT 1 FROM "Recipes" r
            WHERE r."recipeId" = "RecipeSteps"."recipeId"
              AND r."userId" = get_current_user_id()
        )
    );

CREATE POLICY "recipe_steps_update_own"
    ON "RecipeSteps" FOR UPDATE
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM "Recipes" r
            WHERE r."recipeId" = "RecipeSteps"."recipeId"
              AND r."userId" = get_current_user_id()
        )
    );

CREATE POLICY "recipe_steps_delete_own"
    ON "RecipeSteps" FOR DELETE
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM "Recipes" r
            WHERE r."recipeId" = "RecipeSteps"."recipeId"
              AND r."userId" = get_current_user_id()
        )
    );

-- ----------------------------------------------------------------
-- RecipeBookmarks: 본인 북마크만 CRUD
-- ----------------------------------------------------------------

ALTER TABLE "RecipeBookmarks" ENABLE ROW LEVEL SECURITY;

CREATE POLICY "recipe_bookmarks_select_own"
    ON "RecipeBookmarks" FOR SELECT
    TO authenticated
    USING ("userId" = get_current_user_id());

CREATE POLICY "recipe_bookmarks_insert_own"
    ON "RecipeBookmarks" FOR INSERT
    TO authenticated
    WITH CHECK ("userId" = get_current_user_id());

CREATE POLICY "recipe_bookmarks_delete_own"
    ON "RecipeBookmarks" FOR DELETE
    TO authenticated
    USING ("userId" = get_current_user_id());

-- ----------------------------------------------------------------
-- RecipeReviews: 공개 레시피 리뷰는 전체 조회, 작성/수정/삭제는 본인만
-- ----------------------------------------------------------------

ALTER TABLE "RecipeReviews" ENABLE ROW LEVEL SECURITY;

CREATE POLICY "recipe_reviews_select"
    ON "RecipeReviews" FOR SELECT
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM "Recipes" r
            WHERE r."recipeId" = "RecipeReviews"."recipeId"
              AND (r."isPublic" = TRUE OR r."userId" = get_current_user_id())
        )
    );

CREATE POLICY "recipe_reviews_insert_own"
    ON "RecipeReviews" FOR INSERT
    TO authenticated
    WITH CHECK ("userId" = get_current_user_id());

CREATE POLICY "recipe_reviews_update_own"
    ON "RecipeReviews" FOR UPDATE
    TO authenticated
    USING ("userId" = get_current_user_id())
    WITH CHECK ("userId" = get_current_user_id());

CREATE POLICY "recipe_reviews_delete_own"
    ON "RecipeReviews" FOR DELETE
    TO authenticated
    USING ("userId" = get_current_user_id());

-- ----------------------------------------------------------------
-- RecipeTags: 공개 읽기 전용
-- ----------------------------------------------------------------

ALTER TABLE "RecipeTags" ENABLE ROW LEVEL SECURITY;

CREATE POLICY "recipe_tags_public_read"
    ON "RecipeTags" FOR SELECT
    USING (true);

-- ----------------------------------------------------------------
-- RecipeTagMap: 레시피 공개 여부에 따라 권한 결정
-- ----------------------------------------------------------------

ALTER TABLE "RecipeTagMap" ENABLE ROW LEVEL SECURITY;

CREATE POLICY "recipe_tag_map_select"
    ON "RecipeTagMap" FOR SELECT
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM "Recipes" r
            WHERE r."recipeId" = "RecipeTagMap"."recipeId"
              AND (r."isPublic" = TRUE OR r."userId" = get_current_user_id())
        )
    );

CREATE POLICY "recipe_tag_map_insert_own"
    ON "RecipeTagMap" FOR INSERT
    TO authenticated
    WITH CHECK (
        EXISTS (
            SELECT 1 FROM "Recipes" r
            WHERE r."recipeId" = "RecipeTagMap"."recipeId"
              AND r."userId" = get_current_user_id()
        )
    );

CREATE POLICY "recipe_tag_map_delete_own"
    ON "RecipeTagMap" FOR DELETE
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM "Recipes" r
            WHERE r."recipeId" = "RecipeTagMap"."recipeId"
              AND r."userId" = get_current_user_id()
        )
    );

-- ----------------------------------------------------------------
-- RecipeNutritionSummaries: 레시피 공개 여부에 따라 권한 결정
-- ----------------------------------------------------------------

ALTER TABLE "RecipeNutritionSummaries" ENABLE ROW LEVEL SECURITY;

CREATE POLICY "recipe_nutrition_select"
    ON "RecipeNutritionSummaries" FOR SELECT
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM "Recipes" r
            WHERE r."recipeId" = "RecipeNutritionSummaries"."recipeId"
              AND (r."isPublic" = TRUE OR r."userId" = get_current_user_id())
        )
    );

CREATE POLICY "recipe_nutrition_insert_own"
    ON "RecipeNutritionSummaries" FOR INSERT
    TO authenticated
    WITH CHECK (
        EXISTS (
            SELECT 1 FROM "Recipes" r
            WHERE r."recipeId" = "RecipeNutritionSummaries"."recipeId"
              AND r."userId" = get_current_user_id()
        )
    );

CREATE POLICY "recipe_nutrition_update_own"
    ON "RecipeNutritionSummaries" FOR UPDATE
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM "Recipes" r
            WHERE r."recipeId" = "RecipeNutritionSummaries"."recipeId"
              AND r."userId" = get_current_user_id()
        )
    );
