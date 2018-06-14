import static spark.Spark.*;
import crypto.Gost3411Hash;
import spark.ModelAndView;
import spark.Request;
import spark.template.velocity.VelocityTemplateEngine;
import util.SAAJ;
import util.timeBasedUUID;
import util.transFromSuckers;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {
    static Map<String, Object> modelDependency = new HashMap<>();
    static ModelAndView view;
    static VelocityTemplateEngine template = new VelocityTemplateEngine();
    static transFromSuckers trans = new transFromSuckers();
    static SAAJ saa= new SAAJ("http://smev3-n0.test.gosuslugi.ru:7500/ws?wsdl");
    static Gost3411Hash hasher = new Gost3411Hash();
    public static void main(String[] argv) {
        port(7777);
        staticFiles.location("/public");
        File uploadDir = new File("upload");
        uploadDir.mkdir(); // create the upload directory if it doesn't exist
        staticFiles.externalLocation("upload");

        get("/", (req, res) -> {
                modelDependency.clear();
                view=new ModelAndView(modelDependency, "/templates/index.html");
            return template.render(view);
        });
        timeBasedUUID gen = new timeBasedUUID();
        get("/uuid", (req,res) -> {
            modelDependency.clear();
            modelDependency.put("UUID", gen.generate());
            view=new ModelAndView(modelDependency, "/templates/uuid.html");
            return template.render(view);
        });

        get("/soap", (req, res) -> {
            modelDependency.clear();
            String message = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\">   <S:Body>      <ns2:SendRequestRequest xmlns:ns3=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/faults/1.1\" xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" xmlns=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.1\">         <ns:SenderProvidedRequestData Id=\"SIGNED_BY_CONSUMER\" xmlns=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" xmlns:ns=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.1\">\t<ns:MessageID>a3c69138-6bf1-11e8-a126-6d349ec8de0d</ns:MessageID><ns2:MessagePrimaryContent><ns1:BreachRequest xmlns:ns1=\"urn://x-artefacts-gibdd-gov-ru/breach/root/1.0\"  xmlns:ns2=\"urn://x-artefacts-gibdd-gov-ru/breach/commons/1.0\"  xmlns:ns3=\"urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1\" Id=\"PERSONAL_SIGNATURE\"> <ns1:RequestedInformation> <ns2:RegPointNum>Т785ЕС57</ns2:RegPointNum> </ns1:RequestedInformation> <ns1:Governance> <ns2:Name>ГИБДД РФ</ns2:Name> <ns2:Code>GIBDD</ns2:Code> <ns2:OfficialPerson> <ns3:FamilyName>Загурский</ns3:FamilyName> <ns3:FirstName>Андрей</ns3:FirstName> <ns3:Patronymic>Петрович</ns3:Patronymic> </ns2:OfficialPerson></ns1:Governance> </ns1:BreachRequest> </ns2:MessagePrimaryContent>\t<ns:TestMessage/></ns:SenderProvidedRequestData>         <ns2:CallerInformationSystemSignature><ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\"><ds:SignedInfo><ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/><ds:SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gostr34102001-gostr3411\"/><ds:Reference URI=\"#SIGNED_BY_CONSUMER\"><ds:Transforms><ds:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/><ds:Transform Algorithm=\"urn://smev-gov-ru/xmldsig/transform\"/></ds:Transforms><ds:DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gostr3411\"/><ds:DigestValue>/jXl70XwnttJB5sSokwh8SaVHwo2gjgILSu0qBaLUAo=</ds:DigestValue></ds:Reference></ds:SignedInfo><ds:SignatureValue>J3746ks34pOcPGQpKzc0sz3n9+gjPtzZbSEEs4c3sTwbtfdaY7N/hxXzEIvXc+3ad9bc35Y8yBhZ/BYbloGt+Q==</ds:SignatureValue><ds:KeyInfo><ds:X509Data><ds:X509Certificate>MIIBcDCCAR2gAwIBAgIEHVmVKDAKBgYqhQMCAgMFADAtMRAwDgYDVQQLEwdTWVNURU0xMQwwCgYDVQQKEwNJUzIxCzAJBgNVBAYTAlJVMB4XDTE1MDUwNzEyMTUzMFoXDTE4MDUwNjEyMTUzMFowLTEQMA4GA1UECxMHU1lTVEVNMTEMMAoGA1UEChMDSVMyMQswCQYDVQQGEwJSVTBjMBwGBiqFAwICEzASBgcqhQMCAiMBBgcqhQMCAh4BA0MABEDoWGZlTUWD43G1N7TEm14+QyXrJWProrzoDoCJRem169q4bezFOUODcNooQJNg3PtAizkWeFcX4b93u8fpVy7RoyEwHzAdBgNVHQ4EFgQUaRG++MAcPZvK/E2vR1BBl5G7s5EwCgYGKoUDAgIDBQADQQCg25vA3RJL3kgcJhVOHA86vnkMAtZYr6HBPa7LpEo0HJrbBF0ygKk50app1lzPdZ5TtK2itfmNgTYiuQHX3+nE</ds:X509Certificate></ds:X509Data></ds:KeyInfo></ds:Signature></ns2:CallerInformationSystemSignature>      </ns2:SendRequestRequest>   </S:Body></S:Envelope>";
            modelDependency.put("message", message);
            view=new ModelAndView(modelDependency, "/templates/soap.html");
            return template.render(view);
        });

        post("/soap", (req, res) -> {
            Path tempFile = upload(req, uploadDir);
            logInfo(req, tempFile);
            saa.send(tempFile.toAbsolutePath().toString(),"resp.xml");
            modelDependency.clear();
            Path wiki_path = Paths.get("resp.xml");
            Charset charset = Charset.forName("UTF-8");
            List<String> lines=null;
            try {
                lines = Files.readAllLines(wiki_path, charset);
                for (String line : lines) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                System.out.println(e);
            }
            modelDependency.put("response", lines);
            view=new ModelAndView(modelDependency, "/templates/response.html");
            return template.render(view);
        });


        get("/transform", (req, res) -> {
            modelDependency.clear();
            String message = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\">   <S:Body>      <ns2:SendRequestRequest xmlns:ns3=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/faults/1.1\" xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" xmlns=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.1\">         <ns:SenderProvidedRequestData Id=\"SIGNED_BY_CONSUMER\" xmlns=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" xmlns:ns=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.1\">\t<ns:MessageID>a3c69138-6bf1-11e8-a126-6d349ec8de0d</ns:MessageID><ns2:MessagePrimaryContent><ns1:BreachRequest xmlns:ns1=\"urn://x-artefacts-gibdd-gov-ru/breach/root/1.0\"  xmlns:ns2=\"urn://x-artefacts-gibdd-gov-ru/breach/commons/1.0\"  xmlns:ns3=\"urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1\" Id=\"PERSONAL_SIGNATURE\"> <ns1:RequestedInformation> <ns2:RegPointNum>Т785ЕС57</ns2:RegPointNum> </ns1:RequestedInformation> <ns1:Governance> <ns2:Name>ГИБДД РФ</ns2:Name> <ns2:Code>GIBDD</ns2:Code> <ns2:OfficialPerson> <ns3:FamilyName>Загурский</ns3:FamilyName> <ns3:FirstName>Андрей</ns3:FirstName> <ns3:Patronymic>Петрович</ns3:Patronymic> </ns2:OfficialPerson></ns1:Governance> </ns1:BreachRequest> </ns2:MessagePrimaryContent>\t<ns:TestMessage/></ns:SenderProvidedRequestData>         <ns2:CallerInformationSystemSignature><ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\"><ds:SignedInfo><ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/><ds:SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gostr34102001-gostr3411\"/><ds:Reference URI=\"#SIGNED_BY_CONSUMER\"><ds:Transforms><ds:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/><ds:Transform Algorithm=\"urn://smev-gov-ru/xmldsig/transform\"/></ds:Transforms><ds:DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gostr3411\"/><ds:DigestValue>/jXl70XwnttJB5sSokwh8SaVHwo2gjgILSu0qBaLUAo=</ds:DigestValue></ds:Reference></ds:SignedInfo><ds:SignatureValue>J3746ks34pOcPGQpKzc0sz3n9+gjPtzZbSEEs4c3sTwbtfdaY7N/hxXzEIvXc+3ad9bc35Y8yBhZ/BYbloGt+Q==</ds:SignatureValue><ds:KeyInfo><ds:X509Data><ds:X509Certificate>MIIBcDCCAR2gAwIBAgIEHVmVKDAKBgYqhQMCAgMFADAtMRAwDgYDVQQLEwdTWVNURU0xMQwwCgYDVQQKEwNJUzIxCzAJBgNVBAYTAlJVMB4XDTE1MDUwNzEyMTUzMFoXDTE4MDUwNjEyMTUzMFowLTEQMA4GA1UECxMHU1lTVEVNMTEMMAoGA1UEChMDSVMyMQswCQYDVQQGEwJSVTBjMBwGBiqFAwICEzASBgcqhQMCAiMBBgcqhQMCAh4BA0MABEDoWGZlTUWD43G1N7TEm14+QyXrJWProrzoDoCJRem169q4bezFOUODcNooQJNg3PtAizkWeFcX4b93u8fpVy7RoyEwHzAdBgNVHQ4EFgQUaRG++MAcPZvK/E2vR1BBl5G7s5EwCgYGKoUDAgIDBQADQQCg25vA3RJL3kgcJhVOHA86vnkMAtZYr6HBPa7LpEo0HJrbBF0ygKk50app1lzPdZ5TtK2itfmNgTYiuQHX3+nE</ds:X509Certificate></ds:X509Data></ds:KeyInfo></ds:Signature></ns2:CallerInformationSystemSignature>      </ns2:SendRequestRequest>   </S:Body></S:Envelope>";
            modelDependency.put("message", message);
            view=new ModelAndView(modelDependency, "/templates/soap.html");
            return template.render(view);
        });

        post("/transform", (req, res) -> {
            Path tempFile = upload(req, uploadDir);
            logInfo(req, tempFile);
            InputStream in  = new FileInputStream(tempFile.toAbsolutePath().toString());
            OutputStream out  = new FileOutputStream("out.xml");
            trans.process(in, out);
            Path p = Paths.get("out.xml");
            byte[] arr = Files.readAllBytes(p);
            try {
                HttpServletResponse raw = res.raw();
                res.header("Content-Disposition", "attachment; filename=out.xml");
                raw.getOutputStream().write( arr);
                raw.getOutputStream().flush();
                raw.getOutputStream().close();
                return raw;
            } catch (IOException ex) {
                halt();
            }
            return res;
        });


        get("/hash", (req, res) -> {
            modelDependency.clear();
            String message = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\">   <S:Body>      <ns2:SendRequestRequest xmlns:ns3=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/faults/1.1\" xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" xmlns=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.1\">         <ns:SenderProvidedRequestData Id=\"SIGNED_BY_CONSUMER\" xmlns=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" xmlns:ns=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.1\">\t<ns:MessageID>a3c69138-6bf1-11e8-a126-6d349ec8de0d</ns:MessageID><ns2:MessagePrimaryContent><ns1:BreachRequest xmlns:ns1=\"urn://x-artefacts-gibdd-gov-ru/breach/root/1.0\"  xmlns:ns2=\"urn://x-artefacts-gibdd-gov-ru/breach/commons/1.0\"  xmlns:ns3=\"urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1\" Id=\"PERSONAL_SIGNATURE\"> <ns1:RequestedInformation> <ns2:RegPointNum>Т785ЕС57</ns2:RegPointNum> </ns1:RequestedInformation> <ns1:Governance> <ns2:Name>ГИБДД РФ</ns2:Name> <ns2:Code>GIBDD</ns2:Code> <ns2:OfficialPerson> <ns3:FamilyName>Загурский</ns3:FamilyName> <ns3:FirstName>Андрей</ns3:FirstName> <ns3:Patronymic>Петрович</ns3:Patronymic> </ns2:OfficialPerson></ns1:Governance> </ns1:BreachRequest> </ns2:MessagePrimaryContent>\t<ns:TestMessage/></ns:SenderProvidedRequestData>         <ns2:CallerInformationSystemSignature><ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\"><ds:SignedInfo><ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/><ds:SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gostr34102001-gostr3411\"/><ds:Reference URI=\"#SIGNED_BY_CONSUMER\"><ds:Transforms><ds:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/><ds:Transform Algorithm=\"urn://smev-gov-ru/xmldsig/transform\"/></ds:Transforms><ds:DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gostr3411\"/><ds:DigestValue>/jXl70XwnttJB5sSokwh8SaVHwo2gjgILSu0qBaLUAo=</ds:DigestValue></ds:Reference></ds:SignedInfo><ds:SignatureValue>J3746ks34pOcPGQpKzc0sz3n9+gjPtzZbSEEs4c3sTwbtfdaY7N/hxXzEIvXc+3ad9bc35Y8yBhZ/BYbloGt+Q==</ds:SignatureValue><ds:KeyInfo><ds:X509Data><ds:X509Certificate>MIIBcDCCAR2gAwIBAgIEHVmVKDAKBgYqhQMCAgMFADAtMRAwDgYDVQQLEwdTWVNURU0xMQwwCgYDVQQKEwNJUzIxCzAJBgNVBAYTAlJVMB4XDTE1MDUwNzEyMTUzMFoXDTE4MDUwNjEyMTUzMFowLTEQMA4GA1UECxMHU1lTVEVNMTEMMAoGA1UEChMDSVMyMQswCQYDVQQGEwJSVTBjMBwGBiqFAwICEzASBgcqhQMCAiMBBgcqhQMCAh4BA0MABEDoWGZlTUWD43G1N7TEm14+QyXrJWProrzoDoCJRem169q4bezFOUODcNooQJNg3PtAizkWeFcX4b93u8fpVy7RoyEwHzAdBgNVHQ4EFgQUaRG++MAcPZvK/E2vR1BBl5G7s5EwCgYGKoUDAgIDBQADQQCg25vA3RJL3kgcJhVOHA86vnkMAtZYr6HBPa7LpEo0HJrbBF0ygKk50app1lzPdZ5TtK2itfmNgTYiuQHX3+nE</ds:X509Certificate></ds:X509Data></ds:KeyInfo></ds:Signature></ns2:CallerInformationSystemSignature>      </ns2:SendRequestRequest>   </S:Body></S:Envelope>";
            modelDependency.put("message", message);
            view=new ModelAndView(modelDependency, "/templates/soap.html");
            return template.render(view);
        });

        post("/hash", (req, res) -> {
            Path tempFile = upload(req, uploadDir);
            logInfo(req, tempFile);
            Path p = Paths.get(tempFile.toAbsolutePath().toString());
            byte[] arr = Files.readAllBytes(p);
            modelDependency.clear();
            modelDependency.put("response", hasher.h_Base64rfc2045(arr));
            view=new ModelAndView(modelDependency, "/templates/response.html");
            return template.render(view);
        });
        get("/soapg", (req,res)->{
                    return req.queryParams("message");
                }
        );
    }
    private static void logInfo(Request req, Path tempFile) throws IOException, ServletException {
        System.out.println("Uploaded file '" + getFileName(req.raw().getPart("uploaded_file")) + "' saved as '" + tempFile.toAbsolutePath() + "'");
    }

    private static String getFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }


    private static  Path upload(Request req, File dir) throws IOException {
        Path tempFile = Files.createTempFile(dir.toPath(), "", "");
        req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
        try (InputStream input = req.raw().getPart("uploaded_file").getInputStream()) { // getPart needs to use same "name" as input field in form
            Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempFile;
    }
}