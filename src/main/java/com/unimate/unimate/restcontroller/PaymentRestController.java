package com.unimate.unimate.restcontroller;


import com.midtrans.Midtrans;
import com.midtrans.httpclient.SnapApi;
import com.midtrans.httpclient.error.MidtransError;
import com.unimate.unimate.aspect.ValidateToken;
import com.unimate.unimate.dto.CreatePaymentSnapDTO;
import com.unimate.unimate.dto.PaymentNotificationDTO;
import com.unimate.unimate.dto.SalesPercentageDTO;
import com.unimate.unimate.dto.SnapTokenDTO;
import com.unimate.unimate.entity.Account;
import com.unimate.unimate.entity.Course;
import com.unimate.unimate.entity.Kelas;
import com.unimate.unimate.entity.Payment;
import com.unimate.unimate.enums.RoleEnum;
import com.unimate.unimate.service.AccountService;
import com.unimate.unimate.service.CourseService;
import com.unimate.unimate.service.KelasService;
import com.unimate.unimate.service.PaymentService;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties.Http;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

import java.util.Map;
import java.util.UUID;
import java.util.Date;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@RestController
@RequestMapping("/api/payment")
public class PaymentRestController {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private AccountService accountService;

    @Autowired
    private KelasService kelasService;
    private static final String JWT_HEADER = "Authorization";
    
    

    private final String midtransServerKey="SB-Mid-server-q1RrOIuuao_goc45qjsbh4eQ";


    private final String midtransClientKey="SB-Mid-client-4zoHiOpZCb-BTIQx";

    @PostMapping("checkout")
    @ValidateToken({RoleEnum.STUDENT})
    public ResponseEntity<?> checkout(@RequestBody CreatePaymentSnapDTO createPaymentSnapDTO) throws MidtransError {
        Kelas kelas  =  kelasService.getKelasById(createPaymentSnapDTO.getCourse());
        String requestToken = createPaymentSnapDTO.getToken().substring(7);
        Account account = accountService.getAccountFromJwt(requestToken);
        List<Payment> listPayments = paymentService.findBySiswaIdAndCourseId(account.getId(), kelas.getId());

        if (listPayments.size()>0) {
            return ResponseEntity.badRequest().body("Maaf, anda masih memiliki pembayaran tertunda. Cek email anda");

        }
        Midtrans.clientKey = midtransClientKey;
        Midtrans.serverKey = midtransServerKey;
        Payment payment =  new Payment();
        UUID idRand = UUID.randomUUID();
        Map<String, Object> params = new HashMap<>();
        Map<String, String> transactionDetails = new HashMap<>();
        transactionDetails.put("order_id", idRand.toString());
        transactionDetails.put("gross_amount", String.valueOf( createPaymentSnapDTO.getPrice()));
        params.put("transaction_details", transactionDetails);

        payment.setId(idRand);
        
        payment.setCourse(kelas);
        payment.setDate(new Date());
        payment.setPrice(createPaymentSnapDTO.getPrice());
        payment.setStatus("Pending");
       
        payment.setSiswa(account);
        paymentService.savePayment(payment);
        SnapTokenDTO token = new SnapTokenDTO();
        String transactionToken = SnapApi.createTransactionToken(params);
        token.setToken(transactionToken);

        return ResponseEntity.ok(token);
    }

    @GetMapping("/history")
     @ValidateToken({RoleEnum.STUDENT, RoleEnum.ADMIN, RoleEnum.TEACHER, RoleEnum.CUSTOMER_SERVICE, RoleEnum.TOP_LEVEL})
    public ResponseEntity<?> getHistory(HttpServletRequest request) {
        String requestToken = request.getHeader(JWT_HEADER).substring(7);
        Account account = accountService.getAccountFromJwt(requestToken);
        return ResponseEntity.ok( paymentService.getAllPaymentByStudent(account));
    }


    @PostMapping("notification")
    public ResponseEntity<?> Notification(@RequestBody PaymentNotificationDTO paymentNotificationDTO) {
        String transactionIdString = paymentNotificationDTO.getTransaction_id();
        UUID id = UUID.fromString(transactionIdString);
        Payment payment = paymentService.getPayment(id);
        if (payment != null) {
            try {
                String statusCode = paymentNotificationDTO.getStatus_code();
                String gross = paymentNotificationDTO.getGross_amount();
                String signKey = id + statusCode + gross +  midtransServerKey;
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA-512");
                    byte[] messageDigest = md.digest(signKey.getBytes()); 
                    BigInteger no = new BigInteger(1, messageDigest); 
                    String hashtext = no.toString(16); 
                    while (hashtext.length() < 32) { 
                        hashtext = "0" + hashtext; 
                    } 

                    signKey = hashtext;
                } catch (NoSuchAlgorithmException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } 
                
                if (signKey.equals(paymentNotificationDTO.getSignature_key())) {
                    String transactionStatus = paymentNotificationDTO.getTransaction_status();
                    String fraudStatus = paymentNotificationDTO.getFraud_status();
                    if (transactionStatus.equals("capture")) {
                        if (fraudStatus.equals("accept")) {
                            Payment payment2 = paymentService.updatePayment(payment, paymentNotificationDTO.getPayment_type(), Long.parseLong( paymentNotificationDTO.getGross_amount().split("\\.")[0]) );
                            return ResponseEntity.ok().body(paymentNotificationDTO.getPayment_type());
                        }
                    } else if (transactionStatus.equals("settlement")) {
                        Payment payment2 = paymentService.updatePayment(payment, paymentNotificationDTO.getPayment_type(), Long.parseLong( paymentNotificationDTO.getGross_amount().split("\\.")[0]) );
                 
                                return ResponseEntity.ok().body(paymentNotificationDTO.getPayment_type());
                    } else if (transactionStatus.equals("cancel") ||
                            transactionStatus.equals("deny") ||
                            transactionStatus.equals("expire")) {
                                payment.setStatus("Failed");
                                           return ResponseEntity.ok().body(paymentNotificationDTO.getPayment_type());
                    } else if (transactionStatus.equals("pending")) {
                        payment.setMethod(paymentNotificationDTO.getPayment_type());
                    
                                  return ResponseEntity.ok().body(paymentNotificationDTO.getPayment_type());
                    }
                    
                    return null;
                } else{  
                    String errorMessage = "Invalid transaction Signature Key : " + signKey;
                    return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(errorMessage);}
            } catch (IllegalArgumentException e) {
                System.out.println(e);
            }
        } else {
            String errorMessage = "Invalid transaction ID";
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorMessage);
        }
        return null;


    }
    

    @GetMapping("/today")
    @ValidateToken({RoleEnum.STUDENT, RoleEnum.ADMIN, RoleEnum.TEACHER, RoleEnum.CUSTOMER_SERVICE, RoleEnum.TOP_LEVEL})
    public List<Payment> findPayments() {
        return paymentService.findPaymentToday();
    }
    

    @GetMapping("/7days")
    @ValidateToken({RoleEnum.STUDENT, RoleEnum.ADMIN, RoleEnum.TEACHER, RoleEnum.CUSTOMER_SERVICE, RoleEnum.TOP_LEVEL})
    public List<Payment> find7DaysPayments() {
        return paymentService.findPaymentsLast7Days();
    }

    @GetMapping("/30days")
    @ValidateToken({RoleEnum.STUDENT, RoleEnum.ADMIN, RoleEnum.TEACHER, RoleEnum.CUSTOMER_SERVICE, RoleEnum.TOP_LEVEL})
    public List<Payment> find30DaysPayments() {
        return paymentService.findPaymentsLast30Days();
    }


    @GetMapping("/g-today")
    @ValidateToken({RoleEnum.STUDENT, RoleEnum.ADMIN, RoleEnum.TEACHER, RoleEnum.CUSTOMER_SERVICE, RoleEnum.TOP_LEVEL})
    public SalesPercentageDTO findGPayments() {
        return paymentService.findGPaymentToday();
    }

    

    @GetMapping("/g2-today")
    @ValidateToken({RoleEnum.STUDENT, RoleEnum.ADMIN, RoleEnum.TEACHER, RoleEnum.CUSTOMER_SERVICE, RoleEnum.TOP_LEVEL})
    public SalesPercentageDTO findG2Payments() {
        return paymentService.findG2PaymentToday();
    }

}
