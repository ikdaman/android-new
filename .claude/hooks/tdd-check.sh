#!/bin/bash
# TDD Hook: Remind to write tests first when modifying source files
# Triggers on PreToolUse for Write|Edit

INPUT=$(cat)
FILE_PATH=$(echo "$INPUT" | jq -r '.tool_input.file_path // empty')

# Skip if no file path
if [ -z "$FILE_PATH" ]; then
  exit 0
fi

# Check if this is a main source file (Kotlin/Java) and NOT a test file
if echo "$FILE_PATH" | grep -qE '\.(kt|java)$'; then
  if echo "$FILE_PATH" | grep -q '/src/main/'; then
    # This is a main source file - check if corresponding test exists
    TEST_PATH=$(echo "$FILE_PATH" | sed 's|/src/main/|/src/test/|')
    TEST_PATH=$(echo "$TEST_PATH" | sed 's|\.kt$|Test.kt|')

    if [ ! -f "$TEST_PATH" ]; then
      jq -n '{
        hookSpecificOutput: {
          hookEventName: "PreToolUse",
          additionalContext: "[TDD] 이 소스 파일에 대응하는 테스트 파일이 없습니다. TDD 원칙에 따라 테스트를 먼저 작성하세요. 테스트 경로: '"$TEST_PATH"'"
        }
      }'
      exit 0
    fi
  fi
fi

exit 0
