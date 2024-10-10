package jpabook.springjpa1.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("B") // 싱글테이블에서 구분하기 위함
@Getter
@Setter
public class Book extends Item {

  private String author;
  private String isbn;
}
