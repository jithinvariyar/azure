package com.jithin.azure.azurefunctions;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;

public class TimeTriggerFunction {
    @FunctionName("keepAlive")
    public void run(
            @TimerTrigger(
                name = "keepAliveTrigger",
                schedule = "0 */5 * * * *")
                String timerInfo,
            final ExecutionContext context) {
        context.getLogger().info("Timer Is Triggered");
    }
}
