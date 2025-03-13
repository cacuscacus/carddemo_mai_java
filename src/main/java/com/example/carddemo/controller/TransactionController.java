package com.example.carddemo.controller;

import com.example.carddemo.model.Transaction;
import com.example.carddemo.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Sort;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Controller
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDateTime.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                LocalDate date = LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE);
                setValue(date.atStartOfDay());
            }

            @Override
            public String getAsText() {
                LocalDateTime value = (LocalDateTime) getValue();
                return value != null ? value.format(DateTimeFormatter.ISO_LOCAL_DATE) : "";
            }
        });
    }

    @GetMapping(value = "/transactions")
    public String listTransactions(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "tranId", required = false) String tranId,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactionPage = transactionService.searchTransactions(tranId, pageable);

        model.addAttribute("transactions", transactionPage.getContent());
        model.addAttribute("currentPage", transactionPage.getNumber());
        model.addAttribute("totalPages", transactionPage.getTotalPages());
        model.addAttribute("tranId", tranId);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        model.addAttribute("currentDate", now.format(dateFormatter));
        model.addAttribute("currentTime", now.format(timeFormatter));
        model.addAttribute("programName", "COTRN00C");
        model.addAttribute("transactionName", "CT00");

        return "transactions/transactionList";
    }

    @GetMapping("/transactions/{id}")
    public String viewTransaction(@PathVariable("id") String id, Model model) {
        Transaction transaction = transactionService.getTransactionById(id);
        if (transaction == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found");
        }
        model.addAttribute("transaction", transaction);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        model.addAttribute("currentDate", now.format(dateFormatter));
        model.addAttribute("currentTime", now.format(timeFormatter));
        model.addAttribute("programName", "COTRN01C");
        model.addAttribute("transactionName", "CT01");

        return "transactions/transactionView";
    }

    // @GetMapping("/transactions/add")
    // public String showAddTransactionForm(Model model) {
    // model.addAttribute("transaction", new Transaction());
    // // Add date and time to the model, similar to listTransactions
    // LocalDateTime now = LocalDateTime.now();
    // DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd-yy");
    // DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    // model.addAttribute("currentDate", now.format(dateFormatter));
    // model.addAttribute("currentTime", now.format(timeFormatter));
    // model.addAttribute("programName", "COTRN02C");
    // model.addAttribute("transactionName", "CT02");

    // return "transactions/addTransaction";
    // }

    @GetMapping("/transactions/add")
    public String showAddTransactionForm(Model model, @RequestParam(required = false) String copyFrom) {
        Transaction transaction = new Transaction();

        if (copyFrom != null) {
            Transaction sourceTransaction = transactionService.getTransactionById(copyFrom);
            if (sourceTransaction != null) {
                // Copy properties (except ID)
                transaction.setCardNumber(sourceTransaction.getCardNumber());
                transaction.setTypeCode(sourceTransaction.getTypeCode());
                transaction.setCategoryCode(sourceTransaction.getCategoryCode());
                transaction.setSource(sourceTransaction.getSource());
                transaction.setDescription(sourceTransaction.getDescription());
                transaction.setAmount(sourceTransaction.getAmount());
                transaction.setOriginalTimestamp(sourceTransaction.getOriginalTimestamp());
                transaction.setProcessedTimestamp(sourceTransaction.getProcessedTimestamp());
                transaction.setMerchantId(sourceTransaction.getMerchantId());
                transaction.setMerchantName(sourceTransaction.getMerchantName());
                transaction.setMerchantCity(sourceTransaction.getMerchantCity());
                transaction.setMerchantZip(sourceTransaction.getMerchantZip());

            }
        }

        model.addAttribute("transaction", transaction);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd-yy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        model.addAttribute("currentDate", now.format(dateFormatter));
        model.addAttribute("currentTime", now.format(timeFormatter));
        model.addAttribute("programName", "COTRN02C");
        model.addAttribute("transactionName", "CT02");

        return "transactions/transactionAdd";
    }

    @PostMapping("/transactions/add")
    public String addTransaction(@ModelAttribute Transaction transaction, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Invalid date format. Use YYYY-MM-DD.");
            return "transactions/transactionAdd";
        }
        try {
            if (transaction.getOriginalTimestamp() == null) {
                transaction.setOriginalTimestamp(LocalDateTime.parse(transaction.getOriginalTimestamp().toString(),
                        DateTimeFormatter.ISO_DATE_TIME));
            }
        } catch (DateTimeParseException e) {
            model.addAttribute("errorMessage", "Invalid date format for Original Date.  Use YYYY-MM-DD.");
            return "transactions/transactionAdd";
        }
        try {
            if (transaction.getProcessedTimestamp() == null) {
                transaction.setProcessedTimestamp(LocalDateTime.parse(transaction.getProcessedTimestamp().toString(),
                        DateTimeFormatter.ISO_DATE_TIME));
            }
        } catch (DateTimeParseException e) {
            model.addAttribute("errorMessage", "Invalid date format for Processed Date. Use YYYY-MM-DD.");
            return "transactions/transactionAdd";
        }
        transactionService.createTransaction(transaction);
        return "redirect:/transactions";
    }

    @PutMapping("/transactions/{id}")
    public String updateTransaction(@PathVariable("id") String id, @ModelAttribute Transaction transaction,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "There was an error updating the transaction.");
            return "transactions/transactionView";
        }

        try {
            if (transaction.getOriginalTimestamp() != null) {
                transaction.setOriginalTimestamp(LocalDateTime.parse(transaction.getOriginalTimestamp().toString(),
                        DateTimeFormatter.ISO_DATE_TIME));
            }
            if (transaction.getProcessedTimestamp() != null) {
                transaction.setProcessedTimestamp(LocalDateTime.parse(transaction.getProcessedTimestamp().toString(),
                        DateTimeFormatter.ISO_DATE_TIME));
            }
        } catch (DateTimeParseException e) {
            model.addAttribute("errorMessage", "Invalid Date Format");
            return "transactions/transactionView";
        }

        transaction.setId(id);
        Transaction updatedTransaction = transactionService.updateTransaction(id, transaction);

        if (updatedTransaction == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found");
        }
        return "redirect:/transactions/" + id;
    }

    @GetMapping("/transactions/report")
    public String showTransactionReportForm(Model model) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        model.addAttribute("currentDate", now.format(dateFormatter));
        model.addAttribute("currentTime", now.format(timeFormatter));
        model.addAttribute("programName", "CORPT00C");
        model.addAttribute("transactionName", "CR00");
        return "transactions/transactionReport";
    }

    @GetMapping("/transactions/showreport")
    public String showReport(
            @RequestParam("reportType") String reportType,
            @RequestParam(value = "startMonth", required = false) String startMonth,
            @RequestParam(value = "startDay", required = false) String startDay,
            @RequestParam(value = "startYear", required = false) String startYear,
            @RequestParam(value = "endMonth", required = false) String endMonth,
            @RequestParam(value = "endDay", required = false) String endDay,
            @RequestParam(value = "endYear", required = false) String endYear,
            @RequestParam("confirm") String confirm,
            Model model) {

        if (!"Y".equalsIgnoreCase(confirm)) {
            model.addAttribute("errorMessage", "Confirmation must be 'Y' to generate the report.");
            return "transactions/transactionReport";
        }

        try {
            LocalDateTime startDate;
            LocalDateTime endDate = LocalDateTime.now();

            switch (reportType) {
                case "monthly":
                    startDate = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                    endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(23, 59, 59);
                    break;
                case "yearly":
                    startDate = LocalDate.now().withDayOfYear(1).atStartOfDay();
                    endDate = LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear()).atTime(23, 59, 59);
                    break;
                case "custom":
                    startDate = parseDate(startMonth, startDay, startYear);
                    endDate = parseDate(endMonth, endDay, endYear).withHour(23).withMinute(59).withSecond(59);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid report type.");
            }

            List<Transaction> transactions = transactionService.getTransactionsByDateRange(startDate, endDate);

            LocalDateTime now = LocalDateTime.now();
            model.addAttribute("currentDate", now.format(DateTimeFormatter.ofPattern("MM/dd/yy")));
            model.addAttribute("currentTime", now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            model.addAttribute("programName", "CORPT01C");
            model.addAttribute("transactionName", "CR01");
            model.addAttribute("transactions", transactions);

            return "transactions/showReport";

        } catch (DateTimeParseException | IllegalArgumentException e) {
            model.addAttribute("errorMessage", "Invalid date input. Please use valid MM/DD/YYYY format.");
            return "transactions/transactionReport";
        }
    }

    private LocalDateTime parseDate(String month, String day, String year) throws DateTimeParseException {
        String dateStr = String.format("%s/%s/%s", month, day, year);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        return LocalDate.parse(dateStr, formatter).atStartOfDay();
    }

    // --- REST API Endpoints ---
    @PostMapping("/api")
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        Transaction savedTransaction = transactionService.createTransaction(transaction);
        return new ResponseEntity<>(savedTransaction, HttpStatus.CREATED);
    }

    @GetMapping("/api")
    public ResponseEntity<List<Transaction>> getAllTransactionsApi() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> transactionPage = transactionService.getAllTransactions(pageable);
        return new ResponseEntity<>(transactionPage.getContent(), HttpStatus.OK);
    }

    @GetMapping("/api/{id}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable String id) {
        Transaction transaction = transactionService.getTransactionById(id);
        return transaction != null
                ? new ResponseEntity<>(transaction, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/api/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable String id,
            @RequestBody Transaction transaction) {
        Transaction updatedTransaction = transactionService.updateTransaction(id, transaction);
        return updatedTransaction != null
                ? new ResponseEntity<>(updatedTransaction, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/api/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable String id) {
        transactionService.deleteTransaction(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/api/last")
    public ResponseEntity<Transaction> getLastTransaction() {
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "id"));
        Page<Transaction> transactionPage = transactionService.getAllTransactions(pageable);
        if (transactionPage.hasContent()) {
            return new ResponseEntity<>(transactionPage.getContent().get(0), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}