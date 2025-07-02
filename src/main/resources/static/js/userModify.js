const MAX_WIDTH = 100, MAX_HEIGHT = 100;

// HTML 요소 등록
const [profilePreview, profileTrigger, profileInput, nickName, updateBtn]
= ['profilePreview', 'profileTrigger', 'profileInput', 'nickName', 'updateBtn'].map(e => document.getElementById(e));

// 프로필 관련
let profileFile = undefined; // 새로 등록하는 경우에만 처리

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

// update
updateBtn.addEventListener('click', () => {
  if (nickName.value == '') {
    nickName.focus();
    return;
  }

  const formData = new FormData();
  if (profileFile != undefined) formData.append('profile', profileFile);
  formData.append("userDTO", new Blob([JSON.stringify({
    email: email,
    nickName: nickName.value
  })], {
    type: "application/json"
  }));

  fetch('/user/update', {
    method: 'post',
    body: formData,
    headers: {
      [csrfHeader]: csrfToken
    }
  }).then(resp => resp.text())
  .then(result => {
    if (result == '1') {
      alert('개인정보가 수정되었습니다. 다시 로그인해주세요.');
      document.getElementById('logout').click();
    } else {
      alert('개인정보 수정에 실패했습니다.');
    }
  })
})