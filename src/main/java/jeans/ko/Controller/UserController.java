package jeans.ko.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jeans.ko.Dao.IPretreatmentDao;
import jeans.ko.Dao.IUserDao;
import jeans.ko.Dto.UserDto;
import jeans.ko.Service.IFileService;
import jeans.ko.Service.IUserService;
import jeans.ko.Service.IUtilService;
import jeans.ko.exception.NotFoundException;
import lombok.extern.flogger.Flogger;
import org.apache.commons.io.IOUtils;
import org.apache.ibatis.io.ResolverUtil;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.BindingResultUtils;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//로그인이
@Controller
public class UserController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    IUserService userService;

    @Autowired
    IPretreatmentDao pretreatmentDao;

    @Autowired
    IUtilService utilService;

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

    //이미지 파일 저장 경로 설정
    @Value("${route}")
    String route;

    @Autowired
    HttpSession httpSession;

    @Autowired
    IUserDao userDao;

    @RequestMapping("/joinUser")
    public String joinUser() {
        return "joinUser";
    }

    //회원정보수정 페이지 이동
    @RequestMapping("/changeUser")
    public String changeUser() {
        return "changeUser";
    }

    //회원탈퇴 페이지 이동
    @RequestMapping("/deleteUser")
    public String deleteUser() {
        return "deleteUser";
    }

    //회원가입 시 프로필사진이 없다
    //회원가입 시 프로필사진이 있다.
    //REST 형식의 회원가입
    @ResponseBody
    @PostMapping(value = "/user")
    public ResponseEntity<String> join(@Valid @RequestPart("UserDto") UserDto user, @RequestPart(value = "file", required = false) MultipartFile picture, BindingResult result) throws Exception {


        if (picture == null) {
            user.setPicture(defaultSthumbnail);
        } else {
            String fileOriginalname = picture.getOriginalFilename();//올린 이미지 파일의 원래이름
            user.setPicture(fileOriginalname);
        }


        if (result.getErrorCount() > 0) {
            //Validation 검사 결과 에러가 있다.
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if(userDao.checkUserid(user.getUserid())!=null){
            return new ResponseEntity<>("이미 사용중인 아이디입니다",HttpStatus.BAD_REQUEST);
        }else if(userDao.checkNickname(user.getNickname())!=null){
            return new ResponseEntity<>("이미 사용중인 닉네임입니다.",HttpStatus.BAD_REQUEST);
        }

        //회원가입 이벤트
        int check = userService.joinUser(user);

        List<String> profilethumbnailPath = utilService.usertoPath(user.getUserid());

        fileService.mkDir(profilethumbnailPath);

        if (!user.getPicture().equals(defaultSthumbnail)) {
            //프로필사진 업로드 이벤트
            //uploadPath 경로 밑에 유저명의 폴더를 만든 후 getBytes()를 통해 받은 사진을 저장시킨다.
            //경로 : uploadPath/유저명/profile//이미지파일명
            //fileService.uploadProfile(uploadPath, user.getUserid(), user.getPicture(), picture.getBytes());

            fileService.uploadFile(profilethumbnailPath, picture);
            //업로드된 폴더를 통해 썸네일 이미지 제작 이벤트
            //uploadPath : 업로드 될 모든 파일들의 기본 부모
            //user.getUserid : 해당유저의 파일
            //profile : 그중에서도 개인 프로파일용사진 폴더.
            // fileService.makeprofileThumbnail(user.getPicture(), uploadPath, user.getUserid(), profile);
            fileService.mkProfilethumbnail(profilethumbnailPath, user.getUserid());
        }

        //성공적으로 회원가입 시 1반환
        if (check > 0) {
            //회원아이디 값으로 전처리테이블에 삽입
            pretreatmentDao.insertUser(user.getUserid());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @PostMapping("/session")//@RequestBody json 형태 문자열로 받겠다 객체는 못받음
    public HashMap<String, Object> loginRequest(@RequestBody UserDto userDto) {

        HashMap<String, Object> map = new HashMap<String, Object>();
        //String nickname = userService.userLogin(userDto);//닉네임 값을 받아오도록

        UserDto successLogin = userService.userLogin(userDto);

        if (successLogin == null) {
            //아이디와 비빌번호가 맞지않음
            throw new NotFoundException(String.format("Please enter your ID and password again"));
        } else {
            //로그인 성공
            httpSession.setAttribute("userrole", successLogin.getRole());
            httpSession.setAttribute("userid", successLogin.getUserid());
            httpSession.setAttribute("usernickname", successLogin.getNickname());
            httpSession.setAttribute("usersex", successLogin.getSex());
            httpSession.setAttribute("userheight", successLogin.getHeight());
            httpSession.setAttribute("userweight", successLogin.getWeight());
            httpSession.setAttribute("useremail", successLogin.getEmail());

            map.put("role", httpSession.getAttribute("userrole"));
            map.put("userid", httpSession.getAttribute("userid"));
            map.put("nickname", httpSession.getAttribute("usernickname"));
            map.put("sex", httpSession.getAttribute("usersex"));
            map.put("height", httpSession.getAttribute("userheight"));
            map.put("weight", httpSession.getAttribute("userweight"));
            map.put("email", httpSession.getAttribute("useremail"));
            System.out.println(" user 에서 세션 = " + httpSession.getId());
            System.out.println("successLogin = " + successLogin);

            return map; //session 아이디 닉네임 넘겨주기
        }
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

        //유저의 id 값 추출
        Object f = httpSession.getAttribute("userid");
        String userid = (String) f;
        //userid로 select를 이용해서 mysql에서 picture문 뽑아낸다.
        String picture = userService.getPicture(userid);
        HttpHeaders headers = new HttpHeaders();
        try {
            if (picture.equals(defaultSthumbnail)) {
                in = new FileInputStream(uploadPath + route + defaultdirectory + route + defaultSthumbnail);
            } else {
                in = new FileInputStream(uploadPath + route + userid + route + profile + route + smallHeader + picture);
            }
            //   headers.setContentType(MediaType.IMAGE_JPEG);//카피한 소스코드에서는 이게 있었는데 이렇게 주석처리해도 돌아가더라

            entity = new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.OK);

        } catch (Exception e) {
            entity = new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
        } finally {
            in.close();
        }
        return entity;
    }

    //중간크기의 썸네일 이미지를 반환. 유저가 작성한 글의 썸네일이미지
    // /displaySthumbnail과 똑같다. 단지 이미지 크기만 다를 뿐이다.
    @GetMapping("/displayMthumbnail/{id}")
    public ResponseEntity<byte[]> displayMthumbnail(@PathVariable String id) throws Exception {
        InputStream in = null;
        ResponseEntity<byte[]> entity = null;
        String picture = userService.getPicture(id);
        HttpHeaders headers = new HttpHeaders();
        try {
            if (picture.equals(defaultSthumbnail)) {
                in = new FileInputStream(uploadPath + route + defaultdirectory + route + defaultSthumbnail);
            } else {
                in = new FileInputStream(uploadPath + route + id + route + profile + route + middleHeader + picture);
            }
            //headers.setContentType(MediaType.IMAGE_JPEG);
            entity = new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.OK);
        } catch (Exception e) {
            entity = new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
        } finally {
            in.close();
        }
        return entity;
    }


    //대형크기의 썸네일 이미지를 반환. 유저가 작성한 글의 썸네일이미지
    // /displaySthumbnail과 똑같다. 단지 이미지 크기만 다를 뿐이다.
    @GetMapping("/displayGthumbnail/{id}")
    public ResponseEntity<byte[]> displayGthumbnail(@PathVariable String id) throws Exception {
        InputStream in = null;
        ResponseEntity<byte[]> entity = null;
        String picture = userService.getPicture(id);
        HttpHeaders headers = new HttpHeaders();
        try {
            if (picture.equals(defaultSthumbnail)) {
                in = new FileInputStream(uploadPath + route + defaultdirectory + route + defaultSthumbnail);
            } else {
                in = new FileInputStream(uploadPath + route + id + route + profile + route + picture);
            }
            //headers.setContentType(MediaType.IMAGE_JPEG);
            entity = new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.OK);
        } catch (Exception e) {
            entity = new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
        } finally {
            in.close();
        }
        return entity;
    }
}