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
     * 2019-05-22 09:07:50.823 14121-14121 D/ScrollingActivity: before launch main
     * 2019-05-22 09:07:50.890 14121-14121 D/ScrollingActivity: after launch main
     * 2019-05-22 09:07:50.917 14121-14180 D/ScrollingActivity: start launch1 DefaultDispatcher-worker-1
     * 2019-05-22 09:07:50.917 14121-14181 D/ScrollingActivity: start launch2 DefaultDispatcher-worker-2
     * 2019-05-22 09:07:51.020 14121-14181 D/ScrollingActivity: start launch3 DefaultDispatcher-worker-2
     * 2019-05-22 09:07:56.919 14121-14180 D/ScrollingActivity: end   launch1 DefaultDispatcher-worker-1
     * 2019-05-22 09:07:56.921 14121-14180 D/ScrollingActivity: start launch4 DefaultDispatcher-worker-1
     * 2019-05-22 09:07:56.985 14121-14180 D/ScrollingActivity: end   launch2 DefaultDispatcher-worker-1
     * 2019-05-22 09:07:57.021 14121-14181 D/ScrollingActivity: end   launch3 DefaultDispatcher-worker-2
     * 2019-05-22 09:08:02.925 14121-14181 D/ScrollingActivity: end   launch4 DefaultDispatcher-worker-2
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
