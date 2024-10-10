package jpabook.springjpa1.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("M") // 싱글테이블에서 구분하기 위함
@Getter
@Setter
public class Movie extends Item{

  private String director;
  private String actor;

}
