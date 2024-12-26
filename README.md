# BrowserStack_-Assignment
A Java Selenium project for cross-browser testing via BrowserStack. It navigates to El País, scrapes Opinion section articles, translates titles using an API, analyzes repeated words, and downloads cover images. Supports parallel execution across desktop and mobile browsers for robust testing.

### Detailed README File

# BrowserStack Cross-Browser Testing Project

## Overview

This project is a Java-based automation solution designed for cross-browser testing using Selenium and BrowserStack. It performs the following tasks:

1. **Visit El País Website**: Ensures the website is displayed in Spanish.
2. **Scrape Opinion Section**: Fetches the first five articles, their titles, and content. Downloads cover images if available.
3. **Translate Article Titles**: Uses an API to translate article titles from Spanish to English.
4. **Analyze Translated Titles**: Identifies repeated words across translated titles and provides their count.
5. **Cross-Browser Testing**: Executes tests in parallel on different desktop and mobile browsers using BrowserStack.

## Features

- **Automated Web Scraping**: Extracts article data and content dynamically.
- **Image Handling**: Downloads cover images of articles when available.
- **Translation Support**: Integrates with a translation API to process Spanish article titles.
- **Parallel Execution**: Tests are run across 5 browser configurations simultaneously for efficiency.
- **Cross-Browser Compatibility**: Ensures functionality on both desktop and mobile platforms.


## Requirements

### Tools and Technologies

- **Programming Language**: Java
- **Browser Automation**: Selenium WebDriver
- **Cross-Browser Testing**: BrowserStack
- **HTTP Client**: Java's HttpClient
- **Dependency Management**: Maven (optional for managing libraries)

## Setup and Installation

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-repo-name/browserstack-cross-browser-testing.git
   cd browserstack-cross-browser-testing
   ```

2. **Add Dependencies**:
   Ensure you have the required libraries for Selenium and HTTP Client. If using Maven, include these in `pom.xml`:
   ```xml
   <dependency>
       <groupId>org.seleniumhq.selenium</groupId>
       <artifactId>selenium-java</artifactId>
       <version>4.0.0</version>
   </dependency>
   ```

3. **Set Environment Variables**:
   Replace the placeholders in the code with your **BrowserStack Username**, **Access Key**, and **RapidAPI Key**.

   ```java
   public static final String USERNAME = "your_browserstack_username";
   public static final String ACCESS_KEY = "your_browserstack_access_key";
   public static final String RAPIDAPI_KEY = "your_rapidapi_key";
   ```

4. **Compile and Run**:
   Use your preferred IDE (e.g., IntelliJ IDEA, Eclipse) or compile via terminal:
   ```bash
   javac BrowserStackTest.java
   java BrowserStackTest
   ```

## How It Works

### Steps Performed by the Code

1. **Initialize BrowserStack Configurations**:
   - Desktop browsers (e.g., Chrome, Firefox, Edge)
   - Mobile devices (e.g., Vivo Y21, Samsung Galaxy S23 Ultra)

2. **Navigate to El País**:
   - Opens the website and confirms the page title.

3. **Handle Cookie Consent**:
   - Accepts cookies if prompted.

4. **Scrape Articles**:
   - Navigates to the Opinion section.
   - Extracts titles, summaries, and downloads images for the first five articles.

5. **Translate Titles**:
   - Sends titles to a translation API and receives translations in English.

6. **Analyze Translated Titles**:
   - Identifies repeated words and displays their frequency.

7. **Run in Parallel**:
   - Executes tests simultaneously across different configurations for faster results.

## Output

1. **Console Logs**:
   - Displays navigation progress, scraped data, translated titles, and repeated word counts.

2. **Downloaded Files**:
   - Article images saved locally in the project directory.

## Future Improvements

- Add error handling for network issues and missing elements.
- Expand the scope to fetch more articles and sections.
- Integrate additional APIs for language detection and sentiment analysis.
