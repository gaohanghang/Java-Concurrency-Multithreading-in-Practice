package org.hackerandpainter.section3;

import java.util.Random;
import java.util.concurrent.*;


/**
 * @Description
 * @Author Gao Hang Hang
 * @Date 2019-07-27 17:23
 **/
public class Lesson3 {

    public static void main(String[] args) {

        // Try this: 按需天气预报发布者
        // SubmissionPublisher<WeatherForecast> weatherForecastPublisher = new OnDemandWeatherForecastPublisher();

        // Try this:
        // SubmissionPublisher<WeatherForecast> weatherForecastPublisher = new SubmissionPublisher<>();

        SubmissionPublisher<WeatherForecast> weatherForecastPublisher = new WeatherForecastPublisher();

        weatherForecastPublisher.subscribe(new TwitterSubscriber());
        weatherForecastPublisher.subscribe(new DatabaseSubscriber());

        // Try this in combination with `weatherForecastPublisher = new SubmissionPiblisher<WeatherForecast>();`
//		for (int i = 0; i < Long.MAX_VALUE; i++) {
//			weatherForecastPublisher.submit(WeatherForecast.nextRandomWeatherForecast());
//		}

        // close the publisher and associated resources after 10 seconds
        // 10秒后关闭发布者和相关资源
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Comment out when using OnDemandWeatherForecastPublisher which is not AutoClosable
        weatherForecastPublisher.close();
    }

    /**
     * Modification of the example from JavaDoc for
     * java.util.concurrent.SubmissionPublisher<T>
     */
    public static class WeatherForecastPublisher extends SubmissionPublisher<WeatherForecast> {
        final ScheduledFuture<?> periodicTask;
        // 创建一个定长线程池，支持定时及周期性任务执行。
        final ScheduledExecutorService scheduler;

        WeatherForecastPublisher() {
            // Try this:
            // super(Executors.newFixedThreadPool(2), Flow.defaultBufferSize());
            scheduler = Executors.newScheduledThreadPool(1);
            periodicTask = scheduler.scheduleAtFixedRate( //
                    // runs submit()
                    () -> submit(WeatherForecast.nextRandomWeatherForecast()), //
                    500, 500, TimeUnit.MILLISECONDS);
        }

        public void close() {
            //  该方法便可以用来（尝试）终止一个任务。
            periodicTask.cancel(false);
            scheduler.shutdown();
            super.close();
        }
    }

    public static class OnDemandWeatherForecastPublisher implements Flow.Publisher<WeatherForecast> {
        private final ExecutorService executor = ForkJoinPool.commonPool();

        /**
         * 订阅
         *
         * @param subscriber
         */
        @Override
        public void subscribe(Flow.Subscriber<? super WeatherForecast> subscriber) {
            // 订阅者订阅
            subscriber.onSubscribe(new OnDemandWeatherForecastSubscription(subscriber, executor));
        }
    }

    // 订阅
    static class OnDemandWeatherForecastSubscription implements Flow.Subscription {
        private final Flow.Subscriber<? super WeatherForecast> subscriber;
        private final ExecutorService executor;
        private Future<?> future; // to allow cancellation

        OnDemandWeatherForecastSubscription(Flow.Subscriber<? super WeatherForecast> subscriber,
                                            ExecutorService executor) {
            this.subscriber = subscriber;
            this.executor = executor;
        }

        public synchronized void request(long n) {
            if (n > 0) {
                for (int i = 0; i < n; i++) {
                    future = executor.submit(() -> {
                        subscriber.onNext(WeatherForecast.nextRandomWeatherForecast());
                    });
                }
            } else if (n < 0) {
                IllegalArgumentException ex = new IllegalArgumentException();
                executor.execute(() -> subscriber.onError(ex));
            }
        }

        public synchronized void cancel() {
            if (future != null)
                future.cancel(false);
        }
    }

    private static final class DatabaseSubscriber implements Flow.Subscriber<WeatherForecast> {
        private Flow.Subscription subscription;
        private final String name = "Database Subscriber";

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            System.out.println(name + " subscribed!");
            this.subscription = subscription;
            subscription.request(1);
        }

        @Override
        public void onNext(WeatherForecast weatherForecast) {
            System.out.println(
                    Thread.currentThread().getName() + " > Saving to DB: " + weatherForecast);
            subscription.request(1);
        }

        @Override
        public void onError(Throwable throwable) {
        }

        @Override
        public void onComplete() {
        }
    }

    private static final class TwitterSubscriber implements Flow.Subscriber<WeatherForecast> {
        private Flow.Subscription subscription;
        private final String name = "Twitter Subscriber";

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            System.out.println(name + " subscribed!");
            this.subscription = subscription;
            subscription.request(1);
        }

        @Override
        public void onNext(WeatherForecast weatherForecast) {
            System.out.println(
                    Thread.currentThread().getName() + " > Twitting: " + weatherForecast);
            subscription.request(1);
        }

        @Override
        public void onError(Throwable throwable) {
            System.err.println(name + " got an error: " + throwable.getMessage());
        }

        @Override
        public void onComplete() {
            System.out.println(name + " completed.");
        }
    }

    /**
     * Weather Forecast in United States customary units
     */
    public static class WeatherForecast {
        private final int temperatureInF;
        private final int windSpeedInMPH;
        private final String weatherCondition;

        private static final Random random = new Random();
        private static final String[] allWeatherConditions = new String[]{"☁️", "☀️", "⛅", "🌧",
                "⛈️"};

        public static WeatherForecast nextRandomWeatherForecast() {
            String weatherCondition = allWeatherConditions[random
                    .nextInt(allWeatherConditions.length)];
            int temperatureInF = random.nextInt(95);
            int windSpeedInMPH = 5 + random.nextInt(30);
            return new WeatherForecast(temperatureInF, windSpeedInMPH, weatherCondition);
        }

        public WeatherForecast(int temperatureInF, int windSpeedInMPH, String weatherCondition) {
            super();
            this.temperatureInF = temperatureInF;
            this.windSpeedInMPH = windSpeedInMPH;
            this.weatherCondition = weatherCondition;
        }

        @Override
        public String toString() {
            return weatherCondition + " " + temperatureInF + "°F wind: " + windSpeedInMPH + "mph";
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + temperatureInF;
            result = prime * result
                    + ((weatherCondition == null) ? 0 : weatherCondition.hashCode());
            result = prime * result + windSpeedInMPH;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            WeatherForecast other = (WeatherForecast) obj;
            if (temperatureInF != other.temperatureInF)
                return false;
            if (weatherCondition == null) {
                if (other.weatherCondition != null)
                    return false;
            } else if (!weatherCondition.equals(other.weatherCondition))
                return false;
            if (windSpeedInMPH != other.windSpeedInMPH)
                return false;
            return true;
        }

    }
}
