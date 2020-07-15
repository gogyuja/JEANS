package jeans.ko.Service;


import jeans.ko.Dto.BoardDto;
import jeans.ko.Dto.PictureDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


public interface IBoardService {
    public void insert(BoardDto boardDto,List<MultipartFile> files);
    //public void insert(BoardDto boardDto, List<MultipartFile> files) ;

    public int delete(int look_num) ;

    public int update(BoardDto boardDto);

    //멀티파트 형식으로 들어온 이미지 파일리스트를 PictureDto 리스트로 반환하는 메소드.
    public List<PictureDto> bindingPicture(List<MultipartFile> e,int looknum);
}
