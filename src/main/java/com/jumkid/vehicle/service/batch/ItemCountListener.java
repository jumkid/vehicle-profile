package com.jumkid.vehicle.service.batch;

import com.jumkid.vehicle.model.VehicleSearch;
import lombok.Getter;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.lang.NonNull;

import java.util.List;

public class ItemCountListener implements ItemWriteListener<VehicleSearch> {

    @Getter
    private int counter = 0;

    void afterWrite(@NonNull List<VehicleSearch> list) {
        counter += list.size();
    }

}
