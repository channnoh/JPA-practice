package jpabook.springjpa1;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

  // Model에 값을 넣어서 View로 넘길 수 있음 (key : value)
  @GetMapping("hello")
  public String hello(Model model) {
    model.addAttribute("data", "hello!!");
    return "hello"; // hello 뒤에 .html이 자동으로 붙어서 resource/template/hello.html 로 이동
  }
}
