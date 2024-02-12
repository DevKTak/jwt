> jwt 공부하기전에 간단하게 정규화, JPA, 클린코드 리마인드~

# 정규화
> 이상현상(대부분 중복)을 없애는 과정, 프로세스   
- 무결성, 단일성...
- 이로인해 정규화가 거듭될수록 DB에 중복된 데이터를 안넣기때문에 `원자성`, `용량`이 줄어듬

**단점**
- 귀찮다. 쿼리 많아진다. 복잡한 연산이 많아진다.
- => 속도가 느려진다. (항상은 아님)   
  
\* **반정규화:** 너무 느려질경우 반대로 정규화

### 제 1 정규화
- 하나의 셀에는 하나의 데이터만

### 제 2 정규화
- 부분 종속을 없애자

### 제 3 정규화
- 종속을 없애자

**`결국엔 1, 2, 3을 나눠서 외우려 하지말고 종속을 없애면 된다.`, `실무에서는 3 정규화면 충분하다.` + BCNF 정도!**

 **참고**
1. 테이블명은 단수, API 복수형
2. 후보키 고려 X
3. PK(2개 이상 컬럼 조합) X

# JPA
### ddl-auto
- 개발: create, create-drop, update
- 테스트: update, validate
- 운영: validate, none

### log level
- trace
- debug: 개발
- info: 운영
- error

# 클린코드
## 의도가 분명하게 이름을 지을 것
// 만료여부   
boolean **`isEnd`**  
boolean **`isExpired`** = false;

cf. 자바 빈 표준: get / set / is / has

// 가장 최근 비밀번호 변경 날짜로부터 로그인한 현재까지의 날짜 차이 수
int **`daysSinceLastChangePassword`**;

## 모호한 단어는 포함하지 말 것
List\<Authority\> authorityList; // 자료형과 중복
List\<Authority\> **`authorities`**;

cf. cmd, biz, ctl, msg, pw, impl, config ... 
너무 명확한 변수기에 써도는 되지만 명확하다는 기준이 애매모호하기에 난 안쓰도록 하겠다.

## 보편적인 이름은 지양할 것
public int getRectangleSum() {
    ❌ int result, sum;   
    ⭕️ int **`rectangleSum`**;

    return rectangleSum;
}

// 보편적으로 tmp를 사용한것이 아니라 진짜로 임시로 쓰는 변수라 가능
if (right < left) {
    **`tmp`** = right;
    right = left;
    left = **`tmp`**;
}

## 불필요한 단어는 지양할 것
Customer **`customerInfo`**;
Account **`accountData`**;   

불필요한 `Info, Data` 제거

## 발음하기 쉬운 이름 사용할 것
// generate year, month, day, hour, minute, secound
public void genymdhms() {}

// 리턴 타입이 void 라는 것은 멤버변수에 값을 넣어준다는 함축적인 의미를 내포한다.

// YYYYMMDD 
// 패턴 같은 경우는 메서드 이름에 나타낼 수 없기 때문에 주석 사용 가능!
public void 
**`generateDate`**() {
    datetime = ...
}

## 중첩 반복문 인덱스 이름은 구체적으로

for (int i = 0; i < ...) {
    for (int j = 0; ...)
}

for (int **`userIdx`**) {
    for (int **`memberIdx`**)
}

## 굳이 안 적어도 아는 말은 적지 않기
String m_strTitle = "Sheet";

String **`title`** = "Sheet";

## 부정보다는 긍정
❌ boolean unsableSSL = true;   
✅ boolean canAbleSSL = true;   
✅ boolean isAbleSSL = true;

ex. ✅ boolean isExpired = true;   
❌ boolean isNotExpired = false;

❌ if (!debug)
✅ if (debug)

✅ if (pets.hasAnimal("pinkElphant"))   

## 이름 지어보기
클래스명: 치킨 집 => ChickenStore, ChickenRestaurant

필드
- 매장 이름: String => storeName / restaurantName -> **`name 클래스가 이미 이름을 가지고 있기 때문에 name으로도 가능`**
- 매장 오픈일: Date => openDate
- 사장님 이름: String => ownerName / ceoName / bossName / masterName **`예를들어 사장님에 대한 개인정보 등 넣을 정보가 많을 경우, 사장님을 객체로 바꿀 수 있다. Owner 등으로 사용 가능`**

메소드
- 매장 정보 출력: void => printInfo(Data) / showInfo => **(Override) toString()**
- 주문 접수: void => acceptOrder()
- 주문 거절: void => rejectOrder()

**`접수에 take, get, receive, accept 등이 있지만 애매할땐 반대되는 개념을 생각해서 짝을 맞춰주자`**

## 어떻게 바꿀 수 있는지 생각해보기 (메소드 추출 + 호출 순서)
**변경 전**
```java
void func() {
    ...
    // compute score
    score = a * b + c;
    score -= discount;
}
```

**변경 후**
```java
void func() {
    ...
    computeScore();
}

void computeScore() {
    score = a * b + c;
    score -= discount;
}
```
  1. **`메소드를 추출`** 한다.
  2. func()에서 computeScore();가 나오고 구현부를 보는 **`순서`** 가 읽기 편하다.

## 반환은 언제 하는게 좋을까
✅ 가능한 빨리, 참조 변수면 무조건 null 체크해서 바로 리턴
❌ 제일 끝에서 한번에

## 코드 포맷 맞추기
1. **K & R (국내 자바에서 많이 사용하는 스타일)**
```java
public class Person {
    public void getNmae() {
        if (...) {

        }
    }
}
```

2. **BSD (JS에서 많이 사용하는 스타일)**
```javascript
public class Person 
{
    public void getName()
    {
        if (...)
        {
            
        }
    }
}
```
✔︎ 한 클래스당 200자 내외로 하자.

## null
🧐 null 체크의 주체는 누구여야 할까?

❌ 널을 확인해놓고 service로 넘겨서 널 체크의 주체를 전가 하는 방법(책임을 안지고 null을 던짐, 프로그램 통틀어서 null을 던지는 것은 있을 수 없는 일!)
```java
public class UserRepository {
    
    EntityManager em;

    public List<User> getUsers() {
        List<User> users = em.findAll();

        if (users.size() == 0) {
            // return null;
        }
    }
}
```

✅ null을 생성하거나 처음만난 주체는 책임을 져야 합니다.
```java
public class UserRepository {
    
    EntityManager em;

    public List<User> getUsers() {
        List<User> users = em.findAll();

        if (users.size() == 0) {
            1) throw new DataNotFoundException();
            2) return new List<>
        }
    }
}
```

<img width="605" alt="image" src="https://github.com/DevKTak/jwt/assets/68748397/d5a696d4-d0fd-4905-8322-fde6bea0d385">
 
 <br>
 
 # Spring Security
 UserDetailsService: 시큐리가 모든 유저를 알지 못하기 때문에 유저들의 인터페이스 유저 디테일즈를 마련해 둔 것

## [SpringBoot 2에서 3으로 올릴 때, 주의 사항]
1. 프로젝트 JDK 버전을 17 이상으로 올립니다.
2. SpringBoot 버전을 순차적으로 (2.5 > 2.6 > 2.7) 업그레이드 합니다.
3. javaEE -> Jakarta EE로 변경하기 위해, import javax. 패키지를 import javkarta.로 변경합니다. (한땀한땀 변경해야함..ㅎ…)
4. Deprecated된 코드와 설정을 확인하세요.
5. (옵션) AntPathMatcher를 사용중이라면, PathPatternParser로 변경하세요.
6. build.gradle을 열어서 SpringBoot 버전을 3으로 마이그레이션 하세요.
