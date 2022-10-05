package ad.dom;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Prueba1 {

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newDefaultInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File("secta.xml"));
        NodeList adeptos = doc.getElementsByTagName("adepto");

        for (int i = 0; i < adeptos.getLength(); i++) {
            Node nodo = adeptos.item(i);
            Element adepto = (Element) nodo;
            // Así no conviene hacerlo:
//            System.out.println(adepto.getFirstChild().getTextContent());

            // Así podríamos hacerlo:
            /*NodeList hijosNombre = doc.getElementsByTagName("nombre");
            Element nombre = (Element) hijosNombre.item(0);
            if(nombre.getTextContent().equals("Juan")) {
                NodeList hijosFecha = adepto.getElementsByTagName("fecha_adhesion");
                Element fecha = (Element) hijosFecha.item(0);
                System.out.println(fecha.getTextContent());
            }*/

            // Así también podríamos:
            NodeList hijos = adepto.getChildNodes();
            ArrayList<Element> hijosElementos = new ArrayList<>();
            for (int j = 0; j < hijos.getLength(); j++) {
                if(hijos.item(j).getNodeType() == Node.ELEMENT_NODE) {
                    hijosElementos.add((Element) hijos.item(j));
                }
            }
            if(hijosElementos.get(0).getTextContent().equals("Juan")) {
                System.out.println(hijosElementos.get(1).getTextContent());
            }
        }

    }

}
