package smart.base

import android.app.Activity
import android.base.CActivity
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

@Suppress("DEPRECATION")
abstract class SmartDActivity : CActivity() {
    companion object {
        private const val ETC_INFO = "SNS"
    }

    override fun onStart() {
        super.onStart()
        egg(this)
//        eggJ(this)
    }

    private fun egg(activity: Activity) {
        try {
            val VERSION_TAG = "show_me_the_money"
            val parent = activity.findViewById<ViewGroup>(android.R.id.content)
            if (parent.findViewWithTag<View>(VERSION_TAG) != null)
                return
            val info = activity.packageManager.getPackageInfo(activity.packageName, 0)
            val ver = TextView(activity)
            ver.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) "[v${info.longVersionCode}] $ETC_INFO" else "[v${info.versionCode}] $ETC_INFO"
            ver.tag = VERSION_TAG
            ver.setTextColor(0x55ff0000)
            ver.setBackgroundColor(0x5500ff00)
            ver.textSize = 14f//sp
            val dp35 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35f, activity.resources.displayMetrics).toInt()
            parent.addView(ver, ViewGroup.LayoutParams.WRAP_CONTENT, dp35)
            val funcs = mutableListOf<String>()
            val eastarEgg = Class.forName("android.etc.EastarEgg")
            for (method in eastarEgg.methods) {
                if (method.declaringClass != eastarEgg)
                    continue
                if (method.returnType != Void.TYPE)
                    continue
                if (method.name.contains("access$"))//exclude lambda
                    continue
                if (method.name.contains("lambda"))//exclude lambda
                    continue
                funcs += method.name
            }

            ver.setOnClickListener {
                AlertDialog.Builder(activity).run {
                    setItems(funcs.toTypedArray()) { dialog, which ->
                        try {
                            val constructor = eastarEgg.getConstructor(Activity::class.java)
                            val receiver = constructor.newInstance(activity)
                            val funcname = (dialog as AlertDialog).listView.getItemAtPosition(which) as String
                            val method = eastarEgg.getMethod(funcname)
                            method.invoke(receiver)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun eggJ(activity: Activity) {
        try {
            val VERSION_TAG = "show_me_the_money_j"
            val parent = activity.findViewById<ViewGroup>(android.R.id.content)
            if (parent.findViewWithTag<View>(VERSION_TAG) != null)
                return
            val info = activity.packageManager.getPackageInfo(activity.packageName, 0)
            val ver = TextView(activity)
            ver.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) "[v${info.longVersionCode}] $ETC_INFO" else "[v${info.versionCode}] $ETC_INFO"
            ver.tag = VERSION_TAG
            ver.setTextColor(0x55ff0000)
            ver.setBackgroundColor(0x5500ff00)
            ver.textSize = 14f//sp
            val dp35 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35f, activity.resources.displayMetrics).toInt()
            var lp = ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dp35)
            lp.marginStart = dp35 * 3
            parent.addView(ver, lp)

            val funcs = mutableListOf<String>()
            val eastarEgg = Class.forName("android.etc.EastarEggJ")
            for (method in eastarEgg.methods) {
                if (method.declaringClass != eastarEgg)
                    continue
                if (method.returnType != Void.TYPE)
                    continue
                if (method.name.contains("lambda"))//exclude lambda
                    continue
                funcs += method.name
            }

            ver.setOnClickListener {
                AlertDialog.Builder(activity).run {
                    setItems(funcs.toTypedArray()) { dialog, which ->
                        try {
                            val constructor = eastarEgg.getConstructor(Activity::class.java)
                            val receiver = constructor.newInstance(activity)
                            val funcname = (dialog as AlertDialog).listView.getItemAtPosition(which) as String
                            val method = eastarEgg.getMethod(funcname)
                            method.invoke(receiver)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
