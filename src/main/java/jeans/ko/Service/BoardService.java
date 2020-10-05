package jeans.ko.Service;

import jeans.ko.Controller.UserController;
import jeans.ko.Dao.IBoardDao;
import jeans.ko.Dao.IMoodDao;
import jeans.ko.Dto.BoardDto;
import jeans.ko.Dto.MoodDto;
import jeans.ko.Dto.PictureDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BoardService implements IBoardService {

    private Logger logger = LoggerFactory.getLogger(BoardService.class);

    @Autowired
    private IBoardDao boardDao;

    @Autowired
    private IMoodDao moodDao;

    @Autowired
    private PictureDto pictureDto;

    @Autowired
    HttpSession httpSession;

    @Autowired
    IFileService iFileService;

    @Autowired
    IUtilService iUtilService;

    public void insert(BoardDto boardDto, List<MoodDto> moodDtos, List<MultipartFile> files) throws Exception {
        //시간을 내가 원하는 형식으로 출력
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy년 MM월 dd일");
        String look_date = formatter.format(new Date());
        boardDto.setCount(0); //조회수 초기값 0줌
        boardDto.setLook_date(look_date); //작성 시간 넣기
        boardDto.setFk_userid_user_userid((String) httpSession.getAttribute("userid"));
        boardDto.setNickname((String) httpSession.getAttribute("usernickname"));
        boardDto.setGood(1); //좋아요 값넣기 수정해야함
        boardDto.setComment_count(2);//댓글수 수정해야함


        //1. 사용자가 입력한 글내역 중 파일,무드를 제외한 글 내용을 입력.
        //여기서 look 데이터베이스에 정보가 입력된다.
        boardDao.insert(boardDto);

        /*
            더 이상 사용되지 않는다. 그전에는 이런식으로 픽쳐테이블에 bindingPicture메소드를 통해 List로 insert 시켰지만,
            글 수정 시 이미지 명이 다 동일하게 바뀌는 문제때문에 더 이상 사용되지 않는다.
            아까워서 남겨뒀다.
            boardDao.insertPicturedatabase(bindingPicture(files, boardDto.getLook_num()));
        */

        //만약 유저가 해당 글의 무드를 선택했다면,
        if (moodDtos != null) {
            for (int i = 0; i < moodDtos.size(); i++) {
                //무드 리스트의 경우 현재 look의 넘버, 글의 id 값이 없어서 이런식으로 다 넣어줘야한다.
                (moodDtos.get(i)).setLook_num(boardDto.getLook_num());
            }
            //무드데이터베이스에 해당글의 무드를 선택해야한다.
            moodDao.insertMooddatabase(moodDtos);
        }

        //looknumtoPath()는 매개변수로 글번호를 받아 해당글의 경로(년/월/룩번호)를 반환한다.
        List<String> path = iUtilService.looknumtoPath(boardDto.getLook_num());

        iFileService.mkDir(path);//폴더 생성
        iFileService.uploadFiles(path, files);//path경로에 파일들을 업로드한다.
        iFileService.mkBoardthumbnail(path);//해당 경로에 있는 모든 사진들을 축소한다.
    }

    //글을 지운다.
    @Override
    public int delete(int look_num) {

        //1. 년/월/룩번호를 반환받는다.
        List<String> path = iUtilService.looknumtoPath(look_num);

        //2. FileService의 deleteFiles를 통해 업로드되어있는 사진을 지우겠다.
        //looknumtoAllPicturename 메소드를 통해 룩번호를 매개변수로 주고 해당되는 글의 모든 사진의 이름을 반환받는다.
        List<String> files = iUtilService.looknumtoallPicturename(look_num);
        //deleteallFiles는 폴더 내의 모든 사진을 지운후 look_num에 해당되는 파일도 지운다.
        //deleteallFiles에 매개변수로 룩번호까지의 패스와 지울파일(해당글의 전체파일들)들을 보낸다.
        iFileService.rmFiles(path, files);
        iFileService.rmDir(path);
        return boardDao.delete(look_num);
    }

    @Override
    public int update(BoardDto boardDto,List<MoodDto> moodDtos, List<MultipartFile> files) throws Exception {

        //폴더내 파일 삭제
        List<String> path = iUtilService.looknumtoPath(boardDto.getLook_num());
        int look_num=boardDto.getLook_num();

        moodDao.deleteMood(look_num);

        //만약 유저가 해당 글의 무드를 선택했다면,
        if (moodDtos != null) {
            for (int i = 0; i < moodDtos.size(); i++) {
                //무드 리스트의 경우 현재 look의 넘버, 글의 id 값이 없어서 이런식으로 다 넣어줘야한다.
                (moodDtos.get(i)).setLook_num(boardDto.getLook_num());
            }
            //무드데이터베이스에 해당글의 무드를 선택해야한다.
            moodDao.insertMooddatabase(moodDtos);
        }

        iFileService.rmFiles(path, iUtilService.looknumtoallPicturename(boardDto.getLook_num()));
        //DB내 해당 룩 번호 관여 제거
        boardDao.deleteAllpictures(boardDto.getLook_num());

        //신규사진 업로드
        iFileService.uploadFiles(path, files);

        return boardDao.update(boardDto);
    }

    //Multipart형식으로 들어온 리스트들을 PictureDto리스트로 반환하는 메소드
    //매개변수 looknum은 외래키로 사용할
    @Override
    public ArrayList<PictureDto> bindingPicture(List<MultipartFile> e, int looknum) {

        ArrayList<PictureDto> lists = new ArrayList<PictureDto>();
        int j = 0;
        for (MultipartFile i : e) {
            PictureDto pictureDto = new PictureDto();
            String name = i.getOriginalFilename();
            pictureDto.setLookNum(looknum);
            pictureDto.setPictureName(name);
            lists.add(pictureDto);
        }

        return lists;
    }

}