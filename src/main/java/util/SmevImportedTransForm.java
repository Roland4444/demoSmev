package util;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.transforms.TransformSpi;
import org.apache.xml.security.transforms.TransformationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.*;
import javax.xml.stream.events.*;
import java.io.*;
import java.util.*;

public class SmevImportedTransForm extends TransformSpi {
    public static final String ALGORITHM_URN = "urn://smev-gov-ru/xmldsig/transform";
    private static final String ENCODING_UTF_8 = "UTF-8";

    private static Logger logger = LoggerFactory.getLogger(SmevImportedTransForm.class);
    private static AttributeSortingComparator attributeSortingComparator =
            new AttributeSortingComparator();

    private static ThreadLocal<XMLInputFactory> inputFactory =
            new ThreadLocal<XMLInputFactory>() {
                @Override
                protected XMLInputFactory initialValue() {
                    return XMLInputFactory.newInstance();
                }
            };

    private static ThreadLocal<XMLOutputFactory> outputFactory =
            new ThreadLocal<XMLOutputFactory>() {
                @Override
                protected XMLOutputFactory initialValue() {
                    return XMLOutputFactory.newInstance();
                }
            };

    private static ThreadLocal<XMLEventFactory> eventFactory =
            new ThreadLocal<XMLEventFactory>() {
                @Override
                protected XMLEventFactory initialValue() {
                    return XMLEventFactory.newInstance();
                }
            };

    @Override
    protected String engineGetURI() {
        return ALGORITHM_URN;
    }


    protected XMLSignatureInput enginePerformTransform(XMLSignatureInput argInput,
                                                       OutputStream argOutput, Transform argTransform) throws IOException,
            CanonicalizationException, InvalidCanonicalizerException,
            TransformationException, ParserConfigurationException, SAXException {

        process(argInput.getOctetStream(), argOutput);
        XMLSignatureInput result = new XMLSignatureInput((byte[]) null);
        result.setOutputStream(argOutput);
        return result;

    }


    protected XMLSignatureInput enginePerformTransform(XMLSignatureInput argInput,
                                                       Transform argTransform) throws IOException, CanonicalizationException,
            InvalidCanonicalizerException, TransformationException,
            ParserConfigurationException, SAXException {

        return enginePerformTransform(argInput);
    }

    @Override
    protected XMLSignatureInput enginePerformTransform(XMLSignatureInput argInput)
            throws IOException, CanonicalizationException,
            InvalidCanonicalizerException, TransformationException,
            ParserConfigurationException, SAXException {

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        process(argInput.getOctetStream(), result);
        byte[] postTransformData = result.toByteArray();

        return new XMLSignatureInput(postTransformData);
    }

    public void process(InputStream argSrc, OutputStream argDst) throws TransformationException {

        DebugOutputStream debugStream = null;

        Stack<List<Namespace>> prefixMappingStack = new Stack<List<Namespace>>();

        int prefixCnt = 1;
        XMLEventReader src = null;
        XMLEventWriter dst = null;
        try {
            src = inputFactory.get().createXMLEventReader(argSrc, ENCODING_UTF_8);
            if (logger.isDebugEnabled()) {
                debugStream = new DebugOutputStream(argDst);
                dst = outputFactory.get().createXMLEventWriter(debugStream, ENCODING_UTF_8);
            } else {
                dst = outputFactory.get().createXMLEventWriter(argDst, ENCODING_UTF_8);
            }
            XMLEventFactory factory = eventFactory.get();

            while(src.hasNext()) {
                XMLEvent event = src.nextEvent();

                if (event.isCharacters()) {
                    String data = event.asCharacters().getData();

                    if (!data.trim().isEmpty()) {
                        dst.add(event);
                    }
                    continue;
                } else if (event.isStartElement()) {
                    List<Namespace> myPrefixMappings = new LinkedList<Namespace>();
                    prefixMappingStack.push(myPrefixMappings);


                    StartElement srcEvent = (StartElement)event;
                    String nsURI = srcEvent.getName().getNamespaceURI();
                    String prefix = findPrefix(nsURI, prefixMappingStack);

                    if (prefix == null) {
                        prefix = "ns" + String.valueOf(prefixCnt++);
                        myPrefixMappings.add(factory.createNamespace(prefix, nsURI));
                    }
                    StartElement dstEvent = factory.createStartElement(
                            prefix, nsURI, srcEvent.getName().getLocalPart());
                    dst.add(dstEvent);



                    Iterator<Attribute> srcAttributeIterator = srcEvent.getAttributes();

                    List<Attribute> srcAttributeList = new LinkedList<Attribute>();
                    while(srcAttributeIterator.hasNext()) {
                        srcAttributeList.add(srcAttributeIterator.next());
                    }

                    Collections.sort(srcAttributeList, attributeSortingComparator);


                    List<Attribute> dstAttributeList = new LinkedList<Attribute>();
                    for (Attribute srcAttribute : srcAttributeList) {
                        String attributeNsURI = srcAttribute.getName().getNamespaceURI();
                        String attributeLocalName = srcAttribute.getName().getLocalPart();
                        String value = srcAttribute.getValue();
                        String attributePrefix = null;
                        Attribute dstAttribute = null;
                        if (attributeNsURI != null && !"".equals(attributeNsURI)) {
                            attributePrefix = findPrefix(attributeNsURI, prefixMappingStack);
                            if (attributePrefix == null) {
                                attributePrefix = "ns" + String.valueOf(prefixCnt++);
                                myPrefixMappings.add(factory.createNamespace(
                                        attributePrefix, attributeNsURI));
                            }
                            dstAttribute = factory.createAttribute(
                                    attributePrefix, attributeNsURI, attributeLocalName, value);
                        } else {
                            dstAttribute = factory.createAttribute(attributeLocalName, value);
                        }
                        dstAttributeList.add(dstAttribute);
                    }


                    for (Namespace mapping : myPrefixMappings) {
                        dst.add(mapping);
                    }


                    for (Attribute attr : dstAttributeList) {
                        dst.add(attr);
                    }

                    continue;
                } else if (event.isEndElement()) {

                    dst.add(eventFactory.get().createSpace(""));


                    EndElement srcEvent = (EndElement)event;
                    String nsURI = srcEvent.getName().getNamespaceURI();
                    String prefix = findPrefix(nsURI, prefixMappingStack);
                    if (prefix == null) {
                        throw new TransformationException(
                                "EndElement: prefix mapping is not found for namespace " + nsURI);
                    }

                    EndElement dstEvent = eventFactory.get().
                            createEndElement(prefix, nsURI, srcEvent.getName().getLocalPart());
                    dst.add(dstEvent);
                    prefixMappingStack.pop();
                    continue;
                } else if (event.isAttribute()) {

                    continue;
                }


            }
        } catch (XMLStreamException e) {
            Object exArgs[] = { e.getMessage() };
            throw new TransformationException(
                    "Can not perform transformation " + ALGORITHM_URN, exArgs, e
            );
        } finally {
            if (src != null) {
                try {
                    src.close();
                } catch (XMLStreamException e) {
                    logger.warn("Can not close XMLEventReader", e);
                }
            }
            if (dst != null) {
                try {
                    dst.close();
                } catch (XMLStreamException e) {
                    logger.warn("Can not close XMLEventWriter", e);
                }
            }
            try {
                argSrc.close();
            } catch (IOException e) {
                logger.warn("Can not close input stream.", e);
            }
            try {
                argDst.close();
            } catch (IOException e) {
                logger.warn("Can not close output stream.", e);
            }

            if (logger.isDebugEnabled()) {
                try {
                    String contentAfterCanonizationAndTransforms =
                            new String(debugStream.getCollectedData(), "UTF-8");
                    logger.debug("Content after canonization: " +
                            contentAfterCanonizationAndTransforms);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String findPrefix(String argNamespaceURI, Stack<List<Namespace>> argMappingStack) {
        if (argNamespaceURI == null) {
            throw new IllegalArgumentException("No namespace elements no supported");
        }

        for (List<Namespace> elementMappingList : argMappingStack) {
            for (Namespace mapping : elementMappingList) {
                if (argNamespaceURI.equals(mapping.getNamespaceURI())) {
                    return mapping.getPrefix();
                }
            }
        }
        return null;
    }

    private static class AttributeSortingComparator implements Comparator<Attribute> {
        @Override
        public int compare(Attribute x, Attribute y) {
            String xNS = x.getName().getNamespaceURI();
            String xLocal = x.getName().getLocalPart();
            String yNS = y.getName().getNamespaceURI();
            String yLocal = y.getName().getLocalPart();
            if (xNS == null || xNS.equals("")) {
                if (yNS != null && !"".equals(xNS)) {
                    return 1;
                }
            } else {
                if (yNS == null || "".equals(yNS)) {
                    return -1;
                } else {
                    int nsComparisonResult = xNS.compareTo(yNS);
                    if (nsComparisonResult != 0) {
                        return nsComparisonResult;
                    }
                }
            }


            return xLocal.compareTo(yLocal);
        }
    }

    private static class DebugOutputStream extends OutputStream {
        private ByteArrayOutputStream collector = new ByteArrayOutputStream();
        private OutputStream wrappedStream;

        public DebugOutputStream(OutputStream arg) {
            wrappedStream = arg;
        }

        public byte[] getCollectedData() {
            try {
                collector.flush();
            } catch (IOException e) {
            }
            return collector.toByteArray();
        }

        @Override
        public void write(int b) throws IOException {
            collector.write(b);
            wrappedStream.write(b);
        }

        @Override
        public void close() throws IOException {
            collector.close();
            wrappedStream.close();
            super.close();
        }

        @Override
        public void flush() throws IOException {
            collector.flush();
            wrappedStream.flush();
        }

    }
}