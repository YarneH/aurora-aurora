package io.reactivex.android.schedulers;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * This class is a test mock, used in tests to prevent errors
 */
public class AndroidSchedulers {

    public static Scheduler mainThread() {
        return Schedulers.trampoline();
    }
}
