#!/bin/bash
# PostToolUse hook: 소스 코드 수정 시 관련 문서 업데이트 확인

INPUT=$(cat)
FILE_PATH=$(echo "$INPUT" | jq -r '.tool_input.file_path // empty')

# 파일 경로가 없으면 무시
if [ -z "$FILE_PATH" ]; then
  exit 0
fi

# docs 폴더 내 파일 수정은 무시
if [[ "$FILE_PATH" == *"/docs/"* ]]; then
  exit 0
fi

# .kt 소스 파일만 대상
if [[ "$FILE_PATH" != *.kt ]]; then
  exit 0
fi

# 관련 docs 파일 매핑
HINTS=""

case "$FILE_PATH" in
  *Auth*|*Login*|*Signup*|*GoogleAuth*|*NaverAuth*|*KakaoAuth*)
    HINTS="auth-login.md, auth-logout.md, auth-signup.md" ;;
  *MyBook*|*StoreBook*|*HomeScreen*|*MainViewModel*)
    HINTS="mybook-store.md, mybook-save.md, mybook-update.md" ;;
  *History*|*HistoryScreen*)
    HINTS="mybook-history.md" ;;
  *Member*|*Setting*|*Nickname*)
    HINTS="member-update-nickname.md, member-get-info.md" ;;
  *BookInfo*|*BookEdit*|*BookDetail*)
    HINTS="mybook-detail.md, mybook-update.md, mybook-delete.md" ;;
  *Splash*|*Navigate*|*navigateIfLoggedIn*)
    HINTS="auth-login.md (화면 이동 플로우 섹션)" ;;
  *AddBook*|*ManualBookInput*|*SearchBook*)
    HINTS="mybook-save.md" ;;
  *)
    exit 0 ;;
esac

if [ -n "$HINTS" ]; then
  jq -n --arg hints "$HINTS" --arg file "$(basename "$FILE_PATH")" '{
    hookSpecificOutput: {
      hookEventName: "PostToolUse",
      additionalContext: ("[DOC] 소스 수정됨: " + $file + ". 관련 문서 확인 필요: " + $hints)
    }
  }'
fi

exit 0
