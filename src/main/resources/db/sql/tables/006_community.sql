-- ================================================================
-- [006] 커뮤니티 관련 테이블
-- 포함 테이블: CommunityPosts, CommunityLikes, CommunityComments,
--             CommunityPostTags, CommunityPostImages
-- 의존성: 001_users.sql (Users), 004_pets.sql (UserPets),
--         005_recipes.sql (Recipes)
-- ================================================================

-- CommunityPosts: 커뮤니티 게시글
CREATE TABLE "CommunityPosts" (
    "postId"       SERIAL    PRIMARY KEY,
    "userId"       INTEGER      NOT NULL REFERENCES "Users"("userId")       ON DELETE SET NULL,
    "petId"        INTEGER      NOT NULL REFERENCES "UserPets"("petId")     ON DELETE SET NULL,
    "recipeId"     INTEGER      NOT NULL REFERENCES "Recipes"("recipeId")   ON DELETE SET NULL,
    "postTitle"    VARCHAR(300) NOT NULL,
    "postContent"  TEXT         NOT NULL,
    "postCategory" VARCHAR(30)  NULL CHECK ("postCategory" IN ('자유', '후기', '질문', '질환정보')),
    "viewCount"    INT          NOT NULL DEFAULT 0,
    "likeCount"    INT          NOT NULL DEFAULT 0,
    "commentCount" INT          NOT NULL DEFAULT 0,
    "postStatus"   VARCHAR(10)  NOT NULL DEFAULT 'ACTIVE'
                       CHECK ("postStatus" IN ('ACTIVE', 'HIDDEN', 'DELETED')),
    "createdAt"    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    "updatedAt"    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- 인덱스
CREATE INDEX idx_community_posts_user_id    ON "CommunityPosts"("userId");
CREATE INDEX idx_community_posts_category   ON "CommunityPosts"("postCategory");
CREATE INDEX idx_community_posts_status     ON "CommunityPosts"("postStatus");
CREATE INDEX idx_community_posts_created_at ON "CommunityPosts"("createdAt" DESC);

COMMENT ON TABLE  "CommunityPosts"          IS '커뮤니티 게시글. 유저 탈퇴 시 userId NULL 처리 (게시글 유지)';
COMMENT ON COLUMN "CommunityPosts"."userId" IS 'NULL 허용: 탈퇴한 유저의 게시글은 익명으로 유지';

-- CommunityLikes: 게시글 좋아요
CREATE TABLE "CommunityLikes" (
    "communityLikeId" SERIAL    PRIMARY KEY,
    "postId"          INTEGER      NOT NULL REFERENCES "CommunityPosts"("postId") ON DELETE CASCADE,
    "userId"          INTEGER      NOT NULL REFERENCES "Users"("userId")          ON DELETE CASCADE,
    "createdAt"       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    UNIQUE ("postId", "userId")
);

COMMENT ON TABLE "CommunityLikes" IS '좋아요. UNIQUE(postId, userId) 중복 좋아요 방지';

-- CommunityComments: 게시글 댓글 및 대댓글
CREATE TABLE "CommunityComments" (
    "commentId"       SERIAL    PRIMARY KEY,
    "postId"          INTEGER      NOT NULL REFERENCES "CommunityPosts"("postId")        ON DELETE CASCADE,
    "userId"          INTEGER      NOT NULL REFERENCES "Users"("userId")                 ON DELETE CASCADE,
    "parentCommentId" INTEGER      NOT NULL     REFERENCES "CommunityComments"("commentId")  ON DELETE CASCADE,
    "commentContent"  TEXT         NOT NULL,
    "commentStatus"   VARCHAR(10)  NOT NULL DEFAULT 'ACTIVE'
                          CHECK ("commentStatus" IN ('ACTIVE', 'DELETED')),
    "createdAt"       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    "updatedAt"       TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- 인덱스
CREATE INDEX idx_community_comments_post_id ON "CommunityComments"("postId");
CREATE INDEX idx_community_comments_parent  ON "CommunityComments"("parentCommentId");

COMMENT ON TABLE  "CommunityComments"                    IS '댓글/대댓글. parentCommentId self-referencing으로 대댓글 구현';
COMMENT ON COLUMN "CommunityComments"."parentCommentId"  IS 'NULL=최상위 댓글 / 값 있음=대댓글';

-- CommunityPostTags: 게시글 해시태그
CREATE TABLE "CommunityPostTags" (
    "communityPostTagId" SERIAL    PRIMARY KEY,
    "postId"             INTEGER      NOT NULL REFERENCES "CommunityPosts"("postId") ON DELETE CASCADE,
    "tagName"            VARCHAR(100) NOT NULL,
    "createdAt"          TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE "CommunityPostTags" IS '게시글 해시태그. 태그 검색 기능 확장 가능';

-- CommunityPostImages: 게시글 다중 이미지
CREATE TABLE "CommunityPostImages" (
    "postImageId" SERIAL    PRIMARY KEY,
    "postId"      INTEGER      NOT NULL REFERENCES "CommunityPosts"("postId") ON DELETE CASCADE,
    "imageUrl"    TEXT         NOT NULL,
    "imageOrder"  INT          NOT NULL DEFAULT 1,
    "createdAt"   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE  "CommunityPostImages"              IS '게시글 다중 이미지. imageOrder로 순서 관리. CASCADE DELETE';
COMMENT ON COLUMN "CommunityPostImages"."imageUrl"   IS 'Supabase Storage URL (community-images 버킷)';
COMMENT ON COLUMN "CommunityPostImages"."imageOrder" IS '이미지 표시 순서. 1번이 대표 이미지';
