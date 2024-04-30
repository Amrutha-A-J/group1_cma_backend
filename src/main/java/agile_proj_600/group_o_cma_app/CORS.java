    package agile_proj_600.group_o_cma_app;

    import java.sql.Connection;
    import java.sql.DriverManager;
    import java.sql.SQLException;
    import org.springframework.context.annotation.Bean;
    import org.springframework.web.cors.CorsConfiguration;
    import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
    import org.springframework.web.filter.CorsFilter;

    public class CORS {

        @Bean
        public CorsFilter corsFilter() {
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            CorsConfiguration config = new CorsConfiguration();
            config.addAllowedOrigin("http://localhost:3000"); // Allow requests from your frontend
            config.addAllowedMethod("*"); // Allow all HTTP methods
            config.addAllowedHeader("*"); // Allow all headers
            source.registerCorsConfiguration("/**", config);
            return new CorsFilter(source);
        }

        // Method to establish a connection to the database
        public static Connection getConnection() {
            Connection conn = null;
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + System.getenv("DB_NAME"),
                        System.getenv("DB_USER"),
                        System.getenv("DB_PASSWORD"));
            } catch (ClassNotFoundException | SQLException e) {
                System.out.println("DB connection failed with error: " + e.getMessage());
            }
            return conn;
        }
    }
