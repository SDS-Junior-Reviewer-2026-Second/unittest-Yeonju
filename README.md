# unittest-Yeonju

Test Double (Mockito) 과제 - Calculator / Adder 를 이용한 Mock Injection, Stubbing & Verify, AssertJ 실습

## 구성
- `Adder` : 두 정수를 더하는 기능만 제공하는 인터페이스
- `Calculator` : Adder 를 생성자로 주입받아 add / subtract / multiply 를 수행
- `CalculatorTest` : Adder 를 Mockito mock 으로 대체하고 Stubbing/Verify, AssertJ 로 검증

## 실행
```
mvn test
```
