package project.side.domain.usecase.member

import kotlinx.coroutines.flow.Flow
import project.side.domain.DataResource
import project.side.domain.repository.MemberRepository
import javax.inject.Inject

class WithdrawUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    operator fun invoke(): Flow<DataResource<Unit>> = memberRepository.withdraw()
}
