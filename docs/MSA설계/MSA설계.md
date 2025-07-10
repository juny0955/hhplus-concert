# MSA 설계

## 배포 단위
- Concert Service
  - 콘서트 정보
  - 콘서트 랭킹 
  - 대기열 토큰 관리
- Reservation Service
  - 예약 처리
  - 좌석 임시 배정
- Payment Service
  - 결제 처리
- User Service
  - 사용자 관리
  - 포인트 충전/차감

## 분리 후 트랜잭션 처리 문제점
### 좌석 예약
```
좌석 예약 {
  예약 정보 생성 + 좌석 임시 배정 (Reservation Service)
  좌석 상태 변경 (Concert Service)
  결제 정보 생성 (Payment Service)
}
```
### 예약 결제
```
예약 결제 {
  유저 포인트 차감 (User Service)
  예약 상태 변경 + 좌석 임시 배정 해제 (Reservation Service)
  좌석 상태 변경 + 대기열 토큰 만료 (Concert Service)
  결제 상태 변경 (Payment Service)
}
```

## 해결 방안 (코레오그래피 사가 패턴)
### 좌석 예약

### 예약 결제

