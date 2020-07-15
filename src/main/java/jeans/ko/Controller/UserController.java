package jeans.ko.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jeans.ko.Dao.IUserDao;
import jeans.ko.Dto.UserDto;
import jeans.ko.Service.IFileService;
import jeans.ko.Service.IUserService;
import lombok.extern.flogger.Flogger;
import org.apache.commons.io.IOUtils;
import org.apache.ibatis.io.ResolverUtil;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.Binding;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

//로그인이
@Controller
public class UserController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    IUserService userService;

    //회원가입 시 프로필사진을 입력했을 때 사진 업로드를 위한 Service
    @Autowired
    private IFileService fileService;

    //회원가입 시 프로필사진을 입력했을 때 사진 업로드를 위한 경로. application.properties에 등록되어있다.
    @Value("${directory}")
    String uploadPath;

    //유저개인 폴더 밑에 유저의 기본프로필사진&썸네일사진들을 저장하는 파일의 이름이다.
    @Value("${profilepath}")
    String profile;

    //${directory}경로 밑에 defaultimagefiles경로이다. 이전에 경로는 ${directory}가 다 잡아줘서 폴더명만 사용한다.
    @Value("${defaultdirectory}")
    String defaultdirectory;

    //${defaultdirectory}밑에 있는 작은 썸네일 이미지다.
    //이 이미지는 회원가입시 프로필사진을 업로드 하지 않은 유저를 위한 기본 프로필사진이다.
    @Value("${defaultprofileSmallimage}")
    String defaultSthumbnail;

    //작은 크기썸네일 식별을 위해 원본파일명 앞에 붙이는 식별자다.
    @Value("${profileSmallheader}")
    String smallHeader;

    //중간 크기썸네일 식별을 위해 원본파일명 앞에 붙이는 식별자다.
    @Value("${profileMiddleheader}")
    String middleHeader;

    @Autowired
    HttpSession httpSession;

    @RequestMapping("/joinUser")
    public String joinUser() {
        return "joinUser";
    }

    //마이페이지 이동
    @RequestMapping("/mypageUser")
    public String mypageUser()
    { return "mypageUser"; }

    //회원정보수정 페이지 이동
    @RequestMapping("/changeUser")
    public String changeUser()
    { return "changeUser"; }

    //회원가입 시 프로필사진이 없다
    @ResponseBody
    @PostMapping(value = "/user")
    public ResponseEntity<Void> join(@RequestPart("UserDto") String userString, BindingResult result) throws IOException {

        @Valid
        UserDto user = new ObjectMapper().readValue(userString, UserDto.class);
        user.setPicture("");//사진이름은 ""으로 둔다.

        System.out.println("result.getErrorCount() = " + result.getErrorCount());
        System.out.println("result.hasGlobalErrors(); = " + result.getFieldError());
        if (result.getFieldError("userid") != null) {
            System.out.println("Error! " + result.getFieldError("userid").getDefaultMessage());
        }
        if (result.getFieldError("nickname") != null) {
            System.out.println("Error! = " + result.getFieldError("nickname").getDefaultMessage());
        }
        if (result.getFieldError("password") != null) {
            System.out.println("Error! = " + result.getFieldError("password").getDefaultMessage());
        }
        if (result.getFieldError("sex") != null) {
            System.out.println("Error! = " + result.getFieldError("sex").getDefaultMessage());
        }
        if (result.getFieldError("email") != null) {
            System.out.println("Error! = " + result.getFieldError("email").getDefaultMessage());
        }
        if (result.getErrorCount() > 0) {
            System.out.println("이제 자바스크립트로 에러를 보낸다.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        //회원가입 이벤트
        int check = userService.joinUser(user);

        //성공적으로 회원가입 시 1반환
        if (check > 0)
            return new ResponseEntity<>(HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    //회원가입 시 프로필사진이 있다.
    //REST 형식의 회원가입
    @ResponseBody
    @PostMapping(value = "/userfile")
    public ResponseEntity<Void> join(@RequestPart("UserDto") String userString, @RequestPart("file") MultipartFile picture, BindingResult result) throws Exception {
              String fileOriginalname = picture.getOriginalFilename();//올린 이미지 파일의 원래이름

        @Valid
        UserDto user = new ObjectMapper().readValue(userString, UserDto.class);

        user.setPicture(fileOriginalname);
        //user의 picture값을 파일의 이름으로 설정한다.

        System.out.println("result.getErrorCount() = " + result.getErrorCount());
        System.out.println("result.hasGlobalErrors(); = " + result.getFieldError());
        if (result.getFieldError("userid") != null) {
            System.out.println("Error! " + result.getFieldError("userid").getDefaultMessage());
        }
        if (result.getFieldError("nickname") != null) {
            System.out.println("Error! = " + result.getFieldError("nickname").getDefaultMessage());
        }
        if (result.getFieldError("password") != null) {
            System.out.println("Error! = " + result.getFieldError("password").getDefaultMessage());
        }
        if (result.getFieldError("sex") != null) {
            System.out.println("Error! = " + result.getFieldError("sex").getDefaultMessage());
        }
        if (result.getFieldError("email") != null) {
            System.out.println("Error! = " + result.getFieldError("email").getDefaultMessage());
        }
        if (result.getErrorCount() > 0) {
            System.out.println("이제 자바스크립트로 에러를 보낸다.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        //회원가입 이벤트
        int check = userService.joinUser(user);

        //프로필사진 업로드 이벤트
        //uploadPath 경로 밑에 유저명의 폴더를 만든 후 getBytes()를 통해 받은 사진을 저장시킨다.
        //경로 : uploadPath/유저명/profile//이미지파일명
        fileService.uploadProfile(uploadPath, user.getUserid(), user.getPicture(), picture.getBytes());

        //업로드된 폴더를 통해 썸네일 이미지 제작 이벤트
        //uploadPath : 업로드 될 모든 파일들의 기본 부모
        //user.getUserid : 해당유저의 파일
        //profile : 그중에서도 개인 프로파일용사진 폴더.
        fileService.makeprofileThumbnail(user.getPicture(), uploadPath, user.getUserid(), profile);

        //성공적으로 회원가입 시 1반환
        if (check > 0)
            return new ResponseEntity<>(HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    @ResponseBody
    @PostMapping("/session")
    public HashMap<String, Object> loginRequest(UserDto userDto) {

        HashMap<String, Object> map = new HashMap<String, Object>();
        String nickname = userService.userLogin(userDto);//닉네임 값을 받아오도록

        httpSession.setAttribute("userid", userDto.getUserid());
        httpSession.setAttribute("usernickname", nickname);

        map.put("userid", httpSession.getAttribute("userid"));
        map.put("usernickname", httpSession.getAttribute("usernickname"));

        return map; //session 아이디 닉네임 넘겨주기
    }

    @ResponseBody
    @DeleteMapping("/session")
    public void logout() {
        //session 아이디  삭제
        httpSession.invalidate();
    }

    //sthumbnail 즉 작은 이미지를 들고오는것이다. 로그인후 헤더의 프로필사진부분.
    @GetMapping("/displaySthumbnail")
    public ResponseEntity<byte[]> display() throws IOException {
        InputStream in = null;
        ResponseEntity<byte[]> entity = null;
        Object f = httpSession.getAttribute("userid");
        String userid = (String) f;
        String picture = userService.getPicture(userid);
        //userid로 select를 이용해서 mysql에서 picture문 뽑아낸다.
        HttpHeaders headers = new HttpHeaders();
        try {
            if (picture.equals("")) {
                in = new FileInputStream(uploadPath + "\\" +defaultdirectory+ "\\"+defaultSthumbnail);
            } else {
                in = new FileInputStream(uploadPath + "\\" + userid + "\\" + profile + "\\" +smallHeader+picture);
            }
          //  headers.setContentType(MediaType.IMAGE_JPEG);//어차피 profile.jpg로 저장되기때문에 Type이 IMAGE_JPEG여도 괜찮다

            entity = new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.OK);
        } catch (Exception e) {
            entity = new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
        } finally {
            in.close();
        }
        return entity;
    }
    
    //중간크기의 썸네일 이미지를 반환. 유저가 작성한 글의 썸네일이미지
    @GetMapping("/displayMthumbnail")
    public ResponseEntity<byte[]> displayMthumbnail(@RequestParam("id") String id) throws Exception {
        InputStream in = null;
        ResponseEntity<byte[]> entity = null;
        String picture = userService.getPicture(id);
        HttpHeaders headers = new HttpHeaders();
        try {
            if(picture.equals("")){
                in=new FileInputStream(uploadPath+ "\\" +defaultdirectory+ "\\"+defaultSthumbnail);
            }else{
                in=new FileInputStream(uploadPath+"\\"+id+"\\"+profile+"\\"+middleHeader+picture);
            }
        //    headers.setContentType(MediaType.IMAGE_JPEG);
            entity = new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.OK);
        } catch (Exception e) {
            entity = new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
        } finally {
            in.close();
        }
        return entity;
    }
}
