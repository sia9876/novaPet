# PetClinic GCP 마이그레이션 문서

온누리 동물병원 PetClinic 시스템을 GCP 3-Tier(Web/WAS/Compute Engine + Cloud SQL) 아키텍처로 전환한 프로젝트의 산출물 모음입니다.

## 폴더 구성

| 폴더 | 내용 |
|---|---|
| [01-gcp-report](01-gcp-report/gcp_report.md) | 구축 완료 보고서 — DB(Cloud SQL MySQL) 선정 근거, 예약(Reservation) 기능 개발 내역, Rocky Linux 호환성 점검 결과 |
| [02-console-slides](02-console-slides/) | GCP 콘솔 실전 가이드 (38슬라이드, PPTX) — 서비스별 개념·선정 근거·예상 Q&A·콘솔 클릭 순서 |
| [03-failover-drill](03-failover-drill/) | 장애 대응 Failover/Failback 시연 가이드 (16슬라이드, PPTX) — Cloud SQL 장애 조치 중심 11개 시나리오 |
| [04-architecture](04-architecture/) | 전체 아키텍처 다이어그램 (draw.io, Google Cloud 공식 아이콘 세트 `mxgraph.gcp2` 사용) |

## 관련 소스 코드 변경

이 저장소의 애플리케이션 코드(`src/`)에는 위 문서에서 다루는 예약(Reservation) 기능이 함께 반영되어 있습니다.
- `model/Reservation.java`, `model/ReservationStatus.java`
- `repository/**/ *Reservation*` (JDBC / JPA / Spring Data JPA 3종 구현)
- `web/ReservationController.java`, `web/VetFormatter.java`
- `webapp/WEB-INF/jsp/pets/createOrUpdateReservationForm.jsp`, `webapp/WEB-INF/jsp/reservationList.jsp`
- DB 스키마(`db/{h2,hsqldb,mysql,postgresql}/schema.sql`, `data.sql`)에 `reservations` 테이블 추가

## 인프라 요약

- **아키텍처**: External HTTP(S) LB(L7) → Web MIG → Internal TCP LB(L4) → WAS MIG → Cloud SQL(MySQL 8.0)
- **OS**: Rocky Linux 9 (Ubuntu 대비 10년 장기 지원, SELinux 기본 활성화)
- **보안**: VPC 서브넷 분리, 방화벽 5종, 계층별 IAM 서비스 계정, IAP 기반 SSH, Cloud Armor
- **DB**: Cloud SQL for MySQL 8.0, Private IP 전용, HA 미사용(Development 등급) + 자동 백업/삭제 보호

자세한 내용과 각 결정의 근거는 위 폴더의 문서를 참고하세요.
