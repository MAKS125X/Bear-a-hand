package com.example.profile.useCase

import com.example.profile.models.FriendUI
import com.example.result.DataError
import com.example.result.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn

class GetFriendListUseCase {
    operator fun invoke(): Flow<Either<DataError, List<FriendUI>>> =
        flowOf(
            Either.Right(
                listOf(
                    FriendUI(
                        "Дмитрий Валерьевич",
                        "https://wallpaperforu.com/wp-content/uploads/2023/11/Leonardo-DiCaprio-Hd-Wallpapers-For-Pc.jpg",
                    ),
                    FriendUI(
                        "Евгений Александров",
                        "https://i.ytimg.com/vi/70jYjONsXTU/maxresdefault.jpg",
                    ),
                    FriendUI(
                        "Виктор Кузнецов",
                        "https://i.pinimg.com/originals/74/8c/ea/748ceabaaf1c09d7d00ce1813681343c.jpg",
                    ),
                )
            )
        ).flowOn(Dispatchers.IO)
}