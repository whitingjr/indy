/*******************************************************************************
 * Copyright 2011 John Casey
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.commonjava.aprox.depbase.conf;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.commonjava.aprox.core.conf.ProxyConfiguration;
import org.commonjava.couch.conf.CouchDBConfiguration;
import org.commonjava.couch.conf.DefaultCouchDBConfiguration;
import org.commonjava.depbase.inject.DepbaseData;
import org.commonjava.web.config.ConfigurationException;
import org.commonjava.web.config.DefaultConfigurationListener;
import org.commonjava.web.config.dotconf.DotConfConfigurationReader;

@Singleton
public class DepbaseAproxConfigurationFactory
    extends DefaultConfigurationListener
{

    private static final String CONFIG_PATH = "/etc/aprox/depbase.conf";

    private static final String DEFAULT_DB_NAME = "depbase";

    private DefaultCouchDBConfiguration couchConfig;

    @Inject
    private ProxyConfiguration proxyConfig;

    public DepbaseAproxConfigurationFactory()
        throws ConfigurationException
    {
        super( DefaultCouchDBConfiguration.class );
    }

    @PostConstruct
    protected void load()
        throws ConfigurationException
    {
        final File configFile = new File( CONFIG_PATH );
        if ( configFile.isFile() )
        {
            InputStream stream = null;
            try
            {
                stream = new FileInputStream( configFile );
                new DotConfConfigurationReader( this ).loadConfiguration( stream );
            }
            catch ( final IOException e )
            {
                throw new ConfigurationException( "Cannot open configuration file: %s. Reason: %s", e, CONFIG_PATH,
                                                  e.getMessage() );
            }
            finally
            {
                closeQuietly( stream );
            }
        }
        else
        {
            couchConfig = new DefaultCouchDBConfiguration( proxyConfig.getDatabaseConfig(), DEFAULT_DB_NAME );
        }
    }

    @Produces
    @DepbaseData
    @Default
    public CouchDBConfiguration getCouchDBConfiguration()
    {
        return couchConfig;
    }

    @Override
    public void configurationComplete()
        throws ConfigurationException
    {
        couchConfig = getConfiguration( DefaultCouchDBConfiguration.class );
    }

}
