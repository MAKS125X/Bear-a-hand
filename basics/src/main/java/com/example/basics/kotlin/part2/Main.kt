package com.example.basics.kotlin.part2

fun main() {
    // Задание 4
    println("Задание 4")
    val user = User(1, "Максим", 21, Type.FULL)
    println("Время пользователя ${user.name}: ${user.startTime}")
    Thread.sleep(1000)
    println("Время пользователя ${user.name} после задержки: ${user.startTime}")

    // Задание 5
    println("\nЗадание 5")
    val userList = mutableListOf(user)
    userList.apply {
        add(User(2, "Иван", 56, Type.DEMO))
        add(User(3, "Петр", 17, Type.FULL))
        add(User(4, "Олег", 73, Type.DEMO))
        add(User(5, "Дмитрий", 26, Type.DEMO))
    }
    println("Все пользователи: $userList")

    // Задание 6
    println("\nЗадание 6")
    val fullTypeUserList = userList.filter { it.type == Type.FULL }
    println("Пользователи с полным доступом: $fullTypeUserList")

    // Задание 7
    println("\nЗадание 7")
    val userNamesList = userList.map { it.name }
    println("Список имён: $userNamesList")

    val firstUserName = userNamesList.first()
    val lastUserName = userNamesList.last()
    println("Первое имя: $firstUserName")
    println("Последнее имя: $lastUserName")

    // Задание 8
    println("\nЗадание 8")

    val youngUser = userList[2]
    println("Проверяемый пользователь: $youngUser")
    println(youngUser.isAdult())

    val oldUser = userList[4]
    println("Проверяемый пользователь: $oldUser")
    println(oldUser.isAdult())

    // Задание 9
    println("\nЗадание 9")

    val callBack =
        object : AuthCallback {
            override fun authSuccess() {
                println("Статус авторизации: успех")
            }

            override fun authFailed() {
                println("Статус авторизации: ошибка")
            }
        }

    println("Вызов методов анонимного объекта")
    callBack.authSuccess()
    callBack.authFailed()

    // Задание 10
    println("\nЗадание 10")

    println("Вызов auth для пользователя: $user")
    auth(user, callBack) {
        println("Updating the cache")
    }

    // Задание 13
    println("\nЗадание 13")

    println("Пользователь: ${userList[4]} осуществляет логин: ")
    val loginAction = Action.Login(userList[4])
    doAction(loginAction, callBack)

    println("\nВыход из системы: ")
    val logoutAction = Action.Logout
    doAction(logoutAction, callBack)
}

/**
 * Сheck that the user is over 18 years old and print this info
 */
fun User.isAdult(): Boolean =
    if (age > 18) {
        println("$name совершеннолетний")
        true
    } else {
        println("$name младше 19-ти лет")
        false
    }

/**
 * Authenticate adult [user] with [callBack] and [updateCache] effect
 */
inline fun auth(
    user: User,
    callBack: AuthCallback,
    updateCache: () -> Unit,
) {
    if (user.isAdult()) {
        callBack.authSuccess()
        updateCache()
    } else {
        callBack.authFailed()
    }
}

/**
 * Perform a certain authorization [action] with [callBack] for login
 */
fun doAction(
    action: Action,
    callBack: AuthCallback,
) {
    when (action) {
        is Action.Logout -> {
            println("Logout started")
        }

        is Action.Registration -> {
            println("Registration started")
        }

        is Action.Login -> {
            println("Login started")

            auth(action.user, callBack) {
                println("Updating the cache")
            }
        }
    }
}
