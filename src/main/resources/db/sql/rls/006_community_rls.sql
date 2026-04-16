-- ================================================================
-- [006] 커뮤니티 관련 RLS 정책
-- 포함 테이블: CommunityPosts, CommunityLikes, CommunityComments,
--             CommunityPostTags, CommunityPostImages
-- 의존성: 000_rls_helper.sql (get_current_user_id 함수)
--
-- [핵심 정책]
-- ACTIVE 상태 게시글/댓글은 로그인 사용자 전체 조회 가능
-- 작성/수정/삭제는 본인만 가능
-- ================================================================

-- ----------------------------------------------------------------
-- CommunityPosts
-- ----------------------------------------------------------------

ALTER TABLE "CommunityPosts" ENABLE ROW LEVEL SECURITY;

-- SELECT: ACTIVE 상태 게시글은 로그인 사용자 전체 조회 가능
CREATE POLICY "community_posts_select"
    ON "CommunityPosts" FOR SELECT
    TO authenticated
    USING ("postStatus" = 'ACTIVE');

CREATE POLICY "community_posts_insert_own"
    ON "CommunityPosts" FOR INSERT
    TO authenticated
    WITH CHECK ("userId" = get_current_user_id());

CREATE POLICY "community_posts_update_own"
    ON "CommunityPosts" FOR UPDATE
    TO authenticated
    USING ("userId" = get_current_user_id())
    WITH CHECK ("userId" = get_current_user_id());

CREATE POLICY "community_posts_delete_own"
    ON "CommunityPosts" FOR DELETE
    TO authenticated
    USING ("userId" = get_current_user_id());

-- ----------------------------------------------------------------
-- CommunityLikes
-- ----------------------------------------------------------------

ALTER TABLE "CommunityLikes" ENABLE ROW LEVEL SECURITY;

-- SELECT: 전체 조회 가능 (좋아요 수 표시 필요)
CREATE POLICY "community_likes_select"
    ON "CommunityLikes" FOR SELECT
    TO authenticated
    USING (true);

CREATE POLICY "community_likes_insert_own"
    ON "CommunityLikes" FOR INSERT
    TO authenticated
    WITH CHECK ("userId" = get_current_user_id());

CREATE POLICY "community_likes_delete_own"
    ON "CommunityLikes" FOR DELETE
    TO authenticated
    USING ("userId" = get_current_user_id());

-- ----------------------------------------------------------------
-- CommunityComments
-- ----------------------------------------------------------------

ALTER TABLE "CommunityComments" ENABLE ROW LEVEL SECURITY;

-- SELECT: ACTIVE 상태 댓글은 전체 조회 가능
CREATE POLICY "community_comments_select"
    ON "CommunityComments" FOR SELECT
    TO authenticated
    USING ("commentStatus" = 'ACTIVE');

CREATE POLICY "community_comments_insert_own"
    ON "CommunityComments" FOR INSERT
    TO authenticated
    WITH CHECK ("userId" = get_current_user_id());

CREATE POLICY "community_comments_update_own"
    ON "CommunityComments" FOR UPDATE
    TO authenticated
    USING ("userId" = get_current_user_id())
    WITH CHECK ("userId" = get_current_user_id());

CREATE POLICY "community_comments_delete_own"
    ON "CommunityComments" FOR DELETE
    TO authenticated
    USING ("userId" = get_current_user_id());

-- ----------------------------------------------------------------
-- CommunityPostTags
-- ----------------------------------------------------------------

ALTER TABLE "CommunityPostTags" ENABLE ROW LEVEL SECURITY;

-- SELECT: 전체 조회 가능 (태그 검색 기능)
CREATE POLICY "community_post_tags_select"
    ON "CommunityPostTags" FOR SELECT
    TO authenticated
    USING (true);

-- INSERT/DELETE: 본인 게시글에만 가능
CREATE POLICY "community_post_tags_insert_own"
    ON "CommunityPostTags" FOR INSERT
    TO authenticated
    WITH CHECK (
        EXISTS (
            SELECT 1 FROM "CommunityPosts" cp
            WHERE cp."postId" = "CommunityPostTags"."postId"
              AND cp."userId" = get_current_user_id()
        )
    );

CREATE POLICY "community_post_tags_delete_own"
    ON "CommunityPostTags" FOR DELETE
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM "CommunityPosts" cp
            WHERE cp."postId" = "CommunityPostTags"."postId"
              AND cp."userId" = get_current_user_id()
        )
    );

-- ----------------------------------------------------------------
-- CommunityPostImages
-- ----------------------------------------------------------------

ALTER TABLE "CommunityPostImages" ENABLE ROW LEVEL SECURITY;

-- SELECT: 전체 조회 가능 (게시글 이미지 표시)
CREATE POLICY "community_post_images_select"
    ON "CommunityPostImages" FOR SELECT
    TO authenticated
    USING (true);

-- INSERT/DELETE: 본인 게시글에만 가능
CREATE POLICY "community_post_images_insert_own"
    ON "CommunityPostImages" FOR INSERT
    TO authenticated
    WITH CHECK (
        EXISTS (
            SELECT 1 FROM "CommunityPosts" cp
            WHERE cp."postId" = "CommunityPostImages"."postId"
              AND cp."userId" = get_current_user_id()
        )
    );

CREATE POLICY "community_post_images_delete_own"
    ON "CommunityPostImages" FOR DELETE
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM "CommunityPosts" cp
            WHERE cp."postId" = "CommunityPostImages"."postId"
              AND cp."userId" = get_current_user_id()
        )
    );
