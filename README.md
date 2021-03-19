# 🛒Spring으로 쇼핑몰 만들기
## 📌프로젝트 소개
spring 프레임워크를 활용하여 쇼핑몰 웹사이트를 만들어보는 프로젝트를 진행했습니다.  
I conducted a project to create a shopping mall website using the spring framework.

## 📌주요 기능
1. 메인페이지&로그인/회원가입  
[1] 구매자와 판매자의 로그인과 회원가입 화면을 다르게 구현, 로그인 이후 메인페이지 화면도 다르게 보이도록 구현함.  
- 구매자로 로그인&회원가입  
![ezgif com-gif-maker (3)](https://user-images.githubusercontent.com/66666533/111775297-ebd57f00-88f3-11eb-99d5-f3f700ae3fc9.gif)  
- 판매자로 로그인&회원가입  
![ezgif com-gif-maker (2)](https://user-images.githubusercontent.com/66666533/111775079-a022d580-88f3-11eb-80a7-1996bda0bcf6.gif)  

[2] 로그인 방식은 네이버 소셜로그인 사용.   
[3] 소셜로그인 후 DB에 회원정보가 없으면 회원가입 페이지로, 회원정보가 존재하면 session을 저장한 후 메인페이지로 이동.  
[4] 주소는 도로명주소 API 사용함.  

2. 마이페이지 - 주문 완료/취소 목록 확인  
![ezgif com-gif-maker (7)](https://user-images.githubusercontent.com/66666533/111785224-aae36780-88ff-11eb-90b7-f32c0b1ca4e8.gif)  
[1] 마이페이지에서 주문 완료된 상품들과 취소된 상품들을 확인할 수 있음.  
[2] 페이징 처리를 위해 axios 활용.  

3. 마이페이지 - 회원정보 수정&탈퇴  
![ezgif com-gif-maker (8)](https://user-images.githubusercontent.com/66666533/111786123-cc911e80-8900-11eb-8157-8a15b08cd662.gif)  

4. 상품보기  
![ezgif com-gif-maker (1)](https://user-images.githubusercontent.com/66666533/111776507-6f43a000-88f5-11eb-8d28-78e7dd84fa6d.gif)  
[1] 카테고리 별로 상품 목록을 확인할 수 있음.  
[2] 페이징처리는 axios 활용.  
[3] 재고가 없는 상품에 대해서는 품절된 상품임을 화면에 보여줌.  

5. 장바구니 담기&목록  
![ezgif com-gif-maker (2)](https://user-images.githubusercontent.com/66666533/111777082-24765800-88f6-11eb-8143-f508811749f9.gif)   

6. 상품 주문&주문상세보기  
[1] 바로 주문하기와 장바구니 페이지에서 여러 상품을 담아 주문을 하는 두 가지 방법으로 구현.  
- 바로 주문하기  
![ezgif com-gif-maker (3)](https://user-images.githubusercontent.com/66666533/111780588-edef0c00-88fa-11eb-9d80-b47153eb76cc.gif)  
- 장바구니 페이지에서 여러 상품 한 번에 주문하기  
![ezgif com-gif-maker (4)](https://user-images.githubusercontent.com/66666533/111782018-e0865180-88fb-11eb-844c-7cef9cd360a7.gif)  
[2] 배송지 입력 시 처음 초기 데이터는 현재 로그인 돼 있는 회원 정보임.  

7. 주문 취소하기  
![ezgif com-gif-maker (5)](https://user-images.githubusercontent.com/66666533/111783961-247a5600-88fe-11eb-903d-9b8132b06c36.gif)  
[1] 마이페이지-주문목록에서 주문 완료된 리스트 중 원하는 주문을 취소할 수 있음.  

8. 배송지 변경하기  
![ezgif com-gif-maker (6)](https://user-images.githubusercontent.com/66666533/111784541-dca7fe80-88fe-11eb-9ec1-68770d81b785.gif)  


## 🛠Tool
|FrontEnd|BackEnd|DataBase|
|:---:|:---:|:---:|
|Bootstrap4|Spring 5.0.2|MySQL 8.0<br>Google cloud platform|
- BackEnd dependencies
  ```
    <dependencies>
      <!-- Spring -->
      <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>${org.springframework-version}</version>
        <exclusions>
          <!-- Exclude Commons Logging in favor of SLF4j -->
          <exclusion>
            <groupId>commons-logging</groupId>
            <artifactId>commons-logging</artifactId>
           </exclusion>
        </exclusions>
      </dependency>
      <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-webmvc</artifactId>
        <version>${org.springframework-version}</version>
      </dependency>

      <!-- AspectJ -->
      <dependency>
        <groupId>org.aspectj</groupId>
        <artifactId>aspectjrt</artifactId>
        <version>${org.aspectj-version}</version>
      </dependency>	

      <!-- Logging -->
      <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>![ezgif com-gif-maker (1)](https://user-images.githubusercontent.com/66666533/111774757-41f5f280-88f3-11eb-9618-bf286c511726.gif)
        <version>${org.slf4j-version}</version>
      </dependency>
      <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>jcl-over-slf4j</artifactId>
        <version>${org.slf4j-version}</version>
        <scope>runtime</scope>
      </dependency>
      <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-log4j12</artifactId>
        <version>${org.slf4j-version}</version>
        <scope>runtime</scope>
      </dependency>
      <dependency>
        <groupId>log4j</groupId>
        <artifactId>log4j</artifactId>
        <version>1.2.17</version>
      </dependency>
      <dependency>
        <groupId>org.bgee.log4jdbc-log4j2</groupId>
        <artifactId>log4jdbc-log4j2-jdbc4</artifactId>
        <version>1.16</version>
      </dependency>

      <!-- @Inject -->
      <dependency>
        <groupId>javax.inject</groupId>
        <artifactId>javax.inject</artifactId>
        <version>1</version>
      </dependency>

      <!-- Servlet -->
      <dependency>
        <groupId>javax.servlet</groupId>
        <artifactId>javax.servlet-api</artifactId>
        <version>4.0.0</version>
        <scope>provided</scope>
      </dependency>
      <dependency>
        <groupId>javax.servlet.jsp</groupId>
        <artifactId>jsp-api</artifactId>
        <version>2.1</version>
        <scope>provided</scope>
      </dependency>
      <dependency>
        <groupId>javax.servlet</groupId>
        <artifactId>jstl</artifactId>
        <version>1.2</version>
      </dependency>

      <!-- Test -->
      <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.12</version>
        <scope>test</scope>
      </dependency>

      <!--db-->
      <!-- Mysql Connector -->
      <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.13</version>
      </dependency>

      <dependency>
        <groupId>org.apache.tomcat</groupId>
        <artifactId>tomcat-jdbc</artifactId>
        <version>8.5.27</version>
      </dependency>

      <!-- db 연동과 Mybatis -->
      <dependency>
        <groupId>org.mybatis</groupId>
        <artifactId>mybatis</artifactId>
        <version>3.2.8</version>
      </dependency>

      <!-- Mybatis-Spring -->
      <dependency>
        <groupId>org.mybatis</groupId>
        <artifactId>mybatis-spring</artifactId>
        <version>1.2.2</version>
      </dependency>

      <!-- spring test -->
      <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-test</artifactId>
        <version>${org.springframework-version}</version>
      </dependency>

      <!-- Spring-JDBC -->
      <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-jdbc</artifactId>
        <version>${org.springframework-version}</version>
      </dependency>

      <!-- 트랜잭션 처리  -->
      <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-tx</artifactId>
        <version>${org.springframework-version}</version>
      </dependency>

      <!-- dataSource 위한 common-dbcp -->
      <dependency>
        <groupId>commons-dbcp</groupId>
        <artifactId>commons-dbcp</artifactId>
        <version>1.4</version>
      </dependency>

      <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.0</version>
        <scope>provided</scope>
      </dependency>

      <dependency>
        <groupId>com.github.scribejava</groupId>
        <artifactId>scribejava-core</artifactId>
        <version>2.8.1</version>
      </dependency>

      <dependency>  <!-- 유틸 라이브러리 -->
        <groupId>commons-lang</groupId>
        <artifactId>commons-lang</artifactId>
        <version>2.3</version>
      </dependency>

      <dependency>  <!-- json 라이브러리 -->
        <groupId>com.googlecode.json-simple</groupId>
        <artifactId>json-simple</artifactId>
        <version>1.1.1</version>
      </dependency>

      <dependency>
          <groupId>com.google.code.gson</groupId>
          <artifactId>gson</artifactId>
          <version>2.8.2</version>
      </dependency>

      <dependency>
          <groupId>commons-fileupload</groupId>
          <artifactId>commons-fileupload</artifactId>
          <version>1.3.3</version>
      </dependency>

      <!-- javadata to Json -->
      <dependency>
          <groupId>net.sf.json-lib</groupId>
          <artifactId>json-lib</artifactId>
          <version>2.4</version>

          <classifier>jdk15</classifier>
      </dependency>

      <!-- javax -->
      <dependency>
          <groupId>javax.annotation</groupId>
          <artifactId>javax.annotation-api</artifactId>
          <version>1.3.2</version>
      </dependency>

      <!-- https://mvnrepository.com/artifact/net.coobird/thumbnailator -->
      <dependency>
          <groupId>net.coobird</groupId>
          <artifactId>thumbnailator</artifactId>
          <version>0.4.8</version>
      </dependency>

      <!-- Jackson -->
      <dependency>
          <groupId>com.fasterxml.jackson.core</groupId>
          <artifactId>jackson-annotations</artifactId>
          <version>2.9.8</version>
      </dependency>
      <dependency>
          <groupId>com.fasterxml.jackson.core</groupId>
          <artifactId>jackson-databind</artifactId>
          <version>2.11.2</version>
      </dependency>
      <dependency>
          <groupId>org.codehaus.jackson</groupId>
          <artifactId>jackson-mapper-asl</artifactId>
          <version>1.9.13</version>
      </dependency>
      <dependency>
        <groupId>com.fasterxml.jackson.dataformat</groupId>
        <artifactId>jackson-dataformat-xml</artifactId>
        <version>2.9.6</version>
      </dependency>

      <!-- OAuth2 -->
      <dependency>
          <groupId>com.github.scribejava</groupId>
          <artifactId>scribejava-core</artifactId>
          <version>2.8.1</version>
      </dependency>

      <!-- Axios -->
      <dependency>
          <groupId>org.webjars.npm</groupId>
          <artifactId>axios</artifactId>
          <version>0.21.1</version>
      </dependency>
    </dependencies>
    ```
  
  ## 📌실행방법
  ```
  git clone https://github.com/EunhaKyeong/ShoppingMall.git
  ```
