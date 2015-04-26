package com.tmzint.android.lived.lifecycle

import com.tmzint.android.lived.lifecycle.LifecycleManager.ManagerContainer

import scala.language.implicitConversions

/**
 * Base trait for all to be managed objects,
 * supplies the ManageContainer for use in the Managed class
 */
sealed trait Lived {
    private[this] var _mngs: Option[ManagerContainer] = None

    implicit protected[this] def mngs: ManagerContainer = _mngs match {
        case Some(m) => m
        case None => _mngs = Some(new ManagerContainer); _mngs.get
    }

    protected[lifecycle] def mngs_=(value: ManagerContainer): Unit = (_mngs, value) match {
        case (Some(a), b) => b ++ a; _mngs = Some(b)
        case _ => _mngs = Some(value)
    }
}

/**
 * Provides onAttach() interface
 */
trait Attachable extends Lived {
    def onAttach(): Unit
}

/**
 * Provides onCreate() interface
 */
trait Creatable extends Lived {
    def onCreate(): Unit
}

/**
 * Provides onCreateView() interface
 */
trait CreateViewable extends Lived {
    def onCreateView(): Unit
}

/**
 * Provides onActivityCreated() interface
 */
trait ActivityCreatedable extends Lived {
    def onActivityCreated(): Unit
}

/**
 * Provides onStart() interface
 */
trait Startable extends Lived {
    def onStart(): Unit
}

/**
 * Provides onResume() interface
 */
trait Resumable extends Lived {
    def onResume(): Unit
}

/**
 * Provides onPause() interface
 */
trait Pausable extends Lived {
    def onPause(): Unit
}

/**
 * Provides onStop() interface
 */
trait Stoppable extends Lived {
    def onStop(): Unit
}

/**
 * Provides onDestroyView() interface
 */
trait DestroyViewable extends Lived {
    def onDestroyView(): Unit
}

/**
 * Provides onDestroy() interface
 */
trait Destroyable extends Lived {
    def onDestroy(): Unit
}

/**
 * Provides onDetach() interface
 */
trait Detachable extends Lived {
    def onDetach(): Unit
}

/**
 * Provides an Activity like interface:
 * Creatable, Startable, Resumable, Pausable, Stoppable and Destroyable
 */
trait ActivityLike
    extends Creatable
    with Startable
    with Resumable
    with Pausable
    with Stoppable
    with Destroyable {

}

/**
 * Provides a Fragment like interface:
 * ActivityLike, CreateViewable, Attachable, ActivityCreatedable, Detachable and DestroyViewable
 */
trait FragmentLike
    extends ActivityLike
    with CreateViewable
    with Attachable
    with ActivityCreatedable
    with Detachable
    with DestroyViewable {

}

/**
 * Manage the lifecycle of an given object.
 * <br><br>
 * A Managed will only be lifecycle managed when the containing object is also Managed,
 * a break in this management chain will result in all Managed objects to silently fail,
 * as they wait for the chain to be completed.
 * <br><br>
 * The execution of the Lived sub traits will be according to the start of creation of the
 * Managed objects or the inverse for destructing lifecycle events like Pausable.
 *
 * @example
{{{
import android.app.Activity
import android.util.Log
import com.tmzint.android.scala.lifecycle._

class MainActivity extends Activity with ActivityLifecycleManager {
    val mA = new Managed(new T1("A"))
}

class T1(name: String) extends Startable with Pausable {
    override def onStart(): Unit = Log.d(name,"onStart")
    override def onPause(): Unit = Log.d(name,"onPause")

    class T2(name2: String) extends Startable with Pausable {
        override def onStart(): Unit = Log.d(name + name2,"onStart")
        override def onPause(): Unit = Log.d(name + name2,"onPause")
    }

    val a = new Managed(new T2("0"))
}
}}}
 * output onStart:<br>A onStart<br>A0 onStart<br><br>
 * output onPause:<br>A0 onPause<br>A onPause<br><br>
 *
 * @param t The object to be managed
 * @param mngs The parent manager of lifecycles
 * @tparam T The managed type
 */
class Managed[T <: Lived](t: T)(implicit mngs: LifecycleManager.ManagerContainer) {
    // TODO: ambiguous implicit values, on inner class stacking in a LifecycleManager; 2+ or more
    // TODO: handle manager not found for Lived sub type, currently just ignore.
    t match {case m: Attachable => mngs.attachMng + m; case _ =>}
    t match {case m: Creatable => mngs.createMng + m; case _ =>}
    t match {case m: CreateViewable => mngs.createViewMng + m; case _ =>}
    t match {case m: ActivityCreatedable => mngs.activityCreatedMng + m; case _ =>}
    t match {case m: Startable => mngs.startMng + m; case _ =>}
    t match {case m: Resumable => mngs.resumeMng + m; case _ =>}
    t match {case m: Pausable => mngs.pauseMng + m; case _ =>}
    t match {case m: Stoppable => mngs.stopMng + m; case _ =>}
    t match {case m: DestroyViewable => mngs.destroyViewMng + m; case _ =>}
    t match {case m: Destroyable => mngs.destroyMng + m; case _ =>}
    t match {case m: Detachable => mngs.detachMng + m; case _ =>}

    t.mngs_=(mngs)

    /**
     * @return The managed object
     */
    def apply(): T = t
}

/**
 * Companion object for the Managed class.
 */
object Managed {

    /**
     * Manage the lifecycle of an given object
     *
     * @param t The object to be managed
     * @param mngs The parent manager of lifecycles
     * @tparam T The managed type
     * @return
     */
    def apply[T <: Lived](t: T)(implicit mngs: LifecycleManager.ManagerContainer): Managed[T] =
        new Managed[T](t)

    /**
     * Implicit conversion that unwraps the managed object.
     *
     * @param m The Managed to be implicitly conversed to the object it manages
     * @tparam T The type of the managed object
     * @return The managed object
     */
    implicit def unwrap[T <: Lived](m: Managed[T]): T = m()
}

/**
 * Unwrapper trait for convenience
 *
 * @tparam T The managed type
 */
trait ManagedUnwrapper[T <: Lived] {

    /**
     * Implicit conversion that unwraps the managed object.
     *
     * @param m The Managed to be implicitly conversed to the object it manages
     * @return The managed object
     */
    implicit def unwrap(m: Managed[T]): T = m()
}
