package jpabook.springjpa1.repsitory;

import jakarta.persistence.EntityManager;
import java.util.List;
import jpabook.springjpa1.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

  private final EntityManager em;

  public void save(Order order) {
    em.persist(order);
  }

  public Order findOne(Long id) {
    return em.find(Order.class, id);
  }

  public List<Order> find() {
    return em.createQuery("select o from Order o", Order.class)
        .getResultList();
  }

  public List<Order> findAllByString(OrderSearch orderSearch) {

    String jpql = "select o from Order o join o.member m ";

    return em.createQuery(jpql, Order.class)
//        .setMaxResults(1000) // 최대 1000건 - 페이징 조건
        .getResultList();

//    return em.createQuery("select o from Order o join o.member m " +
//            "where o.status = :status " +
//            "and m.name like :name", Order.class)
//        .setParameter("status", orderSearch.getOrderStatus())
//        .setParameter("name", orderSearch.getMemberName())
//        .setMaxResults(1000) // 최대 1000건
//        .getResultList();
//
  }

  public List<Order> findAllWithMemberDelivery() {
    return em.createQuery(
        "select o from Order o" +
            " join fetch o.member m" +
            " join fetch o.delivery d", Order.class
    ).getResultList();
  }

  // 컬렉션 fetch 조인 하면 데이터가 many 쪽에 맞게 뻥튀기 됨 (쿼리는 한 방 쿼리)
  // 컬렉션 fetch 조인 하면 페이징 불가능(1 은 상관 x) - 데이터 뻥튀기 되기 때문에
  // 페이징 안 할거면 사용 가능, but 컬렉션 페치조인은 한번만 사용 => 여러번 사용시 데이터 못 맞출수 있음
  // 데이터 뻥튀기 되서 페이징 처리 불가능 하기 때문에 만약에 페이징 쿼리 날리면
  // 데이터베이스에서 데이터 다 끌어와서 애플리케이션 메모리에 올리고
  // 애플리케이션에서 페이징 처리하기 때문에 애플리케이션 warn 발생 -> 메모리 부하 => 사용금지!!! (OutOfMemory)
  // distinct 사용하면 sql distinct + JPA distinct -> JPA 가 애플리케이션 가져와서 같은 엔티티면(id 같으면) 중복제거해줌
  // 중복값 제거 distinct
  // SQL distinct는 열 값이 모두 같아야 중복 제거
  // JPQL의 distinct는 아래에서 OrderId(PK) 값이 같으면 중복 제거
  public List<Order> findAllWithItem() {
    return em.createQuery(
            "select distinct o from Order o" +
                " join fetch o.member m" +
                " join fetch o.delivery d" +
                " join fetch o.orderItems oi" +
                " join fetch oi.item", Order.class)
        .getResultList();
  }

  /**
   * toOne은 fetch 조인으로 한 방 쿼리
   * 컬렉션은 쿼리 따로 for 페이징
   */
  public List<Order> findAllWithMemberDelivery(int offset, int limit) {
    return em.createQuery(
            "select o from Order o" +
                " join fetch o.member m" +
                " join fetch o.delivery d", Order.class)
        .setFirstResult(offset)
        .setMaxResults(limit)
        .getResultList();
  }
}
