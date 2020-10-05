package jeans.ko.Controller;


import jeans.ko.Dao.IBoardDao;
import jeans.ko.Dao.IMoodDao;
import jeans.ko.Dao.IPreferenceDao;
import jeans.ko.Dto.BoardDto;
import jeans.ko.Dto.MoodDto;
import jeans.ko.Service.CommentService;
import jeans.ko.Service.IBoardService;
import jeans.ko.Service.IPretreatmentService;
import jeans.ko.Service.IUtilService;
import jeans.ko.exception.NotFoundException;
import jeans.ko.exception.UnauthorizedException;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

@Controller
public class LookController {

    private Logger logger = LoggerFactory.getLogger(LookController.class);

    @Autowired
    HttpSession session;
    @Autowired
    CommentService commentService;
    @Autowired
    IBoardService boardService;
    @Autowired
    IPretreatmentService pretreatmentService;
    @Autowired
    IBoardDao boardDao;
    @Autowired
    IMoodDao moodDao;
    @Autowired
    IUtilService utilService;
    @Autowired
    IPreferenceDao preferenceDao;

    @Value("${directory}")
    private String uploadPath;

    @Value("${route}")
    private String route;

    //게시판 작성페이지 이동
    @RequestMapping("/look_write")
    public String look_write() {

        return "look_write";
    }

    //게시판 상세보기 model and view : 웹용
    @RequestMapping("/look")
    public String view(@RequestParam("look_num") int look_num, Model model) {

        boardDao.countUpdate(look_num); //글상세보기 하면 조회수 증가
        model.addAttribute("view", boardDao.view(look_num)); //게시글정보가져오기
        model.addAttribute("mood", moodDao.getMooddto(look_num));//글의 무드 타입

        return "look_info";
    }

    //게시판 수정 페이지 이동 model and view : 웹용
    @RequestMapping("/lookModify")
    public String lookModify(@RequestParam("look_num") int look_num, Model model) {

        BoardDto boardDto = boardDao.view(look_num);
        model.addAttribute("view", boardDto); //게시글정보 가져오기
        model.addAttribute("mood", moodDao.getMooddto(look_num));//글의 무드 타입

        return "lookModify";
    }

    @ResponseBody
    @GetMapping("/looksList/{look_num}")  //룩 전체 리스트
    public List<BoardDto> searchAllLook(@PathVariable int look_num) {

        if (boardDao.list(look_num) == null) {
            //게시글 이 없으면 not found 404 에러
            throw new NotFoundException(String.format("board not found"));
        } else {
            return boardDao.list(look_num);
        }
    }

    @GetMapping("/searchlist")
    public String searchPage(@RequestParam(value="searchOption") String searchOption,@RequestParam(value="keyword") String keyword,Model model){
        model.addAttribute("searchOption",searchOption);
        model.addAttribute("keyword",keyword);
        return "search_list";
    }

    @ResponseBody
    @GetMapping("/search/{searchOption}/{keyword}/{looknum}")
    public List<BoardDto> search(@PathVariable String searchOption, @PathVariable String keyword, @PathVariable(required = false) int looknum) {
        List<BoardDto> search = boardDao.searchList(searchOption, keyword,looknum);
        return search;
    }

    @GetMapping("/preferencelist")
    public String preferencePage(){
        //첫번째 매개변수 userid들을 어떻게 받을까.
        return "preference_list";
    }

    @ResponseBody
    @GetMapping("/preference/{userid1}/{userid2}/{userid3}/{userid4}/{userid5}/{looknum}")
    public List<BoardDto> preference(@PathVariable String userid1,@PathVariable String userid2,@PathVariable String userid3,@PathVariable String userid4,@PathVariable String userid5,@PathVariable(required = false) int looknum){
        List<BoardDto>preference=boardDao.preferenceList(userid1,userid2,userid3,userid4,userid5,looknum);
        return preference;
    }

    @ResponseBody
    @GetMapping("/looks/{id}") //룩상세보기 안드로이드에 값주게 json 데이터만 넘기는용
    public HashMap<String, Object> searchLook(@PathVariable int id) {   //looks/1   looks/3  -->String으로 오는데 int id 해서 int 로 변환해서 받음
        HashMap<String, Object> map = new HashMap<String, Object>();
        //게시글 가져오기
        BoardDto boardDto = boardDao.view(id);
        //찬영이랑 테스트. 무드DTO 리스트들을 넘긴다.
        List<MoodDto> moodDtoList = moodDao.getMooddto(id);
        if (boardDto == null) {
            //게시글이 없으면 not found 에러 return
            throw new NotFoundException(String.format("ID[%s] not found", id));
        }
        map.put("look", boardDto); //게시글 가져오기
        map.put("moodlist", moodDtoList);//해쉬맵에 무드DTO리스트 추가

        boardDao.countUpdate(id); //글상세보기 하면 조회수 증가

        return map;
    }

    //삭제
    @ResponseBody
    @DeleteMapping("/looks/{id}")
    public void deleteLook(@PathVariable int id) throws Exception {

        //게시글이 먼저 있는지 확인
        BoardDto boardDto = boardDao.view(id);

        if (boardDto == null) {
            //찾는 게시글이없으므로 Not found 오류 보내기
            throw new NotFoundException(String.format("ID[%s] not found", id));
        }
        if (session.getAttribute("userid") == null ||
                session.getAttribute("userid").equals(boardDto.getFk_userid_user_userid()) == false) {
            //로그인한 아이디와 작성자 아이디가 달라서 권한없음 오류보냄
            throw new UnauthorizedException(String.format("unauthorized you"));
        } else {
            //로그인 아이디 와 작성자 아이디 가 같아서 글삭제
            boardService.delete(id);
        }
    }

    //룩게시판 작성
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/looks")
    public BoardDto boardWrite(@RequestPart("BoardDto") BoardDto boardDto, @RequestPart(value = "MoodDto", required = false) List<MoodDto> moodDtos, @RequestPart("files") List<MultipartFile> files) throws Exception {
        if (session.getAttribute("userid") == null) {
            //서버로 바로접근하는 경우 아이디값 없으면 클라이언트 권한없음 오류보냄
            throw new UnauthorizedException(String.format("unauthorized you"));
        }
        //게시글등록
        boardService.insert(boardDto, moodDtos, files);

        int looknum = boardDto.getLook_num();
        String userid = (String) session.getAttribute("userid");

        preferenceDao.insertPrefer(looknum, userid);
        pretreatmentService.countUp(looknum, userid);

        //selectKey로 등록된 게시글 가져온 기본키로 등록된 게시글 정보보내줌 새롭게 추가되 댓글이없으므로 게시글만넘김
        return boardDao.view(boardDto.getLook_num());
    }

    //룩게시판 수정
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping("/looks")
    public BoardDto boardModify(@RequestPart("BoardDto") BoardDto modifyBoardDto, @RequestPart(value = "MoodDto", required = false) List<MoodDto> moodDtos, @RequestPart("files") List<MultipartFile> files) throws Exception {

        //넘어온 값에 기본키id 값으로 게시글작성자 id 와 기본키넘버값 가져오기
        String lookId = boardDao.view(modifyBoardDto.getLook_num()).getFk_userid_user_userid();
        int lookNum = boardDao.view(modifyBoardDto.getLook_num()).getLook_num();
        if (lookId == null) {
            //수정할 게시글이 없으므로 not found 에러 보냄
            throw new NotFoundException(String.format("lookNum[%s] not found", modifyBoardDto.getLook_num()));
        }
        if (session.getAttribute("userid").equals(lookId)) {
            //로그인한 아이디와 수정할려는 게시글 작성자 아이디 비교하여 같으면 게시글수정
            boardService.update(modifyBoardDto, moodDtos, files);
            //수정된 게시글 정보 넘겨주기
            return boardDao.view(lookNum);
        } else {
            //게시글 작성자 아디이와 로그인한 아이디가 다르면 권한없음 오류 보냄
            throw new UnauthorizedException(String.format("unauthorized you"));
        }
    }

    //룩게시판 조회회

    @ResponseBody
    @PostMapping("/likey/looknum/{num}/user/{id}")
    public ResponseEntity<String> likey(@PathVariable("num") String number, @PathVariable("id") String id) {
        int num = Integer.parseInt(number);

        if (preferenceDao.getUser(num, id) != 0) {
            return new ResponseEntity("이미 좋아요 처리를 하셨습니다", HttpStatus.OK);
        } else {
            preferenceDao.insertPrefer(num, id);
            pretreatmentService.countUp(num, id);
            boardDao.likeUpdate(num);
            return new ResponseEntity("좋아요!", HttpStatus.OK);
        }
    }

    @GetMapping("/displayLthumbnail/{look_num}")
    public ResponseEntity<byte[]> displayLthumbnail(@PathVariable int look_num) throws Exception {
        InputStream in = null;
        ResponseEntity<byte[]> entity = null;
        entity = null;
        List<String> datepath = utilService.looknumtoPath(look_num);

        //워낙 간단한 메소드니 바로 boardDao를 쓰겠다. 룩번호를 입력하고 해당 룩번호에서 한개의 사진을 불러온다.
        String picture = boardDao.getonePicturename(look_num);

        HttpHeaders headers = new HttpHeaders();
        try {
            in = new FileInputStream(datepath.get(0) + route + datepath.get(1) + route + datepath.get(2) + route + datepath.get(3) + route + picture);
            entity = new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.OK);
        } catch (Exception e) {
            entity = new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
        } finally {
            in.close();
        }
        return entity;
    }

    @GetMapping("/displayInthumbnail/{look_num}")
    public ResponseEntity<ArrayList<byte[]>> displayInthumbnail(@PathVariable int look_num) throws Exception {
        //InputStream in =null;
        ArrayList<byte[]> in = new ArrayList<>();

        ResponseEntity<ArrayList<byte[]>> entity = new ResponseEntity<ArrayList<byte[]>>(HttpStatus.OK);
        List<String> datepath = utilService.looknumtoPath(look_num);
        List<String> allpicture = boardDao.getallPicturename(look_num);
        HttpHeaders headers = new HttpHeaders();
        InputStream inp = null;

        try {
            for (int i = 0; i < allpicture.size(); i++) {
                inp = new FileInputStream(datepath.get(0) + route + datepath.get(1) + route + datepath.get(2) + route + datepath.get(3) + route + allpicture.get(i));
                in.add(IOUtils.toByteArray(inp));
            }
            entity = new ResponseEntity<ArrayList<byte[]>>(in, headers, HttpStatus.OK);
            //물린거 풀어줘야 되는게 있나??
        } catch (Exception e) {
            entity = new ResponseEntity<ArrayList<byte[]>>(HttpStatus.BAD_REQUEST);
        } finally {
            inp.close();
            //글 상세보기 내에서 Ajax를 통해 사진을 요청하는 데 System.gc()가 없으면 파일이 정상적으로 삭제가 안된다.
            System.gc();
        }
        return entity;
    }

}