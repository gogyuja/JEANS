function userDelete() {

    let password = document.getElementsByName("password")[0].value;

    let userDto = {
        userid: id,
        password: password,
    };

    if (id != false && password != false) { //자바스크립트는 null값을 false 로 인식

        $.ajax({
            url: "/session", //저장되는 위치가 session 이라 url을 rest형식에 맟춰 /session으로함
            type: "post", //데이터 전달방식
            data: JSON.stringify(userDto), //객체를 json 문자열로 반환 해서보냄 서버와는 문자열로통신
            contentType: 'application/json', //json 으로 데이터줄떄 사용
            success: function (result, textStatus, jqxHR) {
                if (jqxHR.status == 201) {
                    //로그인 성공후 메인 화면 이동
                    location.href = "/main";
                }
            },
            error: function (error) {
                if (error.status == 404) {
                    swal("", "아이디 비밀번호를 다시 입력하세요", "error");
                }
            }
        })
    } else {
        swal("입력안된 사항이 있습니다!!", "", "error");
    }

}


// function changePassword(){
//     let password=document.getElementsByName("password")[0].value;
//     let checkPassword=document.getElementsByName("checkPassword")[0].value;

//     let ps={
//         ps: password
//     };
//     var passwordDto=JSON.stringify(ps)
//     if(password!=checkPassword){
//         alert("두 비밀번호가 다릅니다");
//         swal("","두 비밀번호가 다릅니다", "error");
//         return;
//     }

//     if(pwreg.test(password)){

//     }else{
//         alert("8~12자리 !@#$%^&*()? 중 하나이상이 들어가야합니다.");
//         swal("에러","8~12자리 !@#$%^&*()? 중 하나이상이 들어가야합니다.","error");
//         return;
//     }

//     $.ajax({
//         url:"/userinformation/password",
//         type:"post",
//         contentType:'application/json; charset=utf-8',
//         data: passwordDto,
//         dataType:'text',
//         success: function(result){
//             alert("성공")
//         },
//         error: function(xhr,status,error){
//             alert("code : "+xhr.status+"\n"+"message"+xhr.responseText+"\n"+error);
//         }
//     })
// }