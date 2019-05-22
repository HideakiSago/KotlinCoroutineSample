package jp.hideakisago.kotlincoroutinesample

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_scrolling.*
import kotlinx.android.synthetic.main.content_scrolling.*
import kotlinx.coroutines.*

class ScrollingActivity : AppCompatActivity() {

    private val sleepTime: Long = 6 * 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        launch.setOnClickListener { launchTest() }
        asyncAwait.setOnClickListener { asyncAwaitTest() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * [launch] の動作確認です。
     * また、 [delay] と [Thread.sleep] の違いも検証しています。
     *
     * 以下のような結果になります。
     * ```
     * 2019-05-22 22:20:29.724 16750-16750 D/ScrollingActivity: start launchTest	Thread: main
     * 2019-05-22 22:20:29.834 16750-16750 D/ScrollingActivity: end   launchTest	Thread: main
     * 2019-05-22 22:20:29.853 16750-16822 D/ScrollingActivity: start launch1	Thread: DefaultDispatcher-worker-1
     * 2019-05-22 22:20:29.856 16750-16823 D/ScrollingActivity: start launch2	Thread: DefaultDispatcher-worker-2
     * 2019-05-22 22:20:29.891 16750-16823 D/ScrollingActivity: start launch3	Thread: DefaultDispatcher-worker-2
     * 2019-05-22 22:20:35.854 16750-16822 D/ScrollingActivity: end   launch1	Thread: DefaultDispatcher-worker-1
     * 2019-05-22 22:20:35.855 16750-16822 D/ScrollingActivity: start launch4	Thread: DefaultDispatcher-worker-1
     * 2019-05-22 22:20:35.878 16750-16822 D/ScrollingActivity: end   launch2	Thread: DefaultDispatcher-worker-1
     * 2019-05-22 22:20:35.892 16750-16823 D/ScrollingActivity: end   launch3	Thread: DefaultDispatcher-worker-2
     * 2019-05-22 22:20:41.858 16750-16823 D/ScrollingActivity: end   launch4	Thread: DefaultDispatcher-worker-2
     * ```
     */
    private fun launchTest() {
        log("start launchTest")

        GlobalScope.launch {
            log("start launch1")
            // Thread.sleep だと thread 自体を停止してしまうが、
            Thread.sleep(sleepTime)
            log("end   launch1")
        }

        GlobalScope.launch {
            log("start launch2")
            // delay なら Thread を block するのではなく coroutine を中断するだけなので、
            // 他の coroutine で Thread が使い回される。
            delay(sleepTime)
            log("end   launch2")
        }

        GlobalScope.launch {
            log("start launch3")
            Thread.sleep(sleepTime)
            log("end   launch3")
        }

        GlobalScope.launch {
            log("start launch4")
            delay(sleepTime)
            log("end   launch4")
        }

        log("end   launchTest")
    }

    /**
     * [async], [Deferred.await] の動作確認です。
     *
     * 以下のような結果になります。
     * ```
     * 2019-05-22 22:21:14.503 16750-16750 D/ScrollingActivity: start asyncAwaitTest	Thread: main
     * 2019-05-22 22:21:14.505 16750-16823 D/ScrollingActivity: start asyncTask	Thread: DefaultDispatcher-worker-2
     * 2019-05-22 22:21:20.517 16750-16822 D/ScrollingActivity: end   asyncTask	Thread: DefaultDispatcher-worker-1
     * 2019-05-22 22:21:20.520 16750-16750 D/ScrollingActivity: after asyncTask before GlobalScope.async	Thread: main
     * 2019-05-22 22:21:20.521 16750-16822 D/ScrollingActivity: start GlobalScope.async	Thread: DefaultDispatcher-worker-1
     * 2019-05-22 22:21:26.526 16750-16836 D/ScrollingActivity: end   GlobalScope.async	Thread: DefaultDispatcher-worker-1
     * 2019-05-22 22:21:26.527 16750-16750 D/ScrollingActivity: end   asyncAwaitTest	Thread: main
     * ```
     */
    private fun asyncAwaitTest() = GlobalScope.launch(Dispatchers.Main) {
        log("start asyncAwaitTest")

        asyncTask()

        log("after asyncTask before GlobalScope.async")

        GlobalScope.async(Dispatchers.IO) {
            log("start GlobalScope.async")
            delay(sleepTime)
            log("end   GlobalScope.async")
            return@async 10
        }.await()
        log("end   asyncAwaitTest")
    }

    private suspend fun asyncTask() = GlobalScope.async(Dispatchers.IO) {
        log("start asyncTask")
        delay(sleepTime)
        log("end   asyncTask")
        return@async 10
    }.await()

    private fun log(message: String) {
        Log.d("ScrollingActivity", "$message\tThread: ${Thread.currentThread().name}")
    }
}
