package project.side.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.time.format.DateTimeFormatter
import java.time.LocalDate
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

    // accept optional reason and startDate/endDate from UI bottom sheet
    fun saveManualBookInfoFromUi(
        title: String,
        author: String,
        publisher: String?,
        pubDate: String?,
        isbn: String?,
        pageCount: String?,
        reason: String? = null,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null
    ) {
        val fmt = DateTimeFormatter.ISO_LOCAL_DATE
        saveManualBookInfo(
            ManualBookInfo(
                title = title,
                author = author,
                publisher = publisher,
                pubDate = pubDate,
                isbn = isbn,
                pageCount = pageCount,
                startDate = startDate?.format(fmt),
                endDate = endDate?.format(fmt)
            )
        )
    }
}
