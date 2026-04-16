# pet 모듈

반려동물 등록 및 관리 기능을 담당하는 모듈입니다.

## 폴더 구조

| 폴더 | 역할 |
|------|------|
| `controller/` | 반려동물 등록/조회/수정/삭제 API 엔드포인트 |
| `dto/` | 반려동물 등록 요청 및 응답 DTO |
| `entity/` | UserPets, Breeds, Allergies, Diseases, PetAllergies, PetDiseases 엔티티 |
| `repository/` | 반려동물 데이터 접근 레이어 |
| `service/` | 반려동물 CRUD, 알레르기·질병 연결 비즈니스 로직 |

## 등록 가능 정보

- 종 (DOG / CAT), 품종, 이름, 성별, 생년월일, 체중
- 중성화 여부, 알레르기, 질환, 프로필 이미지
