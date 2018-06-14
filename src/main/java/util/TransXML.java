package util;

import javax.xml.ws.Action;
import java.util.ArrayList;

public class TransXML {
    public ArrayList Rule1Apply(ArrayList input){
        ArrayList out = new ArrayList();
        int counter=0;
        while (counter<input.size()){
            String parse = input.get(counter++).toString();
            while (containDesc(parse))  parse =cutted(parse);
            if (!parse.equals("")) out.add(parse);
        }
        return out;
    }
    public boolean containDesc(String input){
        if (input.indexOf("<?")!=-1) return true;
        else return false;
    }

    public int getStartedPos(String input){
        return input.indexOf("<?");
    }

    public int getFinisherPos(String input){
        return input.indexOf("?>");
    }

    String cutted(String input){
        return input.substring(getFinisherPos(input)+2);
    }

    public ArrayList Rule1ApplyRemoveSpaces(ArrayList input){
        ArrayList out = new ArrayList();
        int counter=0;
        while (counter<input.size()){
            String parse = input.get(counter++).toString();
            String clearedInfo="";
            while (getOpenPos(parse)>0) {
                parse =CutAndReturnCutted(parse,getOpenPos(parse),getClosedPos(parse)).get(1).toString();
                System.out.println("parse="+parse);
                clearedInfo+=CutAndReturnCutted(parse,getOpenPos(parse),getClosedPos(parse)).get(0).toString();
                System.out.println("clear="+clearedInfo);
            }
            System.out.println(clearedInfo);
            out.add(clearedInfo);
        }
        return out;
    }

    public ArrayList CutAndReturnCutted(String input, int cut1, int cut2){
        ArrayList result=new ArrayList();
        String resultStr= input.substring(cut1, cut2+1);
        String inputMod = input.substring(0, cut1)+input.substring(cut2+1, input.length());
        result.add(resultStr);
        result.add(inputMod);
        return result;
    }

    public int getOpenPos(String input){
        return input.indexOf("<");
    }

    public int getClosedPos(String input){
        return input.indexOf(">");
    }
    public ArrayList removefloatSpaces(ArrayList input){
        ArrayList result=new ArrayList();
        for (int i=0;i<input.size();i++)            result.add(removefloatSpaces(input.get(i).toString()));
        return result;
    }

    @Action
    public String removefloatSpaces(String input){
        String process = input;
        int k=0;
        int len=process.length();
        while (k<len){
            int pos = process.indexOf(' ');
            if (pos==0) {process = process.substring(1); k++; continue;}
            boolean openedFlag = false;
            boolean removeIt=false;
            for (int i=pos;i>0;i--){
                if ((process.charAt(i)=='>')&&(openedFlag==false)) {removeIt=true; break;}
                if ((process.charAt(i)=='<')) {openedFlag=true; break;}
            }
            if (removeIt) process = process.substring(0,pos)+process.substring(pos+1,process.length());
            len=process.length();
            k++;
        }
        return process;
    }

    @Action
    public String removeBackSlash(String input){
        String process="";
        int pos = input.indexOf("/>");
        if (pos ==-1)        return process;
        String tagName="";
        int tracer = -1;
        boolean startTag=false;
        for (int i=pos;i>=0;i--) {
            if (input.charAt(i) == '<') {
                tracer = i;
                break;
            }
        };
        int index = tracer+1;
        while ((input.charAt(index)!=' ') ) {
            if (input.charAt(index)=='/') break;
            tagName+=input.charAt(index);
            index++;
        }
        String finished = "></"+tagName+">";
        for (int i=0; i<pos;i++){
            process += input.charAt(i);
        }
        process+=finished;
        return process;
    }


}