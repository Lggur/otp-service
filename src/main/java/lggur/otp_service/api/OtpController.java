package lggur.otp_service.api;

import lggur.otp_service.model.OtpCode;
import lggur.otp_service.service.OtpService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/otp")
public class OtpController {
    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/generate")
    public OtpCode generate(
            @RequestParam Long userId,
            @RequestParam(required = false) String operationId
    ) {
        return otpService.generate(userId, operationId);
    }
}
