package jeans.ko.Controller;

import jeans.ko.Dao.IBoardDao;
import jeans.ko.Dto.BoardDto;
import jeans.ko.Service.BoardService;
import jeans.ko.Service.IBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Map;

@Controller
public class LookController {

    @Autowired
    HttpSession session;

    @Autowired
    IBoardService boardService;
    @Autowired
    IBoardDao boardDao;

    //게시판 작성페이지
    @RequestMapping("/look_write")
    public String writeForm()
    { return "look_write"; }

    //게시판 상세보기
    @RequestMapping("/view")
    public String view(@RequestParam("look_num")int look_num ,Model model)
    {
        boardDao.countUpdate(look_num); //글상세보기 하면 조회수 증가
        model.addAttribute("view",boardDao.view(look_num));
        return "look_info";
    }

    //삭제
    @RequestMapping("/delete")
    public String delete(HttpServletRequest request)
    {
        String look_num = request.getParameter("look_num");
        String look_viewUserId = request.getParameter("look_viewUserId");
        int num = Integer.parseInt(look_num); //String -> Int

        boardService.delete(num,look_viewUserId);

        return "redirect:/main";

    }

    //게시판 작성값 처리
    @PostMapping("/boardWriteRequest") //커멘드 객체로 값받아옴 BindingResult 는 오류값 출력
    public String boardWrite(@Valid BoardDto boardDto, BindingResult result) {

        if(result.hasErrors()) {
            if(result.getFieldError("title")!=null)
                System.out.println(result.getFieldError("title").getDefaultMessage());

            if(result.getFieldError("season")!=null)
                System.out.println(result.getFieldError("season").getDefaultMessage());

            if(result.getFieldError("look_public")!=null) {
                System.out.println(result.getFieldError("look_public").getDefaultMessage());
            }
            if(result.getFieldError("memo")!=null)
                System.out.println(result.getFieldError("memo").getDefaultMessage());

            if(result.getFieldError("tag")!=null)
                System.out.println(result.getFieldError("tag").getDefaultMessage());

            //입력안된오류전달
            session.setAttribute("error","입력안된사항이 있습니다");

            return "look_write";  //다시작성하기
        }

        boardService.insert(boardDto);

        return "redirect:/main"; //게시판 작성해서 재요청
    }

}


