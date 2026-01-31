package project.side.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import project.side.domain.DataResource
import project.side.domain.model.ManualBookInfo
import project.side.domain.usecase.SaveManualBookInfoUseCase
import javax.inject.Inject

@HiltViewModel
class ManualInputViewModel @Inject constructor(
    private val saveManualBookInfoUseCase: SaveManualBookInfoUseCase
) : ViewModel() {
    // expose only boolean success/failure (null = idle/loading)
    val saveState = MutableStateFlow<Boolean?>(null)

    fun saveManualBookInfo(manual: ManualBookInfo) {
        viewModelScope.launch {
            saveManualBookInfoUseCase(manual).collect { result ->
                when (result) {
                    is DataResource.Success -> {
                        saveState.value = result.data
                        // keep the value long enough for UI to observe, then reset
                        delay(200)
                        saveState.value = null
                    }
                    is DataResource.Error -> {
                        saveState.value = false
                        delay(200)
                        saveState.value = null
                    }
                    is DataResource.Loading -> saveState.value = null
                }
            }
        }
    }

    // convenience helper for UI layer: accept raw strings and create domain ManualBookInfo here
    fun saveManualBookInfoFromUi(
        title: String,
        author: String,
        publisher: String?,
        pubDate: String?,
        isbn: String?,
        pageCount: String?
    ) {
        saveManualBookInfo(
            ManualBookInfo(
                title = title,
                author = author,
                publisher = publisher,
                pubDate = pubDate,
                isbn = isbn,
                pageCount = pageCount
            )
        )
    }

    // overload to accept optional reason and startDate from UI bottom sheet
    fun saveManualBookInfoFromUi(
        title: String,
        author: String,
        publisher: String?,
        pubDate: String?,
        isbn: String?,
        pageCount: String?,
        reason: String? = null,
        startDate: java.time.LocalDate? = null
    ) {
        // for now we ignore reason/startDate until domain/data models include them
        saveManualBookInfoFromUi(title, author, publisher, pubDate, isbn, pageCount)
    }
}
