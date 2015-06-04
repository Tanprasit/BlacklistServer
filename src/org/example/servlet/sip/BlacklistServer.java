package org.example.servlet.sip;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.sip.Proxy;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.TooManyHopsException;

public class BlacklistServer extends SipServlet {
    
    private Set<String> blacklist;
    private ServletContext servletContext;
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void init(ServletConfig config) throws ServletException {
        // Creates and stores the blacklist in the servlet context.
        this.blacklist = this.createDefaultBlacklist();
        this.servletContext = config.getServletContext();
        this.servletContext.setAttribute(Constants.BLACKLIST, this.blacklist);
        
        super.init(config);
    }

    public void doInvite(SipServletRequest request) throws IOException, TooManyHopsException {
        
        // Get caller contact address.
	    String contactAddress = request.getFrom().getURI().toString();
	    
	    // Get blacklist form servlet context.
	    this.blacklist = this.getBlacklistFromContext();
	    
	    // Check contact address against the black list.
	    if (this.blacklist.contains(contactAddress)) {
	         request.createResponse(403).send();;
	    } else {
	        // If contact address wasn't in the blacklist proxy it.
	        Proxy proxy = request.getProxy();
	        proxy.proxyTo(request.getRequestURI());
	    }
	}

    private Set<String> createDefaultBlacklist() {
        Set<String> blacklist = new HashSet<String>();
        blacklist.add("sip:charn@alacrity.com");
        return blacklist;   
    }

    @SuppressWarnings("unchecked")
    private Set<String> getBlacklistFromContext() {
        this.servletContext = this.getServletContext();
        return (Set<String>) this.servletContext.getAttribute(Constants.BLACKLIST);
    }
}
