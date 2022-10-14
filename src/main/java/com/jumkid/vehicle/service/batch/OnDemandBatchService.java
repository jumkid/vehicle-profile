package com.jumkid.vehicle.service.batch;

import com.jumkid.vehicle.exception.BatchProcessException;

public interface OnDemandBatchService {

    public int runJob() throws BatchProcessException;
}
