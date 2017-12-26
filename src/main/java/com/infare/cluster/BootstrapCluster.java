package com.infare.cluster;

public class BootstrapCluster {
    public static void main(String[] args) {
        WorkerApp.main(new String[]{"2551"});
        WorkerApp.main(new String[]{"2552"});
        TrackerApp.main(new String[]{"2550"});
    }
}
