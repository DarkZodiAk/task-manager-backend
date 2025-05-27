package com.example.data.local.matchers

import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress


val EmailValidatorImpl = EmailValidator { email ->
    if (!email.contains("@")) {
        return@EmailValidator false
    }

    // Step 3: Use InternetAddress for validation
    return@EmailValidator try {
        val internetAddress = InternetAddress(email)
        internetAddress.validate()
        true
    } catch (e: AddressException) {
        false
    }
}