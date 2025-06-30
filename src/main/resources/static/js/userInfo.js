const [modifyBtnArea, updateBtnArea, resignBtn, cancelBtn, modifyBtn, nickNameLabel, nickName] =
['modifyBtnArea', 'updateBtnArea', 'resignBtn', 'cancelBtn', 'modifyBtn', 'nickNameLabel', 'nickName'].map(e => document.getElementById(e));
document.addEventListener('click', e => {
switch (e.target.id) {
  case 'modifyBtn':
    modifyBtnArea.classList.add('d-none');
    updateBtnArea.classList.remove('d-none');
    nickName.classList.remove('d-none');
    nickNameLabel.classList.add('d-none');
    break;
  case 'resignBtn':
    if (confirm('정말 탈퇴하시겠습니까?')) location.href = '/user/remove';
    break;
  case 'cancelBtn':
    modifyBtnArea.classList.remove('d-none');
    updateBtnArea.classList.add('d-none');
    nickName.classList.add('d-none');
    nickNameLabel.classList.remove('d-none');
    break;
}
});