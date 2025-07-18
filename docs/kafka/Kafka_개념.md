# Kafka란
대규모 실시간 데이터 스프리밍을 위한 분산 메세징 시스템  
높은 처리량 및 개발 효율을 위한 분산 시스템에서 고가용성과 유연함이 필요할때 사용됨.

# Kafka의 구성 요소
## 1. Producer, Consumer
### Producer
> 메세지를 발행하는 주체
 
### Consumer
> 메세지를 읽는 주체

- Offset: Consumer가 Message를 어디까지 읽었는지 기록한 데이터
- Consumer Group을 통해 분산 가능

## 2. Broker
> 카프카 인스턴트의 단위 

### Controller
> 브로커 모니터링, 리더 선출

### Coordinator
> 컨슈머 그룹 모니터링, 리벨런싱 수행

### Bootstrap Servers
> 카프카 클러스터에 처음 연결될 때 진입점이 되는 브로커들.

- 클라이언트는 전체 클러스터의 메타데이터를 얻어 다른 브로커에도 연결할 수 있음

## 3. Message
> 카프카의 Producer에서 Consumer로 전달되는 데이터.

- Key, Value 형태로 구성됨

## 4. Topic, Partition
### Topic
> 메세지를 분류하는 논리적인 단위

- 여러 Producer가 같은 Topic에 메세지를 발행하고 
- 여러 Consumer가 해당 Topic을 구독 가능

### Partition
> 토픽을 물리적으로 분할한 단위

- Partition 개수 만큼 병렬 처리 가능
- 같은 Key의 Message는 같은 Partition에 저장
- Partition 내의 순서 보장 
- Partition별 독립적인 Offset 관리

#### 파티셔닝 동작 방식
**Key가 있는 경우**: Hash를 활용함  
**Key가 없는 경우**: Round Robin 방식으로 파티션에 균등하게 분배