package com.jumkid.vehicle.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Aspect
@Configuration
public class AspectConfig {

    @Around("execution(* com.jumkid.vehicle.controller.VehicleController.*(..)))")
    public Object aroundVehicleControllerOperations(ProceedingJoinPoint point) throws Throwable {
        // before method execution
        var result = point.proceed();
        // after method execution
        return result;
    }

}
