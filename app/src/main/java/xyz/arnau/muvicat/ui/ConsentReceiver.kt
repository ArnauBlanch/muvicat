package xyz.arnau.muvicat.ui

import xyz.arnau.muvicat.ui.home.ConsentStatus

interface ConsentReceiver {
    fun receiveUsageConsent(status: ConsentStatus)
    fun receiveCrashReportingConsent(status: ConsentStatus)
}
