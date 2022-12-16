package com.projectvibewave.vibewaveapp.controller;

import com.projectvibewave.vibewaveapp.dto.UserSettingsDto;
import com.projectvibewave.vibewaveapp.service.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;

@Controller
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @GetMapping("/settings")
    @PreAuthorize("isAuthenticated()")
    public String getUserSettings(Model model) {
        logger.info("Accessed User Settings Page");

        var userSettings = userService.getAuthenticatedUserSettings();
        model.addAttribute("settings", userSettings);
        return "user/settings";
    }

    @PostMapping("/settings")
    @PreAuthorize("isAuthenticated()")
    public String updateUserSettings(@Valid @ModelAttribute("settings") UserSettingsDto userSettingsDto,
                                     BindingResult bindingResult, Model model) throws IOException  {
        logger.info("Trying to update user settings....");

        var isSuccessful = userService.tryUpdateUserSettings(userSettingsDto, bindingResult);

        if (!isSuccessful) {
            return "user/settings";
        }

        return "redirect:/user/settings/?updated";
    }

    @GetMapping("/{userId}")
    public String getUserProfileView(Model model, @PathVariable @NotNull Long userId) {
        logger.info("Accessed User Profile Page");

        var exists = userService.setUserByIdModelView(model, userId);

        if (!exists) {
            return "redirect:/";
        }

        return "user/profile";
    }
}
