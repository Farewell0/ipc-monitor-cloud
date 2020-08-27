/**
 * @title: XMLUtils
 * @package: com.nexhome.community.utils.file
 * @project: nexhome-community
 * @description:
 * @author: hqy
 * @company eVideo
 * @date: 2019/9/19 15:10
 * @version V1.0
 */
package com.starnet.ipcmonitorcloud.utils;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * @className: XMLUtils
 * @description:
 * @author: hqy
 * @date: 2019/9/19 15:10
 * @mark:
 */
public class XMLUtils {

    public static <T> T readAsClass(Class<T> clazz, String fileName) throws Exception {
        try (InputStream stream = FileUtils.getFileInputStream(fileName)) {
            return readAsClass(clazz, stream);
        }
    }

    public static <T> T readAsClass(Class<T> clazz, InputStream file) throws JAXBException {
        if (file == null)
            return null;

        JAXBContext jc = JAXBContext.newInstance(clazz);
        Unmarshaller u = jc.createUnmarshaller();
        return (T) u.unmarshal(file);
    }

    public static <T> boolean writeXmlNotHead(T t, String fileName) throws Exception {
        return writeXml(t, false, fileName);
    }

    public static <T> boolean writeXml(T t, String fileName) throws Exception {
        return writeXml(t, true, fileName);
    }

    public static <T> boolean writeXml(T t, boolean needHead, String fileName) throws Exception {
        String context = write(t, needHead);
        return FileUtils.writeFile(fileName, context);
    }

    public static <T> String write(T t, boolean needHead) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(t.getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "utf-8");
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, !needHead);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter writer = new StringWriter();
        marshaller.marshal(t, writer);
        return writer.toString();
    }
}
