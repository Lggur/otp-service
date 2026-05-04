package lggur.otp_service.api;

import jakarta.servlet.http.HttpServletRequest;
import lggur.otp_service.model.OtpCode;
import lggur.otp_service.service.OtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/otp")
public class OtpController {

    private static final Logger log = LoggerFactory.getLogger(OtpController.class);

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
        log.info("OTP generate request: userId={}, channel={}, destination={}, operationId={}", userId, channel, destination, operationId);

        otpService.generate(userId, operationId, channel, destination);

        log.info("OTP sent successfully: userId={}, channel={}", userId, channel);
        return "OTP sent";
    }

    @PostMapping("/validate")
    public String validate(
            HttpServletRequest request,
            @RequestParam String code,
            @RequestParam(required = false) String operationId
    ) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("OTP validate request: userId={}, operationId={}", userId, operationId);

        boolean ok = otpService.validate(userId, code, operationId);

        log.info("OTP validation result: userId={}, valid={}", userId, ok);
        return ok ? "VALID" : "INVALID";
    }
}
