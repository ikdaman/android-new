package project.side.remote.login

import android.app.Activity
import android.content.Context
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentActivityHolder @Inject constructor() {
    private var ref: WeakReference<Activity>? = null

    fun bind(activity: Activity) {
        ref = WeakReference(activity)
    }

    fun unbind() {
        ref = null
    }

    fun require(): Context = ref?.get()
        ?: throw IllegalStateException("No bound Activity. Social login must be triggered while an Activity is in foreground.")
}
