package com.example.sessionsecurity.sample.scheduler;

import com.example.sessionsecurity.common.response.ApiResponse;
import com.example.sessionsecurity.common.scheduler.QuartzSchedulerService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sample/quartz")
public class QuartzSampleController {

    private final QuartzSchedulerService quartzSchedulerService;

    public QuartzSampleController(QuartzSchedulerService quartzSchedulerService) {
        this.quartzSchedulerService = quartzSchedulerService;
    }

    @Operation(summary = "Quartz 일회성 job 등록 샘플")
    @PostMapping("/once")
    public ApiResponse<Map<String, String>> scheduleOnce(
            @RequestParam(defaultValue = "hello quartz") String message,
            @RequestParam(defaultValue = "5") int delaySeconds
    ) {
        String jobId = quartzSchedulerService.scheduleSampleJob(message, delaySeconds);
        return ApiResponse.ok(Map.of("jobId", jobId));
    }
}
