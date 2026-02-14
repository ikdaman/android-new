package project.side.data.model

import project.side.domain.model.Member

data class MemberEntity(
    val nickname: String
) {
    fun toDomain(): Member = Member(nickname = nickname)
}
