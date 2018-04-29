package springbootapi.restapi.controllers

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import springbootapi.objects.ScraperObject

import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


@Controller
class ScraperController {


    private static Properties mailServerProperties;
    private static Session getMailSession;
    private static MimeMessage generateMailMessage;



    private void executeSeleniumSearch(String url, ArrayList<String> emailList){
        ArrayList<ScraperObject> craigslistObjectsLocalToSearch = new ArrayList<>()
        System.setProperty("webdriver.gecko.driver","/Users/genreboy/Downloads/chromedriver.exe");
        WebDriver driver = new ChromeDriver()
        try{
            driver.get(url); // goes to a url
        } catch (Exception ex){
            // ignore the exception and try again
            Thread.wait(20000)// wait 20 seconds
            println("CAUGHT A SELENIUM EXCEPTION BRAJ: "+ex)
            toggleScraperProcess(true) // start over
        }

        // get list of all craigslist search element results
        List<WebElement> resultElements = driver.findElementsByClassName("result-row")
        WebElement titleElement;
        WebElement priceElement;

        for(WebElement element: resultElements){
            if(isElementPresent(By.className("result-title"), element)){
                titleElement = element?.findElement(By.className("result-title"))
            }

            if(isElementPresent(By.className("result-price"), element)){
                priceElement = element?.findElement(By.className("result-price"))
            }

            if(titleElement && priceElement != null){
                println("Title: "+titleElement.getText())
                println("Price: "+priceElement.getText())
                println("Title URL: "+titleElement.getAttribute("href")) // parses out the link
                //println(element.getText())

                // add to the list to send to the email recipients
                craigslistObjectsLocalToSearch.add(new ScraperObject(price:priceElement.getText(), postTitle: titleElement.getText(), url: titleElement.getAttribute("href")))

                // add to database
                //craigslistObjectDao.save(new ScraperObject(price:priceElement.getText(), postTitle: titleElement.getText(), url: titleElement.getAttribute("href")))
            }
        }

        // send the email of the results
//        String emailBody = formatCraigslistObjectsToEmailHTML(craigslistObjectsLocalToSearch)
//        generateAndSendEmail(emailBody, emailList) // takes a string as the body content of email (html formatted)

        driver.quit() // kill the browser brah
    }

    static boolean isElementPresent(By by, WebElement element)
    {
        boolean present
        try {
            element.findElement(by)
            present = true
        } catch (Exception e) {
            present = false
        }
        return present
    }





//    public static void generateAndSendEmail(String dataToSend, ArrayList<String> emailList) throws AddressException, MessagingException {
//
//        // Step1
//        System.out.println("\n 1st ===> setup Mail Server Properties..");
//        mailServerProperties = System.getProperties();
//        mailServerProperties.put("mail.smtp.port", "587");
//        mailServerProperties.put("mail.smtp.auth", "true");
//        mailServerProperties.put("mail.smtp.starttls.enable", "true");
//        mailServerProperties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
//        System.out.println("Mail Server Properties have been setup successfully..");
//
//        // Step2
//        System.out.println("\n\n 2nd ===> get Mail Session..");
//        getMailSession = Session.getDefaultInstance(mailServerProperties, null);
//        generateMailMessage = new MimeMessage(getMailSession);
//
//        for(String email: emailList){
//            generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
//        }
//
//        generateMailMessage.setSubject("china wok take ur order preez how many craigslist results u like rong time");
//        String emailBody = dataToSend;
//        generateMailMessage.setContent(emailBody, "text/html");
//        System.out.println("Mail Session has been created successfully..");
//
//        // Step3
//        System.out.println("\n\n 3rd ===> Get Session and Send mail");
//        Transport transport = getMailSession.getTransport("smtp");
//
//        // Enter your correct gmail UserID and Password
//        // if you have 2FA enabled then provide App Specific Password
//        transport.connect("smtp.gmail.com", "craigslistbabygurl@gmail.com", "scrape11");
//        transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
//        transport.close();
//    }


//
//    private String formatCraigslistObjectsToEmailHTML(ArrayList<CraigslistObject> craigslistObjects){
//
//        StringBuilder emailHTML = new StringBuilder();
//
//        for(CraigslistObject craigslistObject: craigslistObjects){
//            emailHTML.append("<h2>" + craigslistObject.getPostTitle() + "</h2>")
//            emailHTML.append("<h2>" + craigslistObject.getPrice() + "</h2>")
//            emailHTML.append("<a href=\""+craigslistObject.getUrl()+"\">" + "<h4>" + craigslistObject.getUrl() + "</h4>" + "</a>")
//            emailHTML.append("<hr>") // adds a line under each result
//        }
//
//        return emailHTML.toString();
//    }

//    private void executeSendToKafkaProducer(String url, ArrayList<String> emailList){
//
//        kafkaProducerClass.sendCraigslistDataToKafka(url, emailList.get(0));
//
//    }

    /* Sample requests on the 2 lines below baby */
    //  http://localhost:8080/CraigslistScraper/activate/?json={"activate":"yes"}
    //  http://localhost:8080/CraigslistScraper/activate/?json={"activate":"no"}
    // curl --data "json={"activate":"yes"}" http://localhost:8080/CraigslistScraper/activate
    @RequestMapping(value = "/CraigslistScraper/activate", method = RequestMethod.POST)
    @ResponseStatus(value=org.springframework.http.HttpStatus.OK)
    void activateCraigslistScraper(@RequestParam("json") String json) {

        if(json.contains("yes")){
            toggleScraperProcess(true)
        }

        if(json.contains("no")){
            toggleScraperProcess(false)
        }
    }


    private void toggleScraperProcess(boolean activateScraper){
        int searchObjectCounter = 0;
        int maxRunCounter = 0;
        int maxRun = 10;
        ArrayList<SearchObject> searchObjectArrayList = new ArrayList<>();

        ArrayList<String> emailListCars = new ArrayList<>();
        emailListCars.add("cam.malia92@gmail.com")
        emailListCars.add("unwoundcracker@gmail.com")

        SearchObject searchObject = new SearchObject()
        searchObject.setUrl("https://minneapolis.craigslist.org/search/sso?query=audi+a8l&max_price=5000")
        searchObject.setEmailList(emailListCars)

        SearchObject searchObject1 = new SearchObject()
        searchObject1.setUrl("https://minneapolis.craigslist.org/search/sso?query=bmw+convertible&max_price=5000")
        searchObject1.setEmailList(emailListCars)

        SearchObject searchObject2 = new SearchObject()
        searchObject2.setUrl("https://minneapolis.craigslist.org/search/sso?query=saab+convertible&max_price=5000")
        searchObject2.setEmailList(emailListCars)

        searchObjectArrayList.add(searchObject)
        searchObjectArrayList.add(searchObject1)
        searchObjectArrayList.add(searchObject2)



        while(activateScraper){

            if(maxRunCounter < maxRun){
                println("scrape executed: " + maxRunCounter + " --> times");
                Thread.sleep(4000); // 4 seconds
                if(maxRunCounter >= searchObjectArrayList.size()){
                    searchObjectCounter = 0; // reset the object counter to 0 if the searchObjects index gets too high and will throw null pointer
                }
                executeSeleniumSearch(searchObjectArrayList.get(searchObjectCounter).getUrl(), searchObjectArrayList.get(searchObjectCounter).getEmailList());

//                executeSendToKafkaProducer(searchObjectArrayList.get(searchObjectCounter).getUrl(), searchObjectArrayList.get(searchObjectCounter).getEmailList())

                searchObjectCounter ++;
                maxRunCounter ++;

            } else {
                println("scraper ended because it reached maxRuns: " + maxRunCounter)
                break;
            }
        }
    }


    private class SearchObject{
        String url
        ArrayList<String> emailList
    }
}
