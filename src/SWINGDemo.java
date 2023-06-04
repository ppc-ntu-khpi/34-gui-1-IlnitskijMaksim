package com.mybank.tui;

import com.mybank.domain.Bank;
import com.mybank.domain.CheckingAccount;
import com.mybank.domain.Customer;
import com.mybank.domain.SavingsAccount;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class SWINGDemo {

    private final JEditorPane log;
    private final JButton show;
    private final JComboBox<String> clients;

    public SWINGDemo() {
        log = new JEditorPane("text/html", "");
        log.setPreferredSize(new Dimension(250, 250));
        show = new JButton("Show");
        clients = new JComboBox<>();

        readCustomerData("C:\\Users\\Immrtldrgn\\Documents\\NetBeansProjects\\TUIdemo\\test.dat");

        for (int i = 0; i < Bank.getNumberOfCustomers(); i++) {
            clients.addItem(Bank.getCustomer(i).getFirstName() +" "+ Bank.getCustomer(i).getLastName());
        }
    }

    private void readCustomerData(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\t");
                if (data.length >= 3) {
                    String firstName = data[0].trim();
                    String lastName = data[1].trim();
                    Bank.addCustomer(firstName, lastName);
                    int numAccounts = Integer.parseInt(data[2].trim());
                    for (int i = 0; i < numAccounts; i++) {
                        line = reader.readLine();
                        String[] accountData = line.split("\t");
                        if (accountData.length >= 3) {
                            String accountType = accountData[0].trim();
                            double balance = Double.parseDouble(accountData[1].trim());
                            double interestRate = Double.parseDouble(accountData[2].trim());
                            if (accountType.equals("S")) {
                                SavingsAccount savingsAccount = new SavingsAccount(balance, interestRate);
                                Bank.getCustomer(Bank.getNumberOfCustomers() - 1).addAccount(savingsAccount);
                            } else if (accountType.equals("C")) {
                                double overdraftAmount = Double.parseDouble(accountData[2].trim());
                                CheckingAccount checkingAccount = new CheckingAccount(balance, overdraftAmount);
                                Bank.getCustomer(Bank.getNumberOfCustomers() - 1).addAccount(checkingAccount);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading customers from file: " + e.getMessage());
        }
    }

    private void launchFrame() {
        JFrame frame = new JFrame("MyBank clients");
        frame.setLayout(new BorderLayout());
        JPanel cpane = new JPanel();
        cpane.setLayout(new GridLayout(1, 3));

        cpane.add(clients);
        cpane.add(show);
        frame.add(cpane, BorderLayout.NORTH);
        frame.add(log, BorderLayout.CENTER);

        show.addActionListener((ActionEvent e) -> {
            Customer current = Bank.getCustomer(clients.getSelectedIndex());
            StringBuilder custInfo = new StringBuilder("<br>&nbsp;<b><span style=\"font-size:2em;\">")
                    .append(current.getFirstName())
                    .append(" ")
                    .append(current.getLastName())
                    .append("</span><br><hr>")
                    .append("&nbsp;<b>Accounts:</b><br>");
            
            for (int i = 0; i < current.getNumberOfAccounts(); i++) {
                String accType = current.getAccount(i) instanceof CheckingAccount ? "Checking" : "Savings";
                custInfo.append("&nbsp;&nbsp;&nbsp;<b>Acc Type:</b> ")
                        .append(accType)
                        .append("<br>")
                        .append("&nbsp;&nbsp;&nbsp;<b>Balance:</b> <span style=\"color:red;\">$")
                        .append(current.getAccount(i).getBalance())
                        .append("</span><br>");
                
            }
            
            log.setText(custInfo.toString());
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SWINGDemo demo = new SWINGDemo();
        demo.launchFrame();
    }
}
