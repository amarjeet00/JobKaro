package com.jobkaro.controller;

import com.jobkaro.dao.JobDAO;
import com.jobkaro.dao.UserDAO;
import com.jobkaro.model.Job;
import com.jobkaro.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
public class MainController {

    private final JobDAO jobDAO;
    private final UserDAO userDAO;

    public MainController(JobDAO jobDAO, UserDAO userDAO) {
        this.jobDAO = jobDAO;
        this.userDAO = userDAO;
    }

    // ── Landing page ──────────────────────────────────────────
    @GetMapping("/")
    public String index() { return "redirect:/home"; }

    // ── Login ─────────────────────────────────────────────────
    @GetMapping("/login")
    public String loginPage(@RequestParam(required=false) String error,
                            @RequestParam(required=false) String logout,
                            Model model) {
        if (error != null) model.addAttribute("error", "Invalid email or password.");
        if (logout != null) model.addAttribute("success", "Logged out successfully.");
        return "login";
    }

    // ── Register ──────────────────────────────────────────────
    @GetMapping("/register")
    public String registerPage() { return "register"; }

    @PostMapping("/register")
    public String register(@RequestParam String name,
                           @RequestParam String email,
                           @RequestParam String phone,
                           @RequestParam String password,
                           @RequestParam String role,
                           @RequestParam String city,
                           Model model) {
        try {
            if (name.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank() || city.isBlank()) {
                model.addAttribute("error", "All fields are required.");
                return "register";
            }
            if (!email.contains("@")) { model.addAttribute("error", "Invalid email."); return "register"; }
            if (phone.length() != 10)  { model.addAttribute("error", "Phone must be 10 digits."); return "register"; }
            if (password.length() < 6) { model.addAttribute("error", "Password min 6 characters."); return "register"; }
            if (userDAO.emailExists(email)) { model.addAttribute("error", "Email already registered."); return "register"; }

            User u = new User(name.trim(), email.trim(), phone.trim(), password, role, city.trim());
            userDAO.create(u);
            return "redirect:/login?registered=true";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "register";
        }
    }

    // ── Home Dashboard ────────────────────────────────────────
    @GetMapping("/home")
    public String home(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = userDAO.findByEmail(principal.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("urgentJobs", jobDAO.findUrgent());
        if (user.isWorker()) {
            model.addAttribute("jobs", jobDAO.findByCity(user.getCity()));
        } else {
            model.addAttribute("jobs", jobDAO.findByProvider(user.getId()));
        }
        return "home";
    }

    // ── Browse Jobs ───────────────────────────────────────────
    @GetMapping("/jobs")
    public String jobs(@RequestParam(required=false) String q,
                       @RequestParam(required=false) String city,
                       @AuthenticationPrincipal UserDetails principal,
                       Model model) {
        User user = userDAO.findByEmail(principal.getUsername());
        model.addAttribute("user", user);
        List<Job> jobs;
        if (q != null && !q.isBlank()) {
            jobs = jobDAO.search(q);
            model.addAttribute("query", q);
        } else if (city != null && !city.isBlank()) {
            jobs = jobDAO.findByCity(city);
            model.addAttribute("city", city);
        } else {
            jobs = jobDAO.findOpen();
        }
        model.addAttribute("jobs", jobs);
        return "jobs";
    }

    // ── Job Detail ────────────────────────────────────────────
    @GetMapping("/jobs/{id}")
    public String jobDetail(@PathVariable int id,
                            @AuthenticationPrincipal UserDetails principal,
                            Model model) {
        User user = userDAO.findByEmail(principal.getUsername());
        Job job = jobDAO.findById(id);
        if (job == null) return "redirect:/jobs";
        model.addAttribute("user", user);
        model.addAttribute("job", job);
        return "job-detail";
    }

    // ── Post Job ──────────────────────────────────────────────
    @GetMapping("/post-job")
    public String postJobPage(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = userDAO.findByEmail(principal.getUsername());
        if (!user.isProvider()) return "redirect:/home";
        model.addAttribute("user", user);
        return "post-job";
    }

    @PostMapping("/post-job")
    public String postJob(@AuthenticationPrincipal UserDetails principal,
                          @RequestParam String title,
                          @RequestParam String description,
                          @RequestParam String category,
                          @RequestParam double payment,
                          @RequestParam String paymentType,
                          @RequestParam int workersNeeded,
                          @RequestParam String address,
                          @RequestParam String city,
                          @RequestParam String jobDate,
                          @RequestParam String duration,
                          @RequestParam(defaultValue="false") boolean urgent,
                          Model model) {
        try {
            User user = userDAO.findByEmail(principal.getUsername());
            Job job = new Job();
            job.setProviderId(user.getId());
            job.setTitle(title.trim());
            job.setDescription(description.trim());
            job.setCategory(category);
            job.setPayment(payment);
            job.setPaymentType(paymentType);
            job.setWorkersNeeded(workersNeeded);
            job.setAddress(address.trim());
            job.setCity(city.trim());
            job.setJobDate(LocalDate.parse(jobDate));
            job.setDuration(duration.trim());
            job.setUrgent(urgent);
            job.setStatus("open");
            jobDAO.create(job);
            return "redirect:/home?posted=true";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to post job: " + e.getMessage());
            return "post-job";
        }
    }

    // ── Profile ───────────────────────────────────────────────
    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = userDAO.findByEmail(principal.getUsername());
        model.addAttribute("user", user);
        if (user.isProvider()) {
            model.addAttribute("myJobs", jobDAO.findByProvider(user.getId()));
        }
        return "profile";
    }

    // ── Update Job Status ────────────────────────────────────
    @PostMapping("/jobs/{id}/status")
    public String updateStatus(@PathVariable int id,
                               @RequestParam String status,
                               @AuthenticationPrincipal UserDetails principal) {
        jobDAO.updateStatus(id, status);
        return "redirect:/home";
    }
}
