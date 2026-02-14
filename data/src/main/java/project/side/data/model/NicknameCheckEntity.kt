package project.side.data.model

import project.side.domain.model.NicknameCheck

data class NicknameCheckEntity(
    val available: Boolean
) {
    fun toDomain(): NicknameCheck = NicknameCheck(available = available)
}
