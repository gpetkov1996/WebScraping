package org.example;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

record Player(String name, String id, Integer rating, Integer quickRating) {
}
public class Main {
    public static void main(String[] args) throws IOException {
        // Opening a web browser
        try (WebClient webClient = new WebClient()) {
            // Stop errors from showing
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(false);
            // Get the result from the page
            HtmlPage page = webClient.getPage("https://new.uschess.org/player-search");
            // Return a list of elements (we need only the first one - .get(0))
            HtmlForm form = (HtmlForm) page.getByXPath("//form").get(0);
            HtmlInput displayNameField = form.getInputByName("display_name");
            HtmlInput submitButton = form.getInputByName("op");
            // Typing in the name field
            displayNameField.type("Carlsen");
            // Capture the new page after clicking the submit button
            HtmlPage resultPage = submitButton.click();
            List<Player> players = parseResults(resultPage);
            for (Player player : players) {
                System.out.println(player);
            }
        }
    }

    private static List<Player> parseResults(HtmlPage resultPage) {
        // Everytime we use getByXPath, it will return a list of elements
        HtmlTable table = (HtmlTable) resultPage.getByXPath("//table").get(0);
        List<Player> players = table.getBodies().get(0).getRows().stream()
                .map(r -> {
                    String rating = r.getCell(2).getTextContent();
                    String qRating = r.getCell(3).getTextContent();
                    return new Player(
                            r.getCell(0).getTextContent(),
                            r.getCell(1).getTextContent(),
                            rating.length() == 0 ? null : Integer.parseInt(rating),
                            qRating.length() == 0 ? null : Integer.parseInt(qRating)
                    );
                }).collect(Collectors.toList());
        return players;
    }
}