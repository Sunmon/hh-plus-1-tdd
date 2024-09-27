# TDD

## 기능 요구사항

1. 포인트 충전

- [x] 포인트 충전 기능

- [x] 포인트 충전 금액은 0 이상의 정수만 가능.
- [x] 한번에 충전할 수 있는 금액은 최대 10000 포인트 (step1)

2. 포인트 사용

- [x] 포인트 사용 기능
- [x] 잔고가 부족할 경우 포인트 사용에 실패하여야 함 (default)
- [x] 포인트 사용 금액은 0 이상의 정수만 가능.

3. 포인트 조회가

- [x] 포인트 조회 기능

4. 포인트 내역 조회

- [x] 포인트 내역 조회 기능

5. 공통 추가 작업사항

- [x] request에 필요한 파라미터가 누락되는 경우 > 400 bad request가
- [x] 예외코드를 사용자에게 리턴
- [x] 동시에 여러 건의 포인트 충전, 이용 요청이 들어올 경우 순차적으로 처리해야 함 (step1)
- [x] 동시성 제어에 대한 통합 테스트 작성 (step1)


---

동시성처리 과제를 진행하며 고민한 순서대로 정리해둔 내용을 기록합니다.

“ 동시성이란?

동시성은 여러 작업을 동시에 실행하는 법

” 어플리케이션을 실행해서 여러 요청을 동시에 처리하려면?

- 어플리케이션 자체를 여러개 띄우기
- 어플리케이션 내부의 요청을 처리하는 로직을 여러개 띄우기

첫번째, 어플리케이션 자체를 여러개 띄운다는 것은 다중 프로세스를 사용한다는 의미다. 각 어플리케이션끼리 메모리와 자원을 공유하지 않지만, 연결된 외부 인터페이스(예: DB)등은 공유할 수 있다.

두번째, 어플리케이션 내부의 요청을 처리하는 로직을 여러개 띄운다는것은 쓰레드를 사용하여 처리한다는 의미다. 어플리케이션 내부에서 자원을 공유한다.

두 방법 모두 작업간에 공유하는 자원이 존재한다.

“ 공유하는 자원이 있다면 무슨 문제가 발생할 수 있는가?

어플리케이션의 작업이 부정확하게 실행될 수 있다.

예를 들어 포인트 추가기능을 예시로 들어보자.

서버는 포인트를 추가하라는 요청을 받아, 두가지 작업을 연이어 수행한다.

1. 포인트를 추가하려는 유저의 현재 포인트 조회

2. 현재 포인트에 추가 요청이 들어온 양만큼 추가


시나리오
- 유저1의 현재 포인트:100
- 서버1(혹은 쓰레드1)에서 유저1에게 500 포인트를 추가한다.
- 서버2(혹은 쓰레드2)에서 유저1에게 1500 포인트를 추가한다.

서버는 각각 포인트 충전 내의 두가지 작업을 수행한다. 작업 수행 도중 다른 서버로 인해 공유되는 데이터가 변경된다면 정합성이 맞지 않게 된다.

1. 서버1 포인트조회 (현재포인트:100)
2. 서버2 포인트조회 (현재포인트:100)
3. 서버1 포인트 업데이트 (업데이트된 포인트: 100+500)
4. 서버2 포인트 업데이트(업데이트된 포인트:100+1500)

예상=100+500+1500

실제=100+1500

“ 각 작업을 묶어 하나의 단위로 처리하면 되지 않나?

포인트 업데이트의 두 단계를 하나로 묶어 한개의 작업으로 간주한다면, 서로 다른 서버간에 데이터가 혼입될 가능성이 없어지지 않는가?

트랜젝션으로 묶는다면: 다음 작업이 아토믹하게 처리.
- 포인트 조회
- 포인트 업데이트

트랜젝션으로 묶는 것은 작업을 아토믹하게 처리할 뿐, 서로 공유 자원에 동시에 접근하는 것을 막아주지는 않는다. 원자성과 독립성은 보장되지만 그 외는 아니다.

“ 공유자원을 사용하는 작업을 한번에 하나씩만 수행한다면?

서버1이 포인트 추가를 끝낸 뒤  서버2가 추가한다. 서버2가 포인트를 조회할때는 이미 서버1의 데이터가 업데이트 된 이후이므로 위에 서술된 문제가 발생하지 않는다.

“ 작업을 한번에 하나씩만 수행하는 방법은?

크게 세 가지로 나눌 수 있을것같다.

1. 공유자원에 접근하는 기능을 단일쓰레드로 관리한다.
2. 공유자원을 바로 업데이트하는것이 아니라, 업데이트 요청이 들어왔다는 로그를 남겨놓고 로그대로 수행한다. (리눅스 저널처럼)
3. 공유자원에 접근하는 기능을 한번에 하나씩만 사용할 수 있도록 한다 (락, 세마포어)

다중 서버 환경이라면 공유하는 자원이 데이터베이스니 데이터베이스 레벨에서 락을 걸어야 할 것이고, 다중 쓰레드 환경이라면 어플리케이션의 메소드 레벨에서 락을 걸어도 될 것이다.

“ 데이터의 일관성을 보장하면서 성능을 어떻게 보장할 수 있는가?

- 락을 거는 범위를 필요한 부분만으로 최대한 좁힌다
- 데이터를 읽을 때와 쓸 때를 다르게 관리한다
- 서로 공유하는 자원이 없다면 락을 걸지 않는다.
