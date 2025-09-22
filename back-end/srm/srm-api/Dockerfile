# --- Stage 1: Build Stage ---
# 使用包含 JDK 和 Gradle 的映像檔作為建構環境
FROM gradle:8.8-jdk21 AS builder

# 設定工作目錄
WORKDIR /workspace

# 複製整個專案到容器中
# 因為 build.gradle 在根目錄，需要存取其他 module，所以複製整個 srm 專案
COPY . .

# 執行 Gradle build 指令，--no-daemon 適合在 CI/CD 環境中執行
# 只建構 srm-api 模組
RUN chmod +x gradlew && ./gradlew :srm-api:build --no-daemon

# --- Stage 2: Runtime Stage ---
# 使用一個輕量的 JRE 映像檔作為執行環境
FROM eclipse-temurin:21-jre-jammy AS runner

# 設定工作目錄
WORKDIR /app

# 從 'builder' 階段複製建構好的 JAR 檔到目前階段
COPY --from=builder /workspace/srm-api/build/libs/srm-api-0.0.1-SNAPSHOT.jar .

# 開放 8080 port
EXPOSE 8080

# 設定容器啟動時執行的指令
ENTRYPOINT ["java", "-jar", "srm-api-0.0.1-SNAPSHOT.jar"]
