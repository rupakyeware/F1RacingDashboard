import com.f1.dashboard.F1DashboardApplication;
import org.springframework.boot.SpringApplication;

/**
 * Main entry point for F1 Dashboard Web Application
 * Delegates to the Spring Boot application class
 */
public class MainWebApplication {
    public static void main(String[] args) {
        System.out.println("Starting F1 Dashboard Web Application...");
        // Run the Spring Boot application with the web server host set to 0.0.0.0
        System.setProperty("server.address", "0.0.0.0");
        System.setProperty("server.port", "5000");
        SpringApplication.run(F1DashboardApplication.class, args);
    }
}