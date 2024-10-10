package jpabook.springjpa1.repsitory.order.query;


import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

  private final EntityManager em;

  /**
   * JPA DTO 직접 조회
   */

  /**
   * 데이터베이스에서 가져올 때 new Projection 사용해서 DTO 로 조회
   */
  public List<OrderQueryDto> findOrderQueryDtos() {
    List<OrderQueryDto> result = findOrders();

    result.forEach(o -> {
      List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
      o.setOrderItems(orderItems);
    });

    return result;
  }

  private List<OrderItemQueryDto> findOrderItems(Long orderId) {
    return em.createQuery(
            "select new jpabook.springjpa1.repsitory.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)"
                +
                " from OrderItem oi" +
                " join oi.item i" +
                " where oi.order.id = :orderId", OrderItemQueryDto.class)
        .setParameter("orderId", orderId)
        .getResultList();

  }

  // new projection 사용 -> DTO 로 조회
  private List<OrderQueryDto> findOrders() {
    return em.createQuery(
            "select new jpabook.springjpa1.repsitory.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)"
                +
                " from Order o" +
                " join o.member m" +
                " join o.delivery d", OrderQueryDto.class)
        .getResultList();
  }

  public List<OrderQueryDto> findAllByDto_optimization() {
    List<OrderQueryDto> result = findOrders();

    List<Long> orderIds = toOrderIds(result);
    Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(orderIds);

    result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

    return result;
  }

  private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
    List<OrderItemQueryDto> orderItems = em.createQuery(
            "select new jpabook.springjpa1.repsitory.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)"
                +
                " from OrderItem oi" +
                " join oi.item i" +
                " where oi.order.id in :orderIds", OrderItemQueryDto.class)
        .setParameter("orderIds", orderIds)
        .getResultList();

    Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
        .collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));
    return orderItemMap;
  }

  private static List<Long> toOrderIds(List<OrderQueryDto> result) {
    List<Long> orderIds = result.stream()
        .map(o -> o.getOrderId())
        .collect(Collectors.toList());
    return orderIds;
  }

  public List<OrderFlatDto> findAllByDto_flat() {
    return em.createQuery(
        "select new " +
            " jpabook.springjpa1.repsitory.order.query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count) " +
            " from Order o" +
            " join o.member m" +
            " join o.delivery d" +
            " join o.orderItems oi" +
            " join oi.item i", OrderFlatDto.class)
        .getResultList();
  }

}

