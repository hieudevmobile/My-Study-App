package com.example.mystudyapp.login

class CheckEmail_Password(
    private val e:String,
    private val p: String) {
    fun isValidEmail(): Boolean {
        // Accept common email formats instead of restricting to @gmail.com only.
        val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return emailPattern.matches(e) && e.length in 6..100
    }

    fun isValidPassword(): Boolean {
        val passwordPattern = Regex("^(?=.*\\d)(?=.*[@#\$%^&+=!*])[\\S]{6,}$")
        return passwordPattern.matches(p) && p.length <= 40
    }
}