# 🎓 VK Academy – Professional Education CRM

**VK Academy** is a production-ready, full-stack CRM platform designed for educational institutes. It streamlines course management, student enrollments, and financial tracking through a robust automated ecosystem.

🚀 **Live Demo:** [https://vk-academy.onrender.com](https://vk-academy.onrender.com)

---

## ✨ Key Features

### 🤖 **AI-Powered Assistance**
*   **Gemini AI Integration:** Features a custom educational assistant powered by **Google Gemini AI**, providing students with 24/7 automated support and course guidance.

### 💳 **Financial Ecosystem**
*   **Razorpay Gateway:** Secure, end-to-end payment processing for course enrollments.
*   **Automated Invoicing:** Event-driven system that generates and dispatches professional **PDF Invoices** via email immediately upon successful payment.

### 📊 **Admin Command Center**
*   **Live Analytics:** Real-time data visualization using **Charts.js** to track revenue, enrollment trends, and batch performance.
*   **Advanced Controls:** Administrative tools to manage batches, update promotional tickers, and enforce security policies (including **User Banning** logic).

### ☁️ **Cloud-Native Architecture**
*   **Distributed Hosting:** Application deployed on **Render**, with a decoupled **MySQL** instance managed on **Clever Cloud**.
*   **Persistence:** Integrated **Cloudinary API** for persistent cloud storage of media assets, bypassing ephemeral filesystem limitations.
*   **Communication:** Reliable transaction notifications handled via **Brevo (SMTP)** with asynchronous processing.

---

## 🛠️ Tech Stack

*   **Backend:** Java, **Spring Boot**, Spring Data JPA, Spring Security Crypto.
*   **Frontend:** Thymeleaf, JavaScript (ES6+), **Charts.js**, Bootstrap 5.
*   **Database:** MySQL (Hosted on **Clever Cloud**).
*   **Reporting:** OpenPDF (Automated PDF Generation).
*   **Build Tool:** Maven.

---

## 📂 Project Structure

```text
src/
├── main/
│   ├── java/in/sp/main/
│   │   ├── controllers/    # Web and API Endpoints
│   │   ├── services/       # Business Logic (AI, Invoicing, Payments)
│   │   ├── repository/     # Data Access Layer
│   │   └── entity/         # Database Models
│   └── resources/
│       ├── templates/      # Thymeleaf UI Fragments
│       └── static/         # CSS/JS & Theme Overrides
```

---

## 🔧 Installation & Setup

1.  **Clone the Repository:**
    ```bash
    git clone https://github.com/vishalkcse/VK-Academy.git
    cd VK-Academy
    ```

2.  **Configure Environment Variables:**
    Create an `application.properties` or set your environment variables for:
    *   `SPRING_DATASOURCE_URL`: Your Clever Cloud MySQL URL.
    *   `CLOUDINARY_URL`: Your Cloudinary API credentials.
    *   `GEMINI_API_KEY`: Your Google AI Studio key.
    *   `RAZORPAY_KEY_ID` & `RAZORPAY_KEY_SECRET`.

3.  **Build and Run:**
    ```bash
    mvn clean install
    mvn spring-boot:run
    ```

---

## 🛡️ Security
*   **Password Hashing:** Utilizing Spring Security's crypto modules for industrial-grade user data protection.
*   **Session Management:** Role-based access control (RBAC) ensuring secure separation between Admin, Employee, and Student dashboards.

---

## 👤 Author
**Vishal Kumar**  
*Computer Science & Engineering Student*  
Walchand Institute of Technology, Solapur  
🔗 [LinkedIn](https://linkedin.com/in/vishal-kumar-152451358) | 💻 [GitHub](https://github.com/vishalkcse)

---

### **License**
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
```
