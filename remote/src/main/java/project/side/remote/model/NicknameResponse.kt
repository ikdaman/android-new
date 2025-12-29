package project.side.remote.model

import project.side.data.model.NicknameEntity

data class NicknameResponse(
    val available: Boolean
) {
    fun toData(): NicknameEntity = NicknameEntity(available = available)
}
