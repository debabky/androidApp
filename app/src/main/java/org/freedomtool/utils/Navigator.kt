package org.freedomtool.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import org.freedomtool.R
import org.freedomtool.feature.browser.BrowserActivity
import org.freedomtool.feature.home.HomeActivity
import org.freedomtool.feature.intro.StartActivity
import org.freedomtool.feature.onBoarding.ConfirmationActivity
import org.freedomtool.feature.onBoarding.ScanActivity
import org.freedomtool.feature.security.CheckPinCodeActivity
import org.freedomtool.feature.security.CreateCredentialActivity
import org.freedomtool.feature.security.CreatePinCodeActivity
import org.freedomtool.utils.nfc.model.EDocument


/**
 * Performs transitions between screens.
 * 'open-' will open related screen as a child.<p>
 * 'to-' will open related screen and finish current.
 */
class Navigator private constructor() {
    private var activity: Activity? = null
    private var fragment: Fragment? = null
    private var context: Context? = null

    companion object {
        fun from(activity: Activity): Navigator {
            val navigator = Navigator()
            navigator.activity = activity
            navigator.context = activity
            return navigator
        }

        fun from(fragment: Fragment): Navigator {
            val navigator = Navigator()
            navigator.fragment = fragment
            navigator.context = fragment.requireContext()
            return navigator
        }

        fun from(context: Context): Navigator {
            val navigator = Navigator()
            navigator.context = context
            return navigator
        }
    }

    private fun performIntent(intent: Intent?, requestCode: Int? = null, bundle: Bundle? = null) {
        if (intent != null) {
            if (!IntentLock.checkIntent(intent, context)) return
            activity?.let {
                if (requestCode != null) {
                    it.startActivityForResult(intent, requestCode, bundle ?: Bundle.EMPTY)
                } else {
                    it.startActivity(intent, bundle ?: Bundle.EMPTY)
                }
                return
            }

            fragment?.let {
                if (requestCode != null) {
                    it.startActivityForResult(intent, requestCode, bundle ?: Bundle.EMPTY)
                } else {
                    it.startActivity(intent, bundle ?: Bundle.EMPTY)
                }
                return
            }

            val newIntent = intent.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context?.startActivity(newIntent, bundle ?: Bundle.EMPTY)

        }
    }


    fun openCreateCredential() {
        val intent = Intent(context, CreateCredentialActivity::class.java)
        performIntent(intent)
    }

    fun openCreatePinCode() {
        val intent = Intent(context, CreatePinCodeActivity::class.java)
        performIntent(intent)
    }

    fun openScan() {
        val intent = Intent(context, ScanActivity::class.java)
        performIntent(intent)
    }


    fun openConfirmation(eDocument: EDocument) {
        val intent = Intent(context, ConfirmationActivity::class.java)
        eDocument.personDetails = null
        intent.putExtra(ConfirmationActivity.E_DOCUMENT, eDocument)
        performIntent(intent)
    }

    fun openStart() {
        val intent = Intent(context, StartActivity::class.java)
        performIntent(intent)
    }

    fun openBrowser(url: String) {
        val intent = Intent(context, BrowserActivity::class.java)
        intent.putExtra(BrowserActivity.URL_TRANSFER_KEY, url)
        performIntent(intent)
    }

    private fun finishAffinity(activity: Activity) {
        activity.setResult(Activity.RESULT_CANCELED, null)
        ActivityCompat.finishAffinity(activity)
    }

    private fun fadeOut(activity: Activity) {
        ActivityCompat.finishAfterTransition(activity)
        activity.overridePendingTransition(0, R.anim.activity_fade_out)
        activity.finish()
    }

    private fun createTransitionBundle(
        activity: Activity, vararg pairs: Pair<View?, String>
    ): Bundle {
        val sharedViews = arrayListOf<androidx.core.util.Pair<View, String>>()

        pairs.forEach {
            val view = it.first
            if (view != null) {
                sharedViews.add(androidx.core.util.Pair(view, it.second))
            }
        }

        return if (sharedViews.isEmpty()) {
            Bundle.EMPTY
        } else {
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity, *sharedViews.toTypedArray()
            ).toBundle() ?: Bundle.EMPTY
        }
    }


    fun openCheckPinCode() {
        val intent = Intent(context, CheckPinCodeActivity::class.java)
        performIntent(intent)
    }

    fun openHome() {
        val intent = Intent(context, HomeActivity::class.java)
        performIntent(intent)
    }


}