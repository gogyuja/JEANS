let pwreg = /^(?=.*[!@#$%^&*()?])[0-9ㄱ-힣a-zA-Z!@#$%^&*()?]{8,12}$/;//패스워드정규식 8~12특수문자한자이상무조건포함

function changePassword(){
    let password=document.getElementsByName("password")[0].value;
    let checkPassword=document.getElementsByName("checkPassword")[0].value;

    let ps={
        ps: password
    };
    var passwordDto=JSON.stringify(ps)
    if(password!=checkPassword){
        alert("두 비밀번호가 다릅니다");
        swal("","두 비밀번호가 다릅니다", "error");
        return;
    }

    if(pwreg.test(password)){

    }else{
        alert("8~12자리 !@#$%^&*()? 중 하나이상이 들어가야합니다.");
        swal("에러","8~12자리 !@#$%^&*()? 중 하나이상이 들어가야합니다.","error");
        return;
    }

    $.ajax({
        url:"/userinformation/password",
        type:"post",
        contentType:'application/json; charset=utf-8',
        data: passwordDto,
        dataType:'text',
        success: function(result){
            alert("성공")
        },
        error: function(xhr,status,error){
            alert("code : "+xhr.status+"\n"+"message"+xhr.responseText+"\n"+error);
        }
    })
}

function changeprofileThumbnail(){
    alert("자바스크립트실행");
    let file=$("input[name=profile_pt]")[0].files[0];
    let formData=new FormData();
    formData.append('file',file);
    $.ajax({
        url:"/userinformation/profilethumbnail",
        processData: false,
        contentType: false,
        type:"post",
        data: formData,
        success: function(){
            alert("성공")
        },
        error: function(xhr,status,error){
            alert("code : "+xhr.status+"\n"+"message"+xhr.responseText+"\n"+error);
        }
    })
}