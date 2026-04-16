# recipe 모듈

레시피 조회, 검색, 북마크, 리뷰 기능을 담당하는 모듈입니다.

## 폴더 구조

| 폴더 | 역할 |
|------|------|
| `controller/` | 레시피 CRUD, 검색, 북마크, 리뷰 API 엔드포인트 |
| `dto/` | 레시피 요청/응답, 검색 필터, 리뷰 DTO |
| `entity/` | Menus, Recipes, Ingredients, RecipeIngredients, RecipeSteps, RecipeBookmarks, RecipeReviews, RecipeTags, RecipeTagMap, RecipeNutritionSummaries 엔티티 |
| `repository/` | 레시피 데이터 접근 레이어 |
| `service/` | 레시피 검색·필터링, 북마크, 영양 분석 비즈니스 로직 |

## 검색 유형

- 키워드 검색 (음식명, 재료명)
- 카테고리 검색 (질병별, 목적별)
- 맞춤 필터 (알레르기 자동 제외, 종별 필터링)
