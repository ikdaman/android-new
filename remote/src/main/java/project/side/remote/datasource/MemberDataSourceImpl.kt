package project.side.remote.datasource

import project.side.data.datasource.MemberDataSource
import project.side.data.model.DataApiResult
import project.side.data.model.MemberEntity
import project.side.data.model.NicknameCheckEntity
import project.side.remote.api.MemberService
import project.side.remote.model.member.NicknameUpdateRequest
import javax.inject.Inject

class MemberDataSourceImpl @Inject constructor(
    private val memberService: MemberService
) : MemberDataSource {

    override suspend fun getMyInfo(): DataApiResult<MemberEntity> {
        return try {
            val response = memberService.getMyInfo()
            if (response.isSuccessful) {
                response.body()?.let {
                    DataApiResult.Success(it.toData())
                } ?: DataApiResult.Error("응답이 비어있습니다.")
            } else {
                DataApiResult.Error(mapServerError(response.code(), response.message()))
            }
        } catch (e: Exception) {
            DataApiResult.Error("네트워크 오류: ${e.message}")
        }
    }

    override suspend fun updateNickname(nickname: String): DataApiResult<MemberEntity> {
        return try {
            val response = memberService.updateNickname(NicknameUpdateRequest(nickname))
            if (response.isSuccessful) {
                response.body()?.let {
                    DataApiResult.Success(it.toData())
                } ?: DataApiResult.Error("응답이 비어있습니다.")
            } else {
                DataApiResult.Error(mapServerError(response.code(), response.message()))
            }
        } catch (e: Exception) {
            DataApiResult.Error("네트워크 오류: ${e.message}")
        }
    }

    override suspend fun withdraw(): DataApiResult<Unit> {
        return try {
            val response = memberService.withdraw()
            if (response.isSuccessful) {
                DataApiResult.Success(Unit)
            } else {
                DataApiResult.Error(mapServerError(response.code(), response.message()))
            }
        } catch (e: Exception) {
            DataApiResult.Error("네트워크 오류: ${e.message}")
        }
    }

    override suspend fun checkNickname(nickname: String): DataApiResult<NicknameCheckEntity> {
        return try {
            val response = memberService.checkNickname(nickname)
            if (response.isSuccessful) {
                response.body()?.let {
                    DataApiResult.Success(it.toData())
                } ?: DataApiResult.Error("응답이 비어있습니다.")
            } else {
                DataApiResult.Error(mapServerError(response.code(), response.message()))
            }
        } catch (e: Exception) {
            DataApiResult.Error("네트워크 오류: ${e.message}")
        }
    }

    private fun mapServerError(code: Int, message: String?): String {
        return when (code) {
            400 -> "잘못된 요청입니다 (HTTP $code)."
            401 -> "인증이 만료되었거나 유효하지 않습니다. 다시 로그인해주세요 (HTTP $code)."
            403 -> "접근 권한이 없습니다 (HTTP $code)."
            404 -> "요청한 리소스를 찾을 수 없습니다 (HTTP $code)."
            in 500..599 -> "서버 내부 오류가 발생했습니다 (HTTP $code)."
            else -> "알 수 없는 서버 오류가 발생했습니다 (HTTP $code: ${message ?: "no message"})"
        }
    }
}
