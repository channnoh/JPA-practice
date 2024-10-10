package jpabook.springjpa1.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class Address {  // 값 타입은 변경 불가능하게 설계해야 한다.

  private String city;
  private String street;
  private String zipcode;

  // protected -> 변경 불가
  protected Address() {
  }

  public Address(String city, String street, String zipcode) {
    this.city = city;
    this.street = street;
    this.zipcode = zipcode;
  }
}
