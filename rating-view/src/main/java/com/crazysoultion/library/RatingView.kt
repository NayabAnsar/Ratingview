package com.crazysoultion.library

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener

/**
 * Created by Ornolfr on 10.06.2016.
 */
class RatingView : View, OnTouchListener {
    private var mListener: OnRatingChangedListener? = null

    //Bitmaps for your rating drawables
    private var mDrawableEmpty: Bitmap? = null
    private var mDrawableHalf: Bitmap? = null
    private var mDrawableFilled: Bitmap? = null

    //For drawing view
    private val mRect = Rect()

    //Boolean field: if true - user cannot affect the view
    private var mIsIndicator = false

    //Float field: displayed rating, 0 <= mRating <= mMaxCount
    private var mRating = 0f

    /**
     * @return current max count
     */
    //Integer field: max drawables count and rating as well
    var maxCount = 0
        private set

    /**
     * @return drawable size in px
     */
    //Integer field: drawable size
    var drawableSize = 0
        private set

    /**
     * @return drawable margin in px
     */
    //Integer field: inner margin between drawables
    var drawableMargin = 0
        private set

    /**
     * This interface will return rating value before changing it and after
     */
    interface OnRatingChangedListener {
        fun onRatingChange(oldRating: Float, newRating: Float)
    }

    constructor(context: Context?) : super(context) {
        init(null, 0, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs, 0, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs, defStyleAttr, 0)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs, defStyleAttr, defStyleRes)
    }

    private fun init(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val a =
            context.obtainStyledAttributes(attrs, R.styleable.RatingView, defStyleAttr, defStyleRes)
        drawableMargin = a.getDimension(
            R.styleable.RatingView_drawable_margin,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_DRAWABLE_MARGIN_IN_DP.toFloat(),
                resources.displayMetrics
            )
        ).toInt()
        drawableSize = a.getDimension(
            R.styleable.RatingView_drawable_size,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_DRAWABLE_SIZE_IN_DP.toFloat(),
                resources.displayMetrics
            )
        ).toInt()
        require(drawableSize >= 0) { "Drawable size < 0" }
        maxCount = a.getInteger(R.styleable.RatingView_max_count, DEFAULT_MAX_COUNT)
        require(maxCount >= 1) { "Max count < 1" }
        mRating = a.getFloat(R.styleable.RatingView_rating, DEFAULT_RATING)
        mIsIndicator = a.getBoolean(R.styleable.RatingView_is_indicator, DEFAULT_IS_INDICATOR)
        mDrawableEmpty = BitmapFactory.decodeResource(
            context.resources,
            a.getResourceId(R.styleable.RatingView_drawable_empty, R.drawable.ic_star_empty)
        )
        mDrawableHalf = BitmapFactory.decodeResource(
            context.resources,
            a.getResourceId(R.styleable.RatingView_drawable_half, R.drawable.ic_star_half)
        )
        mDrawableFilled = BitmapFactory.decodeResource(
            context.resources,
            a.getResourceId(R.styleable.RatingView_drawable_filled, R.drawable.ic_star_filled)
        )
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            resolveSize(
                drawableSize * maxCount + drawableMargin * (maxCount - 1) /* + getPaddingLeft() + getPaddingRight()*/,
                widthMeasureSpec
            ),
            resolveSize(drawableSize, heightMeasureSpec) /* + getPaddingBottom() + getPaddingTop()*/
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (!mIsIndicator) setOnTouchListener(this)
        //        canvas.translate(getPaddingLeft(), getPaddingTop());
        if (mDrawableFilled != null && mDrawableHalf != null && mDrawableEmpty != null) {
            //set view size
            mRect[0, 0, drawableSize] = drawableSize
            var fullDrawablesCount = mRating.toInt()
            val emptyDrawablesCount = maxCount - Math.round(mRating)
            if (mRating - fullDrawablesCount >= 0.75f) fullDrawablesCount++

            //drawing full drawables
            for (i in 0 until fullDrawablesCount) {
                canvas.drawBitmap(mDrawableFilled!!, null, mRect, null)
                mRect.offset(drawableSize + drawableMargin, 0)
            }

            //drawing half drawable if needed
            if (mRating - fullDrawablesCount >= 0.25f && mRating - fullDrawablesCount < 0.75f) {
                canvas.drawBitmap(mDrawableHalf!!, null, mRect, null)
                mRect.offset(drawableSize + drawableMargin, 0)
            }

            //drawing empty drawables
            for (i in 0 until emptyDrawablesCount) {
                canvas.drawBitmap(mDrawableEmpty!!, null, mRect, null)
                mRect.offset(drawableSize + drawableMargin, 0)
            }
        }
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> return true
            MotionEvent.ACTION_UP -> {
                rating = Math.round(event.x / width * maxCount + 0.5).toFloat()
                return false
            }

            else -> {}
        }
        return super.onTouchEvent(event)
    }

    /**
     * Sets OnRatingChangedListener on this view, which will give you old and new rating.
     *
     * @param listener your listener
     */
    fun setOnRatingChangedListener(listener: OnRatingChangedListener?) {
        mListener = listener
    }

    /**
     * Sets empty drawable
     *
     * @param drawableEmpty bitmap of your drawable
     */
    fun setDrawableEmpty(drawableEmpty: Bitmap?) {
        mDrawableEmpty = drawableEmpty
        invalidate()
    }

    /**
     * Sets half drawable
     *
     * @param drawableHalf bitmap of your drawable
     */
    fun setDrawableHalf(drawableHalf: Bitmap?) {
        mDrawableHalf = drawableHalf
        invalidate()
    }

    /**
     * Sets filled drawable
     *
     * @param drawableFilled bitmap of your drawable
     */
    fun setDrawableFilled(drawableFilled: Bitmap?) {
        mDrawableFilled = drawableFilled
        invalidate()
    }

    var isIndicator: Boolean
        /**
         * @return is it indicator or not
         */
        get() = mIsIndicator
        /**
         * Sets whether rating view is isIndicator or not
         *
         * @param isIndicator boolean, true - user can't interact the view
         */
        set(isIndicator) {
            mIsIndicator = isIndicator
            setOnTouchListener(if (mIsIndicator) null else this)
        }
    var rating: Float
        /**
         * @return current rating
         */
        get() = mRating
        /**
         * Sets the rating of this view
         *
         * @param rating custom rating
         */
        set(rating) {
            var newRating = rating
            if (newRating < 0) {
                newRating = 0f
            } else if (newRating > maxCount) {
                newRating = maxCount.toFloat()
            }
            if (mListener != null) mListener!!.onRatingChange(mRating, newRating)
            mRating = newRating
            invalidate()
        }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val savedState = SavedState(superState)
        savedState.mRating = mRating
        savedState.mIndicator = mIsIndicator
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is SavedState) {
            val savedState = state
            super.onRestoreInstanceState(savedState.superState)
            mRating = savedState.mRating
            mIsIndicator = savedState.mIndicator
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    internal class SavedState : BaseSavedState {
        var mRating = 0f
        var mIndicator = false

        constructor(superState: Parcelable?) : super(superState)
        private constructor(`in`: Parcel) : super(`in`) {
            mRating = `in`.readFloat()
            mIndicator = `in`.readInt() == 0
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeFloat(mRating)
            out.writeInt(if (mIndicator) 1 else 0)
        }

        companion object {
            val CREATOR: Parcelable.Creator<SavedState?> = object : Parcelable.Creator<SavedState?> {
                override fun createFromParcel(`in`: Parcel): SavedState? {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }

        override fun describeContents(): Int {
            return 0
        }

        object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }

    companion object {
        //Default values
        private const val DEFAULT_IS_INDICATOR = false
        private const val DEFAULT_RATING = 3.5f
        private const val DEFAULT_MAX_COUNT = 5
        private const val DEFAULT_DRAWABLE_SIZE_IN_DP = 32
        private const val DEFAULT_DRAWABLE_MARGIN_IN_DP = 4
    }
}