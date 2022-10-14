package com.jumkid.vehicle.service.batch;

import com.jumkid.vehicle.model.VehicleSearch;
import lombok.Getter;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.lang.NonNull;

import java.util.List;

public class ItemCountListener implements ItemWriteListener<VehicleSearch> {

    @Getter
    private int counter = 0;

    @Override
    public void beforeWrite(@NonNull List list) {
        //void
    }

    @Override
    public void afterWrite(@NonNull List list) {
        counter += list.size();
    }

    @Override
    public void onWriteError(@NonNull Exception e, @NonNull List list) {
        //void
    }
}
