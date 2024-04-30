package com.example.simbirsoftmobile.domain.utils

import com.example.simbirsoftmobile.domain.models.EventModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object UnreadNewsController {
    private val readEventIds: MutableSet<String> = mutableSetOf()

    private val _notificationFlow: MutableSharedFlow<Int> = MutableSharedFlow(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val notificationFlow: SharedFlow<Int> = _notificationFlow.asSharedFlow()

    private var currentEventList: List<EventModel> = emptyList()

    fun addReadValue(readEvent: EventModel) {
        readEventIds.add(readEvent.id)
        updateValue()
    }

    fun emitUnreadValue(eventList: List<EventModel>) {
        currentEventList = eventList
        updateValue()
    }

    private fun updateValue() {
        val unreadCount = currentEventList.count { !readEventIds.contains(it.id) }
        _notificationFlow.tryEmit(unreadCount)
    }
}
