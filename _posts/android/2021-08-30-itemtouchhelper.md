---
title: "RecyclerView.ItemTouchHelper 정리"
categories:
    - Android
---

RecyclerView에 설정하여 왼쪽으로 스와이프 기능을 구현하게 한 ItemTouchHelper이다.

{% highlight kotlin %}
class SwipeHelperCallback: ItemTouchHelper.Callback() {

    private var currentPosition: Int? = null
    private var previousPosition: Int? = null
    private var currentDx = 0f
    private var clamp = 0f

    //  허용할 drag와 swipe의 방향을 설정한다.
    //  안쓸 꺼면 ACTION_STATE_IDLE
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return if (viewHolder is ChatRoomAdapter.OthersTextChatHolder) {
            makeMovementFlags(ACTION_STATE_IDLE, LEFT)
        } else {
            makeMovementFlags(ACTION_STATE_IDLE, ACTION_STATE_IDLE)
        }
    }

    //  Drag and Drop액션 발생시 호출된다.
    //  item의 포지션을 바꾸려면 여기서
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
//        val oldPosition = viewHolder.absoluteAdapterPosition
//        val newPosition = target.absoluteAdapterPosition
//        recyclerView.adapter?.notifyItemMoved(oldPosition, newPosition)

        return false
    }

    //  Drop이 가능한 position을 지정할 때 호출한다.
    override fun canDropOver(
        recyclerView: RecyclerView,
        current: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return super.canDropOver(recyclerView, current, target)
    }

    //  Swipe 액션 발생시 호출된다.
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        Log.d("dhlog", "SwipeHelperCallback onSwiped()")
    }

    //  User의 액션에 의한 어떤 상호작용과 그 애니메이션이 모두 종료되었을 때 호출된다.
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        currentDx = 0f
        previousPosition = viewHolder.adapterPosition
        getDefaultUIUtil().clearView(getView(viewHolder))
    }

    //  선택된 ViewHolder가 바뀔 때, 또는 현재 선택된 ViewHolder의 액션 상태가 바뀔 때 호출된다.
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        Log.d("dhlog", "SwipeHelperCallback onSelectedChanged() : $actionState")
        viewHolder?.let {
            currentPosition = viewHolder.adapterPosition
            getDefaultUIUtil().onSelected(getView(it))
        }
    }

    //  RecyclerView의 가로 또는 세로 길이를 기준값으로 하여
    //  User가 얼마나 ViewHolder를 움직여야 Swipe로 인정될 것인가를 정의함
    //  여기서 Swipe로 인정된다는 말은 escape액션이 일어난다는 의미임.
    //  1을 초과한 값은 escape액션을 없애겠다는 것과 같은 의미이다.
    //  onSelectedChanged()의 action state가 1에서 0이 될 때, 즉 무빙액션이 종료될 때 호출된다.
    // 현재 View가 고정되어있지 않고 사용자가 -clamp 이상 swipe시 isClamped true로 변경 아닐시 false로 변경
    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        Log.d("dhlog", "SwipeHelperCallback getSwipeThreshold()")
        val isClamped = getTag(viewHolder)
        setTag(viewHolder, !isClamped && currentDx <= -clamp)
        return 2f
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        if (actionState == ACTION_STATE_SWIPE) {
            val view = getView(viewHolder)
            val isClamped = getTag(viewHolder)
            val x =  clampViewPositionHorizontal(view, dX, isClamped, isCurrentlyActive)

            currentDx = x
            getDefaultUIUtil().onDraw(
                c,
                recyclerView,
                view,
                x,
                dY,
                actionState,
                isCurrentlyActive
            )
        }
    }

    private fun clampViewPositionHorizontal(
        view: View,
        dX: Float,
        isClamped: Boolean,
        isCurrentlyActive: Boolean
    ) : Float {
        // View의 가로 길이의 절반까지만 swipe 되도록
        val min: Float = -view.width.toFloat()/2
        // RIGHT 방향으로 swipe 막기
        val max: Float = 0f

        val x = if (isClamped) {
            // View가 고정되었을 때 swipe되는 영역 제한
            if (isCurrentlyActive) dX - clamp else -clamp
        } else {
            dX
        }

        return kotlin.math.min(kotlin.math.max(min, x), max)
    }

    //  Swipe액션으로 인정받을 최저속도를 정의한다.
    //  값이 클수록 손가락으로 빠르게 튕겨야 Swipe로 인정된다.
    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return defaultValue * 10
    }

    private fun setTag(viewHolder: RecyclerView.ViewHolder, isClamped: Boolean) {
        // isClamped를 view의 tag로 관리
        viewHolder.itemView.tag = isClamped
    }

    private fun getTag(viewHolder: RecyclerView.ViewHolder) : Boolean {
        // isClamped를 view의 tag로 관리
        return viewHolder.itemView.tag as? Boolean ?: false
    }

    fun setClamp(clamp: Float) {
        this.clamp = clamp
    }

    // 다른 View가 swipe 되거나 터치되면 고정 해제
    fun removePreviousClamp(recyclerView: RecyclerView) {
        if (currentPosition == previousPosition)
            return
        previousPosition?.let {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(it) ?: return
            getView(viewHolder).translationX = 0f
            setTag(viewHolder, false)
            previousPosition = null
        }
    }

    private fun getView(viewHolder: RecyclerView.ViewHolder): View {
        return if (viewHolder is ChatRoomAdapter.OthersTextChatHolder) {
            viewHolder.itemView.findViewById(R.id.container)
        } else {
            viewHolder.itemView
        }
    }
}
{% endhighlight %}