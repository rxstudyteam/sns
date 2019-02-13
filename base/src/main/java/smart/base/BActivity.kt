package smart.base

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.drawable.AnimationDrawable
import android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialog
import com.teamrx.base.R

open class BActivity : BDActivity() {
    override fun createProgress(): AppCompatDialog {
        val context = this

        val layout = FrameLayout(context).apply {
            addView(ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                setImageResource(R.drawable.loading_bg)
                ObjectAnimator.ofFloat(this, "rotation", 360F).apply {
                    duration = 1000L
                    repeatCount = ValueAnimator.INFINITE
                    interpolator = LinearInterpolator()
                }.start()
            })
            addView(ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                setImageResource(R.drawable.loading_anim)
                (drawable as AnimationDrawable).start()
            })
        }

        return AlertDialog.Builder(context).run {
            setView(layout)
            setCancelable(true)
            create().apply {
                window?.setBackgroundDrawableResource(android.R.color.transparent)
                window?.clearFlags(FLAG_DIM_BEHIND)
                setCanceledOnTouchOutside(false)
            }
        }
    }
}