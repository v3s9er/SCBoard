# SCBoard 3.0

세션 로그인 기반의 Spring Boot 게시판입니다. 일반 회원가입과 로그인, Google OAuth 2.0 로그인, 게시글과 댓글 CRUD, 파일 업로드·다운로드, Spring Boot Actuator를 제공합니다.

## 사용 기술

- Java 21
- Spring Boot 3.5.3
- Gradle
- MySQL 8.4
- Thymeleaf, JPA, Flyway
- Docker Compose

## Docker Compose 실행

Docker Desktop을 실행한 뒤, 저장소 루트에서 아래 명령을 실행합니다.

```bash
docker compose -f docker-compose.yml up --build
```

기본값으로 MySQL 컨테이너와 Spring Boot 앱 컨테이너가 함께 실행됩니다. 브라우저에서 <http://localhost:8080>으로 접속합니다.

중지는 다음 명령을 사용합니다.

```bash
docker compose -f docker-compose.yml down
```

DB와 업로드 파일까지 초기화할 때만 다음 명령을 사용합니다.

```bash
docker compose -f docker-compose.yml down -v
```

## Google OAuth 2.0 설정

Google 로그인은 클라이언트 ID와 클라이언트 시크릿을 넣은 경우에만 로그인·회원가입 화면에 표시됩니다. 실제 키는 저장소에 포함하지 않습니다.

`.env.example`을 `.env`로 복사한 뒤 아래 값을 설정합니다.

```env
SPRING_PROFILES_ACTIVE=prod,oauth
GOOGLE_CLIENT_ID=
GOOGLE_CLIENT_SECRET=
```

Google Cloud Console의 승인된 리디렉션 URI는 다음과 같이 등록합니다.

```text
http://localhost:8080/login/oauth2/code/google
```

그 다음 다시 Docker Compose를 실행합니다.

## VS Code 실행

Docker Desktop에서 개발용 MySQL을 먼저 실행합니다.

```bash
docker compose -f compose.yaml up -d
```

VS Code에서 `SCBoard OAuth` 실행 구성을 선택합니다. Google 로그인 설정은 아래 절차를 따릅니다.

### VS Code에서 Google 로그인 실행하기

먼저 `.env.example`을 `.env`로 복사하고 `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`에 발급받은 값을 입력합니다. Docker용 MySQL 설정은 그대로 두어도 됩니다.

그 다음 `.vscode/launch.json`의 `SCBoard OAuth` 실행 구성을 아래처럼 설정합니다. `envFile`이 `.env`의 Google 키를 읽고, `env`의 `dev,oauth` 프로필이 `.env`의 Docker용 프로필보다 우선합니다.

```json
{
  "type": "java",
  "name": "SCBoard OAuth",
  "request": "launch",
  "mainClass": "com.scboard.ScBoardApplication",
  "envFile": "${workspaceFolder}/.env",
  "env": {
    "SPRING_PROFILES_ACTIVE": "dev,oauth"
  }
}
```

`projectName`은 실제 Java 프로젝트 이름과 달라 `ConfigError: The project '...' is not a valid java project` 오류를 낼 수 있으므로 넣지 않습니다.

이후 VS Code에서 `SCBoard OAuth`를 선택하고 `F5`로 실행합니다. 이 구성은 `dev,oauth` 프로필로 실행되므로 `compose.yaml`의 개발용 MySQL을 사용합니다.

Google Cloud Console에는 기본 실행 주소와 동일한 아래 리디렉션 URI를 등록해야 합니다.

```text
http://localhost:8080/login/oauth2/code/google
```

8080 이외의 포트로 실행한다면, Google Cloud Console에 등록한 URI와 `GOOGLE_REDIRECT_URI` 값도 같은 주소로 맞춰야 합니다.

## 확인 주소

- 게시판: <http://localhost:8080/posts>
- Swagger UI: <http://localhost:8080/swagger-ui/index.html>
- Actuator: <http://localhost:8080/actuator>
- Health: <http://localhost:8080/actuator/health>

## 기능

- HttpSession 회원가입, 로그인, 로그아웃
- Google OAuth 2.0 로그인 및 첫 로그인 시 자동 회원 생성
- 게시글 작성, 수정, 삭제
- 파일 업로드 및 다운로드
- 댓글 작성, 수정, 삭제
- 게시글·댓글 작성자만 수정 및 삭제
