package de.upb.cracks.rules;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;


public class RuleParser {

    public ArrayList<Rule> readRules(String source) {

        if (source.isEmpty()) {
            System.out.println("Source is emphty");
            return null;
        }

        ArrayList<Rule> rules = new ArrayList<>();
        File[] files = new File(source).listFiles();

        for (File file : files) {
            if (file.isFile()) {
                String ruleString = readRule(file);
                Rule rule = createRule(ruleString);
                rules.add(rule);
            }
        }
        return rules;
    }

    private String readRule(File file) {
        byte[] encoded = null;
        try {
            encoded = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new String(encoded);
    }

    public Rule createRule(String ruleString) {

        String id = ruleString.substring(ruleString.indexOf("<")+1, ruleString.indexOf(">"));
        String headline = ruleString.substring(ruleString.indexOf("(")+1, ruleString.indexOf(")"));
        System.out.println(id);
        System.out.println(headline);
        String rule = ruleString.substring(ruleString.indexOf(">")+1, ruleString.indexOf("("));

        String regex = rule.replaceAll("\\[[a-zZA-Z]+\\]", "(.*)");
        System.out.println(regex);
        System.out.println("---------");
        return new Rule(Integer.parseInt(id), headline, regex);
    }

}
