package jeans.ko.Dao;


import jeans.ko.Dto.BoardDto;
import jeans.ko.Dto.PictureDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
@Mapper
public interface IBoardDao {
    //전체 게시판 리스트
    public List<BoardDto> list(@Param("look_num") int look_num);
    //글 검색
    public List<BoardDto> searchList(@Param("option")String option, @Param("keyword")String keyword,@Param("looknum")int looknum);
    //추천글 반환
    public List<BoardDto> preferenceList(String userid1,String userid2,String userid3,String userid4,String userid5,int looknum);
    //게시글 작성
    public int insert(BoardDto boardDto);



    public int insertPicture(String boardnum,String uuidname);

    //게시글 상세보기
    public BoardDto view(@Param("look_num") int look_num);

    //게시글 삭제
    public int delete(@Param("look_num") int look_num);

    public void deleteAllpictures(@Param("look_num")int look_num);

    //게시글 조회수 증가
    public void countUpdate(@Param("look_num") int look_num);

    public void likeUpdate(@Param("num")int num);

    //게시글 수정
    public int update(BoardDto boardDto);

    //look_num을 입력받아 look_date를 출력받는다
    public String getLook_date(int look_num);

    //look_num을 입력받아 picture테이블에서 해당 룩을 외래키로 가지고 있는 단 하나의 사진을 반환한다.
    public String getonePicturename(int look_num);

    //look_num을 입력받아 picture테이블에서 해당 룩을 외래키로 가지고 있는 모든 사진들을 반환한다.
    public List<String> getallPicturename(int look_num);

    //user 닉네임을 받아 그 유저가 쓴 글 숫자를 반환하는 메소드
    public int getBoardnum(String nickname);
}