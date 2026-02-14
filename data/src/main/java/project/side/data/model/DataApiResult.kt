package project.side.data.model

sealed class DataApiResult<out T> {
    data class Success<out T>(val data: T) : DataApiResult<T>()
    data class Error(val message: String, val code: Int = 0) : DataApiResult<Nothing>() {
        override fun toString(): String {
            return "Error(message='$message', code=$code)"
        }
    }
    data object Loading : DataApiResult<Nothing>()
}