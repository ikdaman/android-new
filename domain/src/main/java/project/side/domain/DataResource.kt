package project.side.domain

sealed class DataResource<out T> {
    class Success<T>(val data: T): DataResource<T> ()
    class Error(val message: String?, val code: Int = 0): DataResource<Nothing>()
    class Loading<T>(val data: T? = null): DataResource<T>()

    companion object {
        fun <T> success(data: T) = Success(data)
        fun error(message: String?, code: Int = 0) = Error(message, code)
        fun <T> loading(data: T? = null): Loading<T> = Loading(data)
    }
}