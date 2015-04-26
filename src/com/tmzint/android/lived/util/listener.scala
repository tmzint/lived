package com.tmzint.android.lived.util

import scala.collection.immutable.Vector

/**
 * Abstract base class for the different BaseListenerManagers implementations.
 * Handles the storing of given call by names and the combination of BaseListenerManagers.
 *
 * @param inverse Add the call by names in inverse order
 * @tparam T Type of the call by name
 */
sealed abstract class BaseListenerManager[T](val inverse: Boolean = false) {
    protected var ons: Vector[() => T] = Vector()

    /**
     *
     * @param on Add to listeners
     */
    def +(on: => T): Unit = inverse match {
        case true => ons = (() => on) +: ons
        case false => ons = ons :+ (() => on)
    }

    /**
     *  Addition of listeners of two BaseListenerManagers.
     *  Adds the listeners ahead of the local ones when the BaseListenerManager is inverse.
     *
     * @param mng Manager to be added to this one
     * @param inverseAdd Inverse the order of listener execution of the added manager
     */
    def ++(mng: BaseListenerManager[T], inverseAdd:Boolean = false): Unit = {
        def add(inverseAdd: Boolean): Vector[() => T] = inverseAdd match {
            case true => mng.ons.reverse
            case false => mng.ons
        }

        inverse match {
            case true => ons = add(inverseAdd) ++: ons
            case false => ons = ons ++ add(inverseAdd)
        }
    }
}

/**
 * Handles the storing and execution of functions.
 *
 * @param inverse Add the functions in inverse order
 * @tparam T Parameter type of the given functions
 * @tparam R Return type of the given functions
 */
class ListenerManager[T, R](inverse:Boolean = false) extends BaseListenerManager[T => R](inverse) {

    /**
     * Notify Listeners
     *
     * @param t Parameter to be given to listeners on notification
     * @param inverse Notify in inverse order of storage
     */
    def apply(t:T, inverse:Boolean = false): Unit = inverse match {
        case true => ons.reverse.foreach(on => on()(t))
        case false => ons.foreach(on => on()(t))
    }
}

/**
 * Handles the storing and execution of Unit blocks.
 *
 * @param inverse Add the block in inverse order
 */
class BasicListenerManager(inverse:Boolean = false) extends BaseListenerManager[Unit](inverse) {

    /**
     * Notify Listeners
     *
     * @param inverse Notify in inverse order of storage
     */
    def apply(inverse:Boolean = false): Unit = inverse match {
        case true => ons.reverse.foreach(on => on())
        case false => ons.foreach(on => on())
    }
}
