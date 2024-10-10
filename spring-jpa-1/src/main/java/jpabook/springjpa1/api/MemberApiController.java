package jpabook.springjpa1.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;
import jpabook.springjpa1.domain.Member;
import jpabook.springjpa1.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

  private final MemberService memberService;

  // Entity 그대로 반환하게 되면 필드 변경 되었을 때 API 스펙이 바뀔 수 있고, 외부로 Entity 가 그대로 노출되는 문제가 발생한다.
  @GetMapping("/api/v1/members")
  public List<Member> membersV1() {
    return memberService.findMembers();
  }


  // Member 엔티티 말고 MemberDto 로 반환 + List 한번 감싸서 반환(이후 json 포맷 깨지 않고 추가 가능)
  @GetMapping("/api/v2/members")
  public Result membersV2() {
    List<Member> findMembers = memberService.findMembers();
    List<MemberDto> collect = findMembers.stream()
        .map(m -> new MemberDto(m.getName()))
        .collect(Collectors.toList());

    return new Result(collect);
  }

  @Data
  @AllArgsConstructor
  static class MemberDto {
    private String name;
  }

  @Data
  @AllArgsConstructor
  static class Result<T> {

    private T data;
  }

  // 회원 가입 API, 멤버 엔티티를 받아와서 별로임
  @PostMapping("/api/v1/members")
  public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
    Long id = memberService.join(member);
    return new CreateMemberResponse(id);
  }

  // @RequestBody 는 Http Body JSON -> Java Object by MessageConvertor
  @PostMapping("/api/v2/members")
  public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {

    Member member = new Member();
    member.setName(request.getName());

    Long memberId = memberService.join(member);
    return new CreateMemberResponse(memberId);
  }

  @PutMapping("/api/v2/members/{id}")
  public UpdateMemberResponse updateMemberV2(
      @PathVariable("id") Long id,
      @RequestBody @Valid UpdateMemberRequest request) {

    memberService.update(id, request.getName());
    Member findMember = memberService.findOne(id);
    return new UpdateMemberResponse(findMember.getId(), findMember.getName());
  }

  @Data
  static class UpdateMemberRequest {

    private String name;

  }

  @Data
  @AllArgsConstructor
  static class UpdateMemberResponse {

    private Long id;
    private String name;

  }

  @Data
  static class CreateMemberRequest {

    @NotEmpty
    private String name;
  }

  @Data
  @AllArgsConstructor
  static class CreateMemberResponse {

    private Long id;

  }
}
