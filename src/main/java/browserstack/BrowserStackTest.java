package browserstack;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

public class BrowserStackTest {

    public static final String USERNAME = "kiranyerukala_W6loFj";
    public static final String ACCESS_KEY = "vgCY1hxwqmyiwRTfiCq2";
    public static final String URL = "https://" + USERNAME + ":" + ACCESS_KEY + "@hub-cloud.browserstack.com/wd/hub";

    public static void main(String[] args) {
        // Define browser configurations
        HashMap<String, Object>[] browserConfigs = new HashMap[]{
                createDesktopConfig("Chrome", "120.0", "Windows", "10"),
                createMobileConfig("chrome", "11.0", "Vivo Y21"),
                createDesktopConfig("Firefox", "124.0", "Windows", "11"),
                createMobileConfig("chrome", "13.0", "Samsung Galaxy S23 Ultra"),
                createDesktopConfig("Edge", "122.0", "Windows", "11")
        };

        // Create a thread pool with the number of threads equal to the number of configurations
        ExecutorService executor = Executors.newFixedThreadPool(browserConfigs.length);

        for (HashMap<String, Object> config : browserConfigs) {
            executor.submit(() -> runTest(config));
        }

        // Shutdown the executor after all tasks are completed
        executor.shutdown();
    }

    private static void runTest(HashMap<String, Object> bstackOptions) {
        WebDriver driver = null;

        try {
            MutableCapabilities capabilities = new MutableCapabilities();
            capabilities.setCapability("bstack:options", bstackOptions);
            capabilities.setCapability("browserName", bstackOptions.get("browserName"));

            // Initialize RemoteWebDriver with BrowserStack hub
            driver = new RemoteWebDriver(new URL(URL), capabilities);

            // Test logic starts here
            System.out.println("Running test on: " + bstackOptions.get("browserName") + " on " + bstackOptions.get("os"));
            performCustomLogic(driver);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Quit the driver
            if (driver != null) {
                driver.quit();
            }
        }
    }

    private static void performCustomLogic(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20)); // Increased timeout

        try {
            driver.get("https://elpais.com/");
            System.out.println("Navigated to El Pais. Title: " + driver.getTitle());

            // Handle Cookie Consent if visible
            try {
                WebElement cookieButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@id,'agree')]")));
                cookieButton.click();
                 System.out.println("Cookie consent clicked.");
            } catch (Exception e) {
                System.out.println("Cookie consent not found or already handled.");
            }

            // Handle Mobile Menu
            try {
                WebElement menuButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".hamburger-menu-selector"))); // Replace with actual selector
                if (menuButton.isDisplayed()) {
                    menuButton.click();
                    System.out.println("Hamburger menu opened.");
                }
            } catch (Exception e) {
                System.out.println("No hamburger menu found or not required.");
            }

            // Navigate to Opinion Section
            try {
                WebElement opinionLink = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href*='https://elpais.com/opinion/']")));
                opinionLink.click();
                System.out.println("Navigated to Opinion section.");
            } catch (Exception e) {
                System.out.println("Failed to navigate to Opinion section. Error: " + e.getMessage());
                return;
            }

            // Fetch Articles
            List<WebElement> articleHeadings = driver.findElements(By.cssSelector("article h2"));
            List<WebElement> articleContents = driver.findElements(By.cssSelector("article p"));
            List<WebElement> articles = driver.findElements(By.cssSelector("article"));

            List<String> articleTitles = new ArrayList<>();
            StringBuilder combinedResponses = new StringBuilder();

            for (int i = 0; i < Math.min(5, articleHeadings.size()); i++) {
                String title = articleHeadings.get(i).getText();
                String content = articleContents.size() > i ? articleContents.get(i).getText() : "Content not available";

                System.out.println("--------------------------------------------------------------");
                System.out.println("Article " + (i + 1) + ": " + title);
                System.out.println("Content: " + content);
                System.out.println("--------------------------------------------------------------");

                articleTitles.add(title);

                // Download Image if available
                try {
                    WebElement imageElement = articles.get(i).findElement(By.cssSelector("img"));
                    if (imageElement.isDisplayed()) {
                        String imageUrl = imageElement.getAttribute("src");
                        downloadImage(imageUrl, "Article_" + (i + 1) + ".jpg");
                    }
                } catch (Exception e) {
                    System.out.println("Image not available for Article " + (i + 1));
                }
            }

            // Translate Titles and Analyze
            for (String articleTitle : articleTitles) {
                try {
                    HttpClient client = HttpClient.newHttpClient();
                    String requestBody = "{\"from\":\"es\",\"to\":\"en\",\"e\":\"\",\"q\":[\"" + articleTitle + "\"]}";

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("https://ultra-fast-translation.p.rapidapi.com/t"))
                            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                            .header("Content-Type", "application/json")
                            .header("X-RapidAPI-Key", "c2432b444dmsh909f603234f0c69p1a0e09jsne64b3c285be3")
                            .header("X-RapidAPI-Host", "ultra-fast-translation.p.rapidapi.com").build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    combinedResponses.append(response.body()).append(" ");
                    System.out.println("Translated Title: " + response.body());

                } catch (Exception e) {
                    System.out.println("Translation failed for: " + articleTitle);
                }
            }

            analyzeRepeatedWords(combinedResponses.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void downloadImage(String imageUrl, String fileName) {
        try {
            URL url = new URL(imageUrl);
            BufferedImage image = ImageIO.read(url);
            ImageIO.write(image, "jpg", new File(fileName));
            System.out.println("Image saved as: " + fileName);
        } catch (IOException e) {
            System.err.println("Error downloading image: " + e.getMessage());
        }
    }

    private static void analyzeRepeatedWords(String text) {
        Map<String, Integer> wordCount = new HashMap<>();
        String[] words = text.toLowerCase().replaceAll("[^a-zA-Z\\s]", "").split("\\s+");

        for (String word : words) {
            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        }

        System.out.println("Repeated Words and Counts:");
        wordCount.entrySet().stream().filter(entry -> entry.getValue() > 1)
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
    }

    private static HashMap<String, Object> createDesktopConfig(String browserName, String browserVersion, String os, String osVersion) {
        HashMap<String, Object> bstackOptions = new HashMap<>();
        bstackOptions.put("browserName", browserName);
        bstackOptions.put("browserVersion", browserVersion);
        bstackOptions.put("os", os);
        bstackOptions.put("osVersion", osVersion);
        bstackOptions.put("userName", USERNAME);
        bstackOptions.put("accessKey", ACCESS_KEY);
        bstackOptions.put("consoleLogs", "info");
        return bstackOptions;
    }

    private static HashMap<String, Object> createMobileConfig(String browserName, String osVersion, String deviceName) {
        HashMap<String, Object> bstackOptions = new HashMap<>();
        bstackOptions.put("browserName", browserName);
        bstackOptions.put("osVersion", osVersion);
        bstackOptions.put("deviceName", deviceName);
        bstackOptions.put("userName", USERNAME);
        bstackOptions.put("accessKey", ACCESS_KEY);
        bstackOptions.put("consoleLogs", "info");
        return bstackOptions;
    }
}
