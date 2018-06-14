package util
import org.apache.xml.security.transforms.TransformationException
import org.junit.Test
import org.junit.Assert.*
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

class SmevImportedTransFormTest {

    @Test
    fun tarnsformtoSign(){
        val test = SmevImportedTransForm()
        val `in` = FileInputStream("SendRequestRequestNoAttach.xml")
        val out = FileOutputStream("TRANS.xml")
        test.process(`in`, out)

    }


    @Test
    fun tarnsformHighligthedData(){
        val test = SmevImportedTransForm()
        val `in` = FileInputStream("1.xml")
        val out = FileOutputStream("TRANS.xml")
        test.process(`in`, out)

    }


    @Test
    fun processini1() {
        val test = SmevImportedTransForm()
        val wr = FileWriter("input.xml")
        val write = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<?xml-stylesheet type=\"text/css\" href=\"style.css\"?>\n" +
                "<qwe xmlns=\"http://t.e.s.t\">\n" +
                "  <myns:rty xmlns:myns=\"http://y.e.s\">yes!</myns:rty>\n" +
                "  <iop value=\"yes, yes!\"/>\n" +
                "</qwe>"
        wr.write(write)
        wr.close()
        val `in` = FileInputStream("input.xml")
        val out = FileOutputStream("out.xml")
        test.process(`in`, out)
        val contentBuilder = StringBuilder()
        val stream = Files.lines(Paths.get("out.xml"), StandardCharsets.UTF_8)
        run { stream.forEach { s -> contentBuilder.append(s).append("") } }
        val etalon = "<ns1:qwe xmlns:ns1=\"http://t.e.s.t\">\n" +
                "  <ns2:rty xmlns:ns2=\"http://y.e.s\">yes!</ns2:rty>\n" +
                "  <ns1:iop value=\"yes, yes!\"></ns1:iop>\n" +
                "</ns1:qwe>"
        assertEquals(etalon, contentBuilder.toString())
    }


    @Test
    fun processini() {
        val test = SmevImportedTransForm()
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
        assertEquals(etalon.length.toLong(), contentBuilder.toString().length.toLong())
        assertEquals(etalon, contentBuilder.toString())
    }

    @Test
    fun process() {
        val test = SmevImportedTransForm()
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
        assertEquals(etalon, contentBuilder.toString())
    }

    @Test
    fun process2() {
        val test = SmevImportedTransForm()
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
                "</elementOne>  "
        wr.write(write)
        wr.close()
        val `in` = FileInputStream("input.xml")
        val out = FileOutputStream("out.xml")
        test.process(`in`, out)
        val contentBuilder = StringBuilder()
        val stream = Files.lines(Paths.get("out.xml"), StandardCharsets.UTF_8)
        run { stream.forEach { s -> contentBuilder.append(s).append("") } }
        val etalon = "<ns1:elementOne xmlns:ns1=\"http://test/1\"><ns2:elementTwo xmlns:ns2=\"http://test/2\">asd</ns2:elementTwo></ns1:elementOne>"
        assertEquals(etalon, contentBuilder.toString())
    }

    @Test
    fun process3() {
        val test = SmevImportedTransForm()
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
        val etalon = "<ns1:elementOne xmlns:ns1=\"http://test/1\"><ns2:elementTwo xmlns:ns2=\"http://test/2\"><ns3:elementThree xmlns:ns3=\"http://test/3\"><ns1:elementFour> z x c </ns1:elementFour><ns2:elementFive> w w w </ns2:elementFive></ns3:elementThree><ns4:elementSix xmlns:ns4=\"http://test/3\">eee</ns4:elementSix></ns2:elementTwo></ns1:elementOne>"
        assertEquals(etalon, contentBuilder.toString())
    }

    @Test
    fun process4() {
        val test = SmevImportedTransForm()
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
                "</elementOne>   "
        wr.write(write)
        wr.close()
        val `in` = FileInputStream("input.xml")
        val out = FileOutputStream("out.xml")
        test.process(`in`, out)
        val contentBuilder = StringBuilder()
        val stream = Files.lines(Paths.get("out.xml"), StandardCharsets.UTF_8)
        run { stream.forEach { s -> contentBuilder.append(s).append("") } }
        val etalon = "<ns1:elementOne xmlns:ns1=\"http://test/1\"><ns2:elementTwo xmlns:ns2=\"http://test/2\"><ns3:elementThree xmlns:ns3=\"http://test/3\" xmlns:ns4=\"http://test/0\" xmlns:ns5=\"http://test/a\" ns4:attC=\"ccc\" ns2:attF=\"fff\" ns3:attD=\"ddd\" ns3:attE=\"eee\" ns5:attZ=\"zzz\" attA=\"aaa\" attB=\"bbb\"></ns3:elementThree></ns2:elementTwo></ns1:elementOne>"
        assertEquals(etalon, contentBuilder.toString())
    }

    @Test
    fun transformForSign() {
        val test = SmevImportedTransForm()
        val `in` = FileInputStream("1.xml")
        val out = FileOutputStream("result.xml")
        test.process(`in`, out)
    }


}