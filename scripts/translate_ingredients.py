import psycopg2
import requests
import json
import time

DB_URL = "host=aws-1-ap-northeast-2.pooler.supabase.com port=6543 dbname=postgres user=postgres.qptbjdczwcwaheymmnml password='Chawchaw@1324' sslmode=require"
OPENAI_API_KEY = "sk-your-openai-api-key-here"
BATCH_SIZE = 50

def translate_batch(names: list[str]) -> dict:
    prompt = (
        "다음 영어 식재료 이름들을 반려동물 식품 맥락에서 자연스러운 한국어로 번역해주세요.\n"
        "JSON 형식으로만 응답하세요: {\"translations\": {\"영어이름\": \"한국어이름\", ...}}\n\n"
        "번역할 재료:\n" + "\n".join(names)
    )
    resp = requests.post(
        "https://api.openai.com/v1/chat/completions",
        headers={"Authorization": f"Bearer {OPENAI_API_KEY}", "Content-Type": "application/json"},
        json={
            "model": "gpt-4o-mini",
            "max_tokens": 2048,
            "response_format": {"type": "json_object"},
            "messages": [{"role": "user", "content": prompt}]
        },
        timeout=30
    )
    resp.raise_for_status()
    content = resp.json()["choices"][0]["message"]["content"]
    return json.loads(content).get("translations", {})

def main():
    conn = psycopg2.connect(DB_URL)
    cur = conn.cursor()

    cur.execute('SELECT "ingredientId", "ingredientName" FROM "Ingredients" WHERE "ingredientNameKo" IS NULL ORDER BY "ingredientId"')
    rows = cur.fetchall()
    print(f"번역 대상: {len(rows)}개")

    total = 0
    input_tokens = 0
    output_tokens = 0

    for i in range(0, len(rows), BATCH_SIZE):
        batch = rows[i:i + BATCH_SIZE]
        ids = [r[0] for r in batch]
        names = [r[1] for r in batch]

        try:
            resp = requests.post(
                "https://api.openai.com/v1/chat/completions",
                headers={"Authorization": f"Bearer {OPENAI_API_KEY}", "Content-Type": "application/json"},
                json={
                    "model": "gpt-4o-mini",
                    "max_tokens": 2048,
                    "response_format": {"type": "json_object"},
                    "messages": [{"role": "user", "content": (
                        "다음 영어 식재료 이름들을 반려동물 식품 맥락에서 자연스러운 한국어로 번역해주세요.\n"
                        "JSON 형식으로만 응답하세요: {\"translations\": {\"영어이름\": \"한국어이름\", ...}}\n\n"
                        "번역할 재료:\n" + "\n".join(names)
                    )}]
                },
                timeout=30
            )
            resp.raise_for_status()
            data = resp.json()
            usage = data.get("usage", {})
            input_tokens += usage.get("prompt_tokens", 0)
            output_tokens += usage.get("completion_tokens", 0)

            translations = json.loads(data["choices"][0]["message"]["content"]).get("translations", {})

            for row_id, name in zip(ids, names):
                ko = translations.get(name)
                if ko:
                    cur.execute('UPDATE "Ingredients" SET "ingredientNameKo" = %s WHERE "ingredientId" = %s', (ko, row_id))
                    total += 1

            conn.commit()
            print(f"  [{i + len(batch)}/{len(rows)}] 완료 — 누적 번역 {total}개")
            time.sleep(0.3)

        except Exception as e:
            print(f"  배치 {i}~{i+BATCH_SIZE} 실패: {e}")
            conn.rollback()

    cur.close()
    conn.close()

    cost = (input_tokens / 1_000_000 * 0.15) + (output_tokens / 1_000_000 * 0.60)
    print(f"\n완료: {total}개 번역")
    print(f"토큰 사용: 입력 {input_tokens:,} / 출력 {output_tokens:,}")
    print(f"예상 비용: ${cost:.4f}")

if __name__ == "__main__":
    main()
