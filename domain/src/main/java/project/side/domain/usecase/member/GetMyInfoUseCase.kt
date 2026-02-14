package project.side.domain.usecase.member

import kotlinx.coroutines.flow.Flow
import project.side.domain.DataResource
import project.side.domain.model.Member
import project.side.domain.repository.MemberRepository
import javax.inject.Inject

class GetMyInfoUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    operator fun invoke(): Flow<DataResource<Member>> = memberRepository.getMyInfo()
}
