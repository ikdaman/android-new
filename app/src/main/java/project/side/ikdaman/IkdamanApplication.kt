package project.side.ikdaman

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.HiltAndroidApp
import project.side.remote.login.CurrentActivityHolder
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class IkdamanApplication: Application() {

    @Inject
    lateinit var currentActivityHolder: CurrentActivityHolder

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        KakaoSdk.init(this, BuildConfig.KAKAO_APP_KEY)

        NaverIdLoginSDK.initialize(
            this,
            BuildConfig.NAVER_CLIENT_ID,
            BuildConfig.NAVER_CLIENT_SECRET,
            "읽다만"
        )

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {
                currentActivityHolder.bind(activity)
            }
            override fun onActivityPaused(activity: Activity) {
                currentActivityHolder.unbind()
            }
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }
}
