package test.android.flow

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import java.util.Random
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration.Companion.seconds

private fun numberFlow(): Flow<Int> {
    return callbackFlow {
        val TAG = "[Flow|${hashCode()}]"
        println("$TAG: start...")
        val isStopped = AtomicBoolean(false)
        val random = Random()
        while (!isStopped.get()) {
            delay(1.seconds)
            val value = random.nextInt(1000)
            println("$TAG: send \"$value\"")
            send(value)
        }
        awaitClose {
            isStopped.set(true)
            println("$TAG: stop...")
        }
    }
}

internal class MainActivity : AppCompatActivity() {
    private val TAG = "[${this::class.java.simpleName}|${hashCode()}]"
    override fun onCreate(inState: Bundle?) {
        super.onCreate(inState)
        val context: Context = this
        val textView = TextView(context).also {
            it.textSize = 32f
            it.typeface = Typeface.MONOSPACE
            it.setTextColor(Color.WHITE)
        }
        FrameLayout(context).also { root ->
            root.background = ColorDrawable(Color.BLACK)
            root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            textView.also {
                it.layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER
                )
                root.addView(it)
            }
            setContentView(root)
        }
        lifecycleScope.launch {
            println("$TAG: launch...")
            val state = Lifecycle.State.STARTED
            repeatOnLifecycle(state) {
                println("$TAG: repeatOnLifecycle($state)...")
                numberFlow()
//                    .takeWhile { it < 888 }
                    .onCompletion {
                        println("flow stop: $it")
                    }
                    .collect { number ->
                    println("$TAG: collect \"$number\"")
                    textView.text = "%03d".format(number)
//                        if (number > 888) {
//                            coroutineContext.job.cancel(CancellationException("just cause"))
//                        }
                }
            }
        }
    }
}
