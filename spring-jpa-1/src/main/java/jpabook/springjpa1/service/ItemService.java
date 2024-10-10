package jpabook.springjpa1.service;

import java.util.List;
import jpabook.springjpa1.domain.item.Item;
import jpabook.springjpa1.repsitory.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service // Component scan 대상이 되어 bean 등록
@Transactional(readOnly = true) // 읽기 전용 transaction, 영속성 컨텍스트에서 관리 x
@RequiredArgsConstructor // DI 생성자 주입
public class ItemService {

  private final ItemRepository itemRepository;

  @Transactional // 쓰기 Transaction 명시
  public void saveItem(Item item) {
    itemRepository.save(item);
  }

  public List<Item> findItems() {
    return itemRepository.findAll();
  }

  public Item findOne(Long id) {
    return itemRepository.findOne(id);
  }
}
