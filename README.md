# Interparty

Spring Kotlin BE 프로젝트

## 설정

- 데이터 소스 (RDBMS) 설정
  - 로컬 또는 외부 데이터 소스를 연결해 주어야 합니다.
  - `src/main/resources/application-prod.yml` 파일을 생성하여 데이터 소스를 설정합니다.
      ```properties
      spring:
        datasource:
            driver-class-name: {데이터베이스 드라이버 클래스}
            url: {데이터베이스 URL}
            username: {데이터베이스 사용자 이름}
            password: {데이터베이스 사용자 비밀번호}
        jpa:
            properties:
                hibernate:
                    dialect: {사용하는 데이터베이스에 맞는 dialect}
      jwt:
        secret:
            key: {무작위 base64 인코딩 문자열 32자리}
      ```
- Redis 실행 방법
  - 로컬 또는 외부 Redis 서버를 연결해 주어야 합니다.
  - Makefile을 사용해 로컬 Redis 도커 컨테이너를 관리합니다.
  - 아래 명령어는 Make(Linux/Mac) 또는 WSL(Windows)에서 실행 가능합니다.
    - `make up` 로컬 WSL에서 Redis 도커 컨테이너 실행.(최초 실행 시, 자동으로 이미지 설치됨)
    - `make down`실행 중인 Redis 도커 컨테이너 중지.
    - `make ping`서비스 상태 확인 (헬스체크).
    - `make clear` WSL에서 Redis 프로세스 강제 종료(권한 필요).

