package it.wazabit.dev.extensions.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import kotlin.math.max
import kotlin.math.min


class EditImageView : View {

    private var _exampleString: String? = null // TODO: use a default from R.string...
    private var _exampleColor: Int = Color.RED // TODO: use a default from R.color...
    private var _exampleDimension: Float = 0f // TODO: use a default from R.dimen...



    var bitmap:Bitmap? = null
    set(value) {
        field = value
        //TODO cleanup ratio etc
        cleanupBitmap()
//        editedBitmap = value
        setupBitmap()
        invalidate()
    }
//    get() { return editedBitmap}
//    private var editedBitmap = bitmap

    private var selectionRectPadding = 0
    private val selectionRectStrokeWith:Float = 10f
    private val selectionRectTouchOffset:Float = 20f
    private val selectionRectInternalTouchOffset:Float = selectionRectTouchOffset*3.5f

    private val selectionSurface:SelectionSurface
    private val imageSurface:ImageSurface

    private val selectionRectPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    private val testPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }

    private val testPaint2 = Paint().apply {
        color = Color.YELLOW
        style = Paint.Style.FILL
    }

    private var panX = 0
    private var panY= 0
    private var ratio = 0f
    private var bitmapWidth = 0
    private var bitmapHeight = 0

    private val destinationRect: Rect?
        get() {
            return bitmap?.let {
                //assuming that is the first time that the bitmaprect is required
                Rect(0,0,it.width,it.height)
            }

        }

    private val editedBitmapRect:Rect
    get() {
        return  Rect(panX,panY,bitmapWidth+panX,bitmapHeight+panY)
    }

    private fun cleanupBitmap(){
        ratio = 0f
        panX = 0
        panY = 0
        bitmapWidth = 0
        bitmapHeight = 0
    }

    private fun setupBitmap(){
        bitmap?.let {
            if (ratio == 0f){
                //cal ratio
                ratio = when{
                    it.width > width -> width.toFloat()/it.width.toFloat()
                    it.height > height -> height.toFloat()/it.height.toFloat()
                    else -> 1f
                }

                bitmapWidth = (it.width*ratio).toInt()
                bitmapHeight = (it.height*ratio).toInt()
                //center pan
//                panX = (width-bitmapWidth)/2
//                panY = (height-bitmapHeight)/2
            }
        }

    }

    fun cut() : Bitmap{

        val (width:Int,height:Int) = selectionSurface.selectionRect.let {
            (selectionSurface.scaledWidth*bitmap!!.width).toInt() to (selectionSurface.scaledHeight*bitmap!!.height).toInt()
        }
        val (x:Int,y:Int) = selectionSurface.selectionRect.let {
            (selectionSurface.scaledLeft*bitmap!!.width).toInt() to (selectionSurface.scaledTop*bitmap!!.height).toInt()
        }

        return  Bitmap.createBitmap(
            bitmap!!,
            x,
            y,
            width,
            height
        )
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

    init {
        selectionSurface = SelectionSurface(
            this,
            selectionRectPadding,
            selectionRectStrokeWith,
            selectionRectTouchOffset,
            selectionRectInternalTouchOffset
        )
        imageSurface = ImageSurface(this)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {

        setBackgroundColor(Color.BLACK)

        invalidateTextPaintAndMeasurements()

        selectionSurface.setOnInvalidateListener{
            invalidate()
        }
        imageSurface.setOnInvalidateListener{
            invalidate()
        }

        selectionSurface.onSizeChanged()
    }

    private fun invalidateTextPaintAndMeasurements() {
        selectionSurface.onSizeChanged()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        invalidateTextPaintAndMeasurements()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        bitmap?.let {
//            canvas.drawRect(destinationRect!!,testPaint)
//            canvas.drawRect(editedBitmapRect,testPaint2)
            canvas.drawBitmap(it,destinationRect,editedBitmapRect,null)
        }
//        canvas.drawRect(selectionSurface.tlCorner,testPaint)
//        canvas.drawRect(selectionSurface.trCorner,testPaint)
//        canvas.drawRect(selectionSurface.blCorner,testPaint)
//        canvas.drawRect(selectionSurface.brCorner,testPaint)
//        canvas.drawRect(selectionSurface.topEdge,testPaint2)
//        canvas.drawRect(selectionSurface.rightEdge,testPaint2)
//        canvas.drawRect(selectionSurface.bottomEdge,testPaint2)


        canvas.drawRect(selectionSurface.selectionRect,selectionRectPaint)



    }



    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        event?.let {
            val changingSelectionSurface = selectionSurface.onTouchEvent(it)
//            if (!changingSelectionSurface) imageSurface.onTouchEvent(it)
        }

        return true
    }





    private class SelectionSurface(private val view: View, private val selectionRectPadding:Int, private val selectionRectStrokeWith:Float, private val selectionRectTouchOffset:Float, private val selectionRectInternalTouchOffset:Float){

        private var invalidateListener: (() -> Unit)? = null
        fun setOnInvalidateListener(l: () -> Unit){ this.invalidateListener = l }

        private var selectionRectX0 = 0
        private var selectionRectY0 = 0
        private var selectionRectX1 = 0
        private var selectionRectY1 = 0

        val tlCorner:Rect
            get() {
                val left = (selectionRectX0 - selectionRectTouchOffset).toInt()
                val top = (selectionRectY0 - selectionRectTouchOffset).toInt()
                val right = (selectionRectX0 + selectionRectInternalTouchOffset).toInt()
                val bottom = (selectionRectY0 + selectionRectInternalTouchOffset).toInt()
                return Rect(left,top,right,bottom)
            }

        val trCorner:Rect
            get() {
                val left = (selectionRectX1 - selectionRectInternalTouchOffset).toInt()
                val top = (selectionRectY0 - selectionRectTouchOffset).toInt()
                val right = (selectionRectX1 + selectionRectTouchOffset).toInt()
                val bottom = (selectionRectY0 + selectionRectInternalTouchOffset).toInt()
                return Rect(left,top,right,bottom)
            }

        val blCorner:Rect
            get() {
                val left = (selectionRectX0 - selectionRectTouchOffset).toInt()
                val top = (selectionRectY1 - selectionRectInternalTouchOffset).toInt()
                val right = (selectionRectX0 + selectionRectInternalTouchOffset).toInt()
                val bottom = (selectionRectY1 + selectionRectTouchOffset).toInt()
                return Rect(left,top,right,bottom)
            }

        val brCorner:Rect
            get() {
                val left = (selectionRectX1 - selectionRectInternalTouchOffset).toInt()
                val top = (selectionRectY1 - selectionRectInternalTouchOffset).toInt()
                val right = (selectionRectX1 + selectionRectTouchOffset).toInt()
                val bottom = (selectionRectY1 + selectionRectTouchOffset).toInt()
                return Rect(left,top,right,bottom)
            }

        val topEdge:Rect
            get() {
                val left = (selectionRectX0 + selectionRectInternalTouchOffset).toInt()
                val top = (selectionRectY0 - selectionRectTouchOffset).toInt()
                val right = (selectionRectX1 - selectionRectInternalTouchOffset).toInt()
                val bottom = (selectionRectY0 + selectionRectInternalTouchOffset).toInt()
                return Rect(left,top,right,bottom)
            }

        val rightEdge:Rect
            get() {
                val left = (selectionRectX1 - selectionRectInternalTouchOffset).toInt()
                val top = (selectionRectY0 + selectionRectInternalTouchOffset).toInt()
                val right = (selectionRectX1 + selectionRectTouchOffset).toInt()
                val bottom = (selectionRectY1 - selectionRectInternalTouchOffset).toInt()
                return Rect(left,top,right,bottom)
            }

        val bottomEdge:Rect
            get() {
                val left = (selectionRectX0 + selectionRectInternalTouchOffset).toInt()
                val top = (selectionRectY1 - selectionRectInternalTouchOffset).toInt()
                val right = (selectionRectX1 - selectionRectInternalTouchOffset).toInt()
                val bottom = (selectionRectY1 + selectionRectTouchOffset).toInt()
                return Rect(left,top,right,bottom)
            }

        val leftEdge:Rect
            get() {
                val left = (selectionRectX0 - selectionRectTouchOffset).toInt()
                val top = (selectionRectY0 + selectionRectInternalTouchOffset).toInt()
                val right = (selectionRectX0 + selectionRectInternalTouchOffset).toInt()
                val bottom = (selectionRectY1 - selectionRectInternalTouchOffset).toInt()
                return Rect(left,top,right,bottom)
            }

        enum class SelectionDragStart{T,R,B,L,TL,TR,BR,BL}

        val scaledTop:Float
        get() {
            return  (selectionRectY0).toFloat()/view.height
        }
        val scaledLeft:Float
        get() {
            return  (selectionRectX0).toFloat()/view.width
        }

        val scaledWidth:Float
        get() {
            return  (selectionRectX1 - selectionRectX0).toFloat()/view.width
        }

        val scaledHeight:Float
        get() {
            return  (selectionRectY1 - selectionRectY0).toFloat()/view.height
        }

        //TODO deve matchare con l'immagine
        val selectionRect: Rect
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
                    right = view.width.toFloat() - selectionRectStrokeWith/2
                    bottom = view.height.toFloat() - selectionRectStrokeWith/2
                    left = selectionRectStrokeWith/2
                }
            }

        private var draggingFrom:SelectionDragStart? = null
        // The ‘active pointer’ is the one currently moving our object.
        private var activePointerId = -1
        private var lastTouchPoint:Point? = null
        private var deltaPoint = Point()


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

        private fun shouldDrag(start:SelectionDragStart,x:Int,y:Int){
            val backup = Rect(selectionRect)

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
            }else{
                //TODO maximize
            }

            if (selectionRect.left < edges.left) {
                selectionRectX0 = backup.left
            }
        }

        fun onTouchEvent(event: MotionEvent):Boolean{
            activePointerId = event.getPointerId(0)
            return when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    draggingFrom = dragFrom(event.x.toInt(),event.y.toInt())
                    lastTouchPoint = Point(event.x.toInt(),event.y.toInt())
                    false
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
                        invalidateListener?.let { it() }
                        lastTouchPoint?.set(x.toInt(),y.toInt())
                        true
                    } ?: false
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    activePointerId = -1
                    draggingFrom = null
                    lastTouchPoint = null
                    false
                }
                else -> false
            }
        }

        fun onSizeChanged(){
            selectionRectX0 = selectionRectPadding
            selectionRectY0 = selectionRectPadding
            selectionRectX1 = view.width -  selectionRectPadding
            selectionRectY1 = view.height - selectionRectPadding
        }
    }


    private inner class ImageSurface(private val view: View){


        private var hasRecentlyScaled = false
        private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

            override fun onScale(detector: ScaleGestureDetector): Boolean {
                ratio *= detector.scaleFactor
                // Don't let the object get too small or too large.
                ratio = max(0.1f, min(ratio, 5.0f))

                val oldW = bitmapWidth
                val oldH = bitmapHeight

                bitmap?.let {
                    bitmapWidth = (it.width*ratio).toInt()
                    bitmapHeight = (it.height*ratio).toInt()
                }

                //TODO pan or zoom whe scaling ?
                val offsetX = bitmapWidth-oldW
                val offsetY = bitmapHeight-oldH

                pan(-offsetX,-offsetY)

                invalidateListener?.let { it() }

                return true
            }
        }

        private val mScaleDetector = ScaleGestureDetector(context, scaleListener)


        private var invalidateListener: (() -> Unit)? = null
        fun setOnInvalidateListener(l: () -> Unit){ this.invalidateListener = l }

        private var activePointerId = -1
        private var lastTouchPoint:Point? = null
        private var deltaPoint = Point()


        fun onTouchEvent(event: MotionEvent){

            when{
                event.action == MotionEvent.ACTION_UP-> {
                    hasRecentlyScaled = false
                }
                event.pointerCount > 1 ->{
                    hasRecentlyScaled = true
                    mScaleDetector.onTouchEvent(event)
                }
                !hasRecentlyScaled -> {
                    shouldPan(event)
                }

            }

        }

        private fun shouldPan(event: MotionEvent){

            when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    activePointerId = event.getPointerId(0)
                    lastTouchPoint = Point(event.x.toInt(),event.y.toInt())
                }
                MotionEvent.ACTION_MOVE -> {
                    // Find the index of the active pointer and fetch its position

                    val (x: Float, y: Float) = event.findPointerIndex(activePointerId).let { pointerIndex ->
                        // Calculate the distance moved
                        try {
                            event.getX(pointerIndex) to event.getY(pointerIndex)
                        }catch (exception:Exception){
                            lastTouchPoint!!.x.toFloat() to lastTouchPoint!!.y.toFloat()
                        }
                    }
                    lastTouchPoint?.let {
                        deltaPoint.set(x.toInt()-it.x,y.toInt()-it.y)
                    }
                    pan(deltaPoint.x,deltaPoint.y)
                    invalidateListener?.let { it() }
                    lastTouchPoint?.set(x.toInt(),y.toInt())
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    activePointerId = -1
                    lastTouchPoint = null
                }
                else -> {}
            }
        }


        private fun pan(x:Int,y: Int){
            panX += x
            panY += y
        }

    }

}
