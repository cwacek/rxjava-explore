import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.joins.Pattern;
import rx.schedulers.Schedulers;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created by cwacek on 2/20/17.
 */
public class Hello {

    public static void main (String[] argv) throws InterruptedException {
        System.out.println("Hi");



        String[] sourceRaw = new String[4];
        sourceRaw[0] = "Yolo";
        sourceRaw[1] = "Wazoo";
        sourceRaw[2] = "Dinner";

        Random rand = new Random();

        Observable<String> source = Observable.from(sourceRaw);
        Observable<String> timingOut = Observable.interval(3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .zipWith(source, new Func2<Long, String, String>() {
                    @Override
                    public String call(Long aLong, String s) {
                        return s;
                    }
                }).delay(new Func1<String, Observable<? extends Object>>() {
                    @Override
                    public Observable<? extends Object> call(String s) {
                        return null;
                    }
                });

        Observable<String> timeoutFactory = Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                long f = rand.nextInt(15);
                System.out.println("Random will be "+ f);
                return timingOut.delay(f, TimeUnit.SECONDS)
                        .timeout(8, TimeUnit.SECONDS)
                        .doOnError(error -> {
                            System.out.println("Timeout threw error");
                        });
            }
        });


        timeoutFactory
                .doOnNext(str -> {
                    System.out.println("next: " + str);
                })
                .retry()
                .subscribe(str -> {
                    System.out.println(str);
                });


        System.out.println("Done");
        Thread.sleep(10000);
    }
}
