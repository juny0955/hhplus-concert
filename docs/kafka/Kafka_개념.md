# Kafka란
대규모 실시간 데이터 스프리밍을 위한 분산 메세징 시스템  
높은 처리량 및 개발 효율을 위한 분산 시스템에서 고가용성과 유연함이 필요할때 사용됨. 

# Kafka의 구성 요소
1. Producer, Consumer
   - Producer: 메세지를 발행하는 주체
   - Consumer: 메세지를 읽는 주체
2. Broker: 카프카 인스턴트의 단위
   - Controller: 브로커 모니터링, 리더
   - Coordinator: 컨슈머 그룹 관리, 리벨런싱 수행
3. Topic, Partition
    - Topic: 메세지를 분류하는 논리적인 단위
    - Partition: 토픽을 물리적으로 분할한 단위
      - 특징:
        - Partition 개수 만큼 병렬 처리 가능
        - 같은 Key의 메세지는 같은 파티션에 저장
        - 파티션내의 순서 보장 
