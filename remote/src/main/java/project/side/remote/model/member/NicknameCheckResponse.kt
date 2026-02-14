package project.side.remote.model.member

import project.side.data.model.NicknameCheckEntity

data class NicknameCheckResponse(
    val available: Boolean
) {
    fun toData(): NicknameCheckEntity = NicknameCheckEntity(available = available)
}
