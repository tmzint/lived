package com.tmzint.android.lived.lifecycle

import android.app.{Fragment, Activity}
import android.os.Bundle
import android.view.{ViewGroup, LayoutInflater, View}
import com.tmzint.android.lived.lifecycle.LifecycleManager.ManagerContainer
import com.tmzint.android.lived.util.BasicListenerManager

/**
 * Base for all lifecycle Managers, supplies a ListenerManager.
 *
 * @param inverse Inverse the order of addition of the protected ListenerManager
 */
abstract class LivedManager(val inverse: Boolean = false) {
    val listenerManager: BasicListenerManager = new BasicListenerManager(inverse)
}

/**
 * Manager for the Attachable trait.
 *
 * @param inv Inverse the order of addition
 */
class AttachableManager(inv: Boolean = false) extends LivedManager(inv) {
    def +(attachable: Attachable): Unit = listenerManager + {attachable.onAttach()}
    def onAttach(): Unit = listenerManager()
}

/**
 * Manager for the Creatable trait.
 *
 * @param inv Inverse the order of addition
 */
class CreatableManager(inv: Boolean = false) extends LivedManager(inv) {
    def +(creatable: Creatable): Unit = listenerManager + {creatable.onCreate()}
    def onCreate(): Unit = listenerManager()
}

/**
 * Manager for the CreateViewable trait.
 *
 * @param inv Inverse the order of addition
 */
class CreateViewableManager(inv: Boolean = false) extends LivedManager(inv) {
    def +(createVable: CreateViewable): Unit = listenerManager + {createVable.onCreateView()}
    def onCreateView(): Unit = listenerManager()
}

/**
 * Manager for the ActivityCreatedable trait.
 *
 * @param inv Inverse the order of addition
 */
class ActivityCreatedableManager(inv: Boolean = false) extends LivedManager(inv) {
    def +(actCtbl: ActivityCreatedable): Unit = listenerManager + {actCtbl.onActivityCreated()}
    def onActivityCreated(): Unit = listenerManager()
}

/**
 * Manager for the Startable trait.
 *
 * @param inv Inverse the order of addition
 */
class StartableManager(inv: Boolean = false) extends LivedManager(inv) {
    def +(startable: Startable): Unit = listenerManager + {startable.onStart()}
    def onStart(): Unit = listenerManager()
}

/**
 * Manager for the Resumable trait.
 *
 * @param inv Inverse the order of addition
 */
class ResumableManager(inv: Boolean = false) extends LivedManager(inv) {
    def +(Resumable: Resumable): Unit = listenerManager + {Resumable.onResume()}
    def onResume(): Unit = listenerManager()
}

/**
 * Manager for the Pausable trait.
 *
 * @param inv Inverse the order of addition
 */
class PausableManager(inv: Boolean = false) extends LivedManager(!inv) {
    def +(pausable: Pausable): Unit = listenerManager + {pausable.onPause()}
    def onPause(): Unit = listenerManager()
}

/**
 * Manager for the Stoppable trait.
 *
 * @param inv Inverse the order of addition
 */
class StoppableManager(inv: Boolean = false) extends LivedManager(!inv) {
    def +(stoppable: Stoppable): Unit = listenerManager + {stoppable.onStop()}
    def onStop(): Unit = listenerManager()
}

/**
 * Manager for the DestroyViewable trait.
 *
 * @param inv Inverse the order of addition
 */
class DestroyViewableManager(inv: Boolean = false) extends LivedManager(!inv) {
    def +(destroyVble: DestroyViewable): Unit = listenerManager + {destroyVble.onDestroyView()}
    def onDestroyView(): Unit = listenerManager()
}

/**
 * Manager for the Destroyable trait.
 *
 * @param inv Inverse the order of addition
 */
class DestroyableManager(inv: Boolean = false) extends LivedManager(!inv) {
    def +(destroyable: Destroyable): Unit = listenerManager + {destroyable.onDestroy()}
    def onDestroy(): Unit = listenerManager()
}

/**
 * Manager for the Detachable trait.
 *
 * @param inv Inverse the order of addition
 */
class DetachableManager(inv: Boolean = false) extends LivedManager(!inv) {
    def +(detachable: Detachable): Unit = listenerManager + {detachable.onDetach()}
    def onDetach(): Unit = listenerManager()
}

/**
 * Base for all LifecycleManagers, provides a default ManagerContainer.
 */
trait LifecycleManager {
    private[this] val _lifecycleManagers: LifecycleManager.ManagerContainer = new ManagerContainer

    implicit protected[this] def lifecycleManagers: ManagerContainer = _lifecycleManagers
}

/**
 * Companion object of the LifecycleManger class, contains the ManagerContainer class definition.
 */
object LifecycleManager {

    /**
     * Container for various LivedManager implementations, with their default implementation.
     */
    class ManagerContainer {
        var createMng: CreatableManager = new CreatableManager
        var createViewMng: CreateViewableManager = new CreateViewableManager
        var startMng: StartableManager = new StartableManager
        var resumeMng: ResumableManager = new ResumableManager
        var pauseMng: PausableManager = new PausableManager
        var stopMng: StoppableManager = new StoppableManager
        var destroyMng: DestroyableManager = new DestroyableManager
        var attachMng: AttachableManager = new AttachableManager
        var activityCreatedMng: ActivityCreatedableManager = new ActivityCreatedableManager
        var destroyViewMng: DestroyViewableManager = new DestroyViewableManager
        var detachMng: DetachableManager = new DetachableManager

        /**
         * Imports the managed object of an ManagerContainer.
         *
         * @param mng Other to be added ManagerContainer
         */
        def ++(mng: ManagerContainer): Unit = {
            createMng.listenerManager ++ mng.createMng.listenerManager
            createViewMng.listenerManager ++ mng.createViewMng.listenerManager
            startMng.listenerManager ++ mng.startMng.listenerManager
            resumeMng.listenerManager ++ mng.resumeMng.listenerManager
            pauseMng.listenerManager ++ mng.pauseMng.listenerManager
            stopMng.listenerManager ++ mng.stopMng.listenerManager
            destroyMng.listenerManager ++ mng.destroyMng.listenerManager
            attachMng.listenerManager ++ mng.attachMng.listenerManager
            activityCreatedMng.listenerManager ++ mng.activityCreatedMng.listenerManager
            destroyViewMng.listenerManager ++ mng.destroyViewMng.listenerManager
            detachMng.listenerManager ++ mng.detachMng.listenerManager
        }
    }
}

trait ActivityLikeLifecycleManager extends LifecycleManager {
}

/**
 * Default Activity LifecycleManager trait, extend to enable the use of Managed objects in its scope.
 */
trait ActivityLifecycleManager extends Activity with ActivityLikeLifecycleManager {

    override def onCreate(savedInstanceState: Bundle): Unit = {
        super.onCreate(savedInstanceState)
        lifecycleManagers.createMng.onCreate()
    }

    override def onStart(): Unit = {
        super.onStart()
        lifecycleManagers.startMng.onStart()
    }

    override def onResume(): Unit = {
        super.onResume()
        lifecycleManagers.resumeMng.onResume()
    }

    override def onPause(): Unit = {
        lifecycleManagers.pauseMng.onPause()
        super.onPause()
    }

    override def onStop(): Unit = {
        lifecycleManagers.stopMng.onStop()
        super.onStop()
    }

    override def onDestroy(): Unit = {
        lifecycleManagers.destroyMng.onDestroy()
        super.onDestroy()
    }
}

trait FragmentLikeLifecycleManager extends ActivityLikeLifecycleManager {
}

/**
 * Default Fragment LifecycleManager trait, extend to enable the use of Managed objects in its scope.
 */
trait FragmentLifecycleManager extends Fragment with FragmentLikeLifecycleManager {

    override def onCreate(savedInstanceState: Bundle): Unit = {
        super.onCreate(savedInstanceState)
        lifecycleManagers.createMng.onCreate()
    }

    override def onCreateView(inf: LayoutInflater, vg: ViewGroup, saved: Bundle): View = {
        val v = super.onCreateView(inf, vg, saved)
        lifecycleManagers.createViewMng.onCreateView()
        v
    }

    override def onStart(): Unit = {
        super.onStart()
        lifecycleManagers.startMng.onStart()
    }

    override def onResume(): Unit = {
        super.onResume()
        lifecycleManagers.resumeMng.onResume()
    }

    override def onPause(): Unit = {
        lifecycleManagers.pauseMng.onPause()
        super.onPause()
    }

    override def onStop(): Unit = {
        lifecycleManagers.stopMng.onStop()
        super.onStop()
    }

    override def onDestroy(): Unit = {
        lifecycleManagers.destroyMng.onDestroy()
        super.onDestroy()
    }

    override def onAttach(activity: Activity): Unit = {
        super.onAttach(activity)
        lifecycleManagers.attachMng.onAttach()
    }

    override def onActivityCreated(savedInstanceState: Bundle): Unit = {
        super.onActivityCreated(savedInstanceState)
        lifecycleManagers.activityCreatedMng.onActivityCreated()
    }

    override def onDetach(): Unit = {
        lifecycleManagers.detachMng.onDetach()
        super.onDetach()
    }

    override def onDestroyView(): Unit = {
        lifecycleManagers.destroyViewMng.onDestroyView()
        super.onDestroyView()
    }
}
