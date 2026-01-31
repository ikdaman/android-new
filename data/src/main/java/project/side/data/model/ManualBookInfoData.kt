package project.side.data.model

import project.side.domain.model.ManualBookInfo
import project.side.remote.model.ManualBookInfo as RemoteManualBookInfo

// simple mapping helper from domain to remote model
fun ManualBookInfo.toRemote(): RemoteManualBookInfo = RemoteManualBookInfo(
    title = this.title,
    author = this.author,
    publisher = this.publisher,
    pubDate = this.pubDate,
    isbn = this.isbn,
    pageCount = this.pageCount
)
