console.log("boardRegister.js in");

const [trigger, fileInput, fileZone, totalFileSize] = ['trigger', 'file', 'fileZone', 'totalFileSize'].map(e => document.getElementById(e));

trigger.addEventListener('click', () => {
fileInput.click();
});

// 실행파일 막기 / 10MB 이상 파일 막기
// 개별 파일 검증으로, 전체 파일 총합은 fileInput의 eventListener에서 관리
const regExp = new RegExp("\.(exe|jar|msi|dll|sh|bat)$");
const maxSize = 1024*1024*10;
function fileValidation(fileName, fileSize) {
  if (regExp.test(fileName)) return 0;
  if (fileSize > maxSize) return 0;
  return 1;
}

let totalSize = 0;
fileInput.addEventListener('change', () => {
  let isOk = 1; // 검증을 통과했는지의 여부

  let ul = '<ul class="list-group list-group-flush">';
  totalSize = 0;
  for (const file of fileInput.files) {
    totalSize += Number(file.size);
    const validResult = fileValidation(file.name, file.size);
    isOk *= validResult;
    let li = '<li class="list-group-item">';
    li += '<div class="mb-3">';
    li += `<div class="fw-bold text-${validResult ? 'success' : 'danger'}">업로드 ${validResult ? '' : '불'}가능</div>`;
    li += `${file.name}</div>`;
    li += `<span class="badge text-bg-${validResult ? 'success' : 'danger' }">${formattedSize(Number(file.size))}</span>`;
    li += '</li>';
    ul += li;
  }
  ul += '</ul>';
  fileZone.innerHTML = ul;

  flushFileSizes(isOk);
});

function formattedSize(fileSize) {
  if (fileSize < 1024) return fileSize + " Bytes";
  if (fileSize < 1024*1024) return (fileSize/1024).toFixed(2) + " KiB";
  return (fileSize/1024/1024).toFixed(2) + " MiB";
}

function flushFileSizes(isOk) {
  const total = totalSize + Number(uploadedTotalSize);
  const regBtn = document.getElementById('regBtn');

  totalFileSize.innerText = total == 0 ? '' : "총 " + formattedSize(total);

  if (total < 1024*1024*20) {
    totalFileSize.classList.remove('text-bg-danger');
    regBtn.disabled = isOk == 0;
  }
  else {
    totalFileSize.classList.add('text-bg-danger');
    regBtn.disabled = true;
  }
}