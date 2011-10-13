package nl.cubix.scrabble.config;

import java.net.URL;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.apache.commons.configuration.beanutils.BeanHelper;
import org.apache.commons.configuration.beanutils.XMLBeanDeclaration;
import org.apache.commons.configuration.event.ConfigurationErrorEvent;
import org.apache.commons.configuration.event.ConfigurationErrorListener;
import org.apache.commons.configuration.tree.OverrideCombiner;
import org.apache.log4j.Logger;

/**
 * Wrapper for DefaultConfigurationBuilder for the Apache Commons Configuration framework. The only reason is to
 * disable the default error listeners 
 * @author jan-willem
 *
 */
@SuppressWarnings("serial")
public class CustomConfigurationBuilder extends DefaultConfigurationBuilder {

	private transient final Logger logger = Logger.getLogger(CustomConfigurationBuilder.class);
	
	public CustomConfigurationBuilder() {
		
		super();
		
		// Remove the default logger, it prints an exception when an optional log does not exist.
		// Add a customer error listener if you want to catch messages
		super.clearErrorListeners();
		
		//FIXME: is this still necessary?
		super.addErrorListener(new ConfigurationErrorListener() {
			public void configurationError(ConfigurationErrorEvent event) {
					String message = "" + event.getType() + " : ";
					if (event.getCause() != null) {
						message += event.getCause().getMessage();
					}
					logger.warn("CustomConfigurationBuilder.configurationError " + message);
			}
		});
	}
	
    /**
     * Creates a new instance of <code>DefaultConfigurationBuilder</code> and
     * sets the specified configuration definition file.
     *
     * @param url the URL to the configuration definition file
     * @throws ConfigurationException if an error occurs when the file is loaded
     */
    public CustomConfigurationBuilder(URL url) throws ConfigurationException {
        this();
        setURL(url);
    }
    
    /**
     * Creates the resulting combined configuration. This method is called by
     * <code>getConfiguration()</code>. It checks whether the
     * <code>header</code> section of the configuration definition file
     * contains a <code>result</code> element. If this is the case, it will be
     * used to initialize the properties of the newly created configuration
     * object.
     *
     * @return the resulting configuration object
     * @throws ConfigurationException if an error occurs
     */
    @Override
    protected CombinedConfiguration createResultConfiguration() throws ConfigurationException
    {
    	final String SEC_HEADER = "header";
    	final String KEY_RESULT = SEC_HEADER + ".result";
    	final String KEY_COMBINER = KEY_RESULT + ".nodeCombiner";
        
        XMLBeanDeclaration decl = new XMLBeanDeclaration(this, KEY_RESULT, true);
        CombinedConfiguration result = (CombinedConfiguration) BeanHelper.createBean(decl, CombinedConfiguration.class);

        if (getMaxIndex(KEY_COMBINER) < 0) {
            // No combiner defined => set default
            result.setNodeCombiner(new OverrideCombiner());
        }

        return result;
    }
}
