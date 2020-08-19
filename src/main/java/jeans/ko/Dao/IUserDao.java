package jeans.ko.Dao;

import jeans.ko.Dto.UserDto;
import org.apache.catalina.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

//Mapper 어노테이션에 의해 application.properties에 걸려있는 설정으로 mybatis.mapper에 모든 xml을 뒤져서 매핑한다.
//mybatis.mapper 자신과 같은 namespace를 확인하고 같은 id를 사용한다
@Mapper
@Repository //Dao 라는 것을 알려줌
public interface IUserDao {
    //회원가입을 위해 유저정보 기입
    public int insertUser(UserDto userDto);

    //유저 로그인
    public String userLogin(UserDto userDto);

    //유저 닉네임
    public String getNickname(String userid);

    //유저 아이디로 프로필사진유무확인
    public String getPicture(String userid);

    //해당 유저의 사진이름을 교체
    public void setPicture(String userid,String picture);

    //아이디와 비밀번호를 입력하면 userDto 다 반환
    public UserDto getInformation(UserDto userDto);
}
