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

- 使用者註冊 / 登入 / 身份驗證
- 課程與單字內容管理
- 練習題與測驗（含批改邏輯）
- 使用者學習進度追蹤
- 提供給前端 / App 的 REST API

## 專案結構

```
src/
  main/
    java/
      com/languagelearning/
        controller/   # REST API 入口（HealthController、AuthController）
        config/       # 設定（SecurityConfig）
        security/     # JwtService、JwtAuthFilter
        service/      # 商業邏輯（AuthService）
        repository/   # 資料存取層（UserRepository）
        model/        # 實體類別 Entity（User）
        dto/          # 資料傳輸物件（尚未建立課程/單字相關的，加功能時再開）
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

| Method | Path                | 說明                          | 需要 JWT |
|--------|---------------------|-------------------------------|----------|
| GET    | `/api/health`        | 健康檢查                      | 否       |
| POST   | `/api/auth/register`| 註冊，成功回傳 `{ "token": ... }` | 否       |
| POST   | `/api/auth/login`   | 登入，成功回傳 `{ "token": ... }` | 否       |
| GET    | `/api/auth/me`      | 回傳目前登入使用者的 email    | 是       |

呼叫需要 JWT 的端點時，帶上 `Authorization: Bearer <token>`。

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"demo@example.com","password":"password123"}'

curl http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer <上面拿到的 token>"
```

## 安全性現況

- 密碼以 BCrypt 雜湊儲存，JWT 用 HMAC-SHA256 簽章（`JwtService`），有效期預設 24 小時（`jwt.expiration-ms`）。
- `jwt.secret` 目前有內建的開發用預設值，正式環境務必用 `JWT_SECRET` 環境變數覆蓋。
- 除了 `/api/health`、`/api/auth/register`、`/api/auth/login` 外，其餘 API 都需要帶有效 JWT（`SecurityConfig` 的 `anyRequest().authenticated()`）。
- 目前沒有角色 / 權限分級（單一使用者類型），需要的話之後再加。

## API 文件

尚未加入 Springdoc OpenAPI：最新版（2.8.6）目前還不相容 Spring Boot 4.1.0，等相容版本釋出後再補上 `springdoc-openapi-starter-webmvc-ui` 依賴。

## 開發規範

- 分支策略：`main` 為穩定分支，功能開發於 `feature/*` 分支
- Commit 訊息使用清楚的動詞開頭（例如 `Add`、`Fix`、`Update`）

## License

TBD
