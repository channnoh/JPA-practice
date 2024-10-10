package jpabook.springjpa1.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jpabook.springjpa1.domain.Member;
import jpabook.springjpa1.repsitory.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
class MemberServiceTest {

  @Autowired
  MemberService memberService;

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  EntityManager em;

  @Test
  void 회원가입() {
    //given
    Member member = new Member();
    member.setName("kim");

    //when
    Long savedId = memberService.join(member);

    //then
    em.flush();
    assertEquals(member, memberRepository.findOne(savedId));
  }

  @Test
  void 중복_회원_예외() {
    //given
    Member member1 = new Member();
    member1.setName("kim");

    Member member2 = new Member();
    member2.setName("kim");

    //when
    memberService.join(member1);
    try {
      memberService.join(member2); // 예외 발생 해야함
    } catch (IllegalStateException e) {
      return;
    }

    //then
    fail("예외가 발생해야 한다");

  }

}