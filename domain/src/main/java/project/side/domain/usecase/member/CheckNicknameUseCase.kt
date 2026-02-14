package project.side.domain.usecase.member

import kotlinx.coroutines.flow.Flow
import project.side.domain.DataResource
import project.side.domain.model.NicknameCheck
import project.side.domain.repository.MemberRepository
import javax.inject.Inject

class CheckNicknameUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    operator fun invoke(nickname: String): Flow<DataResource<NicknameCheck>> =
        memberRepository.checkNickname(nickname)
}
