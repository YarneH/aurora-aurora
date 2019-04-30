package com.aurora.data.network;

import com.aurora.utilities.InjectorUtils;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class AuroraFirebaseJobService extends JobService {

    /**
     * This is called by the Job Dispatcher to tell us we should start our job. Keep in mind this
     * method is run on the application's main thread, so we need to offload work to a background
     * thread.
     *
     * @return whether there is more work remaining.
     */
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        AuroraNetworkDataSource networkDataSource = InjectorUtils.provideNetworkDataSource(
                this.getApplicationContext());
        networkDataSource.fetchMarketPlugins();

        jobFinished(jobParameters, false);

        return false;
    }

    /**
     * Called when the scheduling engine has decided to interrupt the execution of a running job,
     * most likely because the runtime constraints associated with the job are no longer satisfied.
     *
     * @return whether the job should be retried
     */
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }
}
