package root.utils;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import root.models.Lecture;
import root.models.Student;
import root.models.Subject;
import root.models.enums.Attendance;
import root.models.enums.LectureType;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class XmlUtils {
    private static String schemaFileName     = "schema.xsd";
    private static String schemaRelativePathFromXml = "../../schema/" + schemaFileName;
    private static String schemaRelativePath = "./xml/schema/" + schemaFileName;

    private static File   schemaFile;
    private static Schema schema;

    static {
        schemaFile = new File(schemaRelativePath);
        schema = getSchema();
    }

    public static boolean saveXml(File file, List<Student> students, Subject subject, boolean includeSchema) {
        Document doc;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder        builder = factory.newDocumentBuilder();
            doc = builder.newDocument();
        } catch (ParserConfigurationException e) {
            return false;
        }
        Element subjectEl = doc.createElement("Subject");
        if(includeSchema){
            subjectEl.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xs:noNamespaceSchemaLocation", schemaRelativePathFromXml);
        }
        doc.appendChild(subjectEl);
        appendTextNodeChild(doc, subjectEl, "Title", subject.getTitle());
        appendTextNodeChild(doc, subjectEl, "Group", subject.getGroup());
        appendTextNodeChild(doc, subjectEl, "Lecturer", subject.getLecturer());
        {
            String practicalLecturer = subject.getPracticalLecturer();
            if (!practicalLecturer.equals("")) {
                appendTextNodeChild(doc, subjectEl, "PracticalLecturer", practicalLecturer);
            }
        }
        Element studentsEl = appendChild(doc, subjectEl, "Students");
        for (Student student : students) {
            Element studentEl = appendChild(doc, studentsEl, "Student");
            studentEl.setAttribute("name", student.getName());
            if (student.getAttendance().entrySet().size() == 0) {
                continue;
            }
            Element lecturesEl = appendChild(doc, studentEl, "Lectures");
            for (Map.Entry<Lecture, ObjectProperty<Attendance>> entry : student.getAttendance().entrySet()) {
                Lecture lecture   = entry.getKey();
                Element lectureEl = appendChild(doc, lecturesEl, "Lecture");
                lectureEl.setAttribute("type", lecture.getType().name());
                lectureEl.setAttribute("dateTime", DTF.formatDateTimeToIso(lecture.getDateTime()));
                lectureEl.setAttribute("audience", lecture.getAudience());
                String note = lecture.getNote();
                if (!note.equals("")) {
                    lectureEl.setAttribute("note", note);
                }
                appendTextNodeChild(doc, lectureEl, "Attendance", entry.getValue().getValue().name());
            }
        }

        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//            transformer.setOutputProperty(OutputKeys.METHOD, "html");
            DOMSource    source = new DOMSource(doc);
            StreamResult out    = new StreamResult(file);
            transformer.transform(source, out);
        } catch (TransformerException e) {
            return false;
        }
        return true;
    }

    private static Element appendChild(Document doc, Element parent, String tagName) {
        Element element = doc.createElement(tagName);
        parent.appendChild(element);
        return element;
    }

    private static Element appendTextNodeChild(Document doc, Element parent, String tagName, String tagValue) {
        Element element = appendChild(doc, parent, tagName);
        element.appendChild(doc.createTextNode(tagValue));
        return element;
    }

    //todo bug: can't load files where students haven't lectures
    public static boolean loadXml(File file, ObservableList<Student> students, Subject subject) {
        if (schema == null) {
            schema = getSchema();
            if (schema == null) {
                return false;
            }
        }
        boolean validate = validate(file);
        if (!validate) {
            return false;
        }
        Document doc;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
//            documentBuilderFactory.setIgnoringElementContentWhitespace(true);
//            documentBuilderFactory.setSchema(schema);
//            documentBuilderFactory.setValidating(true);
            DocumentBuilder db = documentBuilderFactory.newDocumentBuilder();
            doc = db.parse(file);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            return false;
        }
        Node rootNode = doc.getDocumentElement();
//        rootNode.normalize();
        NodeList nodeListRoot = rootNode.getChildNodes();

        NodeList studentNodes = null;
        for (int i = 0; i < nodeListRoot.getLength(); i++) {
            Node node = nodeListRoot.item(i);
            if (node.getNodeType() == Node.TEXT_NODE) {
                continue;
            }
            if (node.getNodeName().equals("Students")) {
                studentNodes = node.getChildNodes();
                //break;
                continue;
            }
            Node   firstChild = node.getFirstChild();
            if (firstChild == null) {
                return true;
            }
            String value = firstChild.getNodeValue();
            switch (node.getNodeName()) {
                case "Title":
                    subject.setTitle(value);
                    break;
                case "Group":
                    subject.setGroup(value);
                    break;
                case "Lecturer":
                    subject.setLecturer(value);
                    break;
                case "PracticalLecturer":
                    subject.setPracticalLecturer(value);
                    break;
            }
        }
        if (studentNodes == null) {
            return false;
        }
        for (int i = 0; i < studentNodes.getLength(); i++) {
            Node studentNode = studentNodes.item(i);
            if (studentNode.getNodeType() == Node.TEXT_NODE) {
                continue;
            }
            Student student = new Student();
            student.setName(studentNode.getAttributes().getNamedItem("name").getNodeValue());
            NodeList lectureNodes = null;
            for (int j = 0; j < studentNode.getChildNodes().getLength(); j++) {
                Node node = studentNode.getChildNodes().item(j);
                if (node.getNodeName().equals("Lectures")) {
                    lectureNodes = node.getChildNodes();
                }
            }
            if (lectureNodes == null) {
                continue;
            }
            for (int j = 0; j < lectureNodes.getLength(); j++) {
                Node lectureNode = lectureNodes.item(j);
                if (lectureNode.getNodeType() == Node.TEXT_NODE) {
                    continue;
                }
                Lecture      lecture    = new Lecture();
                NamedNodeMap attributes = lectureNode.getAttributes();
                lecture.setAudience(attributes.getNamedItem("audience").getNodeValue());
                Node note = attributes.getNamedItem("note");
                if (note != null) {
                    lecture.setNote(note.getNodeValue());
                }
                lecture.setType(LectureType.valueOf(attributes.getNamedItem("type").getNodeValue()));
                lecture.setDateTime(DTF.parseDateTimeIso(attributes.getNamedItem("dateTime").getNodeValue()));
                Attendance attendance = Attendance.NULL;
                for (int k = 0; k < lectureNode.getChildNodes().getLength(); k++) {
                    Node item = lectureNode.getChildNodes().item(k);
                    if (item.getNodeName().equals("Attendance")) {
                        attendance = Attendance.valueOf(item.getFirstChild().getNodeValue());
                        break;
                    }
                }
                student.getAttendance().put(lecture, new SimpleObjectProperty<>(attendance));
            }
            students.add(student);
        }
        Collections.sort(students);
        return true;
    }

    public static boolean validate(File xml) {
        return validate(xml, schemaFile);
    }

    public static boolean validate(File xml, File schemaFile) {
        if (schema == null) {
            schema = getSchema();
            if (schema == null) {
                return false;
            }
        }
        Source xmlFile = new StreamSource(xml);
        try {
            Validator validator = schema.newValidator();
            validator.validate(xmlFile);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static Schema getSchema() {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            return schemaFactory.newSchema(schemaFile);
        } catch (SAXException e) {
            return null;
        }
    }
}
