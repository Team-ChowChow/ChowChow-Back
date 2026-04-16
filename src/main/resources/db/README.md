# db 리소스

Supabase(PostgreSQL) DB 초기화에 필요한 SQL 및 시드 데이터를 관리합니다.

## 폴더 구조

| 폴더 | 역할 |
|------|------|
| `csv/` | 마스터 데이터 시드 파일 (품종, 알레르기, 질환, 재료, 메뉴 등) |
| `sql/tables/` | 테이블 생성 DDL (001_users.sql ~ 008_etc.sql) |
| `sql/rls/` | Supabase Row Level Security 정책 (000_rls_helper.sql ~ 008_etc_rls.sql) |

## 실행 순서

1. `sql/tables/` 파일을 번호 순서대로 Supabase SQL Editor에서 실행
2. `sql/rls/` 파일을 번호 순서대로 실행
3. `csv/` 파일을 Supabase 테이블 임포트 기능으로 적재

## CSV 파일 목록

| 파일 | 대상 테이블 |
|------|-------------|
| `breeds.csv` | Breeds |
| `allergies.csv` | Allergies |
| `diseases.csv` | Diseases |
| `ingredients.csv` | Ingredients |
| `allergy_ingredients.csv` | AllergyIngredients |
| `menus.csv` | Menus |
| `recipe_tags.csv` | RecipeTags |
