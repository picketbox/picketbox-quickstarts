package org.picketbox.core.test.authentication.spi.sasl;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.URLName;

import com.sun.mail.smtp.SMTPTransport;

public class TestSMTPTransport extends SMTPTransport {

    public TestSMTPTransport(Session session, URLName urlname) {
        super(session, urlname);
    }

    @Override
    public void startTLS() throws MessagingException {
        super.startTLS();
    }
    
    @Override
    public int simpleCommand(byte[] cmd) throws MessagingException {
        // TODO Auto-generated method stub
        return super.simpleCommand(cmd);
    }
    
}
