package jpabook.springjpa1.mytest;

import jakarta.persistence.EntityManager;
import java.util.List;
import jpabook.springjpa1.domain.Member;
import jpabook.springjpa1.domain.Order;
import jpabook.springjpa1.repsitory.MemberRepository;
import jpabook.springjpa1.repsitory.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class MyOwnTest {

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  OrderRepository orderRepository;

  @Autowired
  EntityManager em;

  // join 사용, LAZY 전략 => member 사용 시점 전에는 member 프록시 객체로 조회
  // 이후 Member 엔티티 조회시 Member 조회 쿼리 날라감
  @Test
  public void joinTest() {

    List<Order> result = em.createQuery("select o from Order o join o.member", Order.class)
        .getResultList();

    System.out.println(result.get(0).getMember().getClass());

  }

  // fetch join 사용, LAZY 전략 => join 할 때 연관된 객체 그래프 탐색해서 한 방 쿼리로 다 가져옴
  // 객체 그래프 탐색해서 가져오기 때문에 프록시 객체가 아닌 진짜 객체 가져옴
  // => 이후 Member 엔티티 사용할 때 따로 쿼리 날라가지 않음
  @Test
  public void fetchJoinTest() {

    List<Order> result = em.createQuery("select o from Order o join fetch o.member", Order.class)
        .getResultList();

    System.out.println(result.get(0).getMember().getClass());


  }

}
