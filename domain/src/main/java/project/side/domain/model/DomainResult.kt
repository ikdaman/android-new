package project.side.domain.model

sealed class DomainResult<out T> {
    data object Init : DomainResult<Nothing>()
    data class Success<out T>(val data: T) : DomainResult<T>()
    data class Error(val message: String) : DomainResult<Nothing>() {
        override fun toString(): String {
            return "Error(message='$message')"
        }
    }
    data object Loading : DomainResult<Nothing>()
}