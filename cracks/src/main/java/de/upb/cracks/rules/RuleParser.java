package de.upb.cracks.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class RuleParser {

    private Rule parseLine(String line){

        String[] split = line.split("#");

        if(split.length < 2){
            System.out.println("Wrong rule: "+line);
            return null;
        }

        String id = split[1];
        String reg = split[0];

        List<String> types = new ArrayList<>();

        String regex = "";
        String typeBuffer = "";

        int state = 0;
        for(int i = 0; i < reg.length(); i++){
            char c = reg.charAt(i);

            switch (state){

                case 0:
                    if(c == '['){
                        regex += "(.*)";
                        state = 1;
                    }else if(c == '.'){
                        regex += "\\.";
                    }else{
                        regex += c;
                    }
                    break;
                case 1:
                    if(c == ']'){
                        types.add(typeBuffer);
                        typeBuffer = "";
                        state = 0;
                    }else{
                        typeBuffer += c;
                    }
                    break;
                    
                default:
                	break;

            }

        }

        if(types.size() != 2){
            System.out.println("Detect more than two types: "+line);
            return null;
        }

        return new Rule(
                id, Pattern.compile(regex), types.get(0), types.get(1)
        );
    }

    public RuleMatcher compile(String serial){
        List<Rule> rules = new ArrayList<>();

        Scanner scanner = new Scanner(serial);

        while(scanner.hasNextLine()){
            Rule r = this.parseLine(scanner.nextLine());
            if(r != null)
                rules.add(r);
        }
        
        scanner.close();

        return new RuleMatcher(rules);
    }


}
