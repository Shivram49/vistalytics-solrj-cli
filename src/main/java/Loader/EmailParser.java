package Loader;

import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;
import javax.mail.Session;
import javax.mail.MessagingException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.HashMap;

public class EmailParser {
    public static HashMap<String,String> parseEnronEmails(String rawEmail) {
        HashMap<String, String> emailInfo = new HashMap<>();


        try {
            // Create a Session object
            Properties properties = new Properties();
            Session session = Session.getDefaultInstance(properties, null);

            // Convert the raw email string to a byte array
            byte[] bytes = rawEmail.getBytes();

            // Create an InputStream from the byte array
            InputStream inputStream = new ByteArrayInputStream(bytes);

            // Parse the raw email into a MimeMessage object
            MimeMessage email = new MimeMessage(session, inputStream);

            // Extract information from the email
            emailInfo.put("Message-ID", email.getMessageID());
            emailInfo.put("Date", email.getSentDate() != null ? email.getSentDate().toString() : "");
            emailInfo.put("From", email.getFrom() != null && email.getFrom().length > 0 ? email.getFrom()[0].toString() : "");
            emailInfo.put("To", email.getRecipients(MimeMessage.RecipientType.TO) != null && email.getRecipients(MimeMessage.RecipientType.TO).length > 0 ? email.getRecipients(MimeMessage.RecipientType.TO)[0].toString() : "");
            emailInfo.put("Subject", email.getSubject() != null ? email.getSubject() : "");
            emailInfo.put("Mime-Version", email.getHeader("Mime-Version") != null && email.getHeader("Mime-Version").length > 0 ? email.getHeader("Mime-Version")[0] : "");
            emailInfo.put("Content-Type", email.getContentType() != null ? email.getContentType() : "");
            emailInfo.put("Content-Transfer-Encoding", email.getHeader("Content-Transfer-Encoding") != null && email.getHeader("Content-Transfer-Encoding").length > 0 ? email.getHeader("Content-Transfer-Encoding")[0] : "");
            emailInfo.put("X-From", email.getHeader("X-From") != null && email.getHeader("X-From").length > 0 ? email.getHeader("X-From")[0] : "");
            emailInfo.put("X-To", email.getHeader("X-To") != null && email.getHeader("X-To").length > 0 ? email.getHeader("X-To")[0] : "");
            emailInfo.put("X-cc", email.getHeader("X-cc") != null && email.getHeader("X-cc").length > 0 ? email.getHeader("X-cc")[0] : "");
            emailInfo.put("X-bcc", email.getHeader("X-bcc") != null && email.getHeader("X-bcc").length > 0 ? email.getHeader("X-bcc")[0] : "");
            emailInfo.put("X-Folder", email.getHeader("X-Folder") != null && email.getHeader("X-Folder").length > 0 ? email.getHeader("X-Folder")[0] : "");
            emailInfo.put("X-Origin", email.getHeader("X-Origin") != null && email.getHeader("X-Origin").length > 0 ? email.getHeader("X-Origin")[0] : "");
            emailInfo.put("X-FileName", email.getHeader("X-FileName") != null && email.getHeader("X-FileName").length > 0 ? email.getHeader("X-FileName")[0] : "");
            emailInfo.put("Body", email.getContent() != null ? email.getContent().toString() : "");


            // Print the extracted information
//            System.out.println(emailInfo);

        }
        catch(AddressException e){
            System.out.println(e);
            return new HashMap<String,String>();
        }
        catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
        return emailInfo;
    }
}
