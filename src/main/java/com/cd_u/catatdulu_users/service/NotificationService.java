package com.cd_u.catatdulu_users.service;

import com.cd_u.catatdulu_users.dto.ExpenseDTO;
import com.cd_u.catatdulu_users.model.UserModel;
import com.cd_u.catatdulu_users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final UserRepository userRepository;
    private final EmailForwadService emailForwadService;
    private final ExpenseService expenseService;

    @Value("${catatdulu.frontend.url}")
    private String frontendUrl;


    @Scheduled(cron = "0 0 20 * * *", zone = "Asia/Jakarta")
    public void sendDailyIncomeExpenseReminder() {
        log.info("Job started : sendDailyIncomeExpenseReminder()");
        List<UserModel> users = userRepository.findAll();
        for (UserModel user : users) {
            String body = "Hi " + user.getFirstName() + ",<br><br>"
                    + "This is a friendly reminder of your income and expenses for today in Catat dulu.<br><br>"
                    + "<a href="+frontendUrl+" style='display:inline-block;padding:10px 20px;background-color:#74992e;color:#fff;border-radius:5px;font-weight:bold;text-decoration:none;'>Go to Catat-dulu apps</a>"
                    + "<br><br>Your Bestie and Closest,<br> Fen-Fen :).";
            emailForwadService.sendMail(user.getEmail(), "Daily reminder: better add your income and expense or else you got into debts", body);
        }
        log.info("Job completed: sendDailyIncomeExpenseReminder()");
    }

    @Scheduled(cron = "0 0 21 * * *", zone = "Asia/Jakarta")
    public void sendDailyExpenseSummary() {
        log.info("Job started: sendDailyExpenseSummary");
        List<UserModel> users = userRepository.findAll();
        for (UserModel user : users) {
            List<ExpenseDTO> todayExpenses = expenseService.getExpensesForUserOnDate(user.getId(), LocalDate.now(ZoneId.of("Asia/Jakarta")));
            if (!todayExpenses.isEmpty()) {
                StringBuilder table = new StringBuilder();
                table.append("<table style='border-collapse:collapse;width:100%;'>");
                table.append("<tr style='background-color:#f2f2f2;'><th style='border:3px solid #ddd;padding:8px;'>E.No</th><th style='border:1px solid #ddd;padding:8px;'>Name</th><th style='border:1px solid #ddd;padding:8px;'>Amount</th><th style='border:1px solid #ddd;padding:8px;'>Category</th></tr>");
                int i =1;
                for (ExpenseDTO expense : todayExpenses) {
                    table.append("<tr>");
                    table.append("<td style='border 2px solid #ddd;padding:8px'>").append(i++).append("</td>");
                    table.append("<td style='border 2px solid #ddd;padding:8px'>").append(expense.getName()).append("</td>");
                    table.append("<td style='border 2px solid #ddd;padding:8px'>").append(expense.getAmount()).append("</td>");
                    table.append("<td style='border 2px solid #ddd;padding:8px'>").append(expense.getCategoryId() != null ? expense.getCategoryName() : "N/A").append("</td>");
                    table.append("</tr>");
                }
                table.append("</table>");
                String body = "Hi "+ user.getFirstName() +",<br/><br/> Here is a summary of your expenses of today: <br/><br/>"+table+"<br/><br/>Best Regards,<br/> Fen-Fen :)";
                emailForwadService.sendMail(user.getEmail(), "Your daily expense summary", body);
            }
        }
        log.info("Job finished: sendDailyExpenseSummary");
    }
}
