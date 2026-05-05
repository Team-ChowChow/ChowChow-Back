# llm 모듈

사용자 프롬프트를 OpenAI Chat Completions API로 전달해 텍스트 응답을 생성하는 모듈입니다.

## 폴더 구조

| 폴더 | 역할 |
|------|------|
| `controller/` | LLM 채팅 요청/응답 API 엔드포인트 |
| `dto/` | LLM 채팅 요청/응답 DTO |
| `service/` | OpenAI 연동 및 응답 파싱 로직 |

## API

- `POST /api/llm/chat`
  - request: `prompt`(필수), `systemPrompt`(선택)
  - response: `answer`
