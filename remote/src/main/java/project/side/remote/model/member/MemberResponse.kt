package project.side.remote.model.member

import project.side.data.model.MemberEntity

data class MemberResponse(
    val nickname: String
) {
    fun toData(): MemberEntity = MemberEntity(nickname = nickname)
}
