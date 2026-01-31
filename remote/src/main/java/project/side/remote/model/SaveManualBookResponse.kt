package project.side.remote.model

data class SaveManualBookResponse(
    val code: Int? = null,
    val message: String? = null
)

fun SaveManualBookResponse.toData(): project.side.data.model.SaveResultEntity =
    project.side.data.model.SaveResultEntity(code = code, message = message)
