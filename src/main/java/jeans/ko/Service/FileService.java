package jeans.ko.Service;

import jeans.ko.Controller.UserController;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
public class FileService implements IFileService {
    private Logger logger = LoggerFactory.getLogger(FileService.class);

    @Value("${directory}")
    String directory;

    //유저 개인 폴더 밑에 유저의 기본프로필사진&썸네일사진들을 저장하는 파일의 이름이다.
    @Value("${profilepath}")
    String profile;

    //작은 크기썸네일 식별을 위해 원본파일명 앞에 붙이는 식별자다.
    @Value("${profileSmallheader}")
    String smallHeader;

    //중간 크기썸네일 식별을 위해 원본파일명 앞에 붙이는 식별자다.
    @Value("${profileMiddleheader}")
    String middleHeader;

    //위도우에서는 \\, 리눅스서버에서는/
    @Value("${route}")
    String route;

    @Override
    public void uploadProfile(String uploadPath, String id, String originalName, byte[] fileData) throws Exception {
        logger.info("uploadProfile메소드");
        //uploadPath : application.properties에 지정된 기본 이미지 저장경로
        //id : 유저의 id 각각유저의 폴더
        //originalName : 이미지파일 원본 명
        //fileData : 파일 byte
        //makeDir은 폴더명들을 매개변수로 받아 폴더를 만든다.
        //여기서 makeDir에 매개변수로, uploadPath,id,profile을 준다.
        //그러면 makeDir은 uploadPath파일 밑에 받은 id로 폴더를 만들고 또 profile이라는 폴더를 만든다.
        String profilePath = makeDir(uploadPath, id, profile);
        //makeDir은 그후 /uploadPath/유저의id/profile이라는 path를 반환한다.

        File target = new File(profilePath, originalName);
        FileCopyUtils.copy(fileData, target);
    }

    //makeDir은 uploadPath를 받고 ...을 통해 여러 path들을 받는다.
    //makeDir은 받은 path를 가지고 폴더를 만든다.
    @Override
    public String makeDir(String uploadPath, String... paths) {
        logger.info("makeDir메소드");
        //폴더가 이미 만들어져있을때가 작동을 안한다... 찜찜하긴 하지만 일단 넘어가자
 /*       if (new File(paths[paths.length - 1]).exists()) {
            logger.info("사진이 업로드 될 폴더가 이미 만들어져있습니다.");
            for (String path : paths) {
                uploadPath += "\\" + path;
            }
            logger.info("사진이 업로드될 위치 : " + uploadPath);
            return uploadPath;
        }*/

        for (String path : paths) {
            //uploadPath 뒤로 매개변수로 입력받은 path들을 다 붙여준다.
            File dirPath = new File(uploadPath += route + path);
            if (!dirPath.exists()) {
                //만약 폴더가 존재하지 않는다면 경로에 해당되는 폴더를 만든다.
                dirPath.mkdir();
            }
        }
        logger.info("폴더를 새로 만들었습니다 : " + uploadPath);
        return uploadPath;
    }

    //썸네일을 만드는 메소드
    //입력된 매개변수로 유저가 올린 이미지 파일을 찾는다.
    @Override
    public void makeprofileThumbnail(String filename, String uploadPath, String... paths) throws Exception {
        logger.info("makeprofileThumbnail메소드");
        //유저가 입력한 path를 통해 해당유저의 profile폴더까지 찾아간다.
        for (String path : paths) {
            uploadPath += route + path;
        }

        //이미지를 읽기 위한 버퍼
        logger.info("썸네일 이미지 읽어들이는 중");
        //위에서 찾은 해당유저의 profile경로와 filename 즉 이미지 파일명을 통해
        File f = new File(uploadPath, filename);

        BufferedImage sourceImg = ImageIO.read(f);

        //중간에 크롭으로 정사각형으로 만든 다.
        //그 후에 resize...
        BufferedImage smallImg = Scalr.resize(sourceImg, Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_TO_WIDTH, 40);
        BufferedImage middleImg = Scalr.resize(sourceImg, Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_TO_WIDTH, 50);


        String formatName = filename.substring(filename.lastIndexOf(".") + 1);
        File smallThumbnail = new File(uploadPath + route + smallHeader + filename);
        File middleThumbnail = new File(uploadPath + route + middleHeader + filename);
        ImageIO.write(smallImg, formatName.toUpperCase(), smallThumbnail);
        ImageIO.write(middleImg, formatName.toUpperCase(), middleThumbnail);
        return;
    }

    //이미지 폴더의 기본 경로를 생성한다. ${directory}/년/월 파일을 만든다.
    @Override
    public String makepictureDir(List<String> lists) {
        logger.info("makepictureDir 메소드 : ${directory}/년/월파일을 만든다.");
        String tempPath = directory;

        //배열 안에 있는 년/월을 뽑아내서 폴더 존재 여부 확인 후 폴더 생성
        for (int i = 0; i < lists.size(); i++) {
            File dirPath = new File(tempPath += route + lists.get(i));
            if (!dirPath.exists()) {
                dirPath.mkdir();
            }
        }
        //${directory}년/월 까지 기본경로로 반환한다.
        return tempPath;
    }

    //이미지 파일들을 업로드한다.
    @Override
    public void uploadPictures(List<MultipartFile> files, String path) throws Exception {
      logger.info("uploadPictures메소드");
        //경로에 보드의 pk 값을 더해줘야해서 String으로 변환.
        File dirPath = new File(path);
        if (!dirPath.exists()) {
            dirPath.mkdir();
        }

        //파일을 업로드한다.
        for (int i = 0; i < files.size(); i++) {
            File target = new File(path, files.get(i).getOriginalFilename());
            FileCopyUtils.copy(files.get(i).getBytes(), target);
        }
    }

    @Override
    public void deleteallFiles(List<String> path, List<String> files) {
        logger.info("deleteallFiles메소드");
        String parentPath = directory;

        //1. 보드넘까지 받아서 보드넘경로까지 들어간다
        //2. 테이블에서 보드넘을 검색한 리스트를 받아온다. boardDao로 해서 부모를 looknum으로 하는 애들의
        //이름을 다 뽑아온다.

        //parentPath에 path리스트로부터 받은 path들을 다 더함으로 look번호 까지 온전한 path를 설정
        for (String i : path) {
           parentPath += route+ i;
        }

        //파일 내 모든 사진 파일 삭제
        for (String i : files) {
            new File(parentPath + route + i).delete();

        }
        //룩번호 폴더 삭제
        new File(parentPath).delete();
    }

    @Override
    public void deleteFiles(List<String> path, List<String> files) {
        logger.info("deleteFiles메소드");
        String parentPath = directory;
        //1. 보드넘까지 받아서 보드넘경로까지 들어간다
        //2. 테이블에서 보드넘을 검색한 리스트를 받아온다. boardDao로 해서 부모를 looknum으로 하는 애들의
        //이름을 다 뽑아온다.

        //parentPath에 path리스트로부터 받은 path들을 다 더함으로 look번호 까지 온전한 path를 설정
        for (String i : path) {
            parentPath += route + i;
        }

        //파일 내 모든 사진 파일 삭제
        for (String i : files) {
            new File(parentPath + route + i).delete();
        }
    }
}
