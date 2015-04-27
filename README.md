lived
======
lived is a Scala library that aims to manage the android lifecycle of non Activity and Fragment objects.

Usage:
------
The Activity or Fragment containing Managed objects needs to respectively extend the
ActivityLifecycleManager or FragmentLifecycleManager traits, to delegate lifecycle events.

A Managed will only be lifecycle managed when the containing object is also Managed,
a break in this management chain will result in all Managed objects to silently fail,
as they wait for the chain to be completed.

The execution of the Lived sub traits will be according to the start of creation of the Managed
objects or the inverse for destructing lifecycle events like Pausable.

### ActivityLike traits
ActivityLike, Creatable, Startable, Resumable, Pausable, Stoppable and Destroyable are traits that
can get Managed in an object contained in an Activity that extends the ActivityLifecycleManager trait.

### FragmentLike traits
ActivityLike, Creatable, CreateViewable, Attachable, Startable, Resumable, ActivityCreatedable,
Detachable, DestroyViewable, Pausable, Stoppable and Destroyable are traits that can get Managed in
an object contained in an Fragment that extends the FragmentLifecycleManager trait.

Example:
------

### Code

    import android.app.Activity
    import android.util.Log
    import com.tmzint.android.lived.lifecycle._
    
    // The MainActivity class extends the ActivityLifecycleManager to delegate lifecycle events.
    class MainActivity extends Activity with ActivityLifecycleManager {
        val mA = new Managed(new T1("A")) // mA is a lifecycle managed object
        mA.ping() // implicit conversion to T1
    }
    
    class T1(name: String) extends Startable with Pausable {
        override def onStart(): Unit = Log.d(name,"onStart")
        override def onPause(): Unit = Log.d(name,"onPause")
        def ping(): Unit = Log.d(name, "ping!")
    
        class T2(name2: String) extends Startable with Pausable {
            override def onStart(): Unit = Log.d(name + name2,"onStart")
            override def onPause(): Unit = Log.d(name + name2,"onPause")
        }
        
        object T2 {
            def apply(name: String): Managed[T2] = new Managed(new T2(name))
        }
    
        val a = T2("0")
    }

### Output

Constructor phase:

1.  A ping!

oStart phase:

1.  A onStart
2.  A0 onStart

onPause phase:

1.  A0 onPause
2.  A onPause

License:
------
This project is licensed under the terms of the GNU Lesser General Public License Version 3,
see LICENSE.txt.
