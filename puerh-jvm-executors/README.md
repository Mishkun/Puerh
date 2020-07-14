# JVM Executors module

This module is a pure jvm module that contains helper class for building an EffectHandler 
implementation using just plain ExecutorService. This can help you if you want go complete bare-bones
regarding your async work.

### Tip for Android developers: using Handler as an executor

When working with android development, you might want to use Handler attached to MainLooper as your
`callerThreadExecutor`. This can be easily accomplished with the snippet below.

```kotlin
class AndroidMainThreadExecutor : Executor {
    private val handler = Handler(Looper.getMainLooper())
    override fun execute(command: Runnable?) {
        handler.post(command)
    }
}
```
