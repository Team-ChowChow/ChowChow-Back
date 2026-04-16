# common 모듈

공통으로 사용되는 응답 형식, 예외 처리, DTO를 관리하는 모듈입니다.

## 폴더 구조

| 폴더 | 역할 |
|------|------|
| `exception/` | GlobalExceptionHandler, CustomException 클래스 |
| `response/` | ApiResponse, 공통 응답 래퍼 클래스 |
| `dto/` | 공통으로 사용되는 DTO (PageRequest, PageResponse 등) |

## 공통 응답 형식

```json
{
  "success": true,
  "message": "처리 완료",
  "data": {}
}
```
