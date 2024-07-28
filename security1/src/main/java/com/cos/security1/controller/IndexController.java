package controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // view를 리턴
public class IndexController {
    // 컨트롤러    
    // localhost:8080/
    // localhost:8080
    @GetMapping({"","/"})
    // 머스테치 :
    public String index(){
        return "index";
    }
}