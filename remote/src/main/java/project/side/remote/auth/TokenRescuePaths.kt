package project.side.remote.auth

/**
 * 서버가 토큰 만료 시 401 대신 5xx를 던지는 케이스를 보정하기 위한 화이트리스트.
 *
 * 여기에 등록된 경로 + 등록된 코드 조합에 한해 응답을 401로 재라벨링해
 * `TokenAuthenticator`의 reissue 흐름이 발동하도록 한다.
 *
 * 화이트리스트는 보수적으로 운영. 순수 서버 장애 5xx까지 모두 reissue 시도하면
 * 무한 루프·잘못된 사용자 경험을 만들 수 있음.
 */
object TokenRescuePaths {
    private val rescuablePaths = setOf(
        "/members/me"
    )

    private val rescuableStatusCodes = setOf(500, 502, 503, 504)

    fun shouldRescue(path: String): Boolean {
        val pure = path.substringBefore('?').trimEnd('/')
        return rescuablePaths.any { pure == it.trimEnd('/') }
    }

    fun isRescuableStatus(code: Int): Boolean = code in rescuableStatusCodes
}
