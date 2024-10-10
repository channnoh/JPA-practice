package jpabook.springjpa1.repsitory.order.simplequery;

import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

  /**
   * Query 용 별도의 Repository
   */

  private final EntityManager em;

  // JPA -> DTO -> fetch join 할 때 원하는 필드만 가져올 수 있음 (new 명령어 사용)
  // 성능 최적화 측면에서는 good, but 재활용, 유지보수성 bad
  public List<OrderSimpleQueryDto> findOrderDtos() {
    return em.createQuery(
            "select new jpabook.springjpa1.repsitory.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)"
                +
                " from Order o" +
                " join o.member m" +
                " join o.delivery d", OrderSimpleQueryDto.class)
        .getResultList();

  }
}
