package com.parcelsanta.base.ktx.view

/**
 * @author : yan
 * @date   : 2019/11/18 10:22
 * @desc   : IClickListener
 */
interface ItemClickListener<T> {

    var itemClickListener: ((position: Int, bean: T) -> Unit)?
    var itemLongClickListener: ((bean: T) -> Boolean)?

}

class ItemClickListenerImpl<T> : ItemClickListener<T> {

    override var itemClickListener: ((position: Int, bean: T) -> Unit)? = null
    override var itemLongClickListener: ((bean: T) -> Boolean)? = null

}