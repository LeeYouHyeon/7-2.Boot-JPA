const [modalTrigger, modalTitle, resignBtn, changePwdBtn, modalInput, modalCloseBtn, modalEnterBtn] = ['modalTrigger', 'modalTitle', 'resignBtn', 'changePwdBtn', 'modalInput', 'modalCloseBtn', 'modalEnterBtn'].map(e => document.getElementById(e));

const modalEl = document.getElementById('staticBackdrop');
const modal = bootstrap.Modal.getOrCreateInstance(modalEl);
let mode = undefined;
let confirmed = false; // 사용자가 확인 버튼을 눌러서 모달을 닫았는지 확인
let changePwd = '';

// 모달 불러오기
function showModal(message) {
  if (message != undefined) modalTitle.innerText = message;
  modal.show();
  confirmed = false;
}

changePwdBtn.addEventListener('click', () => {
  mode = "change";
  showModal("현재 사용중인 비밀번호를 입력해주세요.");
});
resignBtn.addEventListener('click', () => {
  if (!confirm("정말로 탈퇴하시겠습니까?")) return;

  mode = "remove";
  showModal("비밀번호를 입력해주세요.");
});

// 확인 버튼
modalEnterBtn.addEventListener('click', () => {
  confirmed = true;
  modal.hide();
})

// 모달이 닫힌 후 처리 : modal.show()와 modal.hide()는 비동기임
modalEl.addEventListener('hidden.bs.modal', () => {
  const input = modalInput.value;
  modalInput.value = '';
  if (!confirmed) {
    mode = undefined;
    return; // 사용자가 취소한 경우 작업을 하지 않음
  }

  switch (mode) {
    case "change": case "remove":
      fetch('/user/match', {
        method: 'post',
        body: JSON.stringify({
          email: email,
          pwd: input
        }), headers: {
          [csrfHeader]: csrfToken,
          'Content-Type': 'application/json; charset=utf-8'
        }}).then(resp => resp.text())
        .then(result => {
          console.log("result ", result);
          if (result == '0') alert('비밀번호가 맞지 않습니다.');
          else {
            if (mode == 'change') {
              mode = "changePwd";
              console.log("change >> changePwd");
              showModal("새로운 비밀번호를 입력해주세요.");
              console.log("modal loaded");
            }
            else if (mode == 'remove') location.href = '/user/remove';
          }
        });
      break;
    case "changePwd":
      if (input.length < 5) {
        mode = undefined;
        alert('비밀번호는 5자 이상이어야 합니다.');
      }
      else {
        mode = "changePwdCheck";
        changePwd = input;
        showModal("방금 입력한 비밀번호를 다시 입력해주세요.");
      }
      break;
    case "changePwdCheck":
      mode = undefined;
      if (input != changePwd) alert('앞서 적은 비밀번호와 다릅니다.');
      else fetch ('/user/changePwd', {
        method: 'post',
        body : JSON.stringify({
          email: email,
          pwd: changePwd
        }), headers: {
          [csrfHeader]: csrfToken,
          'Content-Type': 'application/json; charset=utf-8'
        }}).then(resp => resp.text())
        .then(result => {
          if (result == '0') alert('비밀번호 변경에 실패했습니다.');
          else {
            alert('비밀번호가 변경되었습니다. 다시 로그인해주세요.');
            document.getElementById('logout').click();
          }
        });
      break;
    default: ;
}});
