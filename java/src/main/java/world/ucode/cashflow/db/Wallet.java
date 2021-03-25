 package world.ucode.cashflow.db;

 import javax.persistence.*;
 import lombok.*;

 @Getter
 @Setter
 @Entity
 @NoArgsConstructor
 @Table(name = "wallet")
 public class Wallet {
     @Id
     @GeneratedValue(strategy = GenerationType.AUTO)
     private Long id;
     private String name;
     private String currency;
     private Integer balance;

     public Integer getBalance() {
         return balance;
     }

     public Long getId() {
         return id;
     }

     public String getCurrency() {
         return currency;
     }

     public String getName() {
         return name;
     }

     public void setBalance(Integer balance) {
         this.balance = balance;
     }

     public void setCurrency(String currency) {
         this.currency = currency;
     }

     public void setId(Long id) {
         this.id = id;
     }

     public void setName(String name) {
         this.name = name;
     }
 }