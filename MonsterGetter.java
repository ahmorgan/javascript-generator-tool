import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;

import org.json.*;
import java.io.FileWriter;


public class MonsterGetter {
    
    public static String HTMLcall(String url) throws URISyntaxException, IOException, InterruptedException {
        
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(url))
            .GET()
            .build();

        HttpResponse<String> response = client.send(request,
            BodyHandlers.ofString());

        return response.body();

    }
    
    public static void fileWriter(String data) {
        try {
            FileWriter myWriter = new FileWriter("codegen.txt");
                
            myWriter.write(data);

            myWriter.flush();
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static String buildMonsterOutput(JSONObject detail) {
        StringBuilder monsterOutput = new StringBuilder();
        monsterOutput.append("new SRDMonster(");
        String monsterIndex = (String)(detail.get("slug"));
        String monsterName2 = (String)(detail.get("name"));
        String monsterHPR = (String)(detail.get("hit_dice"));
        String monsterDiceNum = numOfDice(monsterHPR);
        String monsterDieMax = dieMax(monsterHPR);
        String monsterDieMod = mod(monsterHPR);
        //using toString to parse Integers
        String monsterDex = (detail.get("dexterity")).toString(); 
        String monsterDexModifier = dexModifierGenerator(monsterDex);
        String monsterCR = (detail.get("cr")).toString();
        String monsterAC = (detail.get("armor_class")).toString();
        
        
        monsterOutput.append("\"" + monsterIndex + "\"");
        monsterOutput.append(", ");
        monsterOutput.append("\"" + monsterName2 + "\"");
        monsterOutput.append(", ");
        monsterOutput.append(monsterDiceNum + ", ");
        monsterOutput.append(monsterDieMax + ", ");
        monsterOutput.append(monsterDieMod + ", ");
        monsterOutput.append(monsterDexModifier + ", ");
        monsterOutput.append(monsterCR + ", ");
        monsterOutput.append(monsterAC);
        monsterOutput.append("),");

        return monsterOutput.toString();


    }

    

    public static String numOfDice(String monsterHPR) {
        int index = monsterHPR.indexOf("d");
        return monsterHPR.substring(0, index);
    }

    public static String dieMax(String monsterHPR) {
        int index = monsterHPR.indexOf("d");
        int plusIndex = monsterHPR.indexOf("+");
        int minusIndex = monsterHPR.indexOf("-");
        if (minusIndex != -1) {
            return monsterHPR.substring(index+1, minusIndex);
        }
        if (plusIndex == -1) {
            return monsterHPR.substring(index+1);
        }
        return monsterHPR.substring(index+1, plusIndex);
    }

    public static String mod(String monsterHPR) {
        int plusIndex = monsterHPR.indexOf("+");
        int minusIndex = monsterHPR.indexOf("-");

        if (minusIndex >= 0) {
            return monsterHPR.substring(minusIndex);
        }
        
        if (plusIndex == -1) {
            return "0";
        }

        
        return monsterHPR.substring(plusIndex+1);
    }

    public static String dexModifierGenerator(String monsterDex) {
        if (monsterDex.equals("1"))
            return "-5";
        if (monsterDex.equals("2") || monsterDex.equals("3"))
            return "-4";
        if (monsterDex.equals("4") || monsterDex.equals("5"))
            return "-3";
        if (monsterDex.equals("6") || monsterDex.equals("7"))
            return "-2";
        if (monsterDex.equals("8") || monsterDex.equals("9"))
            return "-1";
        if (monsterDex.equals("10") || monsterDex.equals("11"))
            return "0";
        if (monsterDex.equals("12") || monsterDex.equals("13"))
            return "1";
        if (monsterDex.equals("14") || monsterDex.equals("15"))
            return "2";
        if (monsterDex.equals("16") || monsterDex.equals("17"))
            return "3";
        if (monsterDex.equals("18") || monsterDex.equals("19"))
            return "4";
        if (monsterDex.equals("20") || monsterDex.equals("21"))
            return "5";
        if (monsterDex.equals("22") || monsterDex.equals("23"))
            return "6";
        if (monsterDex.equals("24") || monsterDex.equals("25"))
            return "7";
        if (monsterDex.equals("26") || monsterDex.equals("27"))
            return "8";
        if (monsterDex.equals("28") || monsterDex.equals("29"))
            return "9";
        return "10";
    }

    public static PageResult getPage(String baseURL, String pageNumber) throws URISyntaxException, IOException, InterruptedException {
        String json = HTMLcall(baseURL + "/monsters/" + pageNumber);
        JSONObject jsonObject = new JSONObject(json);

        String nextPage = null;

        if (!jsonObject.isNull("next")){
            nextPage = (String)jsonObject.get("next");
        }
        

        JSONArray result = (JSONArray)jsonObject.get("results");
        StringBuilder output = new StringBuilder();
        
        
        for (Object objectMonster : result) {
                JSONObject jsonMonster = (JSONObject)objectMonster;
                String monsterName = (String)jsonMonster.get("slug");
                output.append("\t " + "'" + monsterName + "'" + ": " + buildMonsterOutput(jsonMonster) + " \r\n");

        }
        
        PageResult pageResult = new PageResult(output.toString(), nextPage);

        return pageResult;
    }
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        String baseURL = "https://api.open5e.com";
        StringBuilder output = new StringBuilder();
        output.append("export const MonsterData: { [key: string]: SRDMonster } = {" + " \r\n");
        PageResult pageResult = getPage(baseURL, "?page=1");
        output.append(pageResult.getPageOutput());
        String nextPage = pageResult.getNextPage();
        System.out.println("Successfully wrote page 1");
        int x = 2;

        
        while (nextPage != null) {
            pageResult = getPage(baseURL, "?page=" + x);
            output.append(pageResult.getPageOutput());
            nextPage = pageResult.getNextPage();
            System.out.println("Successfully wrote page " + x);
            x++;
        }
        
        output.append("}");
        fileWriter(output.toString());
        
        
    }
}


