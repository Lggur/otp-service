package lggur.otp_service.api;

import jakarta.servlet.http.HttpServletRequest;
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
    public String generate(
            HttpServletRequest request,
            @RequestParam(required = false) String operationId,
            @RequestParam String channel,
            @RequestParam String destination
    ) {
        Long userId = (Long) request.getAttribute("userId");

        otpService.generate(userId, operationId, channel, destination);

        return "OTP sent";
    }

    @PostMapping("/validate")
    public String validate(
            HttpServletRequest request,
            @RequestParam String code,
            @RequestParam(required = false) String operationId
    ) {
        Long userId = (Long) request.getAttribute("userId");

        boolean ok = otpService.validate(userId, code, operationId);

        return ok ? "VALID" : "INVALID";
    }
}
