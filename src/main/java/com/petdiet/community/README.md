# community 모듈

사용자 커뮤니티 기능(게시글, 댓글, 좋아요)을 담당하는 모듈입니다.

## 폴더 구조

| 폴더 | 역할 |
|------|------|
| `controller/` | 게시글/댓글/좋아요 API 엔드포인트 |
| `dto/` | 게시글 작성·수정 요청 및 응답 DTO |
| `entity/` | CommunityPosts, CommunityComments, CommunityLikes, CommunityPostImages, CommunityPostTags 엔티티 |
| `repository/` | 커뮤니티 데이터 접근 레이어 |
| `service/` | 게시글 CRUD, 인기글 랭킹, 좋아요 비즈니스 로직 |

## 주요 기능

- 게시글 작성 (사진 다중 업로드 포함)
- 좋아요 / 댓글 / 대댓글
- 카테고리별 검색 (자유, 후기, 질문, 질환 정보)
- 주간 인기 식단 TOP 10, 질병별 베스트 랭킹
