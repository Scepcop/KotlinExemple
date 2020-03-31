package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting

object UserHolder {
    private val map = mutableMapOf<String, User>()

    fun registerUser(
        fullName: String,
        email: String,
        password: String
    ): User {
        if (!map.containsKey(email.toLowerCase())) {
            return User.makeUser(fullName, email = email, password = password)
                .also { user -> map[user.login] = user }
        } else throw IllegalArgumentException("A user with this email already exists")
    }

    fun loginUser(login: String, password: String): String? {
        var log: String = login

        val plus: String? = login?.let {
            Regex(pattern = """\+""")
                .find(input = it)?.value
        }
        if (plus != null && plus.length == 1) {
            log = login.replace("[^+\\d]".toRegex(), "")
        }

        return map[log.trim()]?.run {
            if (checkPassword(password)) this.userInfo
            else null
        }
    }

    fun registerUserByPhone(
        fullName: String,
        rawPhone: String
    ):User = User.makeUser(fullName, phone = rawPhone)
        .also { user ->
        if (map.containsKey(user.login)) {
            throw IllegalArgumentException("A user with this phone already exists")
        }

        return user.also { user ->
            map[user.login] = user
        }
    }


    fun requestAccessCode(login: String): Unit {
        var log: String = login

        val plus: String? = login?.let {
            Regex(pattern = """\+""")
                .find(input = it)?.value
        }
        if (plus != null && plus.length == 1) {
            log = login.replace("[^+\\d]".toRegex(), "")
        }

        val user = map[log]
        user ?: throw IllegalArgumentException("Unregistred phone number")
        user.requestAccessCode()
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder() {
        map.clear()
    }
}
