package jp.hideakisago.kotlincoroutinesample

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_scrolling.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScrollingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        launch.setOnClickListener { launchTest() }
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
     * 2019-05-22 22:04:06.472 16115-16115 D/ScrollingActivity: before launch	Thread: main
     * 2019-05-22 22:04:06.473 16115-16115 D/ScrollingActivity: after launch	Thread: main
     * 2019-05-22 22:04:06.473 16115-16145 D/ScrollingActivity: start launch1	Thread: DefaultDispatcher-worker-2
     * 2019-05-22 22:04:06.473 16115-16144 D/ScrollingActivity: start launch2	Thread: DefaultDispatcher-worker-1
     * 2019-05-22 22:04:06.474 16115-16144 D/ScrollingActivity: start launch3	Thread: DefaultDispatcher-worker-1
     * 2019-05-22 22:04:12.475 16115-16145 D/ScrollingActivity: end   launch1	Thread: DefaultDispatcher-worker-2
     * 2019-05-22 22:04:12.475 16115-16145 D/ScrollingActivity: start launch4	Thread: DefaultDispatcher-worker-2
     * 2019-05-22 22:04:12.476 16115-16145 D/ScrollingActivity: end   launch2	Thread: DefaultDispatcher-worker-2
     * 2019-05-22 22:04:12.479 16115-16144 D/ScrollingActivity: end   launch3	Thread: DefaultDispatcher-worker-1
     * 2019-05-22 22:04:18.484 16115-16145 D/ScrollingActivity: end   launch4	Thread: DefaultDispatcher-worker-2
     * ```
     */
    private fun launchTest() {
        val sleepTime: Long = 6 * 1000
        log("before launch")

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

        log("after launch")
    }

    private fun log(message: String) {
        Log.d("ScrollingActivity", "$message\tThread: ${Thread.currentThread().name}")
    }
}
