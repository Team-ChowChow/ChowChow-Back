import psycopg2
import os

DB_DSN = "host=aws-1-ap-northeast-2.pooler.supabase.com port=6543 dbname=postgres user=postgres.qptbjdczwcwaheymmnml password='Chawchaw@1324' sslmode=require"

MIGRATIONS = [
    ("001", """
ALTER TABLE "Recipes"
    ADD COLUMN IF NOT EXISTS "imageUrl" TEXT NULL,
    ADD COLUMN IF NOT EXISTS "warnings" TEXT NULL;
"""),
    ("002", """
ALTER TABLE "Ingredients"
    ADD COLUMN IF NOT EXISTS "isToxicToDog"    BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS "isToxicToCat"    BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS "toxicityNote"    TEXT NULL,
    ADD COLUMN IF NOT EXISTS "ingredientNameKo" VARCHAR(200) NULL,
    ADD COLUMN IF NOT EXISTS "spoonacularId"    INTEGER NULL,
    ADD COLUMN IF NOT EXISTS "caloriesPer100g"  DECIMAL(8,2) NULL,
    ADD COLUMN IF NOT EXISTS "proteinG"         DECIMAL(8,2) NULL,
    ADD COLUMN IF NOT EXISTS "fatG"             DECIMAL(8,2) NULL,
    ADD COLUMN IF NOT EXISTS "carbohydrateG"    DECIMAL(8,2) NULL,
    ADD COLUMN IF NOT EXISTS "fiberG"           DECIMAL(8,2) NULL;
"""),
    ("003_toxicity_data", """
UPDATE "Ingredients" SET "isToxicToDog"=TRUE,"isToxicToCat"=TRUE,"toxicityNote"='적혈구 손상(하인즈소체 빈혈) 유발. 소량도 위험'
WHERE LOWER("ingredientName") SIMILAR TO '%(onion|garlic|leek|chive|shallot)%';

UPDATE "Ingredients" SET "isToxicToDog"=TRUE,"isToxicToCat"=TRUE,"toxicityNote"='테오브로민 함유. 심장 부정맥·경련·사망 유발'
WHERE LOWER("ingredientName") SIMILAR TO '%(chocolate|cocoa|cacao)%';

UPDATE "Ingredients" SET "isToxicToDog"=TRUE,"isToxicToCat"=TRUE,"toxicityNote"='급성 신부전 유발. 소량도 치명적'
WHERE LOWER("ingredientName") SIMILAR TO '%(grape|raisin|currant)%';

UPDATE "Ingredients" SET "isToxicToDog"=TRUE,"isToxicToCat"=TRUE,"toxicityNote"='카페인 함유. 심박수 증가·경련 유발'
WHERE LOWER("ingredientName") SIMILAR TO '%(coffee|caffeine)%';

UPDATE "Ingredients" SET "isToxicToDog"=TRUE,"toxicityNote"='아보카딘 성분. 구토·설사·심근 손상 유발'
WHERE LOWER("ingredientName") LIKE '%avocado%';
"""),
    ("004_recipe_ingredient_nullable", """
ALTER TABLE "RecipeIngredients"
    ALTER COLUMN "ingredientId" DROP NOT NULL;
"""),
]

def main():
    conn = psycopg2.connect(DB_DSN)
    conn.autocommit = False
    cur = conn.cursor()

    for name, sql in MIGRATIONS:
        try:
            cur.execute(sql)
            conn.commit()
            print(f"  [OK] migration {name}")
        except Exception as e:
            conn.rollback()
            print(f"  [SKIP/ERR] migration {name}: {e}")

    cur.close()
    conn.close()
    print("\n마이그레이션 완료")

if __name__ == "__main__":
    main()
