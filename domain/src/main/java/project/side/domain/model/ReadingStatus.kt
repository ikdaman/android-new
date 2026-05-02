package project.side.domain.model

enum class ReadingStatus(val displayName: String) {
    TODO("읽고 싶은 책"),
    INPROGRESS("읽는 중"),
    DONE("완독");

    companion object {
        fun from(value: String?): ReadingStatus = when (value?.uppercase()) {
            "TODO" -> TODO
            "INPROGRESS" -> INPROGRESS
            "DONE", "COMPLETED" -> DONE
            else -> TODO
        }

        fun displayNameOf(value: String?): String = from(value).displayName
    }
}
