# Language Learning Backend

語言學習平台與 App 的後端服務，使用 Java 開發，提供課程、單字、練習題與使用者學習進度管理的 API。

## 技術棧

- **語言**：Java 17+
- **框架**：Spring Boot（REST API、Spring Security、Spring Data JPA）
- **建置工具**：Maven
- **資料庫**：PostgreSQL
- **驗證**：JWT

> 目前專案為初始階段，以上為預設技術選型，可依實際需求調整。

## 功能規劃

- [x] 使用者註冊 / 登入 / 身份驗證
- [x] 課程與單字內容管理
- [x] 練習題與測驗（含批改邏輯）——目前為依課程單字自動出的選擇題測驗
- [x] 使用者學習進度追蹤——每次測驗的分數歷程，以及每個單字的複習狀態（LEARNING / MASTERED）與複習次數
- [x] 提供給前端 / App 的 REST API
- [x] 角色 / 權限分級——`TEACHER` 管理課程與單字內容，`STUDENT` 唯讀並可測驗

## 專案結構

```
src/
  main/
    java/
      com/languagelearning/
        controller/   # REST API 入口（HealthController、AuthController、CourseController、WordController、QuizController）
        config/       # 設定（SecurityConfig、OpenApiConfig）
        security/     # JwtService、JwtAuthFilter
        exception/    # GlobalExceptionHandler，統一錯誤回應格式
        service/      # 商業邏輯（AuthService、CourseService、WordService、WordProgressService、QuizService）
        repository/   # 資料存取層（UserRepository、CourseRepository、WordRepository、WordProgressRepository、QuizAttemptRepository）
        model/        # 實體類別 Entity（User、Role、Course、Word、WordProgress、WordStatus、QuizAttempt）
        dto/          # 資料傳輸物件（Auth、Course、Word、WordProgress、Quiz、ErrorResponse 相關 DTO）
    resources/
      application.properties
  test/
    resources/
      application.properties   # 測試用 H2 in-memory 覆蓋設定
```

## 環境需求

- JDK 17+
- Maven（已附 `mvnw` wrapper，不用另外裝）
- Docker（本機啟動 PostgreSQL 用，或自行安裝 PostgreSQL 15+）

## 快速開始

```bash
# 啟動本機 PostgreSQL
docker compose up -d

# 啟動服務（wrapper 會自動抓正確的 Maven 版本）
./mvnw spring-boot:run
```

服務預設啟動於 `http://localhost:8080`，可呼叫 `GET /api/health` 確認服務存活。

資料庫連線參數可用環境變數覆蓋：`DB_NAME`、`DB_USERNAME`、`DB_PASSWORD`（預設對應 `docker-compose.yml` 裡的設定）。

## API

| Method | Path                          | 說明                              | 需要 JWT | 需要角色 |
|--------|-------------------------------|-----------------------------------|----------|----------|
| GET    | `/api/health`                 | 健康檢查                          | 否       | -        |
| POST   | `/api/auth/register`          | 註冊，成功回傳 `{ "token": ... }` | 否       | -        |
| POST   | `/api/auth/login`             | 登入，成功回傳 `{ "token": ... }` | 否       | -        |
| GET    | `/api/auth/me`                | 回傳目前登入使用者的 email 與角色 | 是       | 任一     |
| POST   | `/api/courses`                | 新增課程                          | 是       | `TEACHER` |
| GET    | `/api/courses`                | 取得課程列表（分頁）              | 是       | 任一     |
| GET    | `/api/courses/{id}`           | 取得單一課程                      | 是       | 任一     |
| PUT    | `/api/courses/{id}`           | 更新課程                          | 是       | `TEACHER` |
| DELETE | `/api/courses/{id}`           | 刪除課程（連同底下單字與其學習紀錄） | 是       | `TEACHER` |
| POST   | `/api/courses/{courseId}/words` | 在指定課程下新增單字             | 是       | `TEACHER` |
| GET    | `/api/courses/{courseId}/words` | 取得指定課程下的單字列表（分頁） | 是       | 任一     |
| GET    | `/api/words/{id}`             | 取得單一單字                      | 是       | 任一     |
| PUT    | `/api/words/{id}`             | 更新單字                          | 是       | `TEACHER` |
| DELETE | `/api/words/{id}`             | 刪除單字（連同該單字的學習紀錄）  | 是       | `TEACHER` |
| PUT    | `/api/words/{id}/progress`    | 標記單字複習狀態（LEARNING / MASTERED），複習次數會累加 | 是       | 任一     |
| GET    | `/api/courses/{courseId}/word-progress` | 取得目前使用者在此課程下已標記過的單字學習狀態（分頁） | 是       | 任一     |
| GET    | `/api/courses/{courseId}/quiz` | 依課程單字隨機出選擇題（`?size=`，預設 5，最少需課程內有 2 個單字） | 是       | 任一     |
| POST   | `/api/courses/{courseId}/quiz/submit` | 提交作答並批改，回傳分數與每題對錯明細，同時記錄一筆測驗紀錄 | 是       | 任一     |
| GET    | `/api/courses/{courseId}/progress` | 取得目前使用者在此課程的歷次測驗分數，新到舊（分頁）  | 是       | 任一     |

標註「分頁」的清單端點支援 Spring Data 標準查詢參數：`?page=0&size=20&sort=欄位名,asc|desc`（皆可省略，預設 `page=0&size=20`），回傳格式為 Spring Data 的 `Page` 物件（內容在 `content` 欄位，另含 `totalElements`、`totalPages` 等分頁資訊）。

註冊請求格式（`RegisterRequest`）：`{ "email": string (必填), "password": string (必填，至少 8 碼), "role": "TEACHER" | "STUDENT" (可省略，預設 STUDENT) }`

課程請求格式（`CourseRequest`）：`{ "title": string (必填), "description": string }`

單字請求格式（`WordRequest`）：`{ "term": string (必填), "meaning": string (必填), "example": string }`

單字進度請求格式（`WordProgressRequest`）：`{ "status": "LEARNING" | "MASTERED" (必填) }`

測驗作答格式（`QuizSubmissionRequest`）：`{ "answers": [{ "wordId": number, "selectedMeaning": string }] }`，`selectedMeaning` 需與出題時 `GET .../quiz` 回傳的選項之一相符（比對時忽略大小寫與前後空白）。

呼叫需要 JWT 的端點時，帶上 `Authorization: Bearer <token>`。

錯誤回應統一由 `GlobalExceptionHandler` 處理，格式為 `{ "timestamp", "status", "error", "message", "path" }`（`@Valid` 驗證失敗時 `message` 會列出各欄位的錯誤原因）。

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"teacher@example.com","password":"password123","role":"TEACHER"}'

curl http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer <上面拿到的 token>"
```

## 安全性現況

- 密碼以 BCrypt 雜湊儲存，JWT 用 HMAC-SHA256 簽章（`JwtService`），有效期預設 24 小時（`jwt.expiration-ms`）。
- `jwt.secret` 目前有內建的開發用預設值，正式環境務必用 `JWT_SECRET` 環境變數覆蓋。
- 除了 `/api/health`、`/api/auth/register`、`/api/auth/login`、`/v3/api-docs/**`、`/swagger-ui/**` 外，其餘 API 都需要帶有效 JWT（`SecurityConfig` 的 `anyRequest().authenticated()`）。
- 角色分為 `TEACHER`（可管理課程/單字內容）與 `STUDENT`（唯讀 + 測驗），在 `SecurityConfig` 用 HTTP method + path 規則搭配 `hasAuthority("ROLE_TEACHER")` 控管，角色由 `JwtAuthFilter` 每次請求即時從資料庫讀取（不寫進 JWT payload，角色異動不用等舊 token 過期）。
- 角色目前由使用者註冊時自行選擇（`RegisterRequest.role`，省略則預設 `STUDENT`），沒有審核或邀請碼機制，任何人都能自行註冊為 `TEACHER`——之後要防濫用需另外設計審核流程。

## API 文件

已加入 `springdoc-openapi-starter-webmvc-ui`（2.8.6，實測與 Spring Boot 4.1.0 相容）：

- Swagger UI：`http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON：`http://localhost:8080/v3/api-docs`

這兩個端點皆對外公開（免登入），JWT 保護的端點可在 Swagger UI 右上角 Authorize 貼上 `Bearer <token>` 後直接測試。

## 開發規範

- 分支策略：`main` 為穩定分支，功能開發於 `feature/*` 分支
- Commit 訊息使用清楚的動詞開頭（例如 `Add`、`Fix`、`Update`）
