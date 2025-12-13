package com.example.appcuoiky.viewmodel

import java.util.Properties
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class Emailsender(private val email: String, private val password: String) {

    fun sendEmail(toEmail: String, subject: String, message: String) {
        val props = Properties().apply {
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.socketFactory.port", "465")
            put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
            put("mail.smtp.auth", "true")
            put("mail.smtp.port", "465")
        }

        val session = Session.getDefaultInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(email, password)
            }
        })

        try {
            val mm = MimeMessage(session)
            mm.setFrom(InternetAddress(email))
            mm.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail))
            mm.subject = subject
            mm.setText(message)

            Transport.send(mm)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
