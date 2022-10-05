package ad.dom;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * - Quitar a los adeptos cuyo rango sea "pelapatatas".
 * - Dar un "bonus" (descendiente) al resto de los adeptos,
 * cuyo valor sea el 1% del presupuesto.
 * - Reducir el presupuesto de acuerdo con los bonus dados.
 */

/*
    Generar XML con los amos
 */
public class Prueba2 {

    private final DocumentBuilder builder;
    private final Transformer transformer;
    private final Schema schema;

    public Prueba2() throws ParserConfigurationException, TransformerConfigurationException, SAXException {

        SchemaFactory schemaFactory = SchemaFactory.newDefaultInstance();
        // SchemaFactory.newDefaultInstance() hace lo mismo que la siguiente
        // llamada, porque el tipo de esquema W3C_XML_SCHEMA_NS_URI es
        // el que se toma por defecto:
//        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        schema = schemaFactory.newSchema(new File("secta.xsd"));
        DocumentBuilderFactory factory = DocumentBuilderFactory.newDefaultInstance();
        factory.setSchema(schema); // Dar el esquema a la factory activa la validación en lectura del builder.
        factory.setIgnoringElementContentWhitespace(true); // Esto solamente funciona si se ha activado esa validación.
        builder = factory.newDocumentBuilder();
        transformer = TransformerFactory.newDefaultInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    }

    public static void main(String[] args) throws Exception {
        Prueba2 aplicacion = new Prueba2();
        Document doc = aplicacion.deserializar("secta.xml");
        aplicacion.quitarPelapatatas(doc);
        aplicacion.repartirBonus(doc);
        aplicacion.serializar(doc, System.out);
        Document informe = aplicacion.generarInformeAmos(doc);
        aplicacion.serializar(informe, new FileOutputStream("informe.xml"));
    }

    private Document deserializar(String nombreFichero) throws IOException, SAXException {
        return builder.parse(new File(nombreFichero));
    }

    private void quitarPelapatatas(Document doc) {
        // 1. Buscar todos los rangos
        NodeList rangos = doc.getElementsByTagName("rango");
        // 2. Para cada uno:
        for (int i = 0; i < rangos.getLength(); i++) {
            Node rango = rangos.item(i);
            // 2.1. Si el contenido es "pelapatatas", eliminar su padre
            if ("pelapatatas".equals(rango.getTextContent())) {
                Node adeptoPelapatatas = rango.getParentNode();
                adeptoPelapatatas.getParentNode().removeChild(adeptoPelapatatas);
            }
        }
    }

    private void repartirBonus(Document doc) {
        Node nodoPresupuesto = doc.getElementsByTagName("presupuesto").item(0);
        int presupuesto = Integer.parseInt(nodoPresupuesto.getTextContent());
        double bonus = presupuesto * 0.01;
        NodeList adeptos = doc.getElementsByTagName("adepto");
        for (int i = 0; i < adeptos.getLength(); i++) {
            Element adepto = (Element) adeptos.item(i);
            Element nodoBonus = doc.createElement("bonus");
            nodoBonus.setTextContent(String.valueOf(bonus));
            adepto.appendChild(nodoBonus);
            presupuesto -= bonus;
        }
        nodoPresupuesto.setTextContent(String.valueOf(presupuesto));
    }

    private void serializar(Document doc, OutputStream salida) throws TransformerException {
        transformer.transform(new DOMSource(doc), new StreamResult(salida));
    }

    private Document generarInformeAmos(Document doc) {
        Document documentAmos = builder.newDocument();
        NodeList rangos = doc.getElementsByTagName("rango");
        Element raiz = documentAmos.createElement("ElementoRaiz");
        documentAmos.appendChild(raiz);

        for (int i = 0; i < rangos.getLength(); i++) {
            Element rango = (Element) rangos.item(i);
            if (rango.getTextContent().equals("amo")) {
                Element padreDelRango = (Element) rango.getParentNode();

                NodeList nombreSecta =  padreDelRango.getElementsByTagName("nombre");
                Element nombreInforme = documentAmos.createElement("nombre");
                nombreInforme.setTextContent(nombreSecta.item(0).getTextContent());
                raiz.appendChild(nombreInforme);

            }
        }
        return documentAmos;
    }

}
