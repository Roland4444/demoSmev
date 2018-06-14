package util;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class TransXMLTest {

    @Test
    public void rule1Apply() {
        TransXML trans = new TransXML();
        ArrayList input = new ArrayList();
        input.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        input.add("<?xml-stylesheet type=\"text/css\" href=\"style.css\"?>");
        input.add("<qwe xmlns=\"http://t.e.s.t\">");
        input.add("  <myns:rty xmlns:myns=\"http://y.e.s\">yes!</myns:rty>");
        input.add("  <iop value=\"yes, yes!\"/>");
        input.add("</qwe>");

        ArrayList output = new ArrayList();
        output.add("<qwe xmlns=\"http://t.e.s.t\">");
        output.add("  <myns:rty xmlns:myns=\"http://y.e.s\">yes!</myns:rty>");
        output.add("  <iop value=\"yes, yes!\"/>");
        output.add("</qwe>");

        assertEquals(output, trans.Rule1Apply(input));

    }

    @Test
    public void containDesc() {
        TransXML trans = new TransXML();
        ArrayList input = new ArrayList();
        input.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        input.add("<?xml-stylesheet type=\"text/css\" href=\"style.css\"?>");
        input.add("<qwe xmlns=\"http://t.e.s.t\">");
        assertEquals(true, trans.containDesc(input.get(0).toString()));
        assertEquals(true, trans.containDesc(input.get(1).toString()));
        assertEquals(false, trans.containDesc(input.get(2).toString()));

    }

    @Test
    public void getStartedPos() {
        TransXML trans = new TransXML();
        ArrayList input = new ArrayList();
        input.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

        assertEquals(0, trans.getStartedPos(input.get(0).toString()));


    }

    @Test
    public void getFinisherPos() {
        TransXML trans = new TransXML();
        ArrayList input = new ArrayList();
        input.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        assertEquals(36, trans.getFinisherPos(input.get(0).toString()));
    }

    @Test
    public void cutted() {
        TransXML trans = new TransXML();
        ArrayList input = new ArrayList();
        input.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>bnb");
        assertEquals("bnb", trans.cutted(input.get(0).toString()));

    }

    @Test
    public void getOpenPos() {
        TransXML trans = new TransXML();
        ArrayList input = new ArrayList();
        input.add("<xml>bnb");
        input.add("2<xml>bnb");
        assertEquals(0, trans.getOpenPos(input.get(0).toString()));
        assertEquals(1, trans.getOpenPos(input.get(1).toString()));
    }

    @Test
    public void getClosedPos() {
        TransXML trans = new TransXML();
        ArrayList input = new ArrayList();
        input.add("<xml>bnb");
        input.add("2<xml>bnb");
        assertEquals(4, trans.getClosedPos(input.get(0).toString()));
        assertEquals(5, trans.getClosedPos(input.get(1).toString()));
    }

    @Test
    public void cutAndReturnCutted() {
        String input = "Cutted will be cutting";
        String input2 = "Lets_center_CUTTED";
        TransXML trans = new TransXML();
        assertEquals("Cutted", trans.CutAndReturnCutted(input, 0, 5).get(0));
        assertEquals(" will be cutting", trans.CutAndReturnCutted(input, 0, 5).get(1));
        assertEquals("ets", trans.CutAndReturnCutted(input2, 1, 3).get(0));
        assertEquals("L_center_CUTTED", trans.CutAndReturnCutted(input2, 1, 3).get(1));
    }

    @Test
    public void rule1ApplyRemoveSpaces() {
        TransXML trans = new TransXML();
        ArrayList input = new ArrayList();
        input.add("<qwe xmlns=\"http://t.e.s.t\">");
        input.add("  <myns:rty xmlns:myns=\"http://y.e.s\">yes!</myns:rty>");
        input.add("  <iop value=\"yes, yes!\"/>");
        input.add("</qwe>");
        ArrayList output = new ArrayList();
        output.add("<qwe xmlns=\"http://t.e.s.t\">");
        output.add("<myns:rty xmlns:myns=\"http://y.e.s\">yes!</myns:rty>");
        output.add("<iop value=\"yes, yes!\"/>");
        output.add("</qwe>");
        assertEquals(output, trans.removefloatSpaces(input));
    }

    @Test
    public void removefloatSpaces() {
        TransXML trans = new TransXML();
        assertEquals("<><>n", trans.removefloatSpaces("    <><>n"));
        assertEquals("<>n<>n", trans.removefloatSpaces("<>  n<>n"));
        assertEquals("<>n<>n", trans.removefloatSpaces("<>  n    <>n"));
        assertEquals("<>n<>n", trans.removefloatSpaces("<>n<> n"));
        assertEquals("<>n<>n", trans.removefloatSpaces("<>n<> n    "));
    }


    @Test
    public void removefloatSpaces1() {
    }

    @Test
    public void removeBackSlash() {
        TransXML trans = new TransXML();
        assertEquals("<iop></iop>", trans.removeBackSlash("<iop/>"));
    }
}