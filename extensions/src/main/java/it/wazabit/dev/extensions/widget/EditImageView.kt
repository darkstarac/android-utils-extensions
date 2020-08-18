package it.wazabit.dev.extensions.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import timber.log.Timber


class EditImageView : View {

    private var _exampleString: String? = null // TODO: use a default from R.string...
    private var _exampleColor: Int = Color.RED // TODO: use a default from R.color...
    private var _exampleDimension: Float = 0f // TODO: use a default from R.dimen...

    private var selectionRectPadding = 160

    private var textPaint: TextPaint? = null
    private var textWidth: Float = 0f
    private var textHeight: Float = 0f

    /**
     * The text to draw
     */
    var exampleString: String?
        get() = _exampleString
        set(value) {
            _exampleString = value
            invalidateTextPaintAndMeasurements()
        }

    /**
     * The font color
     */
    var exampleColor: Int
        get() = _exampleColor
        set(value) {
            _exampleColor = value
            invalidateTextPaintAndMeasurements()
        }

    /**
     * In the example view, this dimension is the font size.
     */
    var exampleDimension: Float
        get() = _exampleDimension
        set(value) {
            _exampleDimension = value
            invalidateTextPaintAndMeasurements()
        }

    /**
     * In the example view, this drawable is drawn above the text.
     */
    var exampleDrawable: Drawable? = null

    private val selectionRectStrokeWith = 10f
    private val selectionRectTouchOffset = 40f
    private val selectionRectInternalTouchOffset = selectionRectTouchOffset*3.5f

    private val selectionRectPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = selectionRectStrokeWith
    }

    private val testPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }

    private val testPaint2 = Paint().apply {
        color = Color.YELLOW
        style = Paint.Style.FILL
    }




    private var selectionRectX0 = 0
    private var selectionRectY0 = 0
    private var selectionRectX1 = 0
    private var selectionRectY1 = 0

    private val selectionRect: Rect
    get() {
        return Rect(selectionRectX0,selectionRectY0,selectionRectX1,selectionRectY1)
    }

    object Edges{
        var top = 0f
        var right = 0f
        var bottom = 0f
        var left = 0f
    }

    private val edges:Edges
    get() {
        return Edges.apply {
            top = selectionRectStrokeWith/2
            right = width.toFloat() - selectionRectStrokeWith/2
            bottom = height.toFloat() - selectionRectStrokeWith/2
            left = selectionRectStrokeWith/2
        }
    }

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }


    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Inflate layout
//        inflate(context, R.layout.edit_image_view_layout, this)
        setBackgroundColor(Color.BLACK)
        // Load attributes
//        val a = context.obtainStyledAttributes(
//            attrs, R.styleable.EditImageView, defStyle, 0
//        )
//
//        _exampleString = a.getString(
//            R.styleable.EditImageView_exampleString
//        )
//        _exampleColor = a.getColor(
//            R.styleable.EditImageView_exampleColor,
//            exampleColor
//        )
//        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
//        // values that should fall on pixel boundaries.
//        _exampleDimension = a.getDimension(
//            R.styleable.EditImageView_exampleDimension,
//            exampleDimension
//        )
//
//        if (a.hasValue(R.styleable.EditImageView_exampleDrawable)) {
//            exampleDrawable = a.getDrawable(
//                R.styleable.EditImageView_exampleDrawable
//            )
//            exampleDrawable?.callback = this
//        }
//
//        a.recycle()
//
//
        invalidateTextPaintAndMeasurements()
    }

    private fun invalidateTextPaintAndMeasurements() {
//        textPaint?.let {
//            it.textSize = exampleDimension
//            it.color = exampleColor
//            textWidth = it.measureText(exampleString)
//            textHeight = it.fontMetrics.bottom
//        }

        selectionRectX0 = selectionRectPadding
        selectionRectY0 = selectionRectPadding
        selectionRectX1 = width -  selectionRectPadding
        selectionRectY1 = height - selectionRectPadding

        Timber.d("invalidateTextPaintAndMeasurements $selectionRectX0 $selectionRectY0 $selectionRectX1 $selectionRectY1")
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Timber.d("onSizeChanged $selectionRectX0 $selectionRectY0 $selectionRectX1 $selectionRectY1")
        invalidateTextPaintAndMeasurements()
//        selectionRectX0 = selectionRectPadding
//        selectionRectY0 = selectionRectPadding
//        selectionRectX1 = width -  paddingRight
//        selectionRectY1 = height - paddingBottom

    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
//        val paddingLeft = selectionRectPadding
//        val paddingTop = selectionRectPadding
//        val paddingRight = paddingRight
//        val paddingBottom = paddingBottom
//
//        val contentWidth = width - paddingLeft - paddingRight
//        val contentHeight = height - paddingTop - paddingBottom
//        Timber.d("invalidateTextPaintAndMeasurements $paddingLeft $paddingTop $contentWidth $contentHeight")

        canvas.drawRect(tlCorner,testPaint)
        canvas.drawRect(trCorner,testPaint)
        canvas.drawRect(blCorner,testPaint)
        canvas.drawRect(brCorner,testPaint)
        canvas.drawRect(topEdge,testPaint2)
        canvas.drawRect(rightEdge,testPaint2)
        canvas.drawRect(bottomEdge,testPaint2)
        canvas.drawRect(leftEdge,testPaint2)

        canvas.drawRect(selectionRect,selectionRectPaint)

//        exampleString?.let {
//            // Draw the text.
//            canvas.drawText(
//                it,
//                paddingLeft + (contentWidth - textWidth) / 2,
//                paddingTop + (contentHeight + textHeight) / 2,
//                textPaint
//            )
//        }

        // Draw the example drawable on top of the text.
//        exampleDrawable?.let {
//            it.setBounds(
//                paddingLeft, paddingTop,
//                paddingLeft + contentWidth, paddingTop + contentHeight
//            )
//            it.draw(canvas)
//        }
    }

    private val tlCorner:Rect
        get() {
            val left = (selectionRectX0 - selectionRectTouchOffset).toInt()
            val top = (selectionRectY0 - selectionRectTouchOffset).toInt()
            val right = (selectionRectX0 + selectionRectInternalTouchOffset).toInt()
            val bottom = (selectionRectY0 + selectionRectInternalTouchOffset).toInt()
            return Rect(left,top,right,bottom)
        }

    private val trCorner:Rect
    get() {
        val left = (selectionRectX1 - selectionRectInternalTouchOffset).toInt()
        val top = (selectionRectY0 - selectionRectTouchOffset).toInt()
        val right = (selectionRectX1 + selectionRectTouchOffset).toInt()
        val bottom = (selectionRectY0 + selectionRectInternalTouchOffset).toInt()
        return Rect(left,top,right,bottom)
    }

    private val blCorner:Rect
    get() {
        val left = (selectionRectX0 - selectionRectTouchOffset).toInt()
        val top = (selectionRectY1 - selectionRectInternalTouchOffset).toInt()
        val right = (selectionRectX0 + selectionRectInternalTouchOffset).toInt()
        val bottom = (selectionRectY1 + selectionRectTouchOffset).toInt()
        return Rect(left,top,right,bottom)
    }

    private val brCorner:Rect
    get() {
        val left = (selectionRectX1 - selectionRectInternalTouchOffset).toInt()
        val top = (selectionRectY1 - selectionRectInternalTouchOffset).toInt()
        val right = (selectionRectX1 + selectionRectTouchOffset).toInt()
        val bottom = (selectionRectY1 + selectionRectTouchOffset).toInt()
        return Rect(left,top,right,bottom)
    }

    private val topEdge:Rect
    get() {
        val left = (selectionRectX0 + selectionRectInternalTouchOffset).toInt()
        val top = (selectionRectY0 - selectionRectTouchOffset).toInt()
        val right = (selectionRectX1 - selectionRectInternalTouchOffset).toInt()
        val bottom = (selectionRectY0 + selectionRectInternalTouchOffset).toInt()
        return Rect(left,top,right,bottom)
    }

    private val rightEdge:Rect
    get() {
        val left = (selectionRectX1 - selectionRectInternalTouchOffset).toInt()
        val top = (selectionRectY0 + selectionRectInternalTouchOffset).toInt()
        val right = (selectionRectX1 + selectionRectTouchOffset).toInt()
        val bottom = (selectionRectY1 - selectionRectInternalTouchOffset).toInt()
        return Rect(left,top,right,bottom)
    }

    private val bottomEdge:Rect
    get() {
        val left = (selectionRectX0 + selectionRectInternalTouchOffset).toInt()
        val top = (selectionRectY1 - selectionRectInternalTouchOffset).toInt()
        val right = (selectionRectX1 - selectionRectInternalTouchOffset).toInt()
        val bottom = (selectionRectY1 + selectionRectTouchOffset).toInt()
        return Rect(left,top,right,bottom)
    }

    private val leftEdge:Rect
    get() {
        val left = (selectionRectX0 - selectionRectTouchOffset).toInt()
        val top = (selectionRectY0 + selectionRectInternalTouchOffset).toInt()
        val right = (selectionRectX0 + selectionRectInternalTouchOffset).toInt()
        val bottom = (selectionRectY1 - selectionRectInternalTouchOffset).toInt()
        return Rect(left,top,right,bottom)
    }

    enum class SelectionDragStart{T,R,B,L,TL,TR,BR,BL}

    private fun dragFrom(x:Int,y:Int):SelectionDragStart?{
        return when{
            topEdge.contains(x,y) -> SelectionDragStart.T
            rightEdge.contains(x,y) -> SelectionDragStart.R
            bottomEdge.contains(x,y) -> SelectionDragStart.B
            leftEdge.contains(x,y) -> SelectionDragStart.L
            tlCorner.contains(x,y) -> SelectionDragStart.TL
            trCorner.contains(x,y) -> SelectionDragStart.TR
            brCorner.contains(x,y) -> SelectionDragStart.BR
            blCorner.contains(x,y) -> SelectionDragStart.BL
            else -> null
        }
    }

    private var draggingFrom:SelectionDragStart? = null
    // The ‘active pointer’ is the one currently moving our object.
    private var activePointerId = -1

    private var lastTouchPoint:Point? = null
    private var deltaPoint = Point()

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        event?.let {_ ->
            activePointerId = event.getPointerId(0)
            when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    draggingFrom = dragFrom(event.x.toInt(),event.y.toInt())
                    lastTouchPoint = Point(event.x.toInt(),event.y.toInt())
                    Timber.d("Corner or Edge ? $draggingFrom starting at $lastTouchPoint")

                }
                MotionEvent.ACTION_MOVE -> {
                    // Find the index of the active pointer and fetch its position
                    draggingFrom?.let {start ->
                        val (x: Float, y: Float) = event.findPointerIndex(activePointerId).let { pointerIndex ->
                            // Calculate the distance moved
                            event.getX(pointerIndex) to event.getY(pointerIndex)
                        }
                        lastTouchPoint?.let {
                            deltaPoint.set(x.toInt()-it.x,y.toInt()-it.y)
                        }
                        shouldDrag(start,deltaPoint.x,deltaPoint.y)
                        invalidate()
                        lastTouchPoint?.set(x.toInt(),y.toInt())
                    }
                }
                MotionEvent.ACTION_UP -> {
                    draggingFrom = null
                    lastTouchPoint = null
                }
                else -> {
//                    Timber.d("Action ${event.action}")
                }
            }
        }

        return true
    }



    private fun shouldDrag(start:SelectionDragStart,x:Int,y:Int){
        val backup = Rect(selectionRect)
        Timber.d("Dragging $start $x $y")
        when(start){
            SelectionDragStart.T -> {
                selectionRectY0 +=y
            }
            SelectionDragStart.R -> {
                selectionRectX1 += x
            }
            SelectionDragStart.B -> {
                selectionRectY1 += y
            }
            SelectionDragStart.L -> {
                selectionRectX0 += x
            }
            SelectionDragStart.TL -> {
                selectionRectX0 += x
                selectionRectY0 += y
            }
            SelectionDragStart.TR -> {
                selectionRectX1 += x
                selectionRectY0 += y
            }
            SelectionDragStart.BR -> {
                selectionRectX1 += x
                selectionRectY1 += y
            }
            SelectionDragStart.BL -> {
                selectionRectX0 += x
                selectionRectY1 += y
            }
        }

        if (selectionRect.width() < selectionRectInternalTouchOffset*4){
            selectionRectX0  = backup.left
            selectionRectX1 = backup.right
        }

        if (selectionRect.height() < selectionRectInternalTouchOffset*4){
            selectionRectY0 = backup.top
            selectionRectY1 = backup.bottom
        }

        if (selectionRect.top < edges.top) {
            selectionRectY0 = backup.top
        }

        if (selectionRect.right >= edges.right) {
            selectionRectX1 = backup.right
        }

        if (selectionRect.bottom >= edges.bottom) {
            selectionRectY1 = backup.bottom
        }

        if (selectionRect.left < edges.left) {
            selectionRectX0 = backup.left
        }
    }

    private class SelectionSurface(private val view: View){

    }
}
