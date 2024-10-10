package jpabook.springjpa1.api;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.List;
import jpabook.springjpa1.domain.Address;
import jpabook.springjpa1.domain.Order;
import jpabook.springjpa1.domain.OrderItem;
import jpabook.springjpa1.domain.OrderStatus;
import jpabook.springjpa1.repsitory.OrderRepository;
import jpabook.springjpa1.repsitory.OrderSearch;
import jpabook.springjpa1.repsitory.order.query.OrderFlatDto;
import jpabook.springjpa1.repsitory.order.query.OrderItemQueryDto;
import jpabook.springjpa1.repsitory.order.query.OrderQueryDto;
import jpabook.springjpa1.repsitory.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

  private final OrderRepository orderRepository;
  private final OrderQueryRepository orderQueryRepository;

  @GetMapping("/api/v1/orders")
  public List<Order> ordersV1() {
    List<Order> all = orderRepository.findAllByString(new OrderSearch());
    for (Order order : all) {
      order.getMember().getName(); // Lazy 로딩 프록시 객체 초기화
      order.getDelivery().getAddress(); // Lazy 로딩 프록시 객체 초기화
      List<OrderItem> orderItems = order.getOrderItems(); // Lazy 로딩 프록시 객체 초기화

      orderItems.stream().forEach(o -> o.getItem().getName());
    }

    return all;
  }

  @GetMapping("/api/v2/orders")
  public List<OrderDto> ordersV2() {
    List<Order> orders = orderRepository.findAllByString(new OrderSearch());

    return orders.stream()
        .map(OrderDto::new)
        .collect(toList());
  }

  @GetMapping("/api/v3/orders")
  public List<OrderDto> ordersV3() {
    List<Order> orders = orderRepository.findAllWithItem();
    return orders.stream()
        .map(OrderDto::new)
        .collect(toList());
  }

  // order 를 먼저 페이징 처리해서 가져온 후 -> toOne 관계만 fetch 조인 했기 때문에 페이징 처리 가능 -> 뻥튀기 되지 않음
  // order 에 orderItems 넣어줌
  // batch_fetch_size 옵션 사용 (최적화) => sql in 절 사용
  // 기존에는 컬렉션 각각 가져왔지만 ( ex)orderItemId = ? )
  // batch_fetch_size 옵션 넣으면 in 쿼리로 한 번에 가져옴 (batch_size = ? -> ? 갯수 만큼 in 쿼리로 가져옴)
  // 쿼리 호출 수가 1 + N + M => 1 + 1 + 1 by batch_fetch_size 사용하여 in 쿼리로 !!!!
  @GetMapping("/api/v3.1/orders")
  public List<OrderDto> ordersV3_page(
      @RequestParam(value = "offset", defaultValue = "0") int offset,
      @RequestParam(value = "limit", defaultValue = "100") int limit) {
    List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);

    return orders.stream()
        .map(OrderDto::new)
        .collect(toList());
  }

  @GetMapping("/api/v4/orders")
  public List<OrderQueryDto> ordersV4() {
    return orderQueryRepository.findOrderQueryDtos();
  }

  @GetMapping("/api/v5/orders")
  public List<OrderQueryDto> ordersV5() {
    return orderQueryRepository.findAllByDto_optimization();
  }

  @GetMapping("/api/v6/orders")
  public List<OrderQueryDto> ordersV6() {
    List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();
    return flats.stream()
        .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(),
                o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
            mapping(o -> new OrderItemQueryDto(o.getOrderId(),
                o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
        )).entrySet().stream()
        .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
            e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),
            e.getKey().getAddress(), e.getValue()))
        .collect(toList());
  }


  @Data
  static class OrderDto {

    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItemDto> orderItems;

    public OrderDto(Order order) {
      orderId = order.getId();
      name = order.getMember().getName();
      orderDate = order.getOrderDate();
      orderStatus = order.getStatus();
      address = order.getDelivery().getAddress();
      orderItems = order.getOrderItems().stream()
          .map(OrderItemDto::new)
          .collect(toList());
    }
  }

  @Getter
  static class OrderItemDto {

    private String itemName;
    private int orderPrice;
    private int count;

    public OrderItemDto(OrderItem orderItem) {
      itemName = orderItem.getItem().getName();
      orderPrice = orderItem.getOrderPrice();
      count = orderItem.getCount();
    }
  }


}
