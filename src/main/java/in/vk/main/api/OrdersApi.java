package in.vk.main.api;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import in.vk.main.entities.Orders;
import in.vk.main.services.OrderService;

@RestController
@RequestMapping("/api")
public class OrdersApi {

    @Autowired
    private in.vk.main.services.OrderService orderService;

    @Autowired
    private in.vk.main.repositories.UserRepository userRepository;

    @Value("${razorpay.key_id}")
    private String key;

    @Value("${razorpay.key_secret}")
    private String secret;

    @Autowired
    private in.vk.main.services.InvoiceService invoiceService;

    @Autowired
    private in.vk.main.services.EmailService emailService;

    @PostMapping("/storeOrderDetails")
    public ResponseEntity<String> storeUserOrdersDetails(@RequestBody Orders orders) throws RazorpayException {

        RazorpayClient razorpayClient = new RazorpayClient(key, secret);

        JSONObject orderRequest = new JSONObject();

        int amountInPaise = (int)(Double.parseDouble(orders.getCourseAmount()) * 100);

        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "rcpt_id_" + System.currentTimeMillis());

        Order order = razorpayClient.orders.create(orderRequest);

        return ResponseEntity.ok(order.toString());
    }

    @PostMapping("/paymentSuccess")
    public ResponseEntity<String> paymentSuccess(@RequestBody Orders orders) {
        System.out.println("DEBUG: paymentSuccess called for course: " + orders.getCourseName() + " by user: " + orders.getUserEmail());
        try {
            // 0. Check if course already purchased by this user to prevent duplicates
            Orders existingOrder = orderService.findOrderByUserAndCourse(orders.getUserEmail(), orders.getCourseName());
            if (existingOrder != null) {
                System.out.println("DEBUG: Course already purchased. Skipping duplicate entry.");
                return ResponseEntity.ok("{\"status\": \"already_purchased\"}");
            }

            // 1. Set purchase date and time
            java.time.LocalDate ld = java.time.LocalDate.now();
            String pdate = ld.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy"));
            java.time.LocalTime lt = java.time.LocalTime.now();
            String ptime = lt.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm:ss a"));
            orders.setDateOfPurchase(pdate + ", " + ptime);

            // 2. Save order to database
            System.out.println("DEBUG: Storing order in DB...");
            orderService.storeUserOrders(orders);
            System.out.println("DEBUG: Order stored successfully.");

            // 3. Generate Invoice and Send Email
            System.out.println("DEBUG: Generating invoice...");
            in.vk.main.entities.User user = userRepository.findFirstByEmail(orders.getUserEmail());
            if (user != null) {
                byte[] invoicePdf = invoiceService.generateInvoiceAsByteArray(orders, user);
                
                System.out.println("DEBUG: Sending email with attachment...");
                String subject = "Your Invoice for " + orders.getCourseName() + " | VK Academy";
                String body = "Hi " + user.getName() + ",\n\n" +
                              "Thank you for enrolling in " + orders.getCourseName() + ".\n" +
                              "We have attached your invoice for your reference.\n\n" +
                              "Best Regards,\n" +
                              "VK Academy Team";
                
                emailService.sendEmailWithAttachment(user.getEmail(), subject, body, invoicePdf, "Invoice_" + orders.getCourseName().replace(" ", "_") + ".pdf");
                System.out.println("DEBUG: Email sent successfully.");
            }

            return ResponseEntity.ok("{\"status\": \"success\"}");
        } catch (Exception e) {
            System.err.println("ERROR: paymentSuccess failed!");
            e.printStackTrace();
            return ResponseEntity.status(500).body("{\"status\": \"error\"}");
        }
    }
}
