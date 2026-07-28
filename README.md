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
        controller/   # REST API 入口（目前：HealthController）
        config/        # 設定（目前：SecurityConfig）
        service/      # 商業邏輯（尚未建立，加功能時再開）
        repository/   # 資料存取層（尚未建立）
        model/        # 實體類別 Entity（尚未建立）
        dto/          # 資料傳輸物件（尚未建立）
    resources/
      application.properties
  test/
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

## 安全性現況

目前 `SecurityConfig` 對所有 API 全開放（`permitAll`），因為登入 / JWT 機制還沒開發。開始做身份驗證功能時記得回來收斂權限規則。

## API 文件

尚未加入 Springdoc OpenAPI：最新版（2.8.6）目前還不相容 Spring Boot 4.1.0，等相容版本釋出後再補上 `springdoc-openapi-starter-webmvc-ui` 依賴。

## 開發規範

- 分支策略：`main` 為穩定分支，功能開發於 `feature/*` 分支
- Commit 訊息使用清楚的動詞開頭（例如 `Add`、`Fix`、`Update`）

## License

TBD
