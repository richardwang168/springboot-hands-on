package crs.fcl.eim.sftp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import crs.fcl.eim.sftp.dto.PasswordForgotDto;
import crs.fcl.eim.sftp.model.Mail;
import crs.fcl.eim.sftp.model.PasswordResetToken;
import crs.fcl.eim.sftp.model.User;
import crs.fcl.eim.sftp.repository.PasswordResetTokenRepository;
import crs.fcl.eim.sftp.service.EmailService;
import crs.fcl.eim.sftp.service.MyUserDetailsService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/forgot-password")
public class PasswordForgotController {

	@Autowired
	private MyUserDetailsService myUserDetailsService;
	@Autowired
	private PasswordResetTokenRepository tokenRepository;
	@Autowired
	private EmailService emailService;

	@ModelAttribute("forgotPasswordForm")
	public PasswordForgotDto forgotPasswordDto() {
		return new PasswordForgotDto();
	}

	@GetMapping
	public String displayForgotPasswordPage() {
		return "forgot-password";
	}

	@PostMapping
	public String processForgotPasswordForm(@ModelAttribute("forgotPasswordForm") @Valid PasswordForgotDto form,
			BindingResult result, HttpServletRequest request) {

		if (result.hasErrors()) {
			return "forgot-password";
		}

		User user = myUserDetailsService.findByEmail(form.getEmail());
		if (user == null) {
			result.rejectValue("email", null, "We could not find an account for that e-mail address.");
			return "forgot-password";
		}
		
		System.out.println("User-object=" + user.toString());
		
		PasswordResetToken token = new PasswordResetToken();
		token.setToken(UUID.randomUUID().toString());
		token.setUser(user);
		token.setExpiryDate(30);
		System.out.println("Token-object=" + token.toString());
		System.out.println("Save token to DB......");
		tokenRepository.save(token);
		System.out.println("Saved.");

		Mail mail = new Mail();
		mail.setFrom("no-reply@fcl.crs");
		mail.setTo(user.getEmail());
		mail.setSubject("Password reset request");

		Map<String, Object> model = new HashMap<>();
		model.put("token", token);
		model.put("user", user);
		model.put("signature", "https://hub.crs/");
		String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
		model.put("resetUrl", url + "/reset-password?token=" + token.getToken());
		mail.setModel(model);
		emailService.sendEmail(mail);

		return "redirect:/forgot-password?success";

	}

}
