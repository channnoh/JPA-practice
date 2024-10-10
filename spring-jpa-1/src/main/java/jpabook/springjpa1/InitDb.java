package jpabook.springjpa1;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jpabook.springjpa1.domain.Address;
import jpabook.springjpa1.domain.Delivery;
import jpabook.springjpa1.domain.Member;
import jpabook.springjpa1.domain.Order;
import jpabook.springjpa1.domain.OrderItem;
import jpabook.springjpa1.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitDb {

  private final InitService initService;

  @PostConstruct
  public void init() {
    initService.dbInit1();
    initService.dbInit2();
  }

  @Component
  @Transactional
  @RequiredArgsConstructor
  static class InitService {

    private final EntityManager em;

    public void dbInit1() {
      Member member = createMember("userA", "서울" , "1" , "11");
      em.persist(member);

      Book book1 = createBook("JPA1", 10000, 100);
      em.persist(book1);

      Book book2 = createBook("JPA2", 10000, 100);
      em.persist(book2);

      OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
      OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);

      Delivery delivery = new Delivery();
      delivery.setAddress(member.getAddress());
      Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
      em.persist(order);
    }

    private Book createBook(String bookName, int price, int stockQuantity) {
      Book book = new Book();
      book.setName(bookName);
      book.setPrice(price);
      book.setStockQuantity(stockQuantity);
      return book;
    }

    public void dbInit2() {
      Member member = createMember("userB", "수원" , "2" , "22");
      em.persist(member);

      Book book1 = createBook("SPRING1", 20000, 200);
      em.persist(book1);

      Book book2 = createBook("SPRING2", 40000, 300);
      em.persist(book2);

      OrderItem orderItem1 = OrderItem.createOrderItem(book1, 20000, 3);
      OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 4);

      Delivery delivery = new Delivery();
      delivery.setAddress(member.getAddress());
      Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
      em.persist(order);
    }


    private Member createMember(String username, String city, String street, String zipcode) {
      Member member = new Member();
      member.setName(username);
      member.setAddress(new Address(city, street, zipcode));
      return member;
    }

  }
}

