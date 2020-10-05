package jeans.ko.Dto;


import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import javax.validation.constraints.*;
import java.util.Date;


@Data
@Component
public class BoardDto {

    private int look_num; //오토 인크리먼트
    @NotBlank(message = "제목을 입력하세요")
    private String title;
    private int good;
    @NotBlank(message = " 계절을 선택하세요")
    private String season;
    @Min(value = 1, message = " 공개 비공개를 선택하세요")
    private int look_privacy; //공개설정 1공개 2비공개
    private String fk_userid_user_userid;  //회원 id
    @NotBlank(message = " 메모를 입력하세요")
    private String memo;   //null값 허용
    private String look_date;  //작성날짜
    private int count;    //조회수
    private Integer comment_count; //댓글수
    private String nickname;

}
