package world.ucode.cashflow.controller;

import world.ucode.cashflow.db.User;
import world.ucode.cashflow.db.Transaction;
import world.ucode.cashflow.banking.*;
import world.ucode.cashflow.db.Wallet;
import world.ucode.cashflow.repos.TransactionRepo;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import javax.servlet.http.HttpServletResponse;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class MainController {
    @Autowired
    private TransactionRepo transactionRepo;

	@GetMapping("/records")
	public String recordGet(@AuthenticationPrincipal User user,Model model, @ModelAttribute Transaction transaction) {
        model.addAttribute("user",user.getUsername());
        model.addAttribute("transaction",transaction);
		return "records";
    }

    @PostMapping("/records")
    public String recordPost(@AuthenticationPrincipal User user,Map<String, Object> model, @ModelAttribute Transaction transaction) {
        transaction.setAuthor(user);
        transactionRepo.save(transaction);
		return "redirect:transaction";
    }
    
    @GetMapping("/home")
    public String homeGet(@AuthenticationPrincipal User user,Model model) {
        Iterable<Transaction> transactions = transactionRepo.findAll();
        // Map<String, Integer> income_m = new LinkedHashMap<>();
        // Map<String, Integer> exp_m = new LinkedHashMap<>();
        HashMap<String,Integer> hm = new HashMap<>();
        HashMap<String,Integer> e_hm = new HashMap<>();
        int income = 0;
        int exp = 0;
        for(Transaction tr : transactions) {
            String name = tr.getCategory();
            // int price = tr.getText();
            // String name = i.getShop();
            if (tr.getText() > 0) {
                int price = hm.containsKey(name) ? hm.get(name) : 0;
                price += tr.getText();
                hm.put(name, price);
            } else {
                int price = hm.containsKey(name) ? hm.get(name) : 0;
                price += tr.getText();
                e_hm.put(name, price);

            }
//            int price = hm.containsKey(name) ? hm.get(name) : 0;
//            price += tr.getText();
//            hm.put(name, price);
//             if (price < 0) {
//                 price *= -1;
//                 e_hm.put(name, hm.getOrDefault(price, 0) + price);
//             }
//             else {
//                 hm.put(name, e_hm.getOrDefault(category, 0) + price);
//             }
            if (tr.getText() > 0 && tr.getType().equals("Income")) {
                income += tr.getText();
            }
            else {
                exp += tr.getText() * -1;
            }
        }
        exp *= -1;
        model.addAttribute("surveyMap", hm);
        model.addAttribute("surveyMapE", hm);
        model.addAttribute("pass", income);
        model.addAttribute("fail", exp);
        model.addAttribute("user",user.getUsername());
        return "dashboard";
    }
    // @PostMapping("/home")
	// public String homePost(@AuthenticationPrincipal User user,Model model) {
	// 	return "records";
	// }

    @GetMapping("/transaction")
	public String transaction(@AuthenticationPrincipal User user,Map<String, Object> model) {
        Iterable<Transaction> transactions = transactionRepo.findAll();
        model.put("user",user.getUsername());
        model.put("transactions",transactions);
		return "transaction";
    }
    @GetMapping("/wallet")
    public String getPage() {
	    return "wallet";
    }

    @PostMapping("/wallet")
    public String transaction(@ModelAttribute Wallet wallet, Map<String, Object> model) {
        Iterable<Transaction> transactions = transactionRepo.findByWallet(wallet.getName());
        model.put("walletName",wallet.getName());
        model.put("res", transactions);
        return "wallet";
    }
    @GetMapping("/tag")
    public String getPageTag() {
        return "tag";
    }

    @PostMapping("/tags")
    public String getTag(@ModelAttribute Wallet wallet, Map<String, Object> model) {
        Iterable<Transaction> transactions = transactionRepo.findByTag(wallet.getName());
//        model.put("walletName",wallet.getName());
        model.put("res", transactions);
        return "tag";
    }
    
    @GetMapping("/monobank")
    public String Mono(Map<String, Object> model,@AuthenticationPrincipal User user) throws Exception {
        Mono mono = new Mono();
        List<MonoTransaction> data = mono.sendGet();
        int count = 0;
        String balance = "";
        for(MonoTransaction i : data) {
            if (count == 0) {
                balance = i.getBalance();
            }
            Date currentTime = new Date(Long.parseLong(i.getTime())*1000);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = formatter.format(currentTime);
            i.setBalance(Double.toString(Double.parseDouble(i.getBalance()) / 100));
            i.setAmount(Double.toString(Double.parseDouble(i.getAmount()) / 100));
            i.setTime(dateString);
            if (i.getCurrencyCode().equals("980")) {
                i.setCurrencyCode("UAN");
            } else if (i.getCurrencyCode().equals("978")) {
                i.setCurrencyCode("EUR");
            } else if (i.getCurrencyCode().equals("840")) {
                i.setCurrencyCode("USD");
            }
            if (Integer.parseInt(i.getOperationAmount()) > 0){
                i.setOperationAmount("Income");
            } else {
                i.setOperationAmount("Expense");
            } 
            count++;
        }
        model.put("balance",balance);
        model.put("monos",data);
        model.put("user",user.getUsername());
		return "monobank";
    }
    
    @GetMapping("/privatbank")
    public String getPriv(@AuthenticationPrincipal User user,Model model) {
        model.addAttribute("user",user.getUsername());
        return "privatbank";
    }


    @GetMapping("/profile")
    public String getProfile(@AuthenticationPrincipal User user,Model model) {
        model.addAttribute("user",user.getUsername());
        return "profile";
    }

}