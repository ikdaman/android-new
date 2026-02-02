package project.side.data.model

import project.side.domain.model.SaveResult

data class SaveResultEntity(
    val code: Int? = null,
    val message: String? = null
) {
    fun toDomain(): SaveResult = SaveResult(code = code, message = message)
}
