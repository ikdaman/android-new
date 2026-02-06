package project.side.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
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
    private val _saveEvent = MutableSharedFlow<SaveEvent>()
    val saveEvent: SharedFlow<SaveEvent> = _saveEvent.asSharedFlow()

    fun saveManualBookInfo(manual: ManualBookInfo) {
        viewModelScope.launch {
            saveManualBookInfoUseCase(manual).collect { result ->
                when (result) {
                    is DataResource.Success -> {
                        if (result.data) _saveEvent.emit(SaveEvent.Success)
                        else _saveEvent.emit(SaveEvent.Error("책 저장에 실패했어요."))
                    }
                    is DataResource.Error -> {
                        _saveEvent.emit(SaveEvent.Error(result.message ?: "책 저장에 실패했어요."))
                    }
                    is DataResource.Loading -> { /* no-op */ }
                }
            }
        }
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
                reason = reason,
                startDate = startDate?.format(fmt),
                endDate = endDate?.format(fmt)
            )
        )
    }
}
