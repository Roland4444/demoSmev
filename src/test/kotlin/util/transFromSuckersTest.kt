package util;

import crypto.Gost3411Hash
import junit.framework.Assert.assertEquals
import org.junit.Assert
import org.junit.Test;
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths



public class transFromSuckersTest {
    @Test
    fun tarnsformtoSign(){
        val test = transFromSuckers()
        val `in` = FileInputStream("SendRequestRequestNoAttach.xml")
        val out = FileOutputStream("TRANS.xml")
        test.process(`in`, out)

    }

    @Test
    fun tarnsformHighligthedData(){
        val test = transFromSuckers()
        val `in` = FileInputStream("1.xml")
        val out = FileOutputStream("TRANS.xml")
        test.process(`in`, out)
    }

    @Test
    fun processini() {
        val test = transFromSuckers()
        val wr = FileWriter("input.xml")
        val write = "<ns2:SenderProvidedRequestData xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.0\" Id=\"SIGNED_BY_CONSUMER\">\n" +
                " <MessagePrimaryContent xmlns=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.0\">\n" +
                "  <SomeRequest:SomeRequest xmlns:SomeRequest=\"urn://x-artifacts-it-ru/vs/smev/test/test-business-data/1.0\">\n" +
                "   <x xmlns=\"urn://x-artifacts-it-ru/vs/smev/test/test-business-data/1.0\">qweqwe</x>\n" +
                "  </SomeRequest:SomeRequest>\n" +
                " </MessagePrimaryContent>\n" +
                "</ns2:SenderProvidedRequestData>"
        wr.write(write)
        wr.close()
        val `in` = FileInputStream("input.xml")
        val out = FileOutputStream("out.xml")
        test.process(`in`, out)
        val contentBuilder = StringBuilder()
        val stream = Files.lines(Paths.get("out.xml"), StandardCharsets.UTF_8)
        run { stream.forEach { s -> contentBuilder.append(s).append("") } }
        val etalon = "<ns1:SenderProvidedRequestData xmlns:ns1=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.0\" Id=\"SIGNED_BY_CONSUMER\"><ns2:MessagePrimaryContent xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.0\"><ns3:SomeRequest xmlns:ns3=\"urn://x-artifacts-it-ru/vs/smev/test/test-business-data/1.0\"><ns3:x>qweqwe</ns3:x></ns3:SomeRequest></ns2:MessagePrimaryContent></ns1:SenderProvidedRequestData>"
        Assert.assertEquals(etalon.length.toLong(), contentBuilder.toString().length.toLong())
        Assert.assertEquals(etalon, contentBuilder.toString())
    }

    @Test
    fun process() {
        val test = transFromSuckers()
        val wr = FileWriter("input.xml")
        val write = "<ns2:SenderProvidedRequestData xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.0\" Id=\"SIGNED_BY_CONSUMER\">\n" +
                " <MessagePrimaryContent xmlns=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.0\">\n" +
                "  <SomeRequest:SomeRequest xmlns:SomeRequest=\"urn://x-artifacts-it-ru/vs/smev/test/test-business-data/1.0\">\n" +
                "   <x xmlns=\"urn://x-artifacts-it-ru/vs/smev/test/test-business-data/1.0\">qweqwe</x>\n" +
                "  </SomeRequest:SomeRequest>\n" +
                " </MessagePrimaryContent>\n" +
                "</ns2:SenderProvidedRequestData>"
        wr.write(write)
        wr.close()
        val `in` = FileInputStream("input.xml")
        val out = FileOutputStream("out.xml")
        test.process(`in`, out)
        val contentBuilder = StringBuilder()
        val stream = Files.lines(Paths.get("out.xml"), StandardCharsets.UTF_8)
        run { stream.forEach { s -> contentBuilder.append(s).append("") } }
        val etalon = "<ns1:SenderProvidedRequestData xmlns:ns1=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.0\" Id=\"SIGNED_BY_CONSUMER\"><ns2:MessagePrimaryContent xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.0\"><ns3:SomeRequest xmlns:ns3=\"urn://x-artifacts-it-ru/vs/smev/test/test-business-data/1.0\"><ns3:x>qweqwe</ns3:x></ns3:SomeRequest></ns2:MessagePrimaryContent></ns1:SenderProvidedRequestData>"
        Assert.assertEquals(etalon, contentBuilder.toString())
    }

    @Test
    fun process2() {
        val test = transFromSuckers()
        val wr = FileWriter("input.xml")
        val write = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!-- Тестирование правил 1, 2, 6: \n" +
                "\t- XML declaration выше, этот комментарий, и следующая за ним processing instruction должны быть вырезаны;\n" +
                "\t- Переводы строки должны быть удалены;\n" +
                "\t- Namespace prefixes заменяются на автоматически сгенерированные.\n" +
                "-->\n" +
                "<?xml-stylesheet type=\"text/xsl\" href=\"style.xsl\"?>\n" +
                "\n" +
                "<elementOne xmlns=\"http://test/1\">\n" +
                "\t<qwe:elementTwo xmlns:qwe=\"http://test/2\">asd</qwe:elementTwo>  \n" +
                "</elementOne>"
        wr.write(write)
        wr.close()
        val `in` = FileInputStream("input.xml")
        val out = FileOutputStream("out.xml")
        test.process(`in`, out)
        val contentBuilder = StringBuilder()
        val stream = Files.lines(Paths.get("out.xml"), StandardCharsets.UTF_8)
        run { stream.forEach { s -> contentBuilder.append(s).append("") } }
        val etalon = "<ns1:elementOne xmlns:ns1=\"http://test/1\"><ns2:elementTwo xmlns:ns2=\"http://test/2\">asd</ns2:elementTwo></ns1:elementOne>"
        Assert.assertEquals(etalon, contentBuilder.toString())
    }


    @Test
    fun processEquals() {
        val test = transFromSuckers()
        val wr = FileWriter("input.xml")
        val example1 = "<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>\\n\" +\n" +
                "                \"<!-- \\n\" +\n" +
                "                \"\\tВсё то же, что в test case 1, плюс правила 3, 7 и 8:\\n\" +\n" +
                "                \"\\t- Атрибуты должны быть отсортированы в алфавитном порядке: сначала по namespace URI (если атрибут - в qualified form), затем – по local name. \\n\" +\n" +
                "                \"\\t\\tАтрибуты в unqualified form после сортировки идут после атрибутов в qualified form.\\n\" +\n" +
                "                \"\\t- Объявления namespace prefix должны находиться перед атрибутами. Объявления префиксов должны быть отсортированы в порядке объявления, а именно:\\n\" +\n" +
                "                \"\\t\\ta.\\tПервым объявляется префикс пространства имен элемента, если он не был объявлен выше по дереву.\\n\" +\n" +
                "                \"\\t\\tb.\\tДальше объявляются префиксы пространств имен атрибутов, если они требуются. \\n\" +\n" +
                "                \"\\t\\t\\tПорядок этих объявлений соответствует порядку атрибутов, отсортированных в алфавитном порядке (см. п.5).\\n\" +\n" +
                "                \"-->\\n\" +\n" +
                "                \"<?xml-stylesheet type=\\\"text/xsl\\\" href=\\\"style.xsl\\\"?>\\n\" +\n" +
                "                \"\\n\" +\n" +
                "                \"<elementOne xmlns=\\\"http://test/1\\\" xmlns:qwe=\\\"http://test/2\\\" xmlns:asd=\\\"http://test/3\\\">\\n\" +\n" +
                "                \"\\t<qwe:elementTwo>\\n\" +\n" +
                "                \"\\t\\t<asd:elementThree xmlns:wer=\\\"http://test/a\\\" xmlns:zxc=\\\"http://test/0\\\" wer:attZ=\\\"zzz\\\" attB=\\\"bbb\\\" attA=\\\"aaa\\\" zxc:attC=\\\"ccc\\\" asd:attD=\\\"ddd\\\" asd:attE=\\\"eee\\\" qwe:attF=\\\"fff\\\"/>\\n\" +\n" +
                "                \"\\t</qwe:elementTwo>  \\n\" +\n" +
                "                \"</elementOne>"
        val example2 = "<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>\\n\" +\n" +
                "                \"<!-- \\n\" +\n" +
                "                \"\\tВсё то же, что в test case 1, плюс правила 3, 7 и 8:\\n\" +\n" +
                "                \"\\t- Атрибуты должны быть отсортированы в алфавитном порядке: сначала по namespace URI (если атрибут - в qualified form), затем – по local name. \\n\" +\n" +
                "                \"\\t\\tАтрибуты в unqualified form после сортировки идут после атрибутов в qualified form.\\n\" +\n" +
                "                \"\\t- Объявления namespace prefix должны находиться перед атрибутами. Объявления префиксов должны быть отсортированы в порядке объявления, а именно:\\n\" +\n" +
                "                \"\\t\\ta.\\tПервым объявляется префикс пространства имен элемента, если он не был объявлен выше по дереву.\\n\" +\n" +
                "                \"\\t\\tb.\\tДальше объявляются префиксы пространств имен атрибутов, если они требуются. \\n\" +\n" +
                "                \"\\t\\t\\tПорядок этих объявлений соответствует порядку атрибутов, отсортированных в алфавитном порядке (см. п.5).\\n\" +\n" +
                "                \"-->\\n\" +\n" +
                "                \"<?xml-stylesheet type=\\\"text/xsl\\\" href=\\\"style.xsl\\\"?>\\n\" +\n" +
                "                \"\\n\" +\n" +
                "                \"<elementOne xmlns=\\\"http://test/1\\\" xmlns:qwe=\\\"http://test/2\\\" xmlns:asd=\\\"http://test/3\\\">\\n\" +\n" +
                "                \"\\t<qwe:elementTwo>\\n\" +\n" +
                "                \"\\t\\t<asd:elementThree xmlns:wer=\\\"http://test/a\\\" xmlns:zxc=\\\"http://test/0\\\" wer:attZ=\\\"zzz\\\" attB=\\\"bbb\\\" attA=\\\"aaa\\\" zxc:attC=\\\"ccc\\\" asd:attD=\\\"ddd\\\" asd:attE=\\\"eee\\\" qwe:attF=\\\"fff\\\"/>\\n\" +\n" +
                "                \"\\t</qwe:elementTwo>  \\n\" +\n" +
                "                \"</elementOne>"
        Assert.assertEquals(example1, example2)
        val etalon1="<ns1:elementOne xmlns:ns1=\"http://test/1\"><ns2:elementTwo xmlns:ns2=\"http://test/2\"><ns3:elementThree xmlns:ns3=\"http://test/3\"><ns1:elementFour> z x c </ns1:elementFour><ns2:elementFive> w w w </ns2:elementFive></ns3:elementThree><ns4:elementSix xmlns:ns4=\"http://test/3\">eee</ns4:elementSix></ns2:elementTwo></ns1:elementOne>"
        val etalon2="<ns1:elementOne xmlns:ns1=\"http://test/1\"><ns2:elementTwo xmlns:ns2=\"http://test/2\"><ns3:elementThree xmlns:ns3=\"http://test/3\" xmlns:ns4=\"http://test/0\" xmlns:ns5=\"http://test/a\" ns4:attC=\"ccc\" ns2:attF=\"fff\" ns3:attD=\"ddd\" ns3:attE=\"eee\" ns5:attZ=\"zzz\" attA=\"aaa\" attB=\"bbb\"></ns3:elementThree></ns2:elementTwo></ns1:elementOne>"
    }

    @Test
    fun process3() {
        val test = transFromSuckers()
        val wr = FileWriter("input.xml")
        val write = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!-- \n" +
                "\tВсё то же, что в test case 1, плюс правила 4 и 5:\n" +
                "\t- Удалить namespace prefix, которые на текущем уровне объявляются, но не используются.\n" +
                "\t- Проверить, что namespace текущего элемента объявлен либо выше по дереву, либо в текущем элементе. Если не объявлен, объявить в текущем элементе\n" +
                "-->\n" +
                "<?xml-stylesheet type=\"text/xsl\" href=\"style.xsl\"?>\n" +
                "\n" +
                "<elementOne xmlns=\"http://test/1\" xmlns:qwe=\"http://test/2\" xmlns:asd=\"http://test/3\">\n" +
                "\t<qwe:elementTwo>\n" +
                "\t\t<asd:elementThree>\n" +
                "\t\t\t<!-- Проверка обработки default namespace. -->\n" +
                "\t\t\t<elementFour> z x c </elementFour>     \n" +
                "\t\t\t<!-- Тестирование ситуации, когда для одного namespace объявляется несколько префиксов во вложенных элементах. -->\n" +
                "\t\t\t<qqq:elementFive xmlns:qqq=\"http://test/2\"> w w w </qqq:elementFive>\n" +
                "\t\t</asd:elementThree>\n" +
                "\t\t<!-- Ситуация, когда prefix был объявлен выше, чем должно быть в нормальной форме, \n" +
                "\t\tпри нормализации переносится ниже, и это приводит к генерации нескольких префиксов  \n" +
                "\t\tдля одного namespace в sibling элементах. -->\n" +
                "\t\t<asd:elementSix>eee</asd:elementSix>\n" +
                "\t</qwe:elementTwo>  \n" +
                "</elementOne>  "
        wr.write(write)
        wr.close()
        val `in` = FileInputStream("input.xml")
        val out = FileOutputStream("out.xml")
        test.process(`in`, out)
        val contentBuilder = StringBuilder()
        val stream = Files.lines(Paths.get("out.xml"), StandardCharsets.UTF_8)
        run { stream.forEach { s -> contentBuilder.append(s).append("") } }
        val etalon = "<ns1:elementOne xmlns:ns1=\"http://test/1\"><ns2:elementTwo xmlns:ns2=\"http://test/2\"><ns3:elementThree xmlns:ns3=\"http://test/3\"><ns1:elementFour> z x c </ns1:elementFour><ns2:elementFive> w w w </ns2:elementFive></ns3:elementThree><ns4:elementSix xmlns:ns4=\"http://test/3\">eee</ns4:elementSix></ns2:elementTwo></ns1:elementOne>"
        Assert.assertEquals(etalon, contentBuilder.toString())
    }

    @Test
    fun process4() {
        val test = transFromSuckers()
        val wr = FileWriter("input.xml")
        val write = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!-- \n" +
                "\tВсё то же, что в test case 1, плюс правила 3, 7 и 8:\n" +
                "\t- Атрибуты должны быть отсортированы в алфавитном порядке: сначала по namespace URI (если атрибут - в qualified form), затем – по local name. \n" +
                "\t\tАтрибуты в unqualified form после сортировки идут после атрибутов в qualified form.\n" +
                "\t- Объявления namespace prefix должны находиться перед атрибутами. Объявления префиксов должны быть отсортированы в порядке объявления, а именно:\n" +
                "\t\ta.\tПервым объявляется префикс пространства имен элемента, если он не был объявлен выше по дереву.\n" +
                "\t\tb.\tДальше объявляются префиксы пространств имен атрибутов, если они требуются. \n" +
                "\t\t\tПорядок этих объявлений соответствует порядку атрибутов, отсортированных в алфавитном порядке (см. п.5).\n" +
                "-->\n" +
                "<?xml-stylesheet type=\"text/xsl\" href=\"style.xsl\"?>\n" +
                "\n" +
                "<elementOne xmlns=\"http://test/1\" xmlns:qwe=\"http://test/2\" xmlns:asd=\"http://test/3\">\n" +
                "\t<qwe:elementTwo>\n" +
                "\t\t<asd:elementThree xmlns:wer=\"http://test/a\" xmlns:zxc=\"http://test/0\" wer:attZ=\"zzz\" attB=\"bbb\" attA=\"aaa\" zxc:attC=\"ccc\" asd:attD=\"ddd\" asd:attE=\"eee\" qwe:attF=\"fff\"/>\n" +
                "\t</qwe:elementTwo>  \n" +
                "</elementOne>"
        wr.write(write)
        wr.close()
        val `in` = FileInputStream("input.xml")
        val out = FileOutputStream("out.xml")
        test.process(`in`, out)
        val contentBuilder = StringBuilder()
        val stream = Files.lines(Paths.get("out.xml"), StandardCharsets.UTF_8)
        run { stream.forEach { s -> contentBuilder.append(s).append("") } }
        val etalon = "<ns1:elementOne xmlns:ns1=\"http://test/1\"><ns2:elementTwo xmlns:ns2=\"http://test/2\"><ns3:elementThree xmlns:ns3=\"http://test/3\" xmlns:ns4=\"http://test/0\" xmlns:ns5=\"http://test/a\" ns4:attC=\"ccc\" ns2:attF=\"fff\" ns3:attD=\"ddd\" ns3:attE=\"eee\" ns5:attZ=\"zzz\" attA=\"aaa\" attB=\"bbb\"></ns3:elementThree></ns2:elementTwo></ns1:elementOne>"
        Assert.assertEquals(etalon, contentBuilder.toString())
    }

    @Test
    fun process4testfromstupidGuide() {
        val test = transFromSuckers()
        val wr = FileWriter("input.xml")
        val write = "<ns2:SenderProvidedRequestData xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.0\" Id=\"SIGNED_BY_CONSUMER\">\n" +
                " <MessagePrimaryContent xmlns=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.0\">\n" +
                "  <SomeRequest:SomeRequest xmlns:SomeRequest=\"urn://x-artifacts-it-ru/vs/smev/test/test-business-data/1.0\">\n" +
                "   <x xmlns=\"urn://x-artifacts-it-ru/vs/smev/test/test-business-data/1.0\">qweqwe</x>\n" +
                "  </SomeRequest:SomeRequest>\n" +
                " </MessagePrimaryContent>\n" +
                "</ns2:SenderProvidedRequestData>"
        wr.write(write)
        wr.close()
        val `in` = FileInputStream("input.xml")
        val out = FileOutputStream("out.xml")
        test.process(`in`, out)
        val contentBuilder = StringBuilder()
        val stream = Files.lines(Paths.get("out.xml"), StandardCharsets.UTF_8)
        run { stream.forEach { s -> contentBuilder.append(s).append("") } }
        val etalon = "<ns1:SenderProvidedRequestData xmlns:ns1=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.0\" Id=\"SIGNED_BY_CONSUMER\"><ns2:MessagePrimaryContent xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.0\"><ns3:SomeRequest xmlns:ns3=\"urn://x-artifacts-it-ru/vs/smev/test/test-business-data/1.0\"><ns3:x>qweqwe</ns3:x></ns3:SomeRequest></ns2:MessagePrimaryContent></ns1:SenderProvidedRequestData>"
        Assert.assertEquals(etalon, contentBuilder.toString())
    }

    @Test
    fun process4test2fromstupidGuide() {
        val test = transFromSuckers()
        val wr = FileWriter("input.xml")
        val write = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!-- \n" +
                "\tВсё то же, что в test case 1, плюс правила 3, 7 и 8:\n" +
                "\t- Атрибуты должны быть отсортированы в алфавитном порядке: сначала по namespace URI (если атрибут - в qualified form), затем – по local name. \n" +
                "\t\tАтрибуты в unqualified form после сортировки идут после атрибутов в qualified form.\n" +
                "\t- Объявления namespace prefix должны находиться перед атрибутами. Объявления префиксов должны быть отсортированы в порядке объявления, а именно:\n" +
                "\t\ta.\tПервым объявляется префикс пространства имен элемента, если он не был объявлен выше по дереву.\n" +
                "\t\tb.\tДальше объявляются префиксы пространств имен атрибутов, если они требуются. \n" +
                "\t\t\tПорядок этих объявлений соответствует порядку атрибутов, отсортированных в алфавитном порядке (см. п.5).\n" +
                "-->\n" +
                "<?xml-stylesheet type=\"text/xsl\" href=\"style.xsl\"?>\n" +
                "\n" +
                "<elementOne xmlns=\"http://test/1\" xmlns:qwe=\"http://test/2\" xmlns:asd=\"http://test/3\">\n" +
                "\t<qwe:elementTwo>\n" +
                "\t\t<asd:elementThree xmlns:wer=\"http://test/a\" xmlns:zxc=\"http://test/0\" wer:attZ=\"zzz\" attB=\"bbb\" attA=\"aaa\" zxc:attC=\"ccc\" asd:attD=\"ddd\" asd:attE=\"eee\" qwe:attF=\"fff\"/>\n" +
                "\t</qwe:elementTwo>  \n" +
                "</elementOne>  "
        wr.write(write)
        wr.close()
        val `in` = FileInputStream("input.xml")
        val out = FileOutputStream("out.xml")
        test.process(`in`, out)
        val contentBuilder = StringBuilder()
        val stream = Files.lines(Paths.get("out.xml"), StandardCharsets.UTF_8)
        run { stream.forEach { s -> contentBuilder.append(s).append("") } }
        val etalon = "<ns1:elementOne xmlns:ns1=\"http://test/1\"><ns2:elementTwo xmlns:ns2=\"http://test/2\"><ns3:elementThree xmlns:ns3=\"http://test/3\" xmlns:ns4=\"http://test/0\" xmlns:ns5=\"http://test/a\" ns4:attC=\"ccc\" ns2:attF=\"fff\" ns3:attD=\"ddd\" ns3:attE=\"eee\" ns5:attZ=\"zzz\" attA=\"aaa\" attB=\"bbb\"></ns3:elementThree></ns2:elementTwo></ns1:elementOne>"
        Assert.assertEquals(etalon, contentBuilder.toString())
    }


    @Test
    fun process4test2fromsHabr() {
        val test = transFromSuckers()
        val wr = FileWriter("input.xml")
        val write = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\">\n" +
                "   <S:Body>\n" +
                "      <ns2:SendRequestRequest xmlns:ns3=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/faults/1.1\" xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" xmlns=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.1\">\n" +
                "         <ns:SenderProvidedRequestData Id=\"SIGNED_BY_CONSUMER\" xmlns=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" xmlns:ns=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.1\">\t<ns:MessageID>db0486d0-3c08-11e5-95e2-d4c9eff07b77</ns:MessageID><ns2:MessagePrimaryContent><ns1:BreachRequest xmlns:ns1=\"urn://x-artefacts-gibdd-gov-ru/breach/root/1.0\"  xmlns:ns2=\"urn://x-artefacts-gibdd-gov-ru/breach/commons/1.0\"  xmlns:ns3=\"urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1\" Id=\"PERSONAL_SIGNATURE\"> <ns1:RequestedInformation> <ns2:RegPointNum>Т785ЕС57</ns2:RegPointNum> </ns1:RequestedInformation> <ns1:Governance> <ns2:Name>ГИБДД РФ</ns2:Name> <ns2:Code>GIBDD</ns2:Code> <ns2:OfficialPerson> <ns3:FamilyName>Загурский</ns3:FamilyName> <ns3:FirstName>Андрей</ns3:FirstName> <ns3:Patronymic>Петрович</ns3:Patronymic> </ns2:OfficialPerson></ns1:Governance> </ns1:BreachRequest> </ns2:MessagePrimaryContent>\t<ns:TestMessage/></ns:SenderProvidedRequestData>\n" +
                "         <ns2:CallerInformationSystemSignature><ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\"><ds:SignedInfo><ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/><ds:SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gostr34102001-gostr3411\"/><ds:Reference URI=\"#SIGNED_BY_CONSUMER\"><ds:Transforms><ds:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/><ds:Transform Algorithm=\"urn://smev-gov-ru/xmldsig/transform\"/></ds:Transforms><ds:DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gostr3411\"/><ds:DigestValue>/jXl70XwnttJB5sSokwh8SaVHwo2gjgILSu0qBaLUAo=</ds:DigestValue></ds:Reference></ds:SignedInfo><ds:SignatureValue>J3746ks34pOcPGQpKzc0sz3n9+gjPtzZbSEEs4c3sTwbtfdaY7N/hxXzEIvXc+3ad9bc35Y8yBhZ/BYbloGt+Q==</ds:SignatureValue><ds:KeyInfo><ds:X509Data><ds:X509Certificate>MIIBcDCCAR2gAwIBAgIEHVmVKDAKBgYqhQMCAgMFADAtMRAwDgYDVQQLEwdTWVNURU0xMQwwCgYDVQQKEwNJUzIxCzAJBgNVBAYTAlJVMB4XDTE1MDUwNzEyMTUzMFoXDTE4MDUwNjEyMTUzMFowLTEQMA4GA1UECxMHU1lTVEVNMTEMMAoGA1UEChMDSVMyMQswCQYDVQQGEwJSVTBjMBwGBiqFAwICEzASBgcqhQMCAiMBBgcqhQMCAh4BA0MABEDoWGZlTUWD43G1N7TEm14+QyXrJWProrzoDoCJRem169q4bezFOUODcNooQJNg3PtAizkWeFcX4b93u8fpVy7RoyEwHzAdBgNVHQ4EFgQUaRG++MAcPZvK/E2vR1BBl5G7s5EwCgYGKoUDAgIDBQADQQCg25vA3RJL3kgcJhVOHA86vnkMAtZYr6HBPa7LpEo0HJrbBF0ygKk50app1lzPdZ5TtK2itfmNgTYiuQHX3+nE</ds:X509Certificate></ds:X509Data></ds:KeyInfo></ds:Signature></ns2:CallerInformationSystemSignature>\n" +
                "      </ns2:SendRequestRequest>\n" +
                "   </S:Body>\n" +
                "</S:Envelope>"
        wr.write(write)
        wr.close()
        val `in` = FileInputStream("input.xml")
        val out = FileOutputStream("out.xml")
        test.process(`in`, out)
        val contentBuilder = StringBuilder()
        val stream = Files.lines(Paths.get("out.xml"), StandardCharsets.UTF_8)
        run { stream.forEach { s -> contentBuilder.append(s).append("") } }
        val etalon = "<ns1:elementOne xmlns:ns1=\"http://test/1\"><ns2:elementTwo xmlns:ns2=\"http://test/2\"><ns3:elementThree xmlns:ns3=\"http://test/3\" xmlns:ns4=\"http://test/0\" xmlns:ns5=\"http://test/a\" ns4:attC=\"ccc\" ns2:attF=\"fff\" ns3:attD=\"ddd\" ns3:attE=\"eee\" ns5:attZ=\"zzz\" attA=\"aaa\" attB=\"bbb\"></ns3:elementThree></ns2:elementTwo></ns1:elementOne>"
        //Assert.assertEquals(etalon, contentBuilder.toString())
    }



    @Test
    fun process4test22fromsHabr() {
        val test = transFromSuckers()
        val wr = FileWriter("input.xml")
        wr.close()
        val `in` = FileInputStream("SendRequestRequestNoAttach.xml")
        val out = FileOutputStream("pound.xml")
        test.process(`in`, out)
    //    val contentBuilder = StringBuilder()
    //    val stream = Files.lines(Paths.get("out.xml"), StandardCharsets.UTF_8)
    //    run { stream.forEach { s -> contentBuilder.append(s).append("") } }
    //    val etalon = "<ns1:elementOne xmlns:ns1=\"http://test/1\"><ns2:elementTwo xmlns:ns2=\"http://test/2\"><ns3:elementThree xmlns:ns3=\"http://test/3\" xmlns:ns4=\"http://test/0\" xmlns:ns5=\"http://test/a\" ns4:attC=\"ccc\" ns2:attF=\"fff\" ns3:attD=\"ddd\" ns3:attE=\"eee\" ns5:attZ=\"zzz\" attA=\"aaa\" attB=\"bbb\"></ns3:elementThree></ns2:elementTwo></ns1:elementOne>"
        //Assert.assertEquals(etalon, contentBuilder.toString())
    }


    @Test
    fun process4testonlysenderfromsHabr() {
        val test = transFromSuckers()
        val wr = FileWriter("rawReqData.xml")
        val write = "<ns:SenderProvidedRequestData Id=\"SIGNED_BY_CONSUMER\" xmlns=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" xmlns:ns=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.1\">\t<ns:MessageID>db0486d0-3c08-11e5-95e2-d4c9eff07b77</ns:MessageID><ns2:MessagePrimaryContent><ns1:BreachRequest xmlns:ns1=\"urn://x-artefacts-gibdd-gov-ru/breach/root/1.0\"  xmlns:ns2=\"urn://x-artefacts-gibdd-gov-ru/breach/commons/1.0\"  xmlns:ns3=\"urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1\" Id=\"PERSONAL_SIGNATURE\"> <ns1:RequestedInformation> <ns2:RegPointNum>Т785ЕС57</ns2:RegPointNum> </ns1:RequestedInformation> <ns1:Governance> <ns2:Name>ГИБДД РФ</ns2:Name> <ns2:Code>GIBDD</ns2:Code> <ns2:OfficialPerson> <ns3:FamilyName>Загурский</ns3:FamilyName> <ns3:FirstName>Андрей</ns3:FirstName> <ns3:Patronymic>Петрович</ns3:Patronymic> </ns2:OfficialPerson></ns1:Governance> </ns1:BreachRequest> </ns2:MessagePrimaryContent>\t<ns:TestMessage/></ns:SenderProvidedRequestData>"
        wr.write(write)
        wr.close()
        val `in` = FileInputStream("rawReqData.xml")
        val out = FileOutputStream("rawReqDataOUT.xml")
        test.process(`in`, out)
        val contentBuilder = StringBuilder()
        val stream = Files.lines(Paths.get("rawReqDataOUT.xml"), StandardCharsets.UTF_8)
        run { stream.forEach { s -> contentBuilder.append(s).append("") } }
        val etalon = "<ns1:SenderProvidedRequestData xmlns:ns1=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" Id=\"SIGNED_BY_CONSUMER\"><ns1:MessageID>db0486d0-3c08-11e5-95e2-d4c9eff07b77</ns1:MessageID><ns2:MessagePrimaryContent xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.1\"><ns3:BreachRequest xmlns:ns3=\"urn://x-artefacts-gibdd-gov-ru/breach/root/1.0\" Id=\"PERSONAL_SIGNATURE\"><ns3:RequestedInformation><ns4:RegPointNum xmlns:ns4=\"urn://x-artefacts-gibdd-gov-ru/breach/commons/1.0\">Т785ЕС57</ns4:RegPointNum></ns3:RequestedInformation><ns3:Governance><ns5:Name xmlns:ns5=\"urn://x-artefacts-gibdd-gov-ru/breach/commons/1.0\">ГИБДД РФ</ns5:Name><ns6:Code xmlns:ns6=\"urn://x-artefacts-gibdd-gov-ru/breach/commons/1.0\">GIBDD</ns6:Code><ns7:OfficialPerson xmlns:ns7=\"urn://x-artefacts-gibdd-gov-ru/breach/commons/1.0\"><ns8:FamilyName xmlns:ns8=\"urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1\">Загурский</ns8:FamilyName><ns9:FirstName xmlns:ns9=\"urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1\">Андрей</ns9:FirstName><ns10:Patronymic xmlns:ns10=\"urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1\">Петрович</ns10:Patronymic></ns7:OfficialPerson></ns3:Governance></ns3:BreachRequest></ns2:MessagePrimaryContent><ns1:TestMessage></ns1:TestMessage></ns1:SenderProvidedRequestData>"
        Assert.assertEquals(etalon, contentBuilder.toString())
        val hash = Gost3411Hash()
        val input = "<ns2:SenderProvidedRequestData Id=\"SIGNED_BY_CONSUMER\"><ns2:MessageID>db0486d0-3c08-11e5-95e2-d4c9eff07b77</ns2:MessageID><ns3:MessagePrimaryContent xmlns:ns3=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.1\"><ns4:BreachRequest xmlns:ns4=\"urn://x-artefacts-gibdd-gov-ru/breach/root/1.0\" Id=\"PERSONAL_SIGNATURE\"><ns4:RequestedInformation><ns5:RegPointNum xmlns:ns5=\"urn://x-artefacts-gibdd-gov-ru/breach/commons/1.0\">Т785ЕС57</ns5:RegPointNum></ns4:RequestedInformation><ns4:Governance><ns6:Name xmlns:ns6=\"urn://x-artefacts-gibdd-gov-ru/breach/commons/1.0\">ГИБДД РФ</ns6:Name><ns7:Code xmlns:ns7=\"urn://x-artefacts-gibdd-gov-ru/breach/commons/1.0\">GIBDD</ns7:Code><ns8:OfficialPerson xmlns:ns8=\"urn://x-artefacts-gibdd-gov-ru/breach/commons/1.0\"><ns9:FamilyName xmlns:ns9=\"urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1\">Загурский</ns9:FamilyName><ns10:FirstName xmlns:ns10=\"urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1\">Андрей</ns10:FirstName><ns11:Patronymic xmlns:ns11=\"urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1\">Петрович</ns11:Patronymic></ns8:OfficialPerson></ns4:Governance></ns4:BreachRequest></ns3:MessagePrimaryContent><ns2:TestMessage></ns2:TestMessage></ns2:SenderProvidedRequestData>"
        assertEquals("/jXl70XwnttJB5sSokwh8SaVHwo2gjgILSu0qBaLUAo=", hash.h_Base64rfc2045(contentBuilder.toString()))
    }


}