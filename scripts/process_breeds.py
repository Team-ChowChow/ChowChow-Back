import pandas as pd
from deep_translator import GoogleTranslator
import time
import re

# ──────────────────────────────────────────────
# 설정
# ──────────────────────────────────────────────
INPUT_FILE = 'src/main/resources/db/csv/query.csv'
OUTPUT_FILE = 'src/main/resources/db/csv/breeds.csv'

# ──────────────────────────────────────────────
# 무의미한 설명 판별
# ──────────────────────────────────────────────
MEANINGLESS_EXACT = {
    'dog breed', 'Dog breed', 'Dog Breed',
    'dog breeds', 'Dog breeds',
    'type of dog', 'Type of dog', 'Type of Dog',
    'dog type', 'Dog type', 'Dog Type',
    'dog crossbreed', 'Dog crossbreed',
    'dog bread',                         # 오타
    'dog species',
    'Purebred dog breed',
    'dog breed variation, hunting dog',
}

MEANINGLESS_REGEX = [
    r'^$',                                              # 빈 값
    r'^(a |an )?(certain |modern |extinct |ancient |rare |disputed )?dog breeds?$',
    r'^(extinct|ancient|rare) dog (breed|type)s?$',
    r'^[a-zA-ZÀ-ÿ\s\-]+ breed of dog$',               # "Spanish breed of dog" 등 지역+breed of dog
    r'^dog breed (from|of|originating)',                # "dog breed from Turkey" 등
    r'^(breed|type) of dog$',
    r'^[a-zA-ZÀ-ÿ\s\-]+ dog breed$',                  # "German dog breed" 등 지역+dog breed
    r'위키미디어',                                        # 동음이의어 문서
]

def is_meaningless(desc: str) -> bool:
    if pd.isna(desc):
        return True
    desc = str(desc).strip()
    if desc == '':
        return True
    if desc in MEANINGLESS_EXACT:
        return True
    for pattern in MEANINGLESS_REGEX:
        if re.match(pattern, desc, re.IGNORECASE):
            return True
    return False


# ──────────────────────────────────────────────
# 한국어 판별
# ──────────────────────────────────────────────
def is_korean(text: str) -> bool:
    if pd.isna(text):
        return False
    return any('\uAC00' <= c <= '\uD7A3' for c in str(text))


# ──────────────────────────────────────────────
# 번역
# ──────────────────────────────────────────────
translator = GoogleTranslator(source='en', target='ko')

def translate(text: str) -> str:
    text = str(text).strip()
    if not text or is_korean(text):
        return text
    try:
        result = translator.translate(text)
        time.sleep(0.3)   # API 요청 간격
        return result
    except Exception as e:
        print(f"  번역 실패: {text[:40]!r} → {e}")
        return text


# ──────────────────────────────────────────────
# 메인 처리
# ──────────────────────────────────────────────
def main():
    df = pd.read_csv(INPUT_FILE)
    print(f"원본 행 수: {len(df)}")

    rows = []
    skipped = 0
    translated = 0

    for _, row in df.iterrows():
        name = str(row['breedName']).strip() if not pd.isna(row['breedName']) else ''
        desc = str(row['breedDescription']).strip() if not pd.isna(row['breedDescription']) else ''

        if not name or name == 'nan':
            skipped += 1
            continue

        name_is_ko = is_korean(name)
        desc_is_meaningful = not is_meaningless(desc)

        # 무의미한 설명 → 설명만 비움 (이름은 유지)
        if not desc_is_meaningful:
            desc = ''

        # 번역 (영어 항목만)
        if not name_is_ko:
            print(f"  번역 중: {name}")
            name = translate(name)
            translated += 1

        if desc and not is_korean(desc):
            desc = translate(desc)

        rows.append({
            'petType':          'DOG',
            'breedName':        name,
            'breedDescription': desc,
        })

    result = pd.DataFrame(rows).drop_duplicates(subset=['breedName'])
    result.to_csv(OUTPUT_FILE, index=False, encoding='utf-8-sig')

    print(f"\n처리 완료")
    print(f"  유지된 품종: {len(result)}개")
    print(f"  삭제된 행:   {skipped}개")
    print(f"  번역된 이름: {translated}개")
    print(f"  저장 위치:   {OUTPUT_FILE}")


if __name__ == '__main__':
    main()
