const [modalTrigger, modalTitle, resignBtn, changePwdBtn, modalInput, modalCloseBtn, modalEnterBtn] = ['modalTrigger', 'modalTitle', 'resignBtn', 'changePwdBtn', 'modalInput', 'modalCloseBtn', 'modalEnterBtn'].map(e => document.getElementById(e));

let mode = undefined;

// 모달 불러오기
function loadModal(message) {
  modalTitle.innerText = message;
  modalTrigger.click();
}

// 모달 닫기
function closeModal() {
  modalCloseBtn.click();
  modalInput.value = '';
}

changePwdBtn.addEventListener('click', () => {
  mode = "change";
  loadModal("현재 사용중인 비밀번호를 입력해주세요.");
});

resignBtn.addEventListener('click', () => {
  if (!confirm("정말로 탈퇴하시겠습니까?")) return;

  mode = "remove";
  loadModal("비밀번호를 입력해주세요.");
});

modalEnterBtn.addEventListener('click', () => {
  const getPwd = modalInput.value;
  modalCloseBtn.click();

  fetch('/user/match', {
    method: 'post',
    body: JSON.stringify({
      email: email,
      pwd: getPwd
    }), headers: {
      [csrfHeader]: csrfToken,
      'Content-Type': 'application/json; charset=utf-8'
    }}).then(resp => resp.text())
    .then(result => {
      if (result == '0') alert('비밀번호가 맞지 않습니다.');
      else {
        if (mode == 'change') location.href = '/user/changePwd';
        else location.href = '/user/remove';
      }
    });
});