const MAX_WIDTH = 100, MAX_HEIGHT = 100;

// HTML 요소 등록
const [profileInput, profilePreview, profileTrigger,
  emailInput, emailCheckBtn, emailResultArea,
  pwd, pwdCheck, nickName, signUpBtn
] = ['profileInput', 'profilePreview', 'profileTrigger',
  'emailInput', 'emailCheckBtn', 'emailCheckResult',
  'pwd', 'pwdCheck', 'nickName', 'signUpBtn'
].map(e => document.getElementById(e));

// 프로필 관련
let profileFile = undefined;
// 1. 프로필 사진 추가하기
profileTrigger.addEventListener('click', () => {
  profileInput.click();
});
// 2. 받은 사진 사이즈 조절 후 미리보기에 출력 및 파일로 임시저장
profileInput.addEventListener('change', () => {
  const rawImage = profileInput.files[0];
  if (!rawImage) return;

  const splitted = rawImage.name.split(".");
  let extension = splitted.pop();
  if (extension == 'gif') {
    alert('GIF 파일은 지원되지 않습니다.');
    return;
  }
  if (extension == 'jpg') extension = 'jpeg';

  splitted.push(extension);
  const name = splitted.join(".");
  const mime = 'image/' + extension;

  const reader = new FileReader();
  
  reader.onload = function(event) {
    const img = new Image();

    img.onload = function() {
      let {width, height} = img;
      if (width > MAX_WIDTH) {
        height *= MAX_WIDTH / width;
        width = MAX_WIDTH;
      }
      if (height > MAX_HEIGHT) {
        width *= MAX_HEIGHT / height;
        height = MAX_HEIGHT;
      }
      
      const canvas = document.createElement('canvas');
      canvas.width = width;
      canvas.height = height;
      const canvas2D = canvas.getContext('2d');
      canvas2D.drawImage(img, 0, 0, width, height);

      profilePreview.src = canvas.toDataURL(mime);

      canvas.toBlob(function(blob) {
        profileFile = new File([blob], name + "." + extension, {type: mime});
      }, mime);
    };

    img.src = event.target.result;
  };

  reader.readAsDataURL(rawImage);
});

// 기본정보 관련
// 1. 이메일 중복체크
let emailResult = undefined;
emailInput.addEventListener('change', () => {
  emailResult = undefined;
  emailResultArea.innerText = '';
});
emailCheckBtn.addEventListener('click', () => {
  if (emailInput.value == '') {
    emailInput.focus();
    return;
  }
  if (emailResult != undefined) return;

  fetch('/user/check?email=' + emailInput.value)
  .then(resp => resp.text())
  .then(result => {
    emailResultArea.innerHTML = result == '1' ?
    '<div class="text-success d-flex align-items-center">사용 가능합니다.</div>' :
    '<div class="text-danger d-flex align-items-center">이미 사용중입니다.</div>';
    emailResult = result == '1';
  });
});

// 2. 비밀번호 확인
let pwdResult = undefined;
function pwdValidation() {
  if (pwd.value != pwdCheck.value) {
    pwdResult = false;
    return;
  }

  pwdResult = pwd.value.length > 4;
}
pwd.onchange = pwdValidation;
pwdCheck.onchange = pwdValidation;

signUpBtn.addEventListener('click', () => {
  if (emailResult != true) {
    emailInput.focus();
    return;
  }

  if (pwdResult != true) {
    pwd.focus();
    return;
  }

  const formData = new FormData();
  if (profileFile != undefined) formData.append('profile', profileFile);
  formData.append("userDTO", new Blob([JSON.stringify({
    email: emailInput.value,
    pwd: pwd.value,
    nickName: nickName.value
  })], {
    type: "application/json"
  }));

  fetch('/user/join', {
    method: 'post',
    body: formData,
    headers: {
      [csrfHeader]: csrfToken
    }
  }).then(resp => resp.text())
  .then(result => {
    if (result == '1') {
      alert('회원가입에 성공했습니다. 로그인 해주세요.');
      location.href = "/user/login";
    } else {
      alert('회원가입에 실패했습니다.');
      console.log(result);
    }
  })
})