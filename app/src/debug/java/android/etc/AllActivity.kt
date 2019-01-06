package android.etc

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import com.teamrx.rxtargram.base.AppActivity
import java.util.*

class AllActivity : AppActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sc = ScrollView(this)
        var ll = LinearLayout(this)
        sc.addView(ll)
        setContentView(sc)

        val activities = getActivities(mContext)
        for (activity in activities!!) {
            if (javaClass.name == activity)
                continue
            val btn = Button(mContext)
            btn.text = activity.substring(activity.lastIndexOf('.') + 1)
            btn.setOnClickListener { startActivity(Intent().setClassName(mContext, activity)) }
            ll.addView(btn, -1, -2)
        }
    }

    fun getActivities(context: Context): List<String>? {
        try {
            val pm = context.packageManager
            val list = pm.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES).activities
            val clzs = ArrayList<String>()
            for (activityInfo in list) {
                val clzName = activityInfo.name
                val pkgName = activityInfo.packageName
                if (clzName.startsWith(pkgName)) {
                    clzs.add(activityInfo.name)
                    //                    Log.d(activityInfo.packageName, activityInfo.name);
                }
            }
            return clzs
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return null
    }

}
