package com.januarzidanetinendeng.eightcanteen

import android.app.Application
import com.januarzidanetinendeng.eightcanteen.data.local.SessionManager
import com.januarzidanetinendeng.eightcanteen.data.remote.ApiConfig

class EightCanteenApp : Application() {

    companion object {
        lateinit var instance: EightCanteenApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this 
        // Inisialisasi ApiConfig dengan Application Context dan load JWT token jika tersimpan
        ApiConfig.init(this)
    }
}
