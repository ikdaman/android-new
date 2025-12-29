package project.side.data.model

import project.side.domain.model.Nickname

data class NicknameEntity(
    val available: Boolean = false
) {
    fun toDomain() = Nickname(available = available)
}
