package xyz.santeri.citybike.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Parcel
import android.os.Parcelable
import android.text.format.DateUtils
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import xyz.santeri.citybike.R
import java.lang.ref.WeakReference

/**
 * A TextView that, given a reference time, renders that time as a time period relative to the current time.
 *
 * @author Kiran Rao
 * @author Santeri Elo <me@santeri.xyz>
 */
class RelativeTimeTextView : TextView {
    private var referenceTime: Long = 0
    private var prefix: String? = null
    private var suffix: String? = null
    private var updateHandler: Handler = Handler()
    private var updateTimeTask: UpdateTimeRunnable? = null
    private var isUpdateTaskRunning = false

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        val a = context.theme.obtainStyledAttributes(attrs,
                R.styleable.RelativeTimeTextView, 0, 0)
        try {
            prefix = a.getString(R.styleable.RelativeTimeTextView_relative_time_prefix)
            suffix = a.getString(R.styleable.RelativeTimeTextView_relative_time_suffix)

            prefix = if (prefix == null) "" else prefix
            suffix = if (suffix == null) "" else suffix
        } finally {
            a.recycle()
        }

        setReferenceTime(referenceTime)
    }

    /**
     * Sets the reference time for this view. At any moment, the view will render a relative time period relative to the time set here.
     *
     *
     * This value can also be set with the XML attribute `reference_time`
     * @param referenceTime The timestamp (in milliseconds since epoch) that will be the reference point for this view.
     */
    fun setReferenceTime(referenceTime: Long) {
        this.referenceTime = referenceTime
        stopTaskForPeriodicallyUpdatingRelativeTime()
        initUpdateTimeTask()
        startTaskForPeriodicallyUpdatingRelativeTime()
        updateTextDisplay()
    }

    @SuppressLint("SetTextI18n")
    private fun updateTextDisplay() {
        if (this.referenceTime == -1L)
            return

        text = "$prefix ${getRelativeTimeDisplayString(referenceTime, System.currentTimeMillis())} $suffix".trim()
    }

    /**
     * Get the text to display for relative time. By default, this calls [DateUtils.getRelativeTimeSpanString] passing [DateUtils.FORMAT_ABBREV_RELATIVE] flag.
     * <br></br>
     * You can override this method to customize the string returned. For example you could add prefixes or suffixes, or use Spans to style the string etc
     * @param referenceTime The reference time passed in through [.setReferenceTime] or through `reference_time` attribute
     * @param now The current time
     * @return The display text for the relative time
     */
    private fun getRelativeTimeDisplayString(referenceTime: Long, now: Long): CharSequence {
        val difference = now - referenceTime
        return if (difference >= 0 && difference <= DateUtils.MINUTE_IN_MILLIS)
            resources.getString(R.string.time_now)
        else
            DateUtils.getRelativeTimeSpanString(
                    this.referenceTime,
                    now,
                    DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_RELATIVE)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startTaskForPeriodicallyUpdatingRelativeTime()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopTaskForPeriodicallyUpdatingRelativeTime()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)

        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            stopTaskForPeriodicallyUpdatingRelativeTime()
        } else {
            startTaskForPeriodicallyUpdatingRelativeTime()
        }
    }

    private fun startTaskForPeriodicallyUpdatingRelativeTime() {
        if (updateTimeTask!!.isDetached) initUpdateTimeTask()
        updateHandler.post(updateTimeTask)
        isUpdateTaskRunning = true
    }

    private fun initUpdateTimeTask() {
        updateTimeTask = UpdateTimeRunnable(this, referenceTime)
    }

    private fun stopTaskForPeriodicallyUpdatingRelativeTime() {
        if (isUpdateTaskRunning) {
            updateTimeTask!!.detach()
            updateHandler.removeCallbacks(updateTimeTask)
            isUpdateTaskRunning = false
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)
        ss.referenceTime = referenceTime
        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }

        referenceTime = state.referenceTime
        super.onRestoreInstanceState(state.superState)
    }

    open class SavedState : View.BaseSavedState {
        var referenceTime: Long = 0

        constructor(superState: Parcelable) : super(superState)

        private constructor(parcel: Parcel) : super(parcel) {
            referenceTime = parcel.readLong()
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeLong(referenceTime)
        }

        companion object {
            @Suppress("unused")
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun newArray(size: Int): Array<SavedState> {
                    return emptyArray()
                }

                override fun createFromParcel(parcel: Parcel): SavedState {
                    return SavedState(parcel)
                }
            }
        }
    }

    private class UpdateTimeRunnable internal constructor(rttv: RelativeTimeTextView, private val mRefTime: Long) : Runnable {
        private val weakRefRttv: WeakReference<RelativeTimeTextView> = WeakReference(rttv)

        internal val isDetached: Boolean
            get() = weakRefRttv.get() == null

        internal fun detach() {
            weakRefRttv.clear()
        }

        override fun run() {
            val rttv = weakRefRttv.get() ?: return
            val difference = Math.abs(System.currentTimeMillis() - mRefTime)
            var interval = INITIAL_UPDATE_INTERVAL

            when {
                difference > DateUtils.WEEK_IN_MILLIS -> interval = DateUtils.WEEK_IN_MILLIS
                difference > DateUtils.DAY_IN_MILLIS -> interval = DateUtils.DAY_IN_MILLIS
                difference > DateUtils.HOUR_IN_MILLIS -> interval = DateUtils.HOUR_IN_MILLIS
            }

            rttv.updateTextDisplay()
            rttv.updateHandler.postDelayed(this, interval)
        }
    }

    companion object {
        private const val INITIAL_UPDATE_INTERVAL = DateUtils.MINUTE_IN_MILLIS
    }
}