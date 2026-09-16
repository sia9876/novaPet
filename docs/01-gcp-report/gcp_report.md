PetClinic GCP 구축 보고서 — 개정판 (최종)
작성일: 2026-09-17 · 대상: 온누리 동물병원 운영진 · 작성: 클라우드 엔지니어링 4인 팀
이 문서는 기존 구축 완료 보고서에 대한 개정 사항을 정리합니다: (1) DB를 매니지드 서비스인 Cloud SQL로 전환, (2) 예약(Reservation) 기능 신규 추가 완료, (3) Ubuntu→Rocky Linux 전환에 따른 기존 코드 호환성 정밀 점검 완료.
1. DB 아키텍처 변경: VM 기반 → Cloud SQL(매니지드)
Web/WAS 계층은 계속 일반 Compute Engine VM을 사용하되, DB 계층만 매니지드인 Cloud SQL로 분리합니다.
1-1. 엔진 선택: Cloud SQL for MySQL 8.0 채택 (PostgreSQL 미채택)
비교 항목
	MySQL 8.0 (채택)
	PostgreSQL (미채택)
	기존 코드 호환성
	mysql-connector-java 8.0.19 — Cloud SQL for MySQL과 즉시 호환
	번들 드라이버가 postgresql 9.4.1211.jre7(2016년, JRE7 타겟)로 매우 구버전. Cloud SQL 연결 안정성을 위해 별도 업그레이드·재검증 필요
	일정 리스크(7일)
	드라이버 교체 불필요 — 일정 내 완료 가능
	드라이버 업그레이드 후 회귀 테스트 필요 — 리스크 있음
	결론: 7일 일정 안에서 가장 리스크가 낮은 선택으로 MySQL을 채택했습니다. PostgreSQL은 장기적 이점(라이선스, IAM DB 인증 등)이 있어 안정화 이후 별도 마이그레이션 검토를 제안합니다.
추가로 확인된 리스크(코드 점검 결과): 실제 채택한 mysql-connector-java 8.0.19 역시 2019년 릴리스로 이후 다수의 CVE가 보고된 버전입니다. 이번 예약 기능 추가와는 별개로, 프로덕션 전환 전 8.0.28 이상으로 드라이버 버전을 올리는 것을 권장합니다.
1-2. 환경 구성: Development 등급 상시 인스턴스 채택 (임시 샌드박스 아님)
GCP 무료/체험 "샌드박스" 모드는 쿼터 제한·SLA 미보장·리소스 자동 회수 위험이 있어 사용하지 않습니다. 대신 아래 구성의 상시 인스턴스를 사용합니다.
* Edition: Enterprise (Enterprise Plus 아님 — 비용 절감)
* 머신 사양: db-custom-2-8192 (2 vCPU / 8GB)
* 가용성: 단일 영역(No HA) — 프로덕션 전환 시 리전 HA로 승급 예정
* 네트워크: Private IP만 사용, WAS 서브넷에서만 접근 허용
* 백업/보호: 자동 백업 + 포인트-인-타임 복구 + 삭제 보호 활성화
선택 이유: 9/20 완료 보고 시연에 실제 사용될 데이터(예약 신청 포함)를 담기 때문에, 데이터가 초기화될 수 있는 일회성 샌드박스 대신 운영 환경과 동일한 구조의 상시 인스턴스로 구성해 신뢰성을 확보했습니다. 프로덕션 승급 시 설정 확장(HA 추가 등)만으로 재구축 없이 전환 가능합니다.
2. 예약(Reservation) 기능 개발 완료
기존 코드 드라이버 업그레이드·재검증 필요
	일정 리스크(7일)
	드라이버 교체 불필요 — 일정 내 완료 가능
	드라이버 업그레이드 후 회귀 테스트 필요 — 리스크 있음
	결론: 7일 일정 안에서 가장 리스크가 낮은 선택으로 MySQL을 채택했습니다. PostgreSQL은 장기적 이점(라이선스, IAM DB 인증 등)이 있어 안정화 이후 별도 마이그레이션 검토를 제안합니다.
추가로 확인된 리스크(코드 점검 결과): 실제 채택한 mysql-connector-java 8.0.19 역시 2019년 릴리스로 이후 다수의 CVE가 보고된 버전입니다. 이번 예약 기능 추가와는 별개로, 프로덕션 전환 전 8.0.28 이상으로 드라이버 버전을 올리는 것을 권장합니다.
1-2. 환경 구성: Development 등급 상시 인스턴스 채택 (임시 샌드박스 아님)
GCP 무료/체험 "샌드박스" 모드는 쿼터 제한·SLA 미보장·리소스 자동 회수 위험이 있어 사용하지 않습니다. 대신 아래 구성의 상시 인스턴스를 사용합니다.
* Edition: Enterprise (Enterprise Plus 아님 — 비용 절감)
* 머신 사양: db-custom-2-8192 (2 vCPU / 8GB)
* 가용성: 단일 영역(No HA) — 프로덕션 전환 시 리전 HA로 승급 예정
* 네트워크: Private IP만 사용, WAS 서브넷에서만 접근 허용
* 백업/보호: 자동 백업 + 포인트-인-타임 복구 + 삭제 보호 활성화
선택 이유: 9/20 완료 보고 시연에 실제 사용될 데이터(예약 신청 포함)를 담기 때문에, 데이터가 초기화될 수 있는 일회성 샌드박스 대신 운영 환경과 동일한 구조의 상시 인스턴스로 구성해 신뢰성을 확보했습니다. 프로덕션 승급 시 설정 확장(HA 추가 등)만으로 재구축 없이 전환 가능합니다.
2. 예약(Reservation) 기능 개발 완료
기존 코드에는 진료 기록(Visit)만 있고 사전 예약 개념이 없었습니다. C:\Users\thddl\novaPet 저장소에 아래와 같이 기능을 추가하고 빌드까지 검증했습니다.
2-1. 신규/수정 파일
신규 생성 11개: model/Reservation.java, model/ReservationStatus.java(REQUESTED/CONFIRMED/CANCELLED), repository/ReservationRepository.java(공통 인터페이스), repository/jdbc/JdbcReservationRepositoryImpl.java, repository/jdbc/JdbcReservationRowMapper.java, repository/jpa/JpaReservationRepositoryImpl.java, repository/springdatajpa/SpringDataReservationRepository.java, web/ReservationController.java, web/VetFormatter.java(수의사 선택 폼 바인딩용), webapp/WEB-INF/jsp/pets/createOrUpdateReservationForm.jsp, webapp/WEB-INF/jsp/reservationList.jsp
수정 9개: model/Pet.java(예약 컬렉션 추가), service/ClinicService.java·ClinicServiceImpl.java(예약 저장/조회 메서드 추가), spring/mvc-core-config.xml(VetFormatter 빈 등록), db/{h2,hsqldb,mysql,postgresql}/schema.sql(reservations 테이블), db/{h2,hsqldb,mysql,postgresql}/data.sql(샘플 예약 3건), messages/messages.properties·messages_de.properties(예약 라벨), webapp/WEB-INF/jsp/owners/ownerDetails.jsp("Book Reservation" 링크), webapp/WEB-INF/tags/menu.tag("Reservations" 메뉴)
기존 코드의 저장소 3종(JDBC/JPA/Spring Data JPA) 구조를 그대로 따라 3종 모두 구현했으며, 운영 환경에서 실제 활성화되는 프로파일은 jpa입니다.
2-2. 예약 기능 동작 흐름
1. 오너 상세 페이지에서 반려동물별 "Book Reservation" 링크 클릭 → 예약 신청 폼 진입(날짜/시간/사유/희망 수의사 선택, 수의사는 미지정 가능)
2. 제출 시 검증 실패면 같은 폼 재표시, 성공 시 저장 후 오너 상세 페이지로 이동. 상태는 기본값 "요청됨(REQUESTED)"
3. 반려동물별 예약 이력은 날짜 내림차순으로 조회 가능
4. 병원 직원은 상단 메뉴 "Reservations"에서 전체 예약(반려동물명·오너명·사유·수의사·상태)을 날짜순으로 한 화면에서 확인
2-3. 빌드 검증 결과
mvnw로 컴파일·패키징을 실행한 결과 BUILD SUCCESS로 petclinic.war(약 40MB)가 정상 생성되었으며, 신규 클래스가 모두 정상 컴파일됨을 확인했습니다. 테스트 스위트 실행은 이번 점검 범위에 제외했습니다(컴파일/패키징만 확인).
3. Ubuntu → Rocky Linux 전환 코드 호환성 — 정밀 점검 완료
이 애플리케이션은 Spring Boot가 아닌 순수 Spring Framework 5.3.9 + JSP + WAR 구조로, 외부 Tomcat 9에 배포됩니다.
3-1. 문제 없음
* 로그/캐시 설정: logback.xml은 콘솔(stdout) 출력만 사용, ehcache.xml은 java.io.tmpdir 사용 — 모두 OS 무관하게 동작
* 소스 코드: Windows 전용 경로, ProcessBuilder, Runtime.exec 등 OS 종속 호출 없음
* LESS→CSS 빌드(wro4j): wro.xml 및 *.less 파일에 OS 종속 경로 없음. Rhino 기반 JS 엔진은 순수 JVM 구현이라 리눅스에서도 동일 동작. 실제 mvnw package 풀빌드에서 정상적으로 CSS 산출물 생성 확인(경고는 기존부터 있던 LESS 소스맵 관련 항목뿐, 이번 변경과 무관)
* Java 1.8 타겟: Rocky Linux 9 AppStream 저장소에 java-1.8.0-openjdk(-devel) 포함되어 설치 자체는 문제 없음. 다만 Rocky 9의 기본 alternatives가 최신 LTS(11/17)를 기본값으로 잡을 수 있어, WAS 실행 시 JAVA_HOME을 8로 명시 지정하거나 최신 JDK로 재컴파일하는 정책을 사전에 결정해야 함
3-2. 조치 필요 (실제 발견된 이슈)
이슈
	내용
	조치
	mvnw 줄바꿈 문제 (실제 확인됨)
	mvnw 셸 스크립트 전체가 CRLF로 저장되어 있고 셔뱅 라인이 "#!/bin/sh\r\n" 형태. 리눅스에서 ./mvnw를 그대로 실행하면 커널이 인터프리터 경로를 "/bin/sh\r"로 해석해 "bad interpreter: No such file or directory" 오류가 발생할 가능성이 매우 높음
	배포 파이프라인에 dos2unix mvnw(또는 sed -i 's/\r$//' mvnw) 실행 후 chmod +x 재부여. Git 저장소에 .gitattributes 추가(mvnw text eol=lf) 권장
	Tomcat 9.0.53 미배포
	Rocky Linux 9 공식 dnf 저장소에 Tomcat 9 패키지 없음
	공식 tarball 수동 설치 + systemd 유닛 직접 구성 (컴퓨트 엔지니어 작업계획서에 반영됨)
	CI/런타임 JDK 버전 불일치 가능성
	.travis.yml은 JDK 11로 빌드하나 컴파일 타겟은 1.8로 설정됨
	Rocky Linux WAS에 배포할 JDK 버전과 빌드 JDK 버전을 일치시키는 정책 결정 필요
	리눅스 환경 실빌드 미검증
	이번 빌드 검증은 Windows(JDK 11) 환경에서 수행됨. mvnw 실행권한 이슈 등은 파일 바이트 분석(정적 점검)으로만 확인
	Rocky Linux 배포 전 실제 리눅스 서버에서 1회 ./mvnw clean package 스모크 테스트 권장(Day 3 컴퓨팅 작업 단계에 포함)
	종합 결론: 애플리케이션 코드 자체의 리눅스 호환성 문제는 없습니다. 유일한 실제 결함은 mvnw 스크립트의 CRLF 줄바꿈이며, 이는 Day 3(컴퓨팅 엔지니어의 VM 셋업) 단계에서 반드시 dos2unix 변환을 거치도록 작업계획서에 반영했습니다. 나머지는 배포 절차상 준비(Tomcat 수동 설치, JDK 버전 정책)의 문제로, 모두 콘솔 작업 세부 계획서에 반영되어 있습니다.