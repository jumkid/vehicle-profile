package com.jumkid.vehicle.service.batch;

import com.jumkid.vehicle.exception.BatchProcessException;

public interface OnDemandBatchService {

    int runJob() throws BatchProcessException;
}
