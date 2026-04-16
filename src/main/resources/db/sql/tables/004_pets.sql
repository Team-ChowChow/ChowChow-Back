-- ================================================================
-- [004] 반려동물 관련 테이블
-- 포함 테이블: UserPets, PetAllergies, PetDiseases
-- 의존성: 001_users.sql (Users), 003_master.sql (Breeds, Allergies, Diseases)
-- ================================================================

-- UserPets: 사용자가 등록한 반려동물 정보
CREATE TABLE "UserPets" (
    "petId"          SERIAL       PRIMARY KEY,
    "userId"         INTEGER         NOT NULL REFERENCES "Users"("userId") ON DELETE CASCADE,
    "petName"        VARCHAR(100)    NOT NULL,
    "petType"        VARCHAR(10)     NOT NULL CHECK ("petType" IN ('DOG', 'CAT')),
    "breedId"        INTEGER         NULL REFERENCES "Breeds"("breedId") ON DELETE SET NULL,
    "petGender"      VARCHAR(10)     NULL CHECK ("petGender" IN ('MALE', 'FEMALE')),
    "petBirthdate"   DATE            NULL,
    "petWeight"      DECIMAL(5, 2)   NULL,
    "isNeutered"     BOOLEAN         NULL,
    "petProfileImg"  TEXT            NULL,
    "petCreatedAt"   TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    "petUpdatedAt"   TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE  "UserPets"                IS '사용자 반려동물 정보. Users:UserPets = 1:N 구조';
COMMENT ON COLUMN "UserPets"."petProfileImg" IS 'Supabase Storage URL (pet-images 버킷)';

-- 인덱스
CREATE INDEX idx_user_pets_user_id ON "UserPets"("userId");
CREATE INDEX idx_user_pets_type    ON "UserPets"("petType");

-- PetAllergies: 반려동물-알러지 N:M 매핑
CREATE TABLE "PetAllergies" (
    "petAllergyId" SERIAL       PRIMARY KEY,
    "petId"        INTEGER      NOT NULL REFERENCES "UserPets"("petId")     ON DELETE CASCADE,
    "allergyId"    INTEGER      NOT NULL REFERENCES "Allergies"("allergyId") ON DELETE CASCADE,
    "createdAt"    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    UNIQUE ("petId", "allergyId")
);

COMMENT ON TABLE "PetAllergies" IS '반려동물-알러지 매핑. UNIQUE(petId, allergyId) 중복 방지';

-- PetDiseases: 반려동물-질환 N:M 매핑
CREATE TABLE "PetDiseases" (
    "petDiseaseId" SERIAL       PRIMARY KEY,
    "petId"        INTEGER      NOT NULL REFERENCES "UserPets"("petId")     ON DELETE CASCADE,
    "diseaseId"    INTEGER      NOT NULL REFERENCES "Diseases"("diseaseId") ON DELETE CASCADE,
    "createdAt"    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    UNIQUE ("petId", "diseaseId")
);

COMMENT ON TABLE "PetDiseases" IS '반려동물-질환 매핑. UNIQUE(petId, diseaseId) 중복 방지';
