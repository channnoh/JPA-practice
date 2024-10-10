package jpabook.springjpa1.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import jakarta.persistence.EntityManager;
import java.util.List;
import javax.security.auth.login.AccountException;
import jpabook.springjpa1.domain.Address;
import jpabook.springjpa1.domain.Member;
import jpabook.springjpa1.domain.Order;
import jpabook.springjpa1.domain.OrderStatus;
import jpabook.springjpa1.domain.item.Book;
import jpabook.springjpa1.domain.item.Item;
import jpabook.springjpa1.exception.NotEnoughStockException;
import jpabook.springjpa1.repsitory.OrderRepository;
import jpabook.springjpa1.repsitory.OrderSearch;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class OrderServiceTest {

  @Autowired
  EntityManager em;

  @Autowired
  OrderService orderService;

  @Autowired
  OrderRepository orderRepository;

  @Test
  void 상품주문() {
    //given
    Member member = createMember();

    Book book = createBook("시골 JPA", 10000, 10);

    int orderCount = 2;

    //when
    Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

    //then
    Order getOrder = orderRepository.findOne(orderId);

    assertEquals(OrderStatus.ORDER, getOrder.getStatus(), "상품 주문시 상태는 ORDER");
    assertEquals(1, getOrder.getOrderItems().size(), "주문한 상품 종류 수가 정확해야 한다.");
    assertEquals(10000 * orderCount, getOrder.getTotalPrice(), "주문 가격은 가격 * 수량이다.");
    assertEquals(8, book.getStockQuantity(), "주문 수량만큼 재고가 줄어야 한다.");

  }


  @Test
  void 상품주문_재고수량초과() {
    //given
    Member member = createMember();
    Item item = createBook("시골 JPA", 10000, 10);

    int orderCount = 11;

    //when
    NotEnoughStockException exception = assertThrows(NotEnoughStockException.class,
        () -> orderService.order(member.getId(), item.getId(), orderCount));

    //then
    assertEquals("need more stock", exception.getMessage());
//    fail("재고 수/량 부족 예외가 발생해야 합니다.");
  }


  @Test
  void 주문취소() {
    //given
    Member member = createMember();
    Book item = createBook("시골 JPA", 10000, 10);

    int orderCount = 2;

    Long orderId = orderService.order(member.getId(), item.getId(), orderCount);
    //when
    orderService.cancelOrder(orderId);

    //then
    Order getOrder = orderRepository.findOne(orderId);

    assertEquals(OrderStatus.CANCEL, getOrder.getStatus(), "주문 취소시 상태는 CANCEL 이다.");
    assertEquals(10, item.getStockQuantity(), "주문이 취소된 상품은 그 만큼 재고가 증가해야 한다.");
  }

  private Book createBook(String name, int price, int stockQuantity) {
    Book book = new Book();
    book.setName(name);
    book.setPrice(price);
    book.setStockQuantity(stockQuantity);
    em.persist(book);
    return book;
  }

  private Member createMember() {
    Member member = new Member();
    member.setName("회원1");
    member.setAddress(new Address("서울", "강가", "123-123"));
    em.persist(member);
    return member;
  }

  @Test
  public void joinTest() {

    Member member1 = createMember();
    Member member2 = createMember();
    Book book = createBook("1", 1000, 100);

    orderService.order(member1.getId(), book.getId(), 10);
    orderService.order(member2.getId(), book.getId(), 10);

    em.flush();
    em.clear();

    List<Order> orders = orderRepository.find();

    for (Order order : orders) {
      System.out.println(order.getId());
      System.out.println(order.getMember().getName());
    }


  }

}
